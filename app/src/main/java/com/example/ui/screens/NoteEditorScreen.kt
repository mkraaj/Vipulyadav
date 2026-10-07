package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.example.data.model.StudyNote
import com.example.ui.components.AllSubjects
import com.example.ui.components.shareText
import com.example.ui.theme.NoteCardColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    note: StudyNote?,
    onSaveNote: (id: Long, title: String, subject: String, content: String, tags: String, colorIndex: Int, isPinned: Boolean, isFavorite: Boolean) -> Unit,
    onDeleteNote: (StudyNote) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { onBack() }

    var title by remember { mutableStateOf(note?.title ?: "") }
    var subject by remember { mutableStateOf(note?.subject ?: "Computer Science") }
    var content by remember { mutableStateOf(note?.content ?: "") }
    var tags by remember { mutableStateOf(note?.tags ?: "") }
    var colorIndex by remember { mutableIntStateOf(note?.colorIndex ?: 0) }
    var isPinned by remember { mutableStateOf(note?.isPinned ?: false) }
    var isFavorite by remember { mutableStateOf(note?.isFavorite ?: false) }

    var isSubjectDropdownExpanded by remember { mutableStateOf(false) }
    val subjectsList = AllSubjects.filter { it != "All" }

    Scaffold(
        modifier = modifier.fillMaxSize().imePadding().testTag("note_editor_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (note == null) "New Study Note" else "Edit Note",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isPinned = !isPinned },
                        modifier = Modifier.testTag("editor_pin_toggle")
                    ) {
                        Icon(
                            imageVector = if (isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Pin",
                            tint = if (isPinned) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }

                    IconButton(
                        onClick = { isFavorite = !isFavorite },
                        modifier = Modifier.testTag("editor_fav_toggle")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isFavorite) MaterialTheme.colorScheme.secondary else Color.Gray
                        )
                    }

                    if (note != null) {
                        IconButton(
                            onClick = { shareText(context, title, content) },
                            modifier = Modifier.testTag("editor_share_button")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                        IconButton(
                            onClick = {
                                onDeleteNote(note)
                                onBack()
                            },
                            modifier = Modifier.testTag("editor_delete_button")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Note Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Note Title *") },
                placeholder = { Text("e.g. Asymptotic Analysis & Recurrences") },
                modifier = Modifier.fillMaxWidth().testTag("note_title_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Subject Dropdown
            ExposedDropdownMenuBox(
                expanded = isSubjectDropdownExpanded,
                onExpandedChange = { isSubjectDropdownExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Subject Area *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSubjectDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor().testTag("subject_dropdown"),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = isSubjectDropdownExpanded,
                    onDismissRequest = { isSubjectDropdownExpanded = false }
                ) {
                    subjectsList.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                subject = item
                                isSubjectDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Tags Field
            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text("Tags & Keywords") },
                placeholder = { Text("e.g. Algorithms, Exam1, CheatSheet") },
                modifier = Modifier.fillMaxWidth().testTag("note_tags_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // Card Color Tint Selector
            Column {
                Text(
                    text = "Note Card Accent",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NoteCardColors.forEachIndexed { index, color ->
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (colorIndex == index) 2.5.dp else 1.dp,
                                    color = if (colorIndex == index) MaterialTheme.colorScheme.primary else Color.LightGray,
                                    shape = CircleShape
                                )
                                .clickable { colorIndex = index }
                                .testTag("color_picker_$index"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (colorIndex == index) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Formatting Helper Toolbar
            Column {
                Text(
                    text = "Quick Study Formats",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AssistChip(
                        onClick = { content += "\n### Section Heading\n" },
                        label = { Text("Heading") },
                        colors = AssistChipDefaults.assistChipColors(),
                        modifier = Modifier.testTag("format_heading")
                    )
                    AssistChip(
                        onClick = { content += "\n• Key concept: " },
                        label = { Text("• Bullet") },
                        modifier = Modifier.testTag("format_bullet")
                    )
                    AssistChip(
                        onClick = { content += " **important** " },
                        label = { Text("Bold") },
                        modifier = Modifier.testTag("format_bold")
                    )
                    AssistChip(
                        onClick = { content += "\n```\n// code/formula\n```\n" },
                        label = { Text("{ } Code") },
                        modifier = Modifier.testTag("format_code")
                    )
                    AssistChip(
                        onClick = { content += "\n[Formula: F = m·a]\n" },
                        label = { Text("Formula") },
                        modifier = Modifier.testTag("format_formula")
                    )
                    AssistChip(
                        onClick = { content += "\n- [ ] Review before exam\n" },
                        label = { Text("☑ Task") },
                        modifier = Modifier.testTag("format_task")
                    )
                }
            }

            // Note Content
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Study Content & Notes *") },
                placeholder = { Text("Type summary, equations, key definitions, or lecture insights here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .testTag("note_content_input"),
                shape = RoundedCornerShape(12.dp)
            )

            // Save Button
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSaveNote(
                            note?.id ?: 0L,
                            title,
                            subject,
                            content,
                            tags,
                            colorIndex,
                            isPinned,
                            isFavorite
                        )
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_note_button"),
                shape = RoundedCornerShape(14.dp),
                enabled = title.isNotBlank()
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (note == null) "Save Study Note" else "Update Note",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
