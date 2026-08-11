package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.NoteEntry
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotePdfViewerDialog(
    note: NoteEntry,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF121222)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                TopAppBar(
                    title = {
                        Column {
                            Text(note.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text("${note.subject} • ${note.date}", color = BrightCyan, fontSize = 12.sp)
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = BrightCyan)
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_pdf_dialog")) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDarkNavy)
                )

                // Scrollable Document Body
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // PDF Cover Page Simulation Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = SecondarySurfaceNavy)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("MY COLLEGE NOTES • COVER PAGE", color = ElectricViolet, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Subject: ${note.subject}", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text("Teacher: ${note.teacher}", color = Color.LightGray, fontSize = 13.sp)
                            Text("Time: ${note.classTime} (${note.dayOfWeek})", color = InactiveMuted, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = ElectricViolet.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("⚡ Gemini Verified AI Note • ${note.confidenceScore}% Confidence", color = ElectricViolet, fontSize = 11.sp, modifier = Modifier.padding(6.dp))
                            }
                        }
                    }

                    // Markdown Content View
                    val lines = note.summaryMarkdown.lines()
                    lines.forEach { line ->
                        when {
                            line.startsWith("#") -> {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = line.replace("#", "").trim(),
                                    color = ElectricViolet,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            line.startsWith("-") || line.startsWith("*") -> {
                                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                    Text("• ", color = BrightCyan, fontWeight = FontWeight.Bold)
                                    Text(line.substring(1).trim(), color = Color.White, fontSize = 14.sp)
                                }
                            }
                            line.startsWith(">") -> {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy)
                                ) {
                                    Text(
                                        text = line.substring(1).trim(),
                                        color = BrightCyan,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            }
                            else -> {
                                if (line.trim().isNotEmpty()) {
                                    Text(
                                        text = line.trim(),
                                        color = Color.LightGray,
                                        fontSize = 14.sp,
                                        lineHeight = 20.sp,
                                        modifier = Modifier.padding(vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (note.formulas.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("FORMULA CHEAT BOX", color = CoralRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        note.formulas.forEach { formula ->
                            Surface(
                                color = CoralRed.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(formula, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(10.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("PDF Saved to: ${note.pdfFilePath}", color = InactiveMuted, fontSize = 10.sp)
                }
            }
        }
    }
}
