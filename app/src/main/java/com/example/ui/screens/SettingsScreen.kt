package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var autoRecordEnabled by remember { mutableStateOf(true) }
    var cloudSyncEnabled by remember { mutableStateOf(true) }
    var selectedLanguage by remember { mutableStateOf("English") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("APP SETTINGS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Gemini API Key Notice Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = ElectricViolet)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("GOOGLE GEMINI API KEY", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "API keys are securely managed via the Secrets panel in Google AI Studio UI.",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = ElectricViolet.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Status: Secret injected from AI Studio runtime environment",
                            color = BrightCyan,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }

            // Settings Preferences
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("RECORDING & SCHEDULER", color = BrightCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Auto-Record Classes", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Start recording 2 min before class from routine", color = Color.Gray, fontSize = 11.sp)
                        }
                        Switch(
                            checked = autoRecordEnabled,
                            onCheckedChange = { autoRecordEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = ElectricViolet
                            ),
                            modifier = Modifier.testTag("auto_record_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = SecondarySurfaceNavy)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Cloud Sync (Firebase Firestore)", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Sync PDFs and notes across devices", color = Color.Gray, fontSize = 11.sp)
                        }
                        Switch(
                            checked = cloudSyncEnabled,
                            onCheckedChange = { cloudSyncEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = BrightCyan
                            ),
                            modifier = Modifier.testTag("cloud_sync_switch")
                        )
                    }
                }
            }

            // Language Preference
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = BrightCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("App Language", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = selectedLanguage == "English",
                            onClick = { selectedLanguage = "English" },
                            label = { Text("English") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricViolet,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = selectedLanguage == "Bengali",
                            onClick = { selectedLanguage = "Bengali" },
                            label = { Text("বাংলা (Bengali)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricViolet,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}
