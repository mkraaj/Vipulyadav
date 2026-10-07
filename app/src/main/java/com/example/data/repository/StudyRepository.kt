package com.example.data.repository

import com.example.data.db.DefaultStudyData
import com.example.data.db.StudyDao
import com.example.data.model.LectureResource
import com.example.data.model.QuizAttempt
import com.example.data.model.QuizQuestion
import com.example.data.model.StudyNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class StudyRepository(private val dao: StudyDao) {

    val allNotes: Flow<List<StudyNote>> = dao.getAllNotes()
    val allLectures: Flow<List<LectureResource>> = dao.getAllLectures()
    val allQuestions: Flow<List<QuizQuestion>> = dao.getAllQuestions()
    val allAttempts: Flow<List<QuizAttempt>> = dao.getAllAttempts()

    fun getNotesBySubject(subject: String): Flow<List<StudyNote>> {
        return if (subject.equals("All", ignoreCase = true)) {
            dao.getAllNotes()
        } else {
            dao.getNotesBySubject(subject)
        }
    }

    fun getLecturesBySubject(subject: String): Flow<List<LectureResource>> {
        return if (subject.equals("All", ignoreCase = true)) {
            dao.getAllLectures()
        } else {
            dao.getLecturesBySubject(subject)
        }
    }

    suspend fun getNoteById(id: Long): StudyNote? = withContext(Dispatchers.IO) {
        dao.getNoteById(id)
    }

    suspend fun saveNote(note: StudyNote): Long = withContext(Dispatchers.IO) {
        if (note.id == 0L) {
            dao.insertNote(note)
        } else {
            dao.updateNote(note)
            note.id
        }
    }

    suspend fun deleteNote(note: StudyNote) = withContext(Dispatchers.IO) {
        dao.deleteNote(note)
    }

    suspend fun deleteNoteById(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteNoteById(id)
    }

    suspend fun togglePinNote(id: Long) = withContext(Dispatchers.IO) {
        dao.togglePinNote(id)
    }

    suspend fun toggleFavoriteNote(id: Long) = withContext(Dispatchers.IO) {
        dao.toggleFavoriteNote(id)
    }

    suspend fun saveLecture(lecture: LectureResource): Long = withContext(Dispatchers.IO) {
        if (lecture.id == 0L) {
            dao.insertLecture(lecture)
        } else {
            dao.updateLecture(lecture)
            lecture.id
        }
    }

    suspend fun deleteLecture(lecture: LectureResource) = withContext(Dispatchers.IO) {
        dao.deleteLecture(lecture)
    }

    suspend fun toggleLectureCompleted(id: Long) = withContext(Dispatchers.IO) {
        dao.toggleLectureCompleted(id)
    }

    suspend fun toggleLectureBookmarked(id: Long) = withContext(Dispatchers.IO) {
        dao.toggleLectureBookmarked(id)
    }

    suspend fun getQuizQuestions(subject: String?, limit: Int = 5): List<QuizQuestion> = withContext(Dispatchers.IO) {
        if (subject == null || subject.equals("All", ignoreCase = true)) {
            dao.getRandomQuestions(limit)
        } else {
            dao.getRandomQuestionsBySubject(subject, limit)
        }
    }

    suspend fun saveQuizQuestion(question: QuizQuestion): Long = withContext(Dispatchers.IO) {
        dao.insertQuestion(question)
    }

    suspend fun recordQuizAttempt(attempt: QuizAttempt): Long = withContext(Dispatchers.IO) {
        dao.insertAttempt(attempt)
    }

    suspend fun ensureDataSeeded() = withContext(Dispatchers.IO) {
        val count = dao.getQuestionCount()
        if (count == 0) {
            dao.insertAllNotes(DefaultStudyData.initialNotes)
            dao.insertAllLectures(DefaultStudyData.initialLectures)
            dao.insertAllQuestions(DefaultStudyData.initialQuestions)
        }
    }
}
