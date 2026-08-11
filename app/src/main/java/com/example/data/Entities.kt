package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routine_entries")
data class RoutineEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayOfWeek: String, // e.g., "Monday", "Tuesday"
    val startTime: String, // e.g., "09:00"
    val endTime: String,   // e.g., "10:00"
    val subject: String,
    val teacher: String,
    val roomNo: String,
    val colorHex: String = "#7C4DFF",
    val isAutoRecordEnabled: Boolean = true
)

@Entity(tableName = "notes")
data class NoteEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val title: String,
    val date: String, // e.g. "11/08/2026"
    val dayOfWeek: String,
    val classTime: String,
    val teacher: String,
    val durationSeconds: Long,
    val audioFilePath: String,
    val pdfFilePath: String,
    val summaryMarkdown: String,
    val topics: List<String> = emptyList(),
    val confusionPoints: List<String> = emptyList(),
    val teacherQuotes: List<String> = emptyList(),
    val formulas: List<String> = emptyList(),
    val confidenceScore: Int = 92, // percentage e.g. 95%
    val isSynced: Boolean = false,
    val readTimeMinutes: Int = 5,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "recordings")
data class RecordingItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filename: String,
    val subject: String,
    val timestamp: Long,
    val durationSeconds: Long,
    val fileSizeFormatted: String,
    val filePath: String,
    val isProcessed: Boolean = false,
    val syncStatus: String = "PENDING"
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "USER" or "GEMINI"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val referencedSubject: String? = null
)

@Entity(tableName = "test_papers")
data class TestPaper(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val monthYear: String,
    val createdDate: String,
    val totalQuestions: Int = 30,
    val score: Int? = null,
    val isCompleted: Boolean = false,
    val questionsJson: String, // JSON string of test questions
    val weakAreas: List<String> = emptyList(),
    val pdfPath: String = ""
)

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val totalStudyMinutes: Long = 180,
    val streakDays: Int = 5,
    val lastStudyDate: String = "",
    val xpPoints: Int = 450,
    val currentLevel: String = "Lecture Legend",
    val notesCount: Int = 12,
    val testsCompletedCount: Int = 3
)
