package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TestPaper
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyTestPaperScreen(
    testPapers: List<TestPaper>,
    isProcessing: Boolean,
    onGenerateTest: (subject: String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Available Tests, 1 = Practice Simulator
    var selectedSubject by remember { mutableStateOf("Data Structures & Chemistry") }
    var userAnswers by remember { mutableStateOf(mapOf<Int, Int>()) }
    var testSubmitted by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MONTHLY TEST PAPERS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
        ) {
            // Generate New Test Button
            Button(
                onClick = { onGenerateTest(selectedSubject) },
                enabled = !isProcessing,
                colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("generate_test_button")
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gemini Generating 30-Q Test...")
                } else {
                    Icon(Icons.Default.Assignment, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("GENERATE NEW 30-QUESTION EXAM", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tabs
            Row(modifier = Modifier.fillMaxWidth()) {
                TabButton(
                    title = "TEST PAPERS (${testPapers.size})",
                    isSelected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TabButton(
                    title = "EXAM SIMULATOR",
                    isSelected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedTab == 0) {
                // Test Papers List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(testPapers.size) { index ->
                        val paper = testPapers[index]
                        TestPaperCard(paper)
                    }
                }
            } else {
                // Interactive Practice Simulator
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("30-Q EXAM SIMULATION", color = BrightCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = CoralRed, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("45:00 min", color = CoralRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Question 1
                        Text("Q1. (MCQ) What is the primary state function discussed in energy state lectures?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        val options = listOf("A. Enthalpy", "B. Kinetic Momentum", "C. Friction Coefficient", "D. Voltage Ratio")
                        options.forEachIndexed { optIndex, optionText ->
                            val isSelected = userAnswers[1] == optIndex
                            Surface(
                                color = if (isSelected) ElectricViolet else SecondarySurfaceNavy,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        userAnswers = userAnswers + (1 to optIndex)
                                    }
                            ) {
                                Text(optionText, color = Color.White, modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Question 2
                        Text("Q2. (MCQ) Which boundary condition applies when t = 0 in RC circuit charging?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))

                        val q2Options = listOf("A. V = V_max", "B. V = 0", "C. V = Infinity", "D. V = 0.5 V_max")
                        q2Options.forEachIndexed { optIndex, optionText ->
                            val isSelected = userAnswers[2] == optIndex
                            Surface(
                                color = if (isSelected) ElectricViolet else SecondarySurfaceNavy,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        userAnswers = userAnswers + (2 to optIndex)
                                    }
                            ) {
                                Text(optionText, color = Color.White, modifier = Modifier.padding(12.dp), fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        if (testSubmitted) {
                            Surface(
                                color = NeonGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("🎉 EXAM SUBMITTED! Score: 28/30", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Weak Areas Identified: Carbocation rearrangement & AVL LR rotation pivots", color = Color.LightGray, fontSize = 12.sp)
                                }
                            }
                        } else {
                            Button(
                                onClick = { testSubmitted = true },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("SUBMIT EXAM & AUTO-GRADE", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TestPaperCard(paper: TestPaper) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(paper.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Surface(
                    color = if (paper.isCompleted) NeonGreen.copy(alpha = 0.2f) else CoralRed.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (paper.isCompleted) "Score: ${paper.score}/30" else "Pending",
                        color = if (paper.isCompleted) NeonGreen else CoralRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text("📅 ${paper.createdDate} • 30 Questions (15 MCQ, 10 Short, 5 Long)", color = Color.LightGray, fontSize = 12.sp)

            if (paper.weakAreas.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Weak Area Report:", color = CoralRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                paper.weakAreas.forEach { area ->
                    Text("• $area", color = Color.Gray, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun TabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) ElectricViolet else SurfaceDarkNavy,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                title,
                color = if (isSelected) Color.White else Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
