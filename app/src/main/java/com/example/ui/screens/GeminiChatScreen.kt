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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
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
import com.example.data.ChatMessage
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiChatScreen(
    messages: List<ChatMessage>,
    onSendMessage: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val suggestedChips = listOf(
        "Explain what sir said about AVL tree balancing",
        "What did I not understand this week?",
        "Quiz me on Chapter 3 definitions",
        "Summarize Organic Chemistry mechanism"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricViolet)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("GEMINI CHAT AI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Context loaded from all lecture notes & PDFs", color = BrightCyan, fontSize = 11.sp)
                        }
                    }
                },
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
            // Suggested Prompt Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                items(suggestedChips) { chip ->
                    Surface(
                        color = SurfaceDarkNavy,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .clickable { onSendMessage(chip) }
                            .testTag("suggested_chip_$chip")
                    ) {
                        Text(
                            text = chip,
                            color = BrightCyan,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Message List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                reverseLayout = false
            ) {
                items(messages, key = { it.id }) { msg ->
                    ChatMessageBubble(msg)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask anything about your classes...", color = Color.Gray) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = ElectricViolet,
                        unfocusedBorderColor = SurfaceDarkNavy,
                        focusedContainerColor = SurfaceDarkNavy,
                        unfocusedContainerColor = SurfaceDarkNavy
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ElectricViolet)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(msg: ChatMessage) {
    val isUser = msg.sender.equals("USER", ignoreCase = true)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isUser) SecondarySurfaceNavy else SurfaceDarkNavy,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!isUser) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricViolet, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Gemini AI", color = ElectricViolet, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = msg.text,
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
