package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LectureResource
import com.example.ui.components.AllSubjects
import com.example.ui.components.EmptyStudyState
import com.example.ui.components.StudySearchBar
import com.example.ui.components.SubjectFilterRow
import com.example.ui.components.getSubjectColor
import com.example.ui.components.getSubjectIcon

@Composable
fun LecturesScreen(
    lectures: List<LectureResource>,
    selectedSubject: String,
    onSelectSubject: (String) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onToggleCompleted: (Long) -> Unit,
    onToggleBookmark: (Long) -> Unit,
    onSaveLecture: (title: String, subject: String, courseCode: String, instructor: String, duration: Int, summary: String, takeaways: String, link: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedLectureDetail by remember { mutableStateOf<LectureResource?>(null) }

    val completedCount = lectures.count { it.isCompleted }
    val progress = if (lectures.isNotEmpty()) completedCount.toFloat() / lectures.size else 0f

    Box(modifier = modifier.fillMaxSize().testTag("lectures_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            item {
                StudySearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    placeholder = "Search lectures, course codes, topics..."
                )
            }

            item {
                SubjectFilterRow(
                    selectedSubject = selectedSubject,
                    onSelectSubject = onSelectSubject
                )
            }

            // Progress Banner
            item {
                LectureProgressCard(
                    completedCount = completedCount,
                    totalCount = lectures.size,
                    progress = progress
                )
            }

            if (lectures.isEmpty()) {
                item {
                    EmptyStudyState(
                        title = "No lectures found",
                        message = "No lecture materials match your filter. Tap the + button to add course resources!",
                        icon = Icons.Default.VideoLibrary
                    )
                }
            } else {
                items(lectures, key = { it.id }) { lecture ->
                    LectureCardItem(
                        lecture = lecture,
                        onToggleCompleted = { onToggleCompleted(lecture.id) },
                        onToggleBookmark = { onToggleBookmark(lecture.id) },
                        onClick = { selectedLectureDetail = lecture }
                    )
                }
            }
        }

        // Floating Action Button to Add Custom Lecture Resource
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 80.dp)
                .testTag("fab_add_lecture"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Lecture")
        }
    }

    // Detail Dialog
    selectedLectureDetail?.let { lecture ->
        LectureDetailDialog(
            lecture = lecture,
            onDismiss = { selectedLectureDetail = null },
            onToggleCompleted = { onToggleCompleted(lecture.id) },
            onToggleBookmark = { onToggleBookmark(lecture.id) }
        )
    }

    // Add Lecture Dialog
    if (showAddDialog) {
        AddLectureDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, subject, courseCode, instructor, duration, summary, takeaways, link ->
                onSaveLecture(title, subject, courseCode, instructor, duration, summary, takeaways, link)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun LectureProgressCard(
    completedCount: Int,
    totalCount: Int,
    progress: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("lecture_progress_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Syllabus Completion",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$completedCount of $totalCount Modules",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
private fun LectureCardItem(
    lecture: LectureResource,
    onToggleCompleted: () -> Unit,
    onToggleBookmark: () -> Unit,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag("lecture_card_${lecture.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (lecture.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = getSubjectColor(lecture.subject).copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = lecture.courseCode,
                            color = getSubjectColor(lecture.subject),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.05f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${lecture.durationMinutes}m",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(36.dp).testTag("bookmark_lecture_${lecture.id}")
                    ) {
                        Icon(
                            imageVector = if (lecture.isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (lecture.isBookmarked) MaterialTheme.colorScheme.secondary else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleCompleted,
                        modifier = Modifier.size(36.dp).testTag("toggle_complete_${lecture.id}")
                    ) {
                        Icon(
                            imageVector = if (lecture.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Complete",
                            tint = if (lecture.isCompleted) Color(0xFF10B981) else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = lecture.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Instructor: ${lecture.instructor}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = lecture.summary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            // Expandable Key Takeaways
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    if (lecture.keyTakeaways.isNotBlank()) {
                        Text(
                            text = "Key Takeaways & Core Concepts:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = lecture.keyTakeaways,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 18.sp
                        )
                    }

                    if (lecture.resourceLink.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(lecture.resourceLink))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.testTag("open_lecture_link_${lecture.id}")
                        ) {
                            Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Lecture Slides / Materials", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded) "Show Less" else "View Key Takeaways",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun LectureDetailDialog(
    lecture: LectureResource,
    onDismiss: () -> Unit,
    onToggleCompleted: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = getSubjectColor(lecture.subject).copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = lecture.courseCode,
                            color = getSubjectColor(lecture.subject),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "${lecture.durationMinutes} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = lecture.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = "Instructor: ${lecture.instructor}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "Summary", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text(text = lecture.summary, style = MaterialTheme.typography.bodyMedium)

                if (lecture.keyTakeaways.isNotBlank()) {
                    Text(text = "Key Takeaways", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Text(text = lecture.keyTakeaways, style = MaterialTheme.typography.bodySmall)
                }

                if (lecture.resourceLink.isNotBlank()) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(lecture.resourceLink))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("View Slides / References")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun AddLectureDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, subject: String, courseCode: String, instructor: String, duration: Int, summary: String, takeaways: String, link: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Computer Science") }
    var courseCode by remember { mutableStateOf("") }
    var instructor by remember { mutableStateOf("Prof. Vipul Yadav") }
    var durationText by remember { mutableStateOf("45") }
    var summary by remember { mutableStateOf("") }
    var takeaways by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Lecture Resource", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Lecture Title *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_lecture_title")
                )
                OutlinedTextField(
                    value = courseCode,
                    onValueChange = { courseCode = it },
                    label = { Text("Course Code (e.g. CS 201) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_lecture_code")
                )
                OutlinedTextField(
                    value = instructor,
                    onValueChange = { instructor = it },
                    label = { Text("Instructor") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("Duration (minutes)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("Lecture Overview / Summary *") },
                    modifier = Modifier.fillMaxWidth().height(100.dp).testTag("add_lecture_summary")
                )
                OutlinedTextField(
                    value = takeaways,
                    onValueChange = { takeaways = it },
                    label = { Text("Key Takeaways (Bullet points)") },
                    modifier = Modifier.fillMaxWidth().height(90.dp)
                )
                OutlinedTextField(
                    value = link,
                    onValueChange = { link = it },
                    label = { Text("Resource Link (URL)") },
                    placeholder = { Text("https://...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && courseCode.isNotBlank() && summary.isNotBlank()) {
                        val duration = durationText.toIntOrNull() ?: 45
                        onSave(title, subject, courseCode, instructor, duration, summary, takeaways, link)
                    }
                },
                enabled = title.isNotBlank() && courseCode.isNotBlank() && summary.isNotBlank(),
                modifier = Modifier.testTag("save_lecture_button")
            ) {
                Text("Save Resource")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
