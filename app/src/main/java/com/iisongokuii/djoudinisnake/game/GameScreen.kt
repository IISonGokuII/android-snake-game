package com.iisongokuii.djoudinisnake.game

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iisongokuii.djoudinisnake.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun GameScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val gameState = remember { GameState() }

    // Haptisches Feedback Helper
    val triggerVibration = { duration: Long, intensity: Int ->
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, intensity))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(duration)
            }
        }
    }

    // Startet das Spiel direkt oder wenn man auf "Restart" klickt
    LaunchedEffect(Unit) {
        gameState.startGameLoop(
            onFoodEaten = {
                // Kurzes, knackiges Vibrieren wie beim Tippen auf einer Premium-Tastatur
                triggerVibration(20L, 100) 
            },
            onGameOver = {
                // Schweres, langes Rumpeln bei Game Over
                triggerVibration(400L, VibrationEffect.DEFAULT_AMPLITUDE)
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
            .pointerInput(Unit) {
                // Die Swipe-Erkennung: Perfekt für große Touchscreens
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val (x, y) = dragAmount
                    if (abs(x) > abs(y)) {
                        if (x > 0) gameState.changeDirection(Direction.RIGHT)
                        else gameState.changeDirection(Direction.LEFT)
                    } else {
                        if (y > 0) gameState.changeDirection(Direction.DOWN)
                        else gameState.changeDirection(Direction.UP)
                    }
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Score-Header (Modern und clean)
        Spacer(modifier = Modifier.height(48.dp)) // Platz für Status Bar
        Text(
            text = "Score: ${gameState.score}",
            color = NeonBlue,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Das magische Spielfeld
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(DarkGrey)
                .fillMaxWidth()
        ) {
            if (gameState.isGameOver) {
                GameOverOverlay(score = gameState.score) {
                    coroutineScope.launch {
                        gameState.startGameLoop(
                            onFoodEaten = { triggerVibration(20L, 100) },
                            onGameOver = { triggerVibration(400L, VibrationEffect.DEFAULT_AMPLITUDE) }
                        )
                    }
                }
            } else {
                GameBoard(gameState = gameState)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun GameBoard(gameState: GameState) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val cellWidth = size.width / gameState.gridWidth
        val cellHeight = size.height / gameState.gridHeight

        // Zeichne Futter (Seelen-Sphäre)
        drawFood(gameState.food, cellWidth, cellHeight)

        // Zeichne die Schlange
        gameState.snake.forEachIndexed { index, position ->
            val isHead = index == 0
            drawSnakeSegment(position, cellWidth, cellHeight, isHead)
        }
    }
}

private fun DrawScope.drawFood(food: Position, cellWidth: Float, cellHeight: Float) {
    val padding = cellWidth * 0.2f
    drawRoundRect(
        color = NeonGreen,
        topLeft = Offset(food.x * cellWidth + padding, food.y * cellHeight + padding),
        size = Size(cellWidth - padding * 2, cellHeight - padding * 2),
        cornerRadius = CornerRadius(50f, 50f) // Perfekter Kreis
    )
}

private fun DrawScope.drawSnakeSegment(position: Position, cellWidth: Float, cellHeight: Float, isHead: Boolean) {
    val padding = if (isHead) cellWidth * 0.1f else cellWidth * 0.15f
    val color = if (isHead) NeonBlue else NeonPurple
    
    drawRoundRect(
        color = color,
        topLeft = Offset(position.x * cellWidth + padding, position.y * cellHeight + padding),
        size = Size(cellWidth - padding * 2, cellHeight - padding * 2),
        cornerRadius = CornerRadius(16f, 16f) // Leichte Apple-Style Abrundungen
    )
}

@Composable
fun GameOverOverlay(score: Int, onRestart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("ILLUSION SHATTERED", color = BloodRed, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Final Score: $score", color = Color.White, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
            ) {
                Text("Try Again", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
