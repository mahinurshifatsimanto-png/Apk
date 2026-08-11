package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RoutineEntry
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineManagerScreen(
    routines: List<RoutineEntry>,
    onAddRoutine: (RoutineEntry) -> Unit,
    onDeleteRoutine: (Long) -> Unit
) {
    var selectedDay by remember { mutableStateOf("Monday") }
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    var showAddDialog by remember { mutableStateOf(false) }

    val filteredRoutines = routines.filter { it.dayOfWeek.equals(selectedDay, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ROUTINE MANAGER", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ElectricViolet,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_routine_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Routine")
            }
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Day selector tabs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                items(daysOfWeek) { day ->
                    val isSelected = day == selectedDay
                    Surface(
                        color = if (isSelected) ElectricViolet else SurfaceDarkNavy,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .clickable { selectedDay = day }
                            .testTag("day_tab_$day")
                    ) {
                        Text(
                            text = day.take(3).uppercase(),
                            color = if (isSelected) Color.White else Color.Gray,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredRoutines.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = InactiveMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No class scheduled for $selectedDay", color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tap + button below to add a class routine", color = ElectricViolet, fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredRoutines, key = { it.id }) { routine ->
                        RoutineCard(routine = routine, onDelete = { onDeleteRoutine(routine.id) })
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddRoutineDialog(
            defaultDay = selectedDay,
            onDismiss = { showAddDialog = false },
            onConfirm = { entry ->
                onAddRoutine(entry)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun RoutineCard(routine: RoutineEntry, onDelete: () -> Unit) {
    val tagColor = parseColorHex(routine.colorHex)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(50.dp)
                    .clip(CircleShape)
                    .background(tagColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = routine.subject,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = BrightCyan.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = routine.roomNo,
                            color = BrightCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "👨‍🏫 ${routine.teacher}",
                    color = Color.LightGray,
                    fontSize = 13.sp
                )

                Text(
                    text = "⏰ ${routine.startTime} - ${routine.endTime}",
                    color = InactiveMuted,
                    fontSize = 12.sp
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = CoralRed)
            }
        }
    }
}

@Composable
fun AddRoutineDialog(
    defaultDay: String,
    onDismiss: () -> Unit,
    onConfirm: (RoutineEntry) -> Unit
) {
    var subject by remember { mutableStateOf("") }
    var teacher by remember { mutableStateOf("") }
    var roomNo by remember { mutableStateOf("Room 101") }
    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("10:00") }
    var selectedDay by remember { mutableStateOf(defaultDay) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ADD NEW CLASS ROUTINE", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        containerColor = SecondarySurfaceNavy,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject Name", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = ElectricViolet,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                OutlinedTextField(
                    value = teacher,
                    onValueChange = { teacher = it },
                    label = { Text("Teacher Name", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = ElectricViolet,
                        unfocusedBorderColor = Color.Gray
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ElectricViolet,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time", color = Color.Gray) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = ElectricViolet,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                }
                OutlinedTextField(
                    value = roomNo,
                    onValueChange = { roomNo = it },
                    label = { Text("Room No.", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = ElectricViolet,
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank()) {
                        onConfirm(
                            RoutineEntry(
                                dayOfWeek = selectedDay,
                                startTime = startTime,
                                endTime = endTime,
                                subject = subject,
                                teacher = teacher.ifBlank { "Professor" },
                                roomNo = roomNo
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet)
            ) {
                Text("Save Routine", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

fun parseColorHex(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        ElectricViolet
    }
}
