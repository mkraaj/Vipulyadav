package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_questions")
data class QuizQuestion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String,
    val topic: String,
    val difficulty: String, // "Easy", "Medium", "Hard"
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOptionIndex: Int, // 0 = A, 1 = B, 2 = C, 3 = D
    val explanation: String,
    val hint: String = ""
) {
    fun getOptions(): List<String> = listOf(optionA, optionB, optionC, optionD)
}
