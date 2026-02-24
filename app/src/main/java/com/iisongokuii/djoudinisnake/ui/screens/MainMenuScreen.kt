package com.iisongokuii.djoudinisnake.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iisongokuii.djoudinisnake.ui.theme.DeepSpace
import com.iisongokuii.djoudinisnake.ui.theme.NeonBlue
import com.iisongokuii.djoudinisnake.ui.theme.NeonPurple
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun MainMenuScreen(onPlayClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(DeepSpace)) {
        // Animierter Partikel-Hintergrund
        AnimatedBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Glühender Titel
            Text(
                text = "Djoudini's",
                color = NeonPurple,
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = NeonPurple.copy(alpha = 0.5f),
                        blurRadius = 20f
                    )
                )
            )
            Text(
                text = "CHALLENGE",
                color = NeonBlue,
                fontSize = 32.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 6.sp,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = NeonBlue.copy(alpha = 0.5f),
                        blurRadius = 15f
                    )
                )
            )

            Spacer(modifier = Modifier.height(100.dp))

            GlassButton("ENTRANCE (PLAY)", NeonPurple, onPlayClick)
            Spacer(modifier = Modifier.height(20.dp))
            GlassButton("SECRETS (SOON)", Color.Gray, {})
            Spacer(modifier = Modifier.height(20.dp))
            GlassButton("ILLUSIONS (SETTINGS)", Color.Gray, {})
        }
    }
}

@Composable
fun AnimatedBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg_anim")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time_anim"
    )

    // Generiere 50 zufällige Sterne
    val stars = remember { List(50) { Offset(Random.nextFloat(), Random.nextFloat()) to Random.nextFloat() * 4f } }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Zeichne weiche Nebel-Gradients im Hintergrund
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(NeonPurple.copy(alpha = 0.15f), Color.Transparent),
                center = Offset(w * 0.2f, h * 0.3f),
                radius = w * 0.8f
            )
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(NeonBlue.copy(alpha = 0.15f), Color.Transparent),
                center = Offset(w * 0.8f, h * 0.7f),
                radius = w * 0.8f
            )
        )

        // Zeichne schwebende Sterne
        stars.forEachIndexed { index, (pos, radius) ->
            val xOffset = sin(time + index) * 50f
            val yOffset = kotlin.math.cos(time + index) * 50f
            drawCircle(
                color = Color.White.copy(alpha = 0.3f + (sin(time * 5f + index) * 0.2f).coerceIn(0f, 1f)),
                radius = radius,
                center = Offset((pos.x * w + xOffset) % w, (pos.y * h + yOffset) % h)
            )
        }
    }
}

@Composable
fun GlassButton(text: String, accentColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(64.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.1f),
                        Color.White.copy(alpha = 0.03f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(accentColor.copy(alpha = 0.8f), accentColor.copy(alpha = 0.2f))
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}
