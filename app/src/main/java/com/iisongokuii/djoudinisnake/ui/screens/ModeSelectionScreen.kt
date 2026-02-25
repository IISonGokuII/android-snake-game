package com.iisongokuii.djoudinisnake.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iisongokuii.djoudinisnake.game.GameMode
import com.iisongokuii.djoudinisnake.ui.theme.DarkGrey
import com.iisongokuii.djoudinisnake.ui.theme.DeepSpace

@Composable
fun ModeSelectionScreen(onModeSelected: (GameMode) -> Unit, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "CHOOSE YOUR FATE",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(48.dp))

        GameMode.values().forEach { mode ->
            ModeCard(mode = mode, onClick = { onModeSelected(mode) })
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "< BACK",
            color = Color.Gray,
            fontSize = 18.sp,
            modifier = Modifier.clickable { onBack() }.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ModeCard(mode: GameMode, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkGrey)
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = mode.displayName,
                color = mode.color,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = mode.description,
                color = Color.LightGray,
                fontSize = 14.sp
            )
        }
    }
}
