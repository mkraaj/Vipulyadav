package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.LectureResource
import com.example.data.model.QuizAttempt
import com.example.data.model.QuizQuestion
import com.example.data.model.StudyNote
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    // --- Study Notes ---
    @Query("SELECT * FROM study_notes ORDER BY isPinned DESC, lastModified DESC")
    fun getAllNotes(): Flow<List<StudyNote>>

    @Query("SELECT * FROM study_notes WHERE subject = :subject ORDER BY isPinned DESC, lastModified DESC")
    fun getNotesBySubject(subject: String): Flow<List<StudyNote>>

    @Query("SELECT * FROM study_notes WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: Long): StudyNote?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: StudyNote): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNotes(notes: List<StudyNote>)

    @Update
    suspend fun updateNote(note: StudyNote)

    @Delete
    suspend fun deleteNote(note: StudyNote)

    @Query("DELETE FROM study_notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Query("UPDATE study_notes SET isPinned = NOT isPinned WHERE id = :id")
    suspend fun togglePinNote(id: Long)

    @Query("UPDATE study_notes SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavoriteNote(id: Long)

    // --- Lecture Resources ---
    @Query("SELECT * FROM lecture_resources ORDER BY orderIndex ASC, id ASC")
    fun getAllLectures(): Flow<List<LectureResource>>

    @Query("SELECT * FROM lecture_resources WHERE subject = :subject ORDER BY orderIndex ASC")
    fun getLecturesBySubject(subject: String): Flow<List<LectureResource>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLecture(lecture: LectureResource): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLectures(lectures: List<LectureResource>)

    @Update
    suspend fun updateLecture(lecture: LectureResource)

    @Delete
    suspend fun deleteLecture(lecture: LectureResource)

    @Query("UPDATE lecture_resources SET isCompleted = NOT isCompleted WHERE id = :id")
    suspend fun toggleLectureCompleted(id: Long)

    @Query("UPDATE lecture_resources SET isBookmarked = NOT isBookmarked WHERE id = :id")
    suspend fun toggleLectureBookmarked(id: Long)

    // --- Quiz Questions ---
    @Query("SELECT * FROM quiz_questions ORDER BY id ASC")
    fun getAllQuestions(): Flow<List<QuizQuestion>>

    @Query("SELECT * FROM quiz_questions WHERE subject = :subject ORDER BY RANDOM()")
    suspend fun getQuestionsForSubject(subject: String): List<QuizQuestion>

    @Query("SELECT * FROM quiz_questions ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestions(limit: Int): List<QuizQuestion>

    @Query("SELECT * FROM quiz_questions WHERE subject = :subject ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestionsBySubject(subject: String, limit: Int): List<QuizQuestion>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuizQuestion): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllQuestions(questions: List<QuizQuestion>)

    @Query("SELECT COUNT(*) FROM quiz_questions")
    suspend fun getQuestionCount(): Int

    // --- Quiz Attempts ---
    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC")
    fun getAllAttempts(): Flow<List<QuizAttempt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: QuizAttempt): Long
}
