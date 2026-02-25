package com.iisongokuii.djoudinisnake

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.iisongokuii.djoudinisnake.game.GameMode
import com.iisongokuii.djoudinisnake.game.GameScreen
import com.iisongokuii.djoudinisnake.ui.screens.MainMenuScreen
import com.iisongokuii.djoudinisnake.ui.screens.ModeSelectionScreen
import com.iisongokuii.djoudinisnake.ui.screens.LeaderboardScreen
import com.iisongokuii.djoudinisnake.ui.screens.SettingsScreen
import com.iisongokuii.djoudinisnake.ui.theme.DjoudinisSnakeTheme

sealed class Screen {
    object Menu : Screen()
    object ModeSelection : Screen()
    object Leaderboard : Screen()
    object Settings : Screen()
    data class Game(val mode: GameMode) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Vollbild-Setup (Edge-to-Edge)
        setContent {
            DjoudinisSnakeTheme {
                DjoudiniApp()
            }
        }
    }
}

@Composable
fun DjoudiniApp() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Menu) }

    when (val screen = currentScreen) {
        is Screen.Menu -> MainMenuScreen(
            onPlayClick = { currentScreen = Screen.ModeSelection },
            onLeaderboardClick = { currentScreen = Screen.Leaderboard },
            onSettingsClick = { currentScreen = Screen.Settings }
        )
        is Screen.ModeSelection -> ModeSelectionScreen(
            onModeSelected = { selectedMode -> currentScreen = Screen.Game(selectedMode) },
            onBack = { currentScreen = Screen.Menu }
        )
        is Screen.Leaderboard -> LeaderboardScreen(
            onBack = { currentScreen = Screen.Menu }
        )
        is Screen.Settings -> SettingsScreen(
            onBack = { currentScreen = Screen.Menu }
        )
        is Screen.Game -> GameScreen(
            mode = screen.mode,
            onBack = { currentScreen = Screen.Menu }
        )
    }
}
