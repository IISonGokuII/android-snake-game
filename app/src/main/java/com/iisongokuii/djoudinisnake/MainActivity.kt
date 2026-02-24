package com.iisongokuii.djoudinisnake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.iisongokuii.djoudinisnake.ui.theme.DjoudinisSnakeTheme
import com.iisongokuii.djoudinisnake.ui.theme.DeepSpace
import com.iisongokuii.djoudinisnake.ui.theme.NeonPurple

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Setzt das Spiel in den Vollbild-Modus (Edge-to-Edge)
        setContent {
            DjoudinisSnakeTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Djoudini's Challenge",
            color = NeonPurple,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
