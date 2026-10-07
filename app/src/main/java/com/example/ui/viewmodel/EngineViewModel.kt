package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.config.AppLanguage
import com.example.config.ExamConfig
import com.example.config.ExamPattern
import com.example.config.QuestionDisplayMode
import com.example.core.ai.AIClient
import com.example.core.crypto.KeyCrypto
import com.example.data.db.AppDatabase
import com.example.data.db.EngineDao
import com.example.data.model.GeminiKeyEntity
import com.example.data.model.QueueTaskItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EngineViewModel(application: Application) : AndroidViewModel(application) {

    private val engineDao: EngineDao
    val aiClient: AIClient

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        engineDao = db.engineDao()
        aiClient = AIClient(engineDao)
    }

    val keys: StateFlow<List<GeminiKeyEntity>> = engineDao.getAllKeys()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cacheCount: StateFlow<Int> = engineDao.getCacheCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val activeQueue: StateFlow<List<QueueTaskItem>> = aiClient.activeQueue

    val dataSaverMode: StateFlow<Boolean> = aiClient.dataSaverMode

    private val _appLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _questionDisplayMode = MutableStateFlow(QuestionDisplayMode.BILINGUAL)
    val questionDisplayMode: StateFlow<QuestionDisplayMode> = _questionDisplayMode.asStateFlow()

    private val _selectedExam = MutableStateFlow(ExamConfig.EXAM_LIST[0])
    val selectedExam: StateFlow<ExamPattern> = _selectedExam.asStateFlow()

    private val _testKeyResult = MutableStateFlow<Map<Long, String>>(emptyMap())
    val testKeyResult: StateFlow<Map<Long, String>> = _testKeyResult.asStateFlow()

    private val _testingKeyId = MutableStateFlow<Long?>(null)
    val testingKeyId: StateFlow<Long?> = _testingKeyId.asStateFlow()

    fun toggleLanguage() {
        _appLanguage.value = if (_appLanguage.value == AppLanguage.ENGLISH) {
            AppLanguage.HINDI
        } else {
            AppLanguage.ENGLISH
        }
    }

    fun setLanguage(language: AppLanguage) {
        _appLanguage.value = language
    }

    fun setQuestionDisplayMode(mode: QuestionDisplayMode) {
        _questionDisplayMode.value = mode
    }

    fun setSelectedExam(examId: String) {
        _selectedExam.value = ExamConfig.getExamById(examId)
    }

    fun setDataSaver(enabled: Boolean) {
        aiClient.setDataSaver(enabled)
    }

    fun addKey(nickname: String, apiKey: String) {
        viewModelScope.launch {
            val key = GeminiKeyEntity(
                nickname = nickname.trim().ifBlank { "Gemini Key #${keys.value.size + 1}" },
                apiKey = apiKey.trim(),
                isEnabled = true,
                status = "UNTESTED"
            )
            engineDao.insertKey(key)
        }
    }

    fun deleteKey(id: Long) {
        viewModelScope.launch {
            engineDao.deleteKeyById(id)
        }
    }

    fun toggleKeyEnabled(id: Long, enabled: Boolean) {
        viewModelScope.launch {
            engineDao.setKeyEnabled(id, enabled)
        }
    }

    fun testKey(key: GeminiKeyEntity) {
        viewModelScope.launch {
            _testingKeyId.value = key.id
            val result = aiClient.testKey(key)
            val message = result.fold(
                onSuccess = { it },
                onFailure = { it.message ?: "Test failed" }
            )
            _testKeyResult.value = _testKeyResult.value + (key.id to message)
            _testingKeyId.value = null
        }
    }

    fun exportEncryptedKeys(passphrase: String): String {
        val currentKeys = keys.value.map {
            KeyCrypto.KeyExportItem(
                nickname = it.nickname,
                apiKey = it.apiKey,
                isEnabled = it.isEnabled
            )
        }
        return KeyCrypto.encryptKeys(currentKeys, passphrase)
    }

    fun importEncryptedKeys(encryptedPackage: String, passphrase: String, onResult: (Result<Int>) -> Unit) {
        viewModelScope.launch {
            val result = KeyCrypto.decryptKeys(encryptedPackage.trim(), passphrase)
            result.fold(
                onSuccess = { items ->
                    val entities = items.map {
                        GeminiKeyEntity(
                            nickname = it.nickname,
                            apiKey = it.apiKey,
                            isEnabled = it.isEnabled,
                            status = "UNTESTED"
                        )
                    }
                    engineDao.insertAllKeys(entities)
                    onResult(Result.success(items.size))
                },
                onFailure = {
                    onResult(Result.failure(it))
                }
            )
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            engineDao.clearCache()
        }
    }
}
