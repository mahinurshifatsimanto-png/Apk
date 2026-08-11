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

    val userStats: StateFlow<UserStats?> = userStatsDao.getUserStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isProcessingGemini = MutableStateFlow(false)
    val isProcessingGemini: StateFlow<Boolean> = _isProcessingGemini

    private val _selectedNoteForPdfView = MutableStateFlow<NoteEntry?>(null)
    val selectedNoteForPdfView: StateFlow<NoteEntry?> = _selectedNoteForPdfView

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _activeSubjectFilter = MutableStateFlow("ALL")
    val activeSubjectFilter: StateFlow<String> = _activeSubjectFilter

    init {
        seedInitialDatabaseIfEmpty()
    }

    private fun seedInitialDatabaseIfEmpty() {
        viewModelScope.launch {
            val existingNotes = noteDao.getAllNotes().firstOrNull() ?: emptyList()
            if (existingNotes.isEmpty()) {
                // Seed Routines
                val r1 = RoutineEntry(dayOfWeek = "Monday", startTime = "09:00", endTime = "10:00", subject = "Data Structures", teacher = "Prof. Ahmed", roomNo = "Room 302", colorHex = "#7C4DFF")
                val r2 = RoutineEntry(dayOfWeek = "Monday", startTime = "10:15", endTime = "11:15", subject = "Organic Chemistry", teacher = "Dr. Rahaman", roomNo = "Room 405", colorHex = "#00E5FF")
                val r3 = RoutineEntry(dayOfWeek = "Tuesday", startTime = "11:30", endTime = "12:30", subject = "Quantum Physics", teacher = "Dr. Khan", roomNo = "Room 201", colorHex = "#FF6B6B")
                val r4 = RoutineEntry(dayOfWeek = "Wednesday", startTime = "09:00", endTime = "10:00", subject = "Linear Algebra", teacher = "Prof. Roy", roomNo = "Room 104", colorHex = "#00FF94")
                val r5 = RoutineEntry(dayOfWeek = "Thursday", startTime = "10:15", endTime = "11:15", subject = "Microprocessors", teacher = "Prof. Sinha", roomNo = "Lab 2", colorHex = "#7C4DFF")

                routineDao.insertRoutine(r1)
                routineDao.insertRoutine(r2)
                routineDao.insertRoutine(r3)
                routineDao.insertRoutine(r4)
                routineDao.insertRoutine(r5)

                // Seed Notes
                val note1 = NoteEntry(
                    subject = "Data Structures",
                    title = "Binary Search Trees & AVL Rotations",
                    date = "09/08/2026",
                    dayOfWeek = "Monday",
                    classTime = "09:00 AM - 10:00 AM",
                    teacher = "Prof. Ahmed",
                    durationSeconds = 2880,
                    audioFilePath = "/MyCollegeNotes/Recordings/REC_DSA_20260809.m4a",
                    pdfFilePath = "",
                    summaryMarkdown = """
                        # Binary Search Trees (BST) & AVL Balance
                        **Prof. Ahmed** | Data Structures & Algorithms
                        
                        ## 1. Core Principles
                        A Binary Search Tree guarantees that for any node N, key(left) < key(N) < key(right).
                        
                        ## 2. AVL Tree Self-Balancing
                        - Height Balance Factor: `BF = Height(Left) - Height(Right)`
                        - Allowed Balance Factor range: `{-1, 0, +1}`
                        
                        ## 3. Rotations
                        - Single Left Rotation (LL Case)
                        - Single Right Rotation (RR Case)
                        - Double Left-Right Rotation (LR Case)
                        
                        > "Prof. Ahmed stressed: AVL Tree balance factor checking happens in O(1) time after each insertion."
                    """.trimIndent(),
                    topics = listOf("BST In-order Traversal", "AVL Height Balance", "Single & Double Rotations", "Search Time O(log N)"),
                    confusionPoints = listOf("Left-Right double rotation pivot calculation"),
                    teacherQuotes = listOf("\"Left-Right rotations are required when inserting into the right subtree of the left child.\""),
                    formulas = listOf("Balance Factor = Height(Left) - Height(Right)", "Search Time Complexity = O(log₂ N)"),
                    confidenceScore = 96,
                    readTimeMinutes = 6
                )

                val note2 = NoteEntry(
                    subject = "Organic Chemistry",
                    title = "Electrophilic Aromatic Substitution",
                    date = "10/08/2026",
                    dayOfWeek = "Monday",
                    classTime = "10:15 AM - 11:15 AM",
                    teacher = "Dr. Rahaman",
                    durationSeconds = 3120,
                    audioFilePath = "/MyCollegeNotes/Recordings/REC_Chem_20260810.m4a",
                    pdfFilePath = "",
                    summaryMarkdown = """
                        # Electrophilic Aromatic Substitution (EAS)
                        **Dr. Rahaman** | Organic Chemistry II
                        
                        ## 1. Mechanism Overview
                        EAS reactions replace a hydrogen atom on an aromatic ring with an electrophile (E⁺).
                        
                        ## 2. Key Steps
                        1. Attack of aromatic ring on E⁺ to form Arenium ion (Sigma Complex).
                        2. Deprotonation by base to restore aromaticity.
                    """.trimIndent(),
                    topics = listOf("Arenium Ion Sigma Complex", "Friedel-Crafts Alkylation", "Ortho/Para Directors"),
                    confusionPoints = listOf("Carbocation rearrangement during alkylation"),
                    teacherQuotes = listOf("\"Ortho/para directors donate electron density through resonance or induction.\""),
                    formulas = listOf("Ar-H + E⁺ → [Ar-H-E]⁺ → Ar-E + H⁺"),
                    confidenceScore = 94,
                    readTimeMinutes = 8
                )

                val note1Id = noteDao.insertNote(note1)
                val note2Id = noteDao.insertNote(note2)

                // Generate PDF files for seeded notes
                val savedNote1 = note1.copy(id = note1Id)
                val pdf1 = PdfGenerator.generateNotePdf(getApplication(), savedNote1)
                noteDao.insertNote(savedNote1.copy(pdfFilePath = pdf1.absolutePath))

                val savedNote2 = note2.copy(id = note2Id)
                val pdf2 = PdfGenerator.generateNotePdf(getApplication(), savedNote2)
                noteDao.insertNote(savedNote2.copy(pdfFilePath = pdf2.absolutePath))

                // Seed Stats
                userStatsDao.insertOrUpdateStats(
                    UserStats(
                        totalStudyMinutes = 450,
                        streakDays = 7,
                        lastStudyDate = "11/08/2026",
                        xpPoints = 850,
                        currentLevel = "Lecture Legend",
                        notesCount = 8,
                        testsCompletedCount = 2
                    )
                )

                // Seed Chat Messages
                chatMessageDao.insertMessage(
                    ChatMessage(sender = "USER", text = "Explain what sir said about AVL tree balancing")
                )
                chatMessageDao.insertMessage(
                    ChatMessage(
                        sender = "GEMINI",
                        text = "Prof. Ahmed emphasized that AVL trees strictly enforce a balance factor between -1 and +1 for every node. When an insertion breaks this invariant, rotations (Left, Right, LR, or RL) are performed immediately to preserve O(log N) search complexity."
                    )
                )

                // Seed Test Paper
                testPaperDao.insertTestPaper(
                    TestPaper(
                        title = "Monthly Test Paper - August 2026",
                        monthYear = "August 2026",
                        createdDate = "25/08/2026",
                        totalQuestions = 30,
                        score = 28,
                        isCompleted = true,
                        questionsJson = GeminiHelper.getFallbackTestPaperJson("Data Structures & Chemistry"),
                        weakAreas = listOf("Carbocation rearrangement in EAS", "Double LR rotations in AVL trees")
                    )
                )
            }
        }
    }

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

    fun stopAndProcessCurrentRecording(subject: String, teacher: String) {
        viewModelScope.launch {
            val audioFile = audioRecorderManager.stopRecording()
            _isProcessingGemini.value = true

            val timeStamp = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
            val duration = audioRecorderManager.recordingDurationSeconds.value

            val transcriptContext = "Recorded lecture audio file: ${audioFile?.name ?: "Lecture_01.m4a"}. Topics discussed: Key principles, derivations, formulas and teacher remarks."
            val processedResult = GeminiHelper.generateLectureNotes(subject, teacher, transcriptContext)

            val newNote = NoteEntry(
                subject = subject,
                title = processedResult.title,
                date = timeStamp,
                dayOfWeek = dayOfWeek,
                classTime = "Recent Lecture",
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
            val replyText = GeminiHelper.answerChatQuestion(userText, contextNotes)

            chatMessageDao.insertMessage(ChatMessage(sender = "GEMINI", text = replyText))
        }
    }

    fun generateNewMonthlyTestPaper(subject: String) {
        viewModelScope.launch {
            _isProcessingGemini.value = true
            val notesList = allNotes.value.map { it.summaryMarkdown }
            val jsonTest = GeminiHelper.generateMonthlyTestPaper(subject, notesList)

            val paper = TestPaper(
                title = "$subject Monthly Test Paper",
                monthYear = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date()),
                createdDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                totalQuestions = 30,
                questionsJson = jsonTest,
                weakAreas = listOf("Review formulas and chapter 3 derivations")
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
}
