package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.BuildConfig
import com.example.data.model.CachedResponse
import com.example.data.model.GeminiKeyEntity
import com.example.data.model.LectureResource
import com.example.data.model.QuizAttempt
import com.example.data.model.QuizQuestion
import com.example.data.model.StudyNote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        StudyNote::class,
        LectureResource::class,
        QuizQuestion::class,
        QuizAttempt::class,
        GeminiKeyEntity::class,
        CachedResponse::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studyDao(): StudyDao
    abstract fun engineDao(): EngineDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "exampilot_master_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.studyDao(), database.engineDao())
                    }
                }
            }

            private suspend fun populateInitialData(studyDao: StudyDao, engineDao: EngineDao) {
                studyDao.insertAllNotes(DefaultStudyData.initialNotes)
                studyDao.insertAllLectures(DefaultStudyData.initialLectures)
                studyDao.insertAllQuestions(DefaultStudyData.initialQuestions)

                // Seed BuildConfig key if available and non-empty
                try {
                    val defaultKey = BuildConfig.GEMINI_API_KEY
                    if (defaultKey.isNotBlank() && !defaultKey.startsWith("MY_")) {
                        engineDao.insertKey(
                            GeminiKeyEntity(
                                nickname = "Primary Studio Key",
                                apiKey = defaultKey,
                                isEnabled = true,
                                status = "HEALTHY"
                            )
                        )
                    }
                } catch (_: Exception) {}
            }
        }
    }
}
