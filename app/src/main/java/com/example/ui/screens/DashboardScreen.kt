package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.config.AppLanguage
import com.example.config.ExamConfig
import com.example.config.ExamPattern
import com.example.config.QuestionDisplayMode
import com.example.config.Strings
import com.example.data.model.GeminiKeyEntity

@Composable
fun DashboardScreen(
    selectedExam: ExamPattern,
    onSelectExam: (String) -> Unit,
    keys: List<GeminiKeyEntity>,
    language: AppLanguage,
    questionMode: QuestionDisplayMode,
    onNavigateToEngine: () -> Unit,
    onNavigateToNotes: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showExamSelector by remember { mutableStateOf(false) }
    val healthyCount = keys.count { it.isEnabled && !it.isCurrentlyInCooldown() && it.status != "INVALID" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // --- Exam Target Header ---
        item {
            ExamTargetCard(
                selectedExam = selectedExam,
                language = language,
                onSwitchClick = { showExamSelector = true }
            )
        }

        // --- AI Engine Live Status Strip ---
        item {
            EngineStatusBanner(
                healthyCount = healthyCount,
                totalKeys = keys.size,
                language = language,
                onClick = onNavigateToEngine
            )
        }

        // --- Readiness & Study Cockpit ---
        item {
            ReadinessCockpitCard(
                selectedExam = selectedExam,
                language = language
            )
        }

        // --- Quick Actions Grid ---
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = Strings.get("quick_actions", language),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CockpitActionCard(
                    title = "Multi-Key Engine",
                    subtitle = "$healthyCount Keys active",
                    icon = Icons.Default.Bolt,
                    color = Color(0xFF2563EB),
                    onClick = onNavigateToEngine,
                    modifier = Modifier.weight(1f).testTag("action_engine_card")
                )
                CockpitActionCard(
                    title = "Study Library",
                    subtitle = "Notes & Syllabus",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    color = Color(0xFFD97706),
                    onClick = onNavigateToNotes,
                    modifier = Modifier.weight(1f).testTag("action_notes_card")
                )
                CockpitActionCard(
                    title = "AI Quiz Center",
                    subtitle = "Interactive Practice",
                    icon = Icons.Default.Quiz,
                    color = Color(0xFF059669),
                    onClick = onNavigateToQuiz,
                    modifier = Modifier.weight(1f).testTag("action_quiz_card")
                )
            }
        }

        // --- Bilingual Sample Demonstration Card ---
        item {
            Spacer(modifier = Modifier.height(16.dp))
            BilingualSampleCard(questionMode = questionMode, language = language)
        }
    }

    // Exam Selector Dialog
    if (showExamSelector) {
        ExamSelectorDialog(
            currentExamId = selectedExam.id,
            language = language,
            onDismiss = { showExamSelector = false },
            onSelect = {
                onSelectExam(it)
                showExamSelector = false
            }
        )
    }
}

@Composable
private fun ExamTargetCard(
    selectedExam: ExamPattern,
    language: AppLanguage,
    onSwitchClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("exam_target_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0F172A), // Slate 900
                            Color(0xFF1E3A8A), // Blue 900
                            Color(0xFF1D4ED8)  // Blue 700
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (language == AppLanguage.HINDI) selectedExam.category.displayNameHi else selectedExam.category.displayNameEn,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = onSwitchClick,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("switch_exam_button")
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(Strings.get("switch_exam", language), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (language == AppLanguage.HINDI) selectedExam.nameHi else selectedExam.nameEn,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(10.dp))
                // Pattern details row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PatternMiniTag(label = "Questions", value = "${selectedExam.totalQuestions} Qs")
                    PatternMiniTag(label = "Total Marks", value = "${selectedExam.totalMarks} M")
                    PatternMiniTag(label = "Time", value = "${selectedExam.durationMinutes} min")
                    PatternMiniTag(label = "Negative", value = "-${selectedExam.negativeMarkingPerWrong}")
                }
            }
        }
    }
}

@Composable
private fun PatternMiniTag(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White.copy(alpha = 0.15f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text(text = label, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall, fontSize = 9.sp)
        }
    }
}

@Composable
private fun EngineStatusBanner(
    healthyCount: Int,
    totalKeys: Int,
    language: AppLanguage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("engine_status_banner"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (healthyCount > 0) Color(0xFFDCFCE7) else Color(0xFFFEF3C7)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (healthyCount > 0) Color(0xFF16A34A) else Color(0xFFD97706)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (healthyCount > 0) "AI Engine Online ($healthyCount Active Keys)" else "AI Engine: 0 Active Keys",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (healthyCount > 0) Color(0xFF14532D) else Color(0xFF78350F)
                    )
                    Text(
                        text = if (healthyCount > 0) "Smart multi-key failover & zero-cost caching ready." else "Tap here to add Gemini API keys for unlimited generation.",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (healthyCount > 0) Color(0xFF166534) else Color(0xFF92400E)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReadinessCockpitCard(
    selectedExam: ExamPattern,
    language: AppLanguage
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("readiness_cockpit_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFEA580C), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Day 8", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFEA580C))
                }
                Text(text = Strings.get("daily_streak", language), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "78%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(text = Strings.get("readiness_score", language), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "${selectedExam.cutoffBandEstimated} M", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                Text(text = "Target Cutoff", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun CockpitActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun BilingualSampleCard(
    questionMode: QuestionDisplayMode,
    language: AppLanguage
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("bilingual_sample_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Bilingual Question Preview", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFEFF6FF)) {
                    Text(
                        text = if (language == AppLanguage.HINDI) questionMode.labelHi else questionMode.labelEn,
                        color = Color(0xFF2563EB),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            // English version
            Text(
                text = "Q. Which Article of the Indian Constitution deals with the Election Commission of India?",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            // Hindi version
            Text(
                text = "प्र. भारतीय संविधान का कौन सा अनुच्छेद भारत के निर्वाचन आयोग से संबंधित है?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "(A) Article 324 / अनुच्छेद 324  •  (B) Article 280 / अनुच्छेद 280",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ExamSelectorDialog(
    currentExamId: String,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(Strings.get("target_exam", language), fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ExamConfig.EXAM_LIST) { exam ->
                    val isSelected = exam.id == currentExamId
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(exam.id) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (language == AppLanguage.HINDI) exam.nameHi else exam.nameEn,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${exam.totalQuestions} Qs • ${exam.totalMarks} Marks • ${exam.durationMinutes}m",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(Strings.get("cancel", language))
            }
        }
    )
}
