package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RoutineEntry
import com.example.ui.theme.*
import com.example.utils.AudioRecorderManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassRecorderScreen(
    recorderManager: AudioRecorderManager,
    routines: List<RoutineEntry>,
    isProcessingGemini: Boolean,
    onStopAndProcess: (subject: String, teacher: String) -> Unit
) {
    val isRecording by recorderManager.isRecording.collectAsState()
    val isPaused by recorderManager.isPaused.collectAsState()
    val durationSeconds by recorderManager.recordingDurationSeconds.collectAsState()
    val amplitude by recorderManager.amplitude.collectAsState()

    var selectedSubject by remember { mutableStateOf("Data Structures") }
    var selectedTeacher by remember { mutableStateOf("Prof. Ahmed") }
    var noteTagInput by remember { mutableStateOf("") }
    var taggedNotesList by remember { mutableStateOf(listOf<String>()) }

    // Auto select from current routine if available
    LaunchedEffect(routines) {
        if (routines.isNotEmpty()) {
            selectedSubject = routines.first().subject
            selectedTeacher = routines.first().teacher
        }
    }

    val minutes = durationSeconds / 60
    val seconds = durationSeconds % 60
    val formattedTimer = String.format("%02d:%02d", minutes, seconds)

    // Waveform animation offset
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
                title = { Text("CLASS RECORDER", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
            // Subject Selection Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Subject for Recording", color = BrightCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(selectedSubject, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Prof. $selectedTeacher", color = Color.LightGray, fontSize = 13.sp)
                        }
                        Surface(
                            color = ElectricViolet.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("AUTO-ROUTINE LINKED", color = ElectricViolet, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Waveform Canvas Visualizer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
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
                        Text("Gemini AI Processing Audio Notes...", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timer & File size
            Text(
                text = formattedTimer,
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = "File Size: ${recorderManager.getFormattedFileSize()}",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Recording Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isRecording) {
                    // Pause/Resume
                    IconButton(
                        onClick = {
                            if (isPaused) recorderManager.resumeRecording() else recorderManager.pauseRecording()
                        },
                        modifier = Modifier
                            .size(56.dp)
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

                    // Stop Button
                    IconButton(
                        onClick = {
                            onStopAndProcess(selectedSubject, selectedTeacher)
                        },
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(CoralRed)
                            .testTag("stop_recording_button")
                    ) {
                        Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                } else {
                    // Start Recording Button
                    Button(
                        onClick = { recorderManager.startRecording(selectedSubject) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrightCyan),
                        shape = CircleShape,
                        modifier = Modifier
                            .height(64.dp)
                            .fillMaxWidth(0.8f)
                            .testTag("start_recording_button")
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = "Mic", tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("START RECORDING", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tagging section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Tag Confusion / Bookmark Timestamp", color = ElectricViolet, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = noteTagInput,
                            onValueChange = { noteTagInput = it },
                            placeholder = { Text("e.g. Confused about formula step 3", color = Color.Gray) },
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
}
