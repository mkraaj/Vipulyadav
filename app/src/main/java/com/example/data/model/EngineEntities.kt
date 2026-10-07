package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gemini_keys")
data class GeminiKeyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nickname: String,
    val apiKey: String,
    val isEnabled: Boolean = true,
    val status: String = "UNTESTED", // HEALTHY, COOLDOWN, RATE_LIMITED, INVALID, UNTESTED
    val cooldownUntil: Long = 0L,
    val callsToday: Int = 0,
    val tokensToday: Long = 0L,
    val errorsCount: Int = 0,
    val lastSuccessTime: Long = 0L,
    val lastError: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getMaskedKey(): String {
        return if (apiKey.length > 8) {
            "${apiKey.take(6)}...${apiKey.takeLast(4)}"
        } else "******"
    }

    fun isCurrentlyInCooldown(): Boolean {
        return cooldownUntil > System.currentTimeMillis()
    }

    fun remainingCooldownSeconds(): Int {
        val remaining = (cooldownUntil - System.currentTimeMillis()) / 1000
        return if (remaining > 0) remaining.toInt() else 0
    }
}

@Entity(tableName = "response_cache")
data class CachedResponse(
    @PrimaryKey
    val cacheKey: String, // SHA256 of prompt + schema + model
    val taskType: String,
    val modelId: String,
    val responseText: String,
    val tokenEstimate: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

data class QueueTaskItem(
    val id: String,
    val taskType: String,
    val priority: Int, // 1 = HIGH (user waiting), 0 = LOW (background)
    val status: String, // QUEUED, RUNNING, COMPLETED, FAILED, CANCELLED
    val timestamp: Long = System.currentTimeMillis()
)
