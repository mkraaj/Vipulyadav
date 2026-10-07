package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_notes")
data class StudyNote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subject: String,
    val content: String,
    val tags: String = "",
    val isPinned: Boolean = false,
    val isFavorite: Boolean = false,
    val colorIndex: Int = 0, // 0 to 5 for note card tint
    val lastModified: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)
