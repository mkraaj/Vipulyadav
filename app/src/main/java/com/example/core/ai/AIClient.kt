package com.example.core.ai

import com.example.config.ModelConfig
import com.example.config.ModelProfile
import com.example.data.db.EngineDao
import com.example.data.model.CachedResponse
import com.example.data.model.GeminiKeyEntity
import com.example.data.model.QueueTaskItem
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

sealed class AIResult<out T> {
    data class Success<out T>(val data: T, val keyNickname: String, val isCached: Boolean = false) : AIResult<T>()
    data class Error(val message: String, val statusCode: Int? = null) : AIResult<Nothing>()
}

enum class TaskPriority(val value: Int) {
    HIGH(1), // User is actively waiting on UI (e.g. quiz generation, instant explanation)
    LOW(0)   // Background pre-fetching, summary caching
}

class AIClient(
    private val engineDao: EngineDao
) {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val queueMutex = Mutex()
    private val _activeQueue = MutableStateFlow<List<QueueTaskItem>>(emptyList())
    val activeQueue: StateFlow<List<QueueTaskItem>> = _activeQueue.asStateFlow()

    private val _dataSaverMode = MutableStateFlow(false)
    val dataSaverMode: StateFlow<Boolean> = _dataSaverMode.asStateFlow()

    fun setDataSaver(enabled: Boolean) {
        _dataSaverMode.value = enabled
    }

    /**
     * Compute SHA-256 hash for deterministic, zero-cost response caching
     */
    fun computeCacheKey(prompt: String, schema: String?, modelId: String): String {
        val input = "$modelId||$schema||$prompt"
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Test an individual key with a lightweight ping to verify validity
     */
    suspend fun testKey(key: GeminiKeyEntity): Result<String> = withContext(Dispatchers.IO) {
        val testModel = ModelConfig.DEFAULT_FAST_PROFILE.modelId
        val url = "${ModelConfig.BASE_GEMINI_URL}$testModel:generateContent?key=${key.apiKey}"

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", "Reply 'OK' if you receive this test."))
                    })
                })
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            val code = response.code
            val bodyString = response.body?.string() ?: ""

            when (code) {
                200 -> {
                    engineDao.markKeyHealthy(key.id)
                    Result.success("Key Verified & Healthy (HTTP 200)")
                }
                400 -> {
                    engineDao.markKeyInvalid(key.id, "Invalid API key (HTTP 400)")
                    Result.failure(Exception("API Key Invalid (HTTP 400). Please check your key."))
                }
                429 -> {
                    val cooldown = System.currentTimeMillis() + 60_000L
                    engineDao.recordKeyCooldown(key.id, "RATE_LIMITED", cooldown, "Rate limited (HTTP 429)")
                    Result.failure(Exception("Key Rate Limited (HTTP 429). Cooldown applied for 60s."))
                }
                403 -> {
                    val cooldown = System.currentTimeMillis() + 300_000L
                    engineDao.recordKeyCooldown(key.id, "COOLDOWN", cooldown, "Quota exceeded (HTTP 403)")
                    Result.failure(Exception("Quota Exceeded (HTTP 403). Cooldown applied for 5m."))
                }
                else -> {
                    Result.failure(Exception("HTTP $code: ${bodyString.take(150)}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Execute AI request through the Multi-Key Engine with smart routing, failover, and caching.
     */
    suspend fun executeRequest(
        taskType: String,
        prompt: String,
        modelProfile: ModelProfile = ModelConfig.DEFAULT_FAST_PROFILE,
        responseSchemaJson: String? = null,
        priority: TaskPriority = TaskPriority.HIGH,
        bypassCache: Boolean = false
    ): AIResult<String> = withContext(Dispatchers.IO) {

        // 1. Check Response Cache (IndexedDB / Room)
        val cacheKey = computeCacheKey(prompt, responseSchemaJson, modelProfile.modelId)
        if (!bypassCache) {
            val cached = engineDao.getCachedResponse(cacheKey)
            if (cached != null) {
                return@withContext AIResult.Success(
                    data = cached.responseText,
                    keyNickname = "Local Cache",
                    isCached = true
                )
            }
        }

        // 2. Track in Queue
        val taskId = "task_${System.currentTimeMillis()}_${Random.nextInt(1000, 9999)}"
        val queueItem = QueueTaskItem(
            id = taskId,
            taskType = taskType,
            priority = priority.value,
            status = "RUNNING"
        )
        queueMutex.withLock {
            _activeQueue.value = _activeQueue.value + queueItem
        }

        try {
            // Apply Data Saver modification if enabled
            val finalPrompt = if (_dataSaverMode.value) {
                "$prompt\n\n[DATA SAVER MODE: Be extremely concise, use bullet points, limit to necessary facts.]"
            } else prompt

            // 3. Multi-Key Smart Routing Loop
            val attemptedKeyIds = mutableSetOf<Long>()
            var lastErrorMessage = "No active Gemini keys available."
            var lastStatusCode: Int? = null

            while (true) {
                val enabledKeys = engineDao.getEnabledKeys()
                if (enabledKeys.isEmpty()) {
                    return@withContext AIResult.Error(
                        message = "No Gemini API keys registered or enabled. Please add at least one key in the AI Engine tab."
                    )
                }

                // Filter out keys already attempted in this request or currently in cooldown
                val now = System.currentTimeMillis()
                val candidateKeys = enabledKeys
                    .filter { !attemptedKeyIds.contains(it.id) }
                    .filter { it.cooldownUntil <= now }
                    // Smart routing: prefer key with lowest calls today, then lowest errors
                    .sortedWith(compareBy({ it.callsToday }, { it.errorsCount }))

                val selectedKey = candidateKeys.firstOrNull()

                if (selectedKey == null) {
                    // Check if all keys are temporarily in cooldown
                    val soonestCooldownKey = enabledKeys.minByOrNull { it.cooldownUntil }
                    val waitSec = if (soonestCooldownKey != null) {
                        ((soonestCooldownKey.cooldownUntil - now) / 1000).coerceAtLeast(1)
                    } else 0

                    return@withContext AIResult.Error(
                        message = "All ${enabledKeys.size} Gemini API keys are currently rate-limited or in cooldown. Cooldown resets in ${waitSec}s. You can add another key to continue immediately.",
                        statusCode = lastStatusCode
                    )
                }

                attemptedKeyIds.add(selectedKey.id)

                // 4. Construct Gemini API Request
                val url = "${ModelConfig.BASE_GEMINI_URL}${modelProfile.modelId}:generateContent?key=${selectedKey.apiKey}"

                val jsonBody = JSONObject().apply {
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", finalPrompt))
                            })
                        })
                    })

                    val genConfig = JSONObject().apply {
                        put("temperature", modelProfile.defaultTemperature)
                        if (responseSchemaJson != null) {
                            put("responseMimeType", "application/json")
                            try {
                                put("responseSchema", JSONObject(responseSchemaJson))
                            } catch (_: Exception) {}
                        }
                    }
                    put("generationConfig", genConfig)
                }

                val request = Request.Builder()
                    .url(url)
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                try {
                    val response = okHttpClient.newCall(request).execute()
                    val statusCode = response.code
                    val responseBody = response.body?.string() ?: ""

                    when (statusCode) {
                        200 -> {
                            val responseJson = JSONObject(responseBody)
                            val candidates = responseJson.optJSONArray("candidates")
                            val firstCandidate = candidates?.optJSONObject(0)
                            val content = firstCandidate?.optJSONObject("content")
                            val parts = content?.optJSONArray("parts")
                            val text = parts?.optJSONObject(0)?.optString("text", "") ?: ""

                            val estimatedTokens = (finalPrompt.length + text.length) / 4
                            engineDao.recordKeySuccess(selectedKey.id, estimatedTokens.toLong(), System.currentTimeMillis())

                            // Cache successful response
                            engineDao.insertCachedResponse(
                                CachedResponse(
                                    cacheKey = cacheKey,
                                    taskType = taskType,
                                    modelId = modelProfile.modelId,
                                    responseText = text,
                                    tokenEstimate = estimatedTokens
                                )
                            )

                            return@withContext AIResult.Success(
                                data = text,
                                keyNickname = selectedKey.nickname,
                                isCached = false
                            )
                        }

                        429 -> {
                            // Rate limited: Cooldown 60s + exponential jitter
                            lastStatusCode = 429
                            lastErrorMessage = "Key '${selectedKey.nickname}' reached rate limit (HTTP 429)."
                            val jitter = Random.nextLong(2000, 5000)
                            val cooldown = System.currentTimeMillis() + 60_000L + jitter
                            engineDao.recordKeyCooldown(selectedKey.id, "RATE_LIMITED", cooldown, "Rate limited (429)")

                            // Small backoff before retrying with next key
                            delay(500)
                            continue
                        }

                        403 -> {
                            // Quota exceeded: Cooldown 5 minutes
                            lastStatusCode = 403
                            lastErrorMessage = "Key '${selectedKey.nickname}' quota exhausted (HTTP 403)."
                            val cooldown = System.currentTimeMillis() + 300_000L
                            engineDao.recordKeyCooldown(selectedKey.id, "COOLDOWN", cooldown, "Quota exhausted (403)")
                            continue
                        }

                        400 -> {
                            // Invalid key: Disable and notify
                            lastStatusCode = 400
                            lastErrorMessage = "Key '${selectedKey.nickname}' is invalid (HTTP 400)."
                            engineDao.markKeyInvalid(selectedKey.id, "API Key Invalid (400)")
                            continue
                        }

                        else -> {
                            lastStatusCode = statusCode
                            lastErrorMessage = "HTTP $statusCode from Gemini API: ${responseBody.take(180)}"
                            continue
                        }
                    }
                } catch (e: Exception) {
                    lastErrorMessage = "Network error with key '${selectedKey.nickname}': ${e.message}"
                    continue
                }
            }

            @Suppress("UNREACHABLE_CODE")
            AIResult.Error(lastErrorMessage, lastStatusCode)
        } finally {
            queueMutex.withLock {
                _activeQueue.value = _activeQueue.value.filter { it.id != taskId }
            }
        }
    }
}
