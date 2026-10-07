package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.config.AppLanguage
import com.example.config.QuestionDisplayMode
import com.example.config.Strings
import com.example.ui.components.ExamPilotTopBar
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EngineScreen
import com.example.ui.screens.LecturesScreen
import com.example.ui.screens.NoteEditorScreen
import com.example.ui.screens.NotesScreen
import com.example.ui.screens.QuizPlayScreen
import com.example.ui.screens.QuizResultScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.EngineViewModel
import com.example.ui.viewmodel.StudyViewModel

data class BottomBarItem(
    val titleKey: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ExamPilotApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamPilotApp(
    engineViewModel: EngineViewModel = viewModel(),
    studyViewModel: StudyViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var isEditingNote by remember { mutableStateOf(false) }

    // Engine & Global States
    val keys by engineViewModel.keys.collectAsStateWithLifecycle()
    val cacheCount by engineViewModel.cacheCount.collectAsStateWithLifecycle()
    val activeQueue by engineViewModel.activeQueue.collectAsStateWithLifecycle()
    val dataSaverMode by engineViewModel.dataSaverMode.collectAsStateWithLifecycle()
    val appLanguage by engineViewModel.appLanguage.collectAsStateWithLifecycle()
    val questionDisplayMode by engineViewModel.questionDisplayMode.collectAsStateWithLifecycle()
    val selectedExam by engineViewModel.selectedExam.collectAsStateWithLifecycle()
    val testingKeyId by engineViewModel.testingKeyId.collectAsStateWithLifecycle()
    val testKeyResults by engineViewModel.testKeyResult.collectAsStateWithLifecycle()

    // Study Content States
    val allNotes by studyViewModel.allNotes.collectAsStateWithLifecycle()
    val filteredNotes by studyViewModel.filteredNotes.collectAsStateWithLifecycle()
    val allLectures by studyViewModel.allLectures.collectAsStateWithLifecycle()
    val filteredLectures by studyViewModel.filteredLectures.collectAsStateWithLifecycle()
    val allQuestions by studyViewModel.allQuestions.collectAsStateWithLifecycle()
    val quizAttempts by studyViewModel.quizAttempts.collectAsStateWithLifecycle()
    val selectedSubject by studyViewModel.selectedSubject.collectAsStateWithLifecycle()
    val searchQuery by studyViewModel.searchQuery.collectAsStateWithLifecycle()
    val editingNote by studyViewModel.currentEditingNote.collectAsStateWithLifecycle()
    val activeQuiz by studyViewModel.activeQuiz.collectAsStateWithLifecycle()

    val healthyKeysCount = keys.count { it.isEnabled && !it.isCurrentlyInCooldown() && it.status != "INVALID" }

    val navItems = listOf(
        BottomBarItem("nav_home", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "nav_dashboard"),
        BottomBarItem("nav_engine", Icons.Filled.Bolt, Icons.Outlined.Bolt, "nav_engine"),
        BottomBarItem("nav_notes", Icons.AutoMirrored.Filled.MenuBook, Icons.AutoMirrored.Outlined.MenuBook, "nav_library"),
        BottomBarItem("nav_exams", Icons.Filled.Quiz, Icons.Outlined.Quiz, "nav_practice")
    )

    // Note Editor Full-Screen View
    if (isEditingNote) {
        NoteEditorScreen(
            note = editingNote,
            onSaveNote = { id, title, subject, content, tags, colorIndex, isPinned, isFavorite ->
                studyViewModel.saveNote(id, title, subject, content, tags, colorIndex, isPinned, isFavorite)
                isEditingNote = false
            },
            onDeleteNote = { note ->
                studyViewModel.deleteNote(note)
                isEditingNote = false
            },
            onBack = {
                studyViewModel.selectNoteForEdit(null)
                isEditingNote = false
            }
        )
        return
    }

    // Active Quiz Session View
    val currentQuiz = activeQuiz
    if (currentQuiz != null) {
        if (currentQuiz.isFinished) {
            QuizResultScreen(
                quizState = currentQuiz,
                onRetakeQuiz = {
                    val subject = currentQuiz.questions.firstOrNull()?.subject
                    studyViewModel.startQuiz(subject, 5)
                },
                onBackToHub = {
                    studyViewModel.resetQuiz()
                    selectedTab = 3
                }
            )
        } else {
            QuizPlayScreen(
                quizState = currentQuiz,
                onSelectOption = { qIdx, optIdx -> studyViewModel.selectQuizOption(qIdx, optIdx) },
                onSubmitAnswer = { qIdx -> studyViewModel.submitQuizAnswer(qIdx) },
                onRevealHint = { qIdx -> studyViewModel.revealHint(qIdx) },
                onNextQuestion = { studyViewModel.nextQuizQuestion() },
                onExitQuiz = { studyViewModel.resetQuiz() }
            )
        }
        return
    }

    // BackHandler on secondary tabs
    if (selectedTab != 0) {
        BackHandler {
            selectedTab = 0
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            ExamPilotTopBar(
                language = appLanguage,
                onToggleLanguage = { engineViewModel.toggleLanguage() },
                questionMode = questionDisplayMode,
                onToggleQuestionMode = {
                    val next = when (questionDisplayMode) {
                        QuestionDisplayMode.BILINGUAL -> QuestionDisplayMode.ENGLISH_ONLY
                        QuestionDisplayMode.ENGLISH_ONLY -> QuestionDisplayMode.HINDI_ONLY
                        QuestionDisplayMode.HINDI_ONLY -> QuestionDisplayMode.BILINGUAL
                    }
                    engineViewModel.setQuestionDisplayMode(next)
                },
                healthyKeysCount = healthyKeysCount,
                onOpenEngine = { selectedTab = 1 }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_navigation_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = selectedTab == index
                    val title = Strings.get(item.titleKey, appLanguage)
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = title
                            )
                        },
                        label = {
                            Text(
                                text = title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "main_nav_tabs"
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> DashboardScreen(
                        selectedExam = selectedExam,
                        onSelectExam = { engineViewModel.setSelectedExam(it) },
                        keys = keys,
                        language = appLanguage,
                        questionMode = questionDisplayMode,
                        onNavigateToEngine = { selectedTab = 1 },
                        onNavigateToNotes = { selectedTab = 2 },
                        onNavigateToQuiz = { selectedTab = 3 }
                    )

                    1 -> EngineScreen(
                        keys = keys,
                        cacheCount = cacheCount,
                        activeQueue = activeQueue,
                        dataSaverMode = dataSaverMode,
                        testingKeyId = testingKeyId,
                        testResults = testKeyResults,
                        language = appLanguage,
                        onAddKey = { nickname, apiKey -> engineViewModel.addKey(nickname, apiKey) },
                        onDeleteKey = { engineViewModel.deleteKey(it) },
                        onToggleKey = { id, enabled -> engineViewModel.toggleKeyEnabled(id, enabled) },
                        onTestKey = { engineViewModel.testKey(it) },
                        onExportKeys = { engineViewModel.exportEncryptedKeys(it) },
                        onImportKeys = { pkg, pass, cb -> engineViewModel.importEncryptedKeys(pkg, pass, cb) },
                        onClearCache = { engineViewModel.clearCache() },
                        onToggleDataSaver = { engineViewModel.setDataSaver(it) }
                    )

                    2 -> NotesScreen(
                        notes = filteredNotes,
                        selectedSubject = selectedSubject,
                        onSelectSubject = { studyViewModel.setSubject(it) },
                        searchQuery = searchQuery,
                        onSearchQueryChange = { studyViewModel.setSearchQuery(it) },
                        onOpenNote = { note ->
                            studyViewModel.selectNoteForEdit(note)
                            isEditingNote = true
                        },
                        onNewNote = {
                            studyViewModel.selectNoteForEdit(null)
                            isEditingNote = true
                        },
                        onTogglePin = { studyViewModel.togglePinNote(it) },
                        onToggleFavorite = { studyViewModel.toggleFavoriteNote(it) },
                        onDeleteNote = { studyViewModel.deleteNote(it) }
                    )

                    3 -> QuizScreen(
                        questions = allQuestions,
                        quizAttempts = quizAttempts,
                        onStartQuiz = { subject, count ->
                            studyViewModel.startQuiz(subject, count)
                        }
                    )
                }
            }
        }
    }
}
