package com.iisongokuii.djoudinisnake.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iisongokuii.djoudinisnake.ui.theme.DeepSpace
import com.iisongokuii.djoudinisnake.ui.theme.NeonBlue
import com.iisongokuii.djoudinisnake.ui.theme.NeonPurple

@Composable
fun MainMenuScreen(onPlayClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Djoudini's",
            color = NeonPurple,
            fontSize = 42.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "CHALLENGE",
            color = NeonBlue,
            fontSize = 32.sp,
            fontWeight = FontWeight.Light,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(80.dp))

        MenuButton("ENTRANCE (PLAY)", NeonPurple, onPlayClick)
        Spacer(modifier = Modifier.height(16.dp))
        MenuButton("SECRETS (COMING SOON)", Color.DarkGray, {})
        Spacer(modifier = Modifier.height(16.dp))
        MenuButton("ILLUSIONS (SETTINGS)", Color.DarkGray, {})
    }
}

@Composable
fun MenuButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.8f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(56.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}
