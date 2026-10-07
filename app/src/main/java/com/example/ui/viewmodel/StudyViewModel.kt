package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.LectureResource
import com.example.data.model.QuizAttempt
import com.example.data.model.QuizQuestion
import com.example.data.model.StudyNote
import com.example.data.repository.StudyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActiveQuizState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptions: Map<Int, Int> = emptyMap(), // question index -> chosen option (0-3)
    val submittedAnswers: Map<Int, Boolean> = emptyMap(), // question index -> isSubmitted
    val isHintRevealed: Map<Int, Boolean> = emptyMap(),
    val isFinished: Boolean = false,
    val startTime: Long = 0L,
    val durationSeconds: Int = 0,
    val finalScore: Int = 0
)

class StudyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudyRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = StudyRepository(database.studyDao())
        viewModelScope.launch {
            repository.ensureDataSeeded()
        }
    }

    val allNotes: StateFlow<List<StudyNote>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allLectures: StateFlow<List<LectureResource>> = repository.allLectures
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allQuestions: StateFlow<List<QuizQuestion>> = repository.allQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizAttempts: StateFlow<List<QuizAttempt>> = repository.allAttempts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedSubject = MutableStateFlow("All")
    val selectedSubject: StateFlow<String> = _selectedSubject.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filtered Notes
    val filteredNotes: StateFlow<List<StudyNote>> = combine(allNotes, _selectedSubject, _searchQuery) { notes, subject, query ->
        notes.filter { note ->
            val matchesSubject = subject.equals("All", ignoreCase = true) || note.subject.equals(subject, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    note.title.contains(query, ignoreCase = true) ||
                    note.content.contains(query, ignoreCase = true) ||
                    note.tags.contains(query, ignoreCase = true)
            matchesSubject && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Lectures
    val filteredLectures: StateFlow<List<LectureResource>> = combine(allLectures, _selectedSubject, _searchQuery) { lectures, subject, query ->
        lectures.filter { lecture ->
            val matchesSubject = subject.equals("All", ignoreCase = true) || lecture.subject.equals(subject, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    lecture.title.contains(query, ignoreCase = true) ||
                    lecture.courseCode.contains(query, ignoreCase = true) ||
                    lecture.instructor.contains(query, ignoreCase = true) ||
                    lecture.summary.contains(query, ignoreCase = true)
            matchesSubject && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently editing note
    private val _currentEditingNote = MutableStateFlow<StudyNote?>(null)
    val currentEditingNote: StateFlow<StudyNote?> = _currentEditingNote.asStateFlow()

    // Currently selected lecture for detail
    private val _selectedLecture = MutableStateFlow<LectureResource?>(null)
    val selectedLecture: StateFlow<LectureResource?> = _selectedLecture.asStateFlow()

    // Active Quiz State
    private val _activeQuiz = MutableStateFlow<ActiveQuizState?>(null)
    val activeQuiz: StateFlow<ActiveQuizState?> = _activeQuiz.asStateFlow()

    fun setSubject(subject: String) {
        _selectedSubject.value = subject
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectNoteForEdit(note: StudyNote?) {
        _currentEditingNote.value = note
    }

    fun selectLectureForDetail(lecture: LectureResource?) {
        _selectedLecture.value = lecture
    }

    // --- Note Actions ---
    fun saveNote(
        id: Long = 0,
        title: String,
        subject: String,
        content: String,
        tags: String = "",
        colorIndex: Int = 0,
        isPinned: Boolean = false,
        isFavorite: Boolean = false
    ) {
        viewModelScope.launch {
            val note = StudyNote(
                id = id,
                title = title.trim(),
                subject = subject.trim(),
                content = content.trim(),
                tags = tags.trim(),
                colorIndex = colorIndex,
                isPinned = isPinned,
                isFavorite = isFavorite,
                lastModified = System.currentTimeMillis()
            )
            repository.saveNote(note)
            _currentEditingNote.value = null
        }
    }

    fun deleteNote(note: StudyNote) {
        viewModelScope.launch {
            repository.deleteNote(note)
            if (_currentEditingNote.value?.id == note.id) {
                _currentEditingNote.value = null
            }
        }
    }

    fun togglePinNote(id: Long) {
        viewModelScope.launch {
            repository.togglePinNote(id)
        }
    }

    fun toggleFavoriteNote(id: Long) {
        viewModelScope.launch {
            repository.toggleFavoriteNote(id)
        }
    }

    // --- Lecture Actions ---
    fun toggleLectureCompleted(id: Long) {
        viewModelScope.launch {
            repository.toggleLectureCompleted(id)
            if (_selectedLecture.value?.id == id) {
                _selectedLecture.value = _selectedLecture.value?.copy(
                    isCompleted = !_selectedLecture.value!!.isCompleted
                )
            }
        }
    }

    fun toggleLectureBookmarked(id: Long) {
        viewModelScope.launch {
            repository.toggleLectureBookmarked(id)
            if (_selectedLecture.value?.id == id) {
                _selectedLecture.value = _selectedLecture.value?.copy(
                    isBookmarked = !_selectedLecture.value!!.isBookmarked
                )
            }
        }
    }

    fun saveLecture(
        id: Long = 0,
        title: String,
        subject: String,
        courseCode: String,
        instructor: String,
        durationMinutes: Int,
        summary: String,
        keyTakeaways: String,
        resourceLink: String
    ) {
        viewModelScope.launch {
            val lecture = LectureResource(
                id = id,
                title = title.trim(),
                subject = subject.trim(),
                courseCode = courseCode.trim(),
                instructor = instructor.trim().ifEmpty { "Prof. Vipul Yadav" },
                durationMinutes = durationMinutes,
                summary = summary.trim(),
                keyTakeaways = keyTakeaways.trim(),
                resourceLink = resourceLink.trim()
            )
            repository.saveLecture(lecture)
        }
    }

    fun deleteLecture(lecture: LectureResource) {
        viewModelScope.launch {
            repository.deleteLecture(lecture)
            if (_selectedLecture.value?.id == lecture.id) {
                _selectedLecture.value = null
            }
        }
    }

    // --- Interactive Quiz Actions ---
    fun startQuiz(subject: String? = null, count: Int = 5) {
        viewModelScope.launch {
            val questions = repository.getQuizQuestions(subject, count)
            if (questions.isNotEmpty()) {
                _activeQuiz.value = ActiveQuizState(
                    questions = questions,
                    currentIndex = 0,
                    selectedOptions = emptyMap(),
                    submittedAnswers = emptyMap(),
                    isHintRevealed = emptyMap(),
                    isFinished = false,
                    startTime = System.currentTimeMillis()
                )
            }
        }
    }

    fun selectQuizOption(questionIndex: Int, optionIndex: Int) {
        val current = _activeQuiz.value ?: return
        if (current.submittedAnswers[questionIndex] == true) return // cannot change after submission

        val updated = current.selectedOptions.toMutableMap()
        updated[questionIndex] = optionIndex
        _activeQuiz.value = current.copy(selectedOptions = updated)
    }

    fun revealHint(questionIndex: Int) {
        val current = _activeQuiz.value ?: return
        val updated = current.isHintRevealed.toMutableMap()
        updated[questionIndex] = true
        _activeQuiz.value = current.copy(isHintRevealed = updated)
    }

    fun submitQuizAnswer(questionIndex: Int) {
        val current = _activeQuiz.value ?: return
        if (!current.selectedOptions.containsKey(questionIndex)) return // must select an option

        val updated = current.submittedAnswers.toMutableMap()
        updated[questionIndex] = true
        _activeQuiz.value = current.copy(submittedAnswers = updated)
    }

    fun nextQuizQuestion() {
        val current = _activeQuiz.value ?: return
        if (current.currentIndex < current.questions.size - 1) {
            _activeQuiz.value = current.copy(currentIndex = current.currentIndex + 1)
        } else {
            finishQuiz()
        }
    }

    fun prevQuizQuestion() {
        val current = _activeQuiz.value ?: return
        if (current.currentIndex > 0) {
            _activeQuiz.value = current.copy(currentIndex = current.currentIndex - 1)
        }
    }

    fun finishQuiz() {
        val current = _activeQuiz.value ?: return
        var score = 0
        current.questions.forEachIndexed { index, question ->
            val chosen = current.selectedOptions[index]
            if (chosen == question.correctOptionIndex) {
                score++
            }
        }
        val durationSeconds = ((System.currentTimeMillis() - current.startTime) / 1000).toInt().coerceAtLeast(1)
        val percentage = ((score.toDouble() / current.questions.size.coerceAtLeast(1)) * 100).toInt()

        val subject = current.questions.firstOrNull()?.subject ?: "General"

        viewModelScope.launch {
            repository.recordQuizAttempt(
                QuizAttempt(
                    subject = subject,
                    score = score,
                    totalQuestions = current.questions.size,
                    percentage = percentage,
                    timeSpentSeconds = durationSeconds
                )
            )
        }

        _activeQuiz.value = current.copy(
            isFinished = true,
            durationSeconds = durationSeconds,
            finalScore = score
        )
    }

    fun resetQuiz() {
        _activeQuiz.value = null
    }
}
