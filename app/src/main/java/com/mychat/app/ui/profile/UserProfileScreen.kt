package com.mychat.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mychat.app.ui.theme.*

@Composable
fun UserProfileScreen(navController: NavController, userId: String) {
    Box(modifier = Modifier.fillMaxSize().background(Black), contentAlignment = Alignment.Center) {
        Text(text = "User Profile — Phase 3", color = Green, fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}
