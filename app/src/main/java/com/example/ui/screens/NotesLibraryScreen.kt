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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
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
import com.example.data.NoteEntry
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesLibraryScreen(
    notes: List<NoteEntry>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onNoteClick: (NoteEntry) -> Unit,
    onDeleteNote: (NoteEntry) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    val filters = listOf("ALL", "Data Structures", "Organic Chemistry", "Quantum Physics", "Incomplete")

    val filteredNotes = notes.filter { note ->
        val matchesQuery = note.title.contains(searchQuery, ignoreCase = true) ||
                note.subject.contains(searchQuery, ignoreCase = true) ||
                note.summaryMarkdown.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "ALL" -> true
            "Incomplete" -> note.confusionPoints.isNotEmpty()
            else -> note.subject.equals(selectedFilter, ignoreCase = true)
        }

        matchesQuery && matchesFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MY NOTES LIBRARY", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search notes by topic, keyword, or subject...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrightCyan) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notes_search_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = ElectricViolet,
                    unfocusedBorderColor = SecondarySurfaceNavy,
                    focusedContainerColor = SurfaceDarkNavy,
                    unfocusedContainerColor = SurfaceDarkNavy
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(filters) { filter ->
                    val isSelected = filter == selectedFilter
                    Surface(
                        color = if (isSelected) ElectricViolet else SurfaceDarkNavy,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .clickable { selectedFilter = filter }
                            .testTag("filter_$filter")
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color.White else Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            if (filteredNotes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = InactiveMuted, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No notes found for query", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredNotes, key = { it.id }) { note ->
                        NoteCardItem(
                            note = note,
                            onClick = { onNoteClick(note) },
                            onDelete = { onDeleteNote(note) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoteCardItem(
    note: NoteEntry,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("note_item_${note.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDarkNavy)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = ElectricViolet.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = note.subject.uppercase(),
                        color = ElectricViolet,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = NeonGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${note.confidenceScore}% AI Match",
                            color = NeonGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CoralRed)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "📅 ${note.date} • ⏱️ ${note.durationSeconds / 60} mins • 📖 ${note.readTimeMinutes} min read",
                color = Color.LightGray,
                fontSize = 12.sp
            )

            if (note.topics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    note.topics.take(2).forEach { topic ->
                        Surface(
                            color = SecondarySurfaceNavy,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "• $topic",
                                color = BrightCyan,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
