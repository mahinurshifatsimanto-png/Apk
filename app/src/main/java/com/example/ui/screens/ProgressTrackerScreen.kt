package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserStats
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressTrackerScreen(userStats: UserStats?) {
    val totalMinutes = userStats?.totalStudyMinutes ?: 450
    val totalHours = totalMinutes / 60
    val remMins = totalMinutes % 60

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PROGRESS TRACKER", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // A. Study Overview Card
            Text("STUDY OVERVIEW", color = BrightCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("RECORDED TIME", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${totalHours}h ${remMins}m", color = NeonGreen, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("This Month", color = InactiveMuted, fontSize = 11.sp)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("STREAK", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("🔥 ${userStats?.streakDays ?: 7} Days", color = CoralRed, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("Active Streak", color = InactiveMuted, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gamified Badge Level Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SecondarySurfaceNavy),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(ElectricViolet),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎓", fontSize = 26.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(userStats?.currentLevel ?: "Lecture Legend", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("${userStats?.xpPoints ?: 850} XP Points • Level 4 Scholar", color = BrightCyan, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { 0.85f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = NeonGreen,
                            trackColor = InactiveMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // B. Topic Coverage Map
            Text("TOPIC COVERAGE & CONFUSION MAP", color = BrightCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Data Structures & Algorithms", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    TopicStatusItem("Binary Search Tree In-order Traversal", isUnderstood = true)
                    TopicStatusItem("AVL Tree Balance Factor Check", isUnderstood = true)
                    TopicStatusItem("Double Left-Right Rotation Pivot (Flagged Confusing)", isUnderstood = false)

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = SecondarySurfaceNavy)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Organic Chemistry II", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    TopicStatusItem("Arenium Ion Sigma Complex", isUnderstood = true)
                    TopicStatusItem("Carbocation Rearrangement in EAS", isUnderstood = false)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Engagement Score Breakdown
            Text("STUDY ENGAGEMENT BREAKDOWN", color = BrightCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Notes Opens & Reads", color = Color.LightGray, fontSize = 13.sp)
                        Text("24 times", color = ElectricViolet, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Gemini AI Chat Queries", color = Color.LightGray, fontSize = 13.sp)
                        Text("18 questions asked", color = BrightCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Monthly Tests Completed", color = Color.LightGray, fontSize = 13.sp)
                        Text("2 tests (Avg 92%)", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun TopicStatusItem(topicName: String, isUnderstood: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isUnderstood) Icons.Default.CheckCircle else Icons.Default.Warning,
            contentDescription = null,
            tint = if (isUnderstood) NeonGreen else CoralRed,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = topicName,
            color = if (isUnderstood) Color.White else CoralRed,
            fontSize = 13.sp,
            fontWeight = if (isUnderstood) FontWeight.Normal else FontWeight.SemiBold
        )
    }
}
