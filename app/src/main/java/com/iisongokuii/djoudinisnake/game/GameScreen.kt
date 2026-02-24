package com.iisongokuii.djoudinisnake.game

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iisongokuii.djoudinisnake.data.GamePrefs
import com.iisongokuii.djoudinisnake.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun GameScreen(mode: GameMode, onBack: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { GamePrefs(context) }
    val coroutineScope = rememberCoroutineScope()
    val gameState = remember(mode) { GameState(mode = mode) }

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

    LaunchedEffect(gameState) {
        gameState.startGameLoop(
            onFoodEaten = { triggerVibration(15L, 80) },
            onTrickPickedUp = { triggerVibration(50L, 255) },
            onGameOver = {
                triggerVibration(400L, VibrationEffect.DEFAULT_AMPLITUDE)
                prefs.saveHighScore(mode.name, gameState.score)
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val (x, y) = dragAmount
                    // Schwelle für Wischen, um unbeabsichtigte Taps zu ignorieren
                    if (abs(x) > 20 || abs(y) > 20) {
                         if (abs(x) > abs(y)) {
                             if (x > 0) gameState.changeDirection(Direction.RIGHT)
                             else gameState.changeDirection(Direction.LEFT)
                         } else {
                             if (y > 0) gameState.changeDirection(Direction.DOWN)
                             else gameState.changeDirection(Direction.UP)
                         }
                    }
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = mode.displayName, color = mode.color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = "Score: ${gameState.score}", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }
        
        // Active Trick Indicator
        Box(modifier = Modifier.height(24.dp).padding(top = 4.dp)) {
            if (gameState.activeTrick != null) {
                val trick = gameState.activeTrick!!
                val remaining = maxOf(0L, trick.endTime - System.currentTimeMillis())
                Text(
                    text = "${trick.type.name} ACTIVE: ${remaining/1000}s", 
                    color = trick.type.color, 
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Spielfeld
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkGrey)
                .fillMaxWidth()
        ) {
            if (gameState.isGameOver) {
                GameOverOverlay(
                    score = gameState.score, 
                    highscore = prefs.getHighScore(mode.name),
                    onRestart = {
                        coroutineScope.launch {
                            gameState.startGameLoop(
                                onFoodEaten = { triggerVibration(15L, 80) },
                                onTrickPickedUp = { triggerVibration(50L, 255) },
                                onGameOver = {
                                    triggerVibration(400L, VibrationEffect.DEFAULT_AMPLITUDE)
                                    prefs.saveHighScore(mode.name, gameState.score)
                                }
                            )
                        }
                    },
                    onBack = onBack
                )
            } else {
                GameBoard(gameState = gameState)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun GameBoard(gameState: GameState) {
    // Pulsierender Effekt für Food
    val infiniteTransition = rememberInfiniteTransition()
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cellWidth = size.width / gameState.gridWidth
        val cellHeight = size.height / gameState.gridHeight

        // Wände / Obstacles
        gameState.obstacles.forEach { pos ->
            drawRoundRect(
                color = Color.DarkGray,
                topLeft = Offset(pos.x * cellWidth, pos.y * cellHeight),
                size = Size(cellWidth, cellHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }

        // Futter
        val padding = cellWidth * 0.2f
        drawRoundRect(
            color = NeonGreen,
            topLeft = Offset(gameState.food.x * cellWidth + padding, gameState.food.y * cellHeight + padding),
            size = Size((cellWidth - padding * 2) * pulseScale, (cellHeight - padding * 2) * pulseScale),
            cornerRadius = CornerRadius(50f, 50f)
        )
        
        // Trick Item
        gameState.trickItem?.let { trick ->
            drawRoundRect(
                color = trick.type.color,
                topLeft = Offset(trick.position.x * cellWidth + padding, trick.position.y * cellHeight + padding),
                size = Size(cellWidth - padding * 2, cellHeight - padding * 2),
                style = Stroke(width = 4f)
            )
        }

        // Schlange
        val isGhost = gameState.activeTrick?.type == TrickType.GHOST
        val snakeColor = if (isGhost) TrickType.GHOST.color else gameState.mode.color
        val opacity = if (isGhost) 0.5f else 1.0f
        
        gameState.snake.forEachIndexed { index, position ->
            val isHead = index == 0
            val p = if (isHead) cellWidth * 0.1f else cellWidth * 0.15f
            
            drawRoundRect(
                color = if (isHead) Color.White.copy(alpha = opacity) else snakeColor.copy(alpha = opacity),
                topLeft = Offset(position.x * cellWidth + p, position.y * cellHeight + p),
                size = Size(cellWidth - p * 2, cellHeight - p * 2),
                cornerRadius = CornerRadius(12f, 12f)
            )
        }
    }
}

@Composable
fun GameOverOverlay(score: Int, highscore: Int, onRestart: () -> Unit, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("ILLUSION SHATTERED", color = BloodRed, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Score: $score", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            if (score >= highscore && score > 0) {
                 Text("NEW HIGHSCORE!", color = NeonGreen, fontSize = 18.sp, fontWeight = FontWeight.Black)
            } else {
                 Text("Highscore: $highscore", color = Color.Gray, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onRestart,
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                modifier = Modifier.fillMaxWidth(0.6f).height(50.dp)
            ) {
                Text("Try Again", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onBack,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                modifier = Modifier.fillMaxWidth(0.6f).height(50.dp)
            ) {
                Text("Back to Menu")
            }
        }
    }
}
