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
        ),
        label = "PulseAnimation"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cellWidth = size.width / gameState.gridWidth
        val cellHeight = size.height / gameState.gridHeight

        // Subtiles Neon Grid im Hintergrund
        for (i in 0..gameState.gridWidth) {
            drawLine(
                color = Color.White.copy(alpha = 0.05f),
                start = Offset(i * cellWidth, 0f),
                end = Offset(i * cellWidth, size.height),
                strokeWidth = 1f
            )
        }
        for (i in 0..gameState.gridHeight) {
            drawLine(
                color = Color.White.copy(alpha = 0.05f),
                start = Offset(0f, i * cellHeight),
                end = Offset(size.width, i * cellHeight),
                strokeWidth = 1f
            )
        }

        // Wände / Obstacles (Neon Style)
        gameState.obstacles.forEach { pos ->
            drawRoundRect(
                color = Color.DarkGray,
                topLeft = Offset(pos.x * cellWidth + 2f, pos.y * cellHeight + 2f),
                size = Size(cellWidth - 4f, cellHeight - 4f),
                cornerRadius = CornerRadius(8f, 8f),
                style = Stroke(width = 4f)
            )
            drawRoundRect(
                color = Color.Black.copy(alpha = 0.5f),
                topLeft = Offset(pos.x * cellWidth + 6f, pos.y * cellHeight + 6f),
                size = Size(cellWidth - 12f, cellHeight - 12f),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }

        // Futter (Glühende Seele)
        val padding = cellWidth * 0.2f
        drawCircle(
            color = NeonGreen.copy(alpha = 0.3f), // Glow
            radius = (cellWidth * 0.6f) * pulseScale,
            center = Offset(gameState.food.x * cellWidth + cellWidth / 2, gameState.food.y * cellHeight + cellHeight / 2)
        )
        drawCircle(
            color = NeonGreen,
            radius = (cellWidth * 0.35f) * pulseScale,
            center = Offset(gameState.food.x * cellWidth + cellWidth / 2, gameState.food.y * cellHeight + cellHeight / 2)
        )
        
        // Trick Item
        gameState.trickItem?.let { trick ->
            drawRoundRect(
                color = trick.type.color,
                topLeft = Offset(trick.position.x * cellWidth + padding, trick.position.y * cellHeight + padding),
                size = Size(cellWidth - padding * 2, cellHeight - padding * 2),
                style = Stroke(width = 6f),
                cornerRadius = CornerRadius(8f, 8f)
            )
        }

        // Moderne durchgehende Neon-Schlange
        val isGhost = gameState.activeTrick?.type == TrickType.GHOST
        val baseColor = if (isGhost) TrickType.GHOST.color else gameState.mode.color
        val snakeColor = baseColor.copy(alpha = if (isGhost) 0.4f else 1.0f)
        
        if (gameState.snake.isNotEmpty()) {
            val path = androidx.compose.ui.graphics.Path()
            val first = gameState.snake.first()
            path.moveTo(first.x * cellWidth + cellWidth / 2, first.y * cellHeight + cellHeight / 2)
            
            for (i in 1 until gameState.snake.size) {
                val pos = gameState.snake[i]
                path.lineTo(pos.x * cellWidth + cellWidth / 2, pos.y * cellHeight + cellHeight / 2)
            }
            
            // Äußerer Glow
            drawPath(
                path = path,
                color = snakeColor.copy(alpha = 0.3f),
                style = Stroke(width = cellWidth * 0.9f, cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
            )
            // Innerer Kern
            drawPath(
                path = path,
                color = snakeColor,
                style = Stroke(width = cellWidth * 0.6f, cap = androidx.compose.ui.graphics.StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
            )

            // Schlangenkopf (Heller und mit "Augen")
            val head = gameState.snake.first()
            val headX = head.x * cellWidth + cellWidth / 2
            val headY = head.y * cellHeight + cellHeight / 2
            
            drawCircle(color = Color.White.copy(alpha = if (isGhost) 0.6f else 1.0f), radius = cellWidth * 0.35f, center = Offset(headX, headY))
            
            // Augen-Ausrichtung basierend auf Richtung
            val eyeOffset1 = when (gameState.currentDirection) {
                Direction.UP -> Offset(-cellWidth * 0.15f, -cellHeight * 0.15f)
                Direction.DOWN -> Offset(cellWidth * 0.15f, cellHeight * 0.15f)
                Direction.LEFT -> Offset(-cellWidth * 0.15f, -cellHeight * 0.15f)
                Direction.RIGHT -> Offset(cellWidth * 0.15f, -cellHeight * 0.15f)
            }
            val eyeOffset2 = when (gameState.currentDirection) {
                Direction.UP -> Offset(cellWidth * 0.15f, -cellHeight * 0.15f)
                Direction.DOWN -> Offset(-cellWidth * 0.15f, cellHeight * 0.15f)
                Direction.LEFT -> Offset(-cellWidth * 0.15f, cellHeight * 0.15f)
                Direction.RIGHT -> Offset(cellWidth * 0.15f, cellHeight * 0.15f)
            }
            
            drawCircle(color = DeepSpace, radius = cellWidth * 0.1f, center = Offset(headX + eyeOffset1.x, headY + eyeOffset1.y))
            drawCircle(color = DeepSpace, radius = cellWidth * 0.1f, center = Offset(headX + eyeOffset2.x, headY + eyeOffset2.y))
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
