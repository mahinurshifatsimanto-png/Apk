package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SurfaceDarkNavy
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val viewModel: MainViewModel = viewModel()
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val userStats by viewModel.userStats.collectAsState()
                val routines by viewModel.allRoutines.collectAsState()
                val notes by viewModel.allNotes.collectAsState()
                val chatMessages by viewModel.chatMessages.collectAsState()
                val testPapers by viewModel.testPapers.collectAsState()
                val selectedNoteForPdfView by viewModel.selectedNoteForPdfView.collectAsState()
                val isProcessingGemini by viewModel.isProcessingGemini.collectAsState()
                val searchQuery by viewModel.searchQuery.collectAsState()

                val showBottomBar = currentRoute in listOf("home", "notes", "record", "chat", "progress")

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = DarkBackground,
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                containerColor = SurfaceDarkNavy,
                                contentColor = Color.White,
                                tonalElevation = 8.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentRoute == "home",
                                    onClick = {
                                        navController.navigate("home") {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(if (currentRoute == "home") Icons.Filled.Home else Icons.Outlined.Home, contentDescription = "Home") },
                                    label = { Text("Home", fontSize = 11.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ElectricViolet,
                                        selectedTextColor = ElectricViolet,
                                        indicatorColor = ElectricViolet.copy(alpha = 0.2f),
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    ),
                                    modifier = Modifier.testTag("nav_home")
                                )

                                NavigationBarItem(
                                    selected = currentRoute == "notes",
                                    onClick = {
                                        navController.navigate("notes") {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(if (currentRoute == "notes") Icons.Filled.PictureAsPdf else Icons.Outlined.PictureAsPdf, contentDescription = "Notes") },
                                    label = { Text("Notes", fontSize = 11.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ElectricViolet,
                                        selectedTextColor = ElectricViolet,
                                        indicatorColor = ElectricViolet.copy(alpha = 0.2f),
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    ),
                                    modifier = Modifier.testTag("nav_notes")
                                )

                                NavigationBarItem(
                                    selected = currentRoute == "record",
                                    onClick = {
                                        navController.navigate("record") {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(if (currentRoute == "record") Icons.Filled.Mic else Icons.Outlined.Mic, contentDescription = "Record") },
                                    label = { Text("Record", fontSize = 11.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ElectricViolet,
                                        selectedTextColor = ElectricViolet,
                                        indicatorColor = ElectricViolet.copy(alpha = 0.2f),
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    ),
                                    modifier = Modifier.testTag("nav_record")
                                )

                                NavigationBarItem(
                                    selected = currentRoute == "chat",
                                    onClick = {
                                        navController.navigate("chat") {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(if (currentRoute == "chat") Icons.Filled.AutoAwesome else Icons.Outlined.AutoAwesome, contentDescription = "Chat") },
                                    label = { Text("Chat", fontSize = 11.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ElectricViolet,
                                        selectedTextColor = ElectricViolet,
                                        indicatorColor = ElectricViolet.copy(alpha = 0.2f),
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    ),
                                    modifier = Modifier.testTag("nav_chat")
                                )

                                NavigationBarItem(
                                    selected = currentRoute == "progress",
                                    onClick = {
                                        navController.navigate("progress") {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(if (currentRoute == "progress") Icons.Filled.BarChart else Icons.Outlined.BarChart, contentDescription = "Progress") },
                                    label = { Text("Progress", fontSize = 11.sp) },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = ElectricViolet,
                                        selectedTextColor = ElectricViolet,
                                        indicatorColor = ElectricViolet.copy(alpha = 0.2f),
                                        unselectedIconColor = Color.Gray,
                                        unselectedTextColor = Color.Gray
                                    ),
                                    modifier = Modifier.testTag("nav_progress")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("splash") {
                            SplashScreen(
                                onTimeout = {
                                    navController.navigate("home") {
                                        popUpTo("splash") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("home") {
                            HomeDashboardScreen(
                                userStats = userStats,
                                onNavigate = { route -> navController.navigate(route) }
                            )
                        }

                        composable("notes") {
                            NotesLibraryScreen(
                                notes = notes,
                                searchQuery = searchQuery,
                                onSearchChange = { viewModel.setSearchQuery(it) },
                                onNoteClick = { note -> viewModel.selectNoteForPdfView(note) },
                                onDeleteNote = { note -> viewModel.deleteNote(note) }
                            )
                        }

                        composable("record") {
                            ClassRecorderScreen(
                                recorderManager = viewModel.audioRecorderManager,
                                routines = routines,
                                isProcessingGemini = isProcessingGemini,
                                onStopAndProcess = { subject, teacher ->
                                    viewModel.stopAndProcessCurrentRecording(subject, teacher)
                                    navController.navigate("notes")
                                }
                            )
                        }

                        composable("chat") {
                            GeminiChatScreen(
                                messages = chatMessages,
                                onSendMessage = { text -> viewModel.sendChatMessage(text) }
                            )
                        }

                        composable("progress") {
                            ProgressTrackerScreen(userStats = userStats)
                        }

                        composable("routine") {
                            RoutineManagerScreen(
                                routines = routines,
                                onAddRoutine = { entry -> viewModel.addRoutine(entry) },
                                onDeleteRoutine = { id -> viewModel.deleteRoutine(id) }
                            )
                        }

                        composable("test") {
                            MonthlyTestPaperScreen(
                                testPapers = testPapers,
                                isProcessing = isProcessingGemini,
                                onGenerateTest = { subject -> viewModel.generateNewMonthlyTestPaper(subject) }
                            )
                        }

                        composable("settings") {
                            SettingsScreen()
                        }
                    }

                    // PDF Viewer Dialog
                    selectedNoteForPdfView?.let { note ->
                        NotePdfViewerDialog(
                            note = note,
                            onDismiss = { viewModel.selectNoteForPdfView(null) }
                        )
                    }
                }
            }
        }
    }
}
