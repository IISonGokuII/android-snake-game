package com.iisongokuii.djoudinisnake.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iisongokuii.djoudinisnake.data.GamePrefs
import com.iisongokuii.djoudinisnake.game.GameMode
import com.iisongokuii.djoudinisnake.ui.theme.DarkGrey
import com.iisongokuii.djoudinisnake.ui.theme.DeepSpace
import com.iisongokuii.djoudinisnake.ui.theme.NeonBlue

@Composable
fun LeaderboardScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { GamePrefs(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "HALL OF FAME",
            color = NeonBlue,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(32.dp))

        GameMode.values().forEach { mode ->
            val score = prefs.getHighScore(mode.name)
            val name = prefs.getHighScoreName(mode.name)
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkGrey)
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = mode.displayName, color = mode.color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(text = name, color = Color.LightGray, fontSize = 16.sp)
                    }
                    Text(text = "$score", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "< BACK TO MENU",
            color = Color.Gray,
            fontSize = 18.sp,
            modifier = Modifier.clickable { onBack() }.padding(16.dp)
        )
    }
}
