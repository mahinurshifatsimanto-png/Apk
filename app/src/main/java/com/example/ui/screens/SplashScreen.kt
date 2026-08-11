package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrightCyan
import com.example.ui.theme.ElectricViolet
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var textIndex by remember { mutableStateOf(0) }
    val fullText = "wlc Simanto"

    LaunchedEffect(Unit) {
        // Typing animation for "wlc Simanto"
        for (i in 1..fullText.length) {
            delay(100)
            textIndex = i
        }
        delay(1200)
        onTimeout()
    }

    // Infinite rotation for floating particles and wave logo
    val infiniteTransition = rememberInfiniteTransition(label = "SplashAnimation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation"
    )

    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Wave"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0A)),
        contentAlignment = Alignment.Center
    ) {
        // Background Floating Particles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val particleCount = 20

            for (i in 0 until particleCount) {
                val x = (width * 0.1f * i + rotation * 2) % width
                val y = (height * 0.15f * i + rotation * 1.5f) % height
                val radius = (i % 4 + 2).dp.toPx()
                val particleColor = if (i % 2 == 0) ElectricViolet.copy(alpha = 0.4f) else BrightCyan.copy(alpha = 0.3f)
                drawCircle(color = particleColor, radius = radius, center = Offset(x, y))
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Animated Logo (Mortarboard + Soundwave Merging Canvas)
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2
                    val cy = size.height / 2

                    // Mortarboard Cap Diamond Top
                    val capPath = Path().apply {
                        moveTo(cx, cy - 35)
                        lineTo(cx + 45, cy - 10)
                        lineTo(cx, cy + 15)
                        lineTo(cx - 45, cy - 10)
                        close()
                    }
                    drawPath(
                        path = capPath,
                        brush = Brush.linearGradient(listOf(ElectricViolet, BrightCyan)),
                        style = Stroke(width = 6f)
                    )

                    // Soundwave bars beneath cap
                    val barWidth = 6f
                    val barGap = 12f
                    val startX = cx - 36f
                    for (i in 0..5) {
                        val amplitude = kotlin.math.sin(waveOffset + i) * 18f + 22f
                        val bx = startX + i * barGap
                        drawLine(
                            color = if (i % 2 == 0) BrightCyan else ElectricViolet,
                            start = Offset(bx, cy + 25 - amplitude / 2),
                            end = Offset(bx, cy + 25 + amplitude / 2),
                            strokeWidth = barWidth
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Typing Text: "wlc Simanto"
            Text(
                text = fullText.take(textIndex),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle
            Text(
                text = "Your AI-Powered Class Companion",
                color = ElectricViolet,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading Indicator
            CircularProgressIndicator(
                color = BrightCyan,
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
