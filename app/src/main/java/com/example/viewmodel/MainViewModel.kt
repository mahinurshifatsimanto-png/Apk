package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.network.GeminiHelper
import com.example.utils.AudioRecorderManager
import com.example.utils.PdfGenerator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CollegeNotesDatabase.getDatabase(application)
    private val routineDao = db.routineDao()
    private val noteDao = db.noteDao()
    private val recordingDao = db.recordingDao()
    private val chatMessageDao = db.chatMessageDao()
    private val testPaperDao = db.testPaperDao()
    private val userStatsDao = db.userStatsDao()

    val audioRecorderManager = AudioRecorderManager(application)

    val allRoutines: StateFlow<List<RoutineEntry>> = routineDao.getAllRoutines()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotes: StateFlow<List<NoteEntry>> = noteDao.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRecordings: StateFlow<List<RecordingItem>> = recordingDao.getAllRecordings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatMessages: StateFlow<List<ChatMessage>> = chatMessageDao.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val testPapers: StateFlow<List<TestPaper>> = testPaperDao.getAllTestPapers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamically computed stats based on real user activity
    val userStats: StateFlow<UserStats> = combine(allNotes, allRoutines, testPapers) { notes, routines, tests ->
        val totalMins: Long = if (notes.isNotEmpty()) notes.sumOf { it.durationSeconds } / 60L else 0L
        val notesCount = notes.size
        val testsCount = tests.size
        val xp = (notesCount * 100) + (testsCount * 150) + (totalMins.toInt() * 2)

        val level = when {
            xp > 1000 -> "Lecture Legend"
            xp > 500 -> "Master Scholar"
            xp > 200 -> "Academic Scholar"
            else -> "Active Learner"
        }

        UserStats(
            totalStudyMinutes = totalMins,
            streakDays = if (notesCount > 0) 1 else 0,
            lastStudyDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            xpPoints = xp,
            currentLevel = level,
            notesCount = notesCount,
            testsCompletedCount = testsCount
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        UserStats(
            totalStudyMinutes = 0,
            streakDays = 0,
            lastStudyDate = "-",
            xpPoints = 0,
            currentLevel = "Active Learner",
            notesCount = 0,
            testsCompletedCount = 0
        )
    )

    private val _isProcessingGemini = MutableStateFlow(false)
    val isProcessingGemini: StateFlow<Boolean> = _isProcessingGemini

    private val _selectedNoteForPdfView = MutableStateFlow<NoteEntry?>(null)
    val selectedNoteForPdfView: StateFlow<NoteEntry?> = _selectedNoteForPdfView

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _activeSubjectFilter = MutableStateFlow("ALL")
    val activeSubjectFilter: StateFlow<String> = _activeSubjectFilter

    fun addRoutine(entry: RoutineEntry) {
        viewModelScope.launch {
            routineDao.insertRoutine(entry)
        }
    }

    fun deleteRoutine(id: Long) {
        viewModelScope.launch {
            routineDao.deleteRoutineById(id)
        }
    }

    fun addManualNote(
        subject: String,
        title: String,
        teacher: String,
        content: String,
        topics: List<String>,
        formulas: List<String>
    ) {
        viewModelScope.launch {
            _isProcessingGemini.value = true
            val timeStamp = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())

            val formattedMarkdown = """
                # $title
                **Subject:** $subject | **Teacher:** $teacher
                **Date:** $timeStamp
                
                ## 1. Class Summary & Notes
                $content
                
                ## 2. Key Formulas & Equations
                ${formulas.joinToString("\n") { "- $it" }}
            """.trimIndent()

            val newNote = NoteEntry(
                subject = subject,
                title = title.ifBlank { "$subject Class Note" },
                date = timeStamp,
                dayOfWeek = dayOfWeek,
                classTime = "Manual Entry",
                teacher = teacher.ifBlank { "Instructor" },
                durationSeconds = 1800,
                audioFilePath = "",
                pdfFilePath = "",
                summaryMarkdown = formattedMarkdown,
                topics = if (topics.isNotEmpty()) topics else listOf(subject, "Lecture Notes"),
                confusionPoints = emptyList(),
                teacherQuotes = listOf("\"Key points captured from $subject class.\""),
                formulas = formulas,
                confidenceScore = 98,
                readTimeMinutes = (content.length / 300).coerceAtLeast(2)
            )

            val newNoteId = noteDao.insertNote(newNote)
            val noteWithId = newNote.copy(id = newNoteId)
            val pdfFile = PdfGenerator.generateNotePdf(getApplication(), noteWithId)
            noteDao.insertNote(noteWithId.copy(pdfFilePath = pdfFile.absolutePath))

            _isProcessingGemini.value = false
        }
    }

    fun stopAndProcessCurrentRecording(subject: String, teacher: String) {
        viewModelScope.launch {
            val audioFile = audioRecorderManager.stopRecording()
            _isProcessingGemini.value = true

            val timeStamp = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
            val duration = audioRecorderManager.recordingDurationSeconds.value

            val transcriptContext = "Recorded live class audio file: ${audioFile?.name ?: "Recorded_Lecture.m4a"}. Duration: ${duration}s. Subject: $subject, Teacher: $teacher."
            val processedResult = GeminiHelper.generateLectureNotes(getApplication(), subject, teacher, transcriptContext)

            val newNote = NoteEntry(
                subject = subject,
                title = processedResult.title,
                date = timeStamp,
                dayOfWeek = dayOfWeek,
                classTime = "Live Recorded Lecture",
                teacher = teacher,
                durationSeconds = duration,
                audioFilePath = audioFile?.absolutePath ?: "",
                pdfFilePath = "",
                summaryMarkdown = processedResult.markdownNotes,
                topics = processedResult.topics,
                confusionPoints = processedResult.confusionPoints,
                teacherQuotes = processedResult.teacherQuotes,
                formulas = processedResult.formulas,
                confidenceScore = processedResult.confidenceScore
            )

            val newNoteId = noteDao.insertNote(newNote)
            val noteWithId = newNote.copy(id = newNoteId)
            val pdfFile = PdfGenerator.generateNotePdf(getApplication(), noteWithId)
            noteDao.insertNote(noteWithId.copy(pdfFilePath = pdfFile.absolutePath))

            _isProcessingGemini.value = false
        }
    }

    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        viewModelScope.launch {
            chatMessageDao.insertMessage(ChatMessage(sender = "USER", text = userText))

            val contextNotes = allNotes.value.joinToString("\n---\n") { "${it.subject} (${it.date}): ${it.summaryMarkdown}" }
            val replyText = GeminiHelper.answerChatQuestion(getApplication(), userText, contextNotes)

            chatMessageDao.insertMessage(ChatMessage(sender = "GEMINI", text = replyText))
        }
    }

    fun generateNewMonthlyTestPaper(subject: String) {
        viewModelScope.launch {
            _isProcessingGemini.value = true
            val notesList = allNotes.value.map { it.summaryMarkdown }
            val jsonTest = GeminiHelper.generateMonthlyTestPaper(getApplication(), subject, notesList)

            val paper = TestPaper(
                title = "$subject Exam Paper",
                monthYear = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date()),
                createdDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                totalQuestions = 30,
                questionsJson = jsonTest,
                weakAreas = listOf("Review core derivations and formulas for $subject")
            )

            testPaperDao.insertTestPaper(paper)
            _isProcessingGemini.value = false
        }
    }

    fun selectNoteForPdfView(note: NoteEntry?) {
        _selectedNoteForPdfView.value = note
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSubjectFilter(subject: String) {
        _activeSubjectFilter.value = subject
    }

    fun deleteNote(note: NoteEntry) {
        viewModelScope.launch {
            noteDao.deleteNote(note)
        }
    }

    fun clearAllChatMessages() {
        viewModelScope.launch {
            chatMessageDao.clearHistory()
        }
    }
}
