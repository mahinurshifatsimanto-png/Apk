package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserStats
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeDashboardScreen(
    userStats: UserStats?,
    onNavigate: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "MY COLLEGE NOTES",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                },
                actions = {
                    IconButton(
                        onClick = { onNavigate("settings") },
                        modifier = Modifier.testTag("notification_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { onNavigate("settings") },
                        modifier = Modifier.testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ElectricViolet)
                            .clickable { onNavigate("settings") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("S", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground
                )
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Streak & Motivation Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🔥 ${userStats?.streakDays ?: 7} Day Streak",
                                color = CoralRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = ElectricViolet.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = userStats?.currentLevel ?: "Lecture Legend",
                                    color = ElectricViolet,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "\"Consistency in listening converts lectures into lasting knowledge.\"",
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Dashboard",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Grid of 6 Glassmorphic Cards
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    DashboardCard(
                        title = "ADD ROUTINE",
                        subtitle = "Weekly timetable",
                        icon = Icons.Outlined.CalendarMonth,
                        accentColor = ElectricViolet,
                        onClick = { onNavigate("routine") },
                        testTag = "add_routine_card"
                    )
                }
                item {
                    DashboardCard(
                        title = "ADD CLASS / RECORD",
                        subtitle = "Live mic audio",
                        icon = Icons.Outlined.Mic,
                        accentColor = BrightCyan,
                        onClick = { onNavigate("record") },
                        testTag = "add_class_record_card"
                    )
                }
                item {
                    DashboardCard(
                        title = "MY NOTES",
                        subtitle = "${userStats?.notesCount ?: 8} Generated PDFs",
                        icon = Icons.Outlined.PictureAsPdf,
                        accentColor = BrightCyan,
                        onClick = { onNavigate("notes") },
                        testTag = "my_notes_card"
                    )
                }
                item {
                    DashboardCard(
                        title = "GEMINI CHAT",
                        subtitle = "Ask your notes AI",
                        icon = Icons.Outlined.AutoAwesome,
                        accentColor = ElectricViolet,
                        gradient = true,
                        onClick = { onNavigate("chat") },
                        testTag = "gemini_chat_card"
                    )
                }
                item {
                    DashboardCard(
                        title = "PROGRESS TRACKER",
                        subtitle = "${userStats?.totalStudyMinutes ?: 450} mins study",
                        icon = Icons.Outlined.BarChart,
                        accentColor = NeonGreen,
                        onClick = { onNavigate("progress") },
                        testTag = "progress_tracker_card"
                    )
                }
                item {
                    DashboardCard(
                        title = "TEST PAPER",
                        subtitle = "30-question exam",
                        icon = Icons.Outlined.Assignment,
                        accentColor = CoralRed,
                        onClick = { onNavigate("test") },
                        testTag = "test_paper_card"
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    gradient: Boolean = false,
    onClick: () -> Unit,
    testTag: String
) {
    val bgModifier = if (gradient) {
        Modifier.background(
            brush = Brush.linearGradient(
                colors = listOf(SurfaceDarkNavy, SecondarySurfaceNavy, ElectricViolet.copy(alpha = 0.3f))
            )
        )
    } else {
        Modifier.background(SurfaceDarkNavy)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .testTag(testTag)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(bgModifier)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = subtitle,
                        color = InactiveMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
