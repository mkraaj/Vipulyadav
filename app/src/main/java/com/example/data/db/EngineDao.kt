package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CachedResponse
import com.example.data.model.GeminiKeyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EngineDao {

    // --- Key Management ---
    @Query("SELECT * FROM gemini_keys ORDER BY id ASC")
    fun getAllKeys(): Flow<List<GeminiKeyEntity>>

    @Query("SELECT * FROM gemini_keys WHERE isEnabled = 1")
    suspend fun getEnabledKeys(): List<GeminiKeyEntity>

    @Query("SELECT * FROM gemini_keys WHERE id = :id LIMIT 1")
    suspend fun getKeyById(id: Long): GeminiKeyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKey(key: GeminiKeyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllKeys(keys: List<GeminiKeyEntity>)

    @Update
    suspend fun updateKey(key: GeminiKeyEntity)

    @Delete
    suspend fun deleteKey(key: GeminiKeyEntity)

    @Query("DELETE FROM gemini_keys WHERE id = :id")
    suspend fun deleteKeyById(id: Long)

    @Query("UPDATE gemini_keys SET isEnabled = :enabled WHERE id = :id")
    suspend fun setKeyEnabled(id: Long, enabled: Boolean)

    @Query("UPDATE gemini_keys SET callsToday = callsToday + 1, tokensToday = tokensToday + :tokens, lastSuccessTime = :timestamp, status = 'HEALTHY', lastError = null WHERE id = :id")
    suspend fun recordKeySuccess(id: Long, tokens: Long, timestamp: Long)

    @Query("UPDATE gemini_keys SET errorsCount = errorsCount + 1, status = :status, cooldownUntil = :cooldownUntil, lastError = :error WHERE id = :id")
    suspend fun recordKeyCooldown(id: Long, status: String, cooldownUntil: Long, error: String)

    @Query("UPDATE gemini_keys SET status = 'INVALID', isEnabled = 0, lastError = :error WHERE id = :id")
    suspend fun markKeyInvalid(id: Long, error: String)

    @Query("UPDATE gemini_keys SET status = 'HEALTHY', lastError = null WHERE id = :id")
    suspend fun markKeyHealthy(id: Long)

    // --- Response Cache ---
    @Query("SELECT * FROM response_cache WHERE cacheKey = :key LIMIT 1")
    suspend fun getCachedResponse(key: String): CachedResponse?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedResponse(cachedResponse: CachedResponse)

    @Query("SELECT COUNT(*) FROM response_cache")
    fun getCacheCount(): Flow<Int>

    @Query("DELETE FROM response_cache")
    suspend fun clearCache()
}
