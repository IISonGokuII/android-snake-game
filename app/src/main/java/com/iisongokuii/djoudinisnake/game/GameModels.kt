package com.iisongokuii.djoudinisnake.game

import androidx.compose.ui.graphics.Color
import com.iisongokuii.djoudinisnake.ui.theme.NeonBlue
import com.iisongokuii.djoudinisnake.ui.theme.NeonGreen
import com.iisongokuii.djoudinisnake.ui.theme.NeonPurple

enum class GameMode(val displayName: String, val description: String, val color: Color) {
    CLASSIC("The Classic Act", "Reines Snake-Erlebnis.", NeonBlue),
    SPEED("Phantom Speed", "Es wird verdammt schnell.", NeonPurple),
    MAZE("Illusionist's Maze", "Vorsicht vor den sich bildenden Wänden.", NeonGreen),
    TRIAL("Djoudini's Trial", "Hindernisse und extreme Geschwindigkeit.", Color(0xFFFF1744))
}

enum class TrickType(val color: Color, val durationMs: Long) {
    GHOST(Color(0xAAFFFFFF), 5000L), // Ghost: Transparent, kann durch sich selbst gleiten
    MIRROR(Color(0xFFFFD700), 10000L) // Mirror: Invertierte Steuerung, aber 3x Punkte!
}

data class ActiveTrick(val type: TrickType, val endTime: Long)

data class TrickItem(val position: Position, val type: TrickType)
