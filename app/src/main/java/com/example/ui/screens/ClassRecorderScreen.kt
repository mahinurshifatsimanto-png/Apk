package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.RoutineEntry
import com.example.ui.theme.*
import com.example.utils.AudioRecorderManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassRecorderScreen(
    recorderManager: AudioRecorderManager,
    routines: List<RoutineEntry>,
    isProcessingGemini: Boolean,
    onStopAndProcess: (subject: String, teacher: String) -> Unit,
    onAddManualNote: (subject: String, title: String, teacher: String, content: String, topics: List<String>, formulas: List<String>) -> Unit = { _, _, _, _, _, _ -> }
) {
    val context = LocalContext.current
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
    }

    val isRecording by recorderManager.isRecording.collectAsState()
    val isPaused by recorderManager.isPaused.collectAsState()
    val durationSeconds by recorderManager.recordingDurationSeconds.collectAsState()
    val amplitude by recorderManager.amplitude.collectAsState()

    var customSubject by remember { mutableStateOf("Physics") }
    var customTeacher by remember { mutableStateOf("Professor") }
    var noteTagInput by remember { mutableStateOf("") }
    var taggedNotesList by remember { mutableStateOf(listOf<String>()) }
    var showManualNoteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(routines) {
        if (routines.isNotEmpty() && customSubject == "Physics") {
            customSubject = routines.first().subject
            customTeacher = routines.first().teacher
        }
    }

    val minutes = durationSeconds / 60
    val seconds = durationSeconds % 60
    val formattedTimer = String.format("%02d:%02d", minutes, seconds)

    val infiniteTransition = rememberInfiniteTransition(label = "WaveformAnimation")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CLASS RECORDER & NOTES", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!hasMicPermission) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = CoralRed.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = CoralRed)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("MICROPHONE PERMISSION REQUIRED", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "To record live class lectures, please grant microphone access.",
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                            colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
                        ) {
                            Text("Grant Microphone Permission", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Subject Input Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Subject & Teacher Info", color = BrightCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customSubject,
                            onValueChange = { customSubject = it },
                            label = { Text("Subject Name", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                        OutlinedTextField(
                            value = customTeacher,
                            onValueChange = { customTeacher = it },
                            label = { Text("Teacher Name", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Waveform Canvas Visualizer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SecondarySurfaceNavy),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    val barCount = 32
                    val barWidth = width / (barCount * 1.5f)

                    for (i in 0 until barCount) {
                        val ampFactor = if (isRecording && !isPaused) {
                            val baseAmp = (amplitude.toFloat() / 10000f).coerceIn(0.1f, 1.0f)
                            kotlin.math.abs(kotlin.math.sin(wavePhase + i * 0.3f)) * baseAmp * height * 0.8f
                        } else {
                            8f
                        }

                        val barHeight = ampFactor.coerceAtLeast(6f)
                        val x = i * (barWidth * 1.5f) + barWidth
                        val yTop = (height - barHeight) / 2f

                        drawRoundRect(
                            color = if (isRecording) BrightCyan else InactiveMuted,
                            topLeft = Offset(x, yTop),
                            size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                        )
                    }
                }

                if (isProcessingGemini) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = ElectricViolet)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Processing Lecture Notes...", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = formattedTimer,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = "File Size: ${recorderManager.getFormattedFileSize()}",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Recording Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isRecording) {
                    IconButton(
                        onClick = {
                            if (isPaused) recorderManager.resumeRecording() else recorderManager.pauseRecording()
                        },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(SurfaceDarkNavy)
                            .testTag("pause_resume_button")
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = "Pause",
                            tint = BrightCyan
                        )
                    }

                    IconButton(
                        onClick = {
                            onStopAndProcess(customSubject, customTeacher)
                        },
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(CoralRed)
                            .testTag("stop_recording_button")
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                } else {
                    Button(
                        onClick = {
                            if (!hasMicPermission) {
                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            } else {
                                recorderManager.startRecording(customSubject)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrightCyan),
                        shape = CircleShape,
                        modifier = Modifier
                            .height(56.dp)
                            .weight(1f)
                            .testTag("start_recording_button")
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Mic", tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("START RECORDING", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to write manual note without recording
            OutlinedButton(
                onClick = { showManualNoteDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricViolet)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = ElectricViolet)
                Spacer(modifier = Modifier.width(8.dp))
                Text("OR CREATE WRITTEN LECTURE NOTE MANUALLY", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tagging section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Tag Key Points / Bookmarks", color = ElectricViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = noteTagInput,
                            onValueChange = { noteTagInput = it },
                            placeholder = { Text("e.g. Teacher emphasized formula step 2", color = Color.Gray) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = BrightCyan,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (noteTagInput.isNotBlank()) {
                                    taggedNotesList = taggedNotesList + "[$formattedTimer] $noteTagInput"
                                    noteTagInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
                        ) {
                            Text("Tag")
                        }
                    }

                    if (taggedNotesList.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        taggedNotesList.forEach { tag ->
                            Text(text = "🔖 $tag", color = Color.LightGray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    if (showManualNoteDialog) {
        ManualNoteDialog(
            defaultSubject = customSubject,
            defaultTeacher = customTeacher,
            onDismiss = { showManualNoteDialog = false },
            onConfirm = { subject, title, teacher, content, topics, formulas ->
                onAddManualNote(subject, title, teacher, content, topics, formulas)
                showManualNoteDialog = false
            }
        )
    }
}

@Composable
fun ManualNoteDialog(
    defaultSubject: String,
    defaultTeacher: String,
    onDismiss: () -> Unit,
    onConfirm: (subject: String, title: String, teacher: String, content: String, topics: List<String>, formulas: List<String>) -> Unit
) {
    var subject by remember { mutableStateOf(defaultSubject) }
    var title by remember { mutableStateOf("") }
    var teacher by remember { mutableStateOf(defaultTeacher) }
    var content by remember { mutableStateOf("") }
    var formula by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("CREATE WRITTEN LECTURE NOTE", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        containerColor = SecondarySurfaceNavy,
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = BrightCyan)
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = BrightCyan)
                )
                OutlinedTextField(
                    value = teacher,
                    onValueChange = { teacher = it },
                    label = { Text("Teacher Name", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = BrightCyan)
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Lecture Content / Summary", color = Color.Gray) },
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = BrightCyan)
                )
                OutlinedTextField(
                    value = formula,
                    onValueChange = { formula = it },
                    label = { Text("Key Formula (Optional)", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedBorderColor = BrightCyan)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank() && content.isNotBlank()) {
                        onConfirm(
                            subject,
                            title.ifBlank { "$subject Lecture Note" },
                            teacher.ifBlank { "Professor" },
                            content,
                            listOf(subject, "Class Lecture"),
                            if (formula.isNotBlank()) listOf(formula) else emptyList()
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrightCyan)
            ) {
                Text("Save Note & Generate PDF", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}
