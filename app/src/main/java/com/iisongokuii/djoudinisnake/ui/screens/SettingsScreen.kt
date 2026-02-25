package com.iisongokuii.djoudinisnake.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iisongokuii.djoudinisnake.data.GamePrefs
import com.iisongokuii.djoudinisnake.ui.theme.DarkGrey
import com.iisongokuii.djoudinisnake.ui.theme.DeepSpace
import com.iisongokuii.djoudinisnake.ui.theme.NeonPurple

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { GamePrefs(context) }
    var isHapticsEnabled by remember { mutableStateOf(prefs.isHapticsEnabled()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ILLUSIONS (SETTINGS)",
            color = NeonPurple,
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(48.dp))

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
                    Text(text = "Haptic Feedback", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Vibration for modern feel", color = Color.LightGray, fontSize = 14.sp)
                }
                Switch(
                    checked = isHapticsEnabled,
                    onCheckedChange = { 
                        isHapticsEnabled = it
                        prefs.setHapticsEnabled(it)
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = NeonPurple, checkedTrackColor = NeonPurple.copy(alpha = 0.5f))
                )
            }
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
