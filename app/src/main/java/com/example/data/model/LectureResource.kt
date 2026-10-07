package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lecture_resources")
data class LectureResource(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subject: String,
    val courseCode: String,
    val instructor: String = "Prof. Vipul Yadav",
    val durationMinutes: Int = 45,
    val summary: String,
    val keyTakeaways: String = "",
    val resourceLink: String = "",
    val isCompleted: Boolean = false,
    val isBookmarked: Boolean = false,
    val orderIndex: Int = 0
)
