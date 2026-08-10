package com.mychat.app.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.mychat.app.navigation.Screen
import com.mychat.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(1f, animationSpec = tween(durationMillis = 900))
        delay(1_200)
        val destination = if (auth.currentUser != null) Screen.Home.route else Screen.Login.route
        navController.navigate(destination) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.alpha(alphaAnim.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = ">_", fontSize = 80.sp, fontWeight = FontWeight.ExtraBold, color = Green, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "MyChat", fontSize = 38.sp, fontWeight = FontWeight.Bold, color = White, fontFamily = FontFamily.Default)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "// secure · fast · real", fontSize = 13.sp, fontWeight = FontWeight.Normal, color = GreenDim, fontFamily = FontFamily.Monospace)
        }
        Text(
            text = "v1.0",
            fontSize = 11.sp,
            color = WhiteSubtle,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp).alpha(alphaAnim.value)
        )
    }
}
