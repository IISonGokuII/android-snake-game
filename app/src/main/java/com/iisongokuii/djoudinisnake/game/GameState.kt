package com.iisongokuii.djoudinisnake.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class Direction { UP, DOWN, LEFT, RIGHT }

data class Position(val x: Int, val y: Int)

class GameState(
    val gridWidth: Int = 20,
    val gridHeight: Int = 30,
    val mode: GameMode = GameMode.CLASSIC
) {
    var snake by mutableStateOf(listOf(Position(10, 15), Position(10, 16), Position(10, 17)))
        private set

    var food by mutableStateOf(Position(-1, -1))
        private set
        
    var trickItem by mutableStateOf<TrickItem?>(null)
        private set
        
    var activeTrick by mutableStateOf<ActiveTrick?>(null)
        private set
        
    var obstacles by mutableStateOf(listOf<Position>())
        private set

    var currentDirection by mutableStateOf(Direction.UP)
    var isGameOver by mutableStateOf(false)
        private set
    var score by mutableStateOf(0)
        private set

    private var nextDirection = currentDirection

    init {
        food = generateFreePosition()
        if (mode == GameMode.MAZE || mode == GameMode.TRIAL) {
            generateObstacles()
        }
    }

    fun changeDirection(newDirection: Direction) {
        val mappedDirection = if (activeTrick?.type == TrickType.MIRROR) {
            // Mirror Trick invertiert die Steuerung!
            when (newDirection) {
                Direction.UP -> Direction.DOWN
                Direction.DOWN -> Direction.UP
                Direction.LEFT -> Direction.RIGHT
                Direction.RIGHT -> Direction.LEFT
            }
        } else newDirection

        if (!isGameOver && isOrthogonal(currentDirection, mappedDirection)) {
            nextDirection = mappedDirection
        }
    }

    private fun isOrthogonal(dir1: Direction, dir2: Direction): Boolean {
        return when (dir1) {
            Direction.UP, Direction.DOWN -> dir2 == Direction.LEFT || dir2 == Direction.RIGHT
            Direction.LEFT, Direction.RIGHT -> dir2 == Direction.UP || dir2 == Direction.DOWN
        }
    }

    suspend fun startGameLoop(onFoodEaten: () -> Unit, onTrickPickedUp: () -> Unit, onGameOver: () -> Unit) {
        isGameOver = false
        score = 0
        snake = listOf(Position(10, 15), Position(10, 16), Position(10, 17))
        currentDirection = Direction.UP
        nextDirection = Direction.UP
        activeTrick = null
        trickItem = null
        food = generateFreePosition()
        if (mode == GameMode.MAZE || mode == GameMode.TRIAL) generateObstacles() else obstacles = emptyList()

        while (!isGameOver) {
            // Trick Ablauf checken
            activeTrick?.let {
                if (System.currentTimeMillis() > it.endTime) {
                    activeTrick = null
                }
            }

            // Speed Berechnen
            var baseSpeed = when (mode) {
                GameMode.CLASSIC -> 200L
                GameMode.SPEED -> 150L
                GameMode.MAZE -> 250L
                GameMode.TRIAL -> 180L
            }
            
            val speedDecrease = if (mode == GameMode.SPEED || mode == GameMode.TRIAL) (score * 3L) else (score * 1.5).toLong()
            val speedDelay = maxOf(40L, baseSpeed - speedDecrease)
            
            delay(speedDelay)
            move(onFoodEaten, onTrickPickedUp, onGameOver)
        }
    }

    private fun move(onFoodEaten: () -> Unit, onTrickPickedUp: () -> Unit, onGameOver: () -> Unit) {
        currentDirection = nextDirection
        val head = snake.first()
        val nextHead = when (currentDirection) {
            Direction.UP -> Position(head.x, head.y - 1)
            Direction.DOWN -> Position(head.x, head.y + 1)
            Direction.LEFT -> Position(head.x - 1, head.y)
            Direction.RIGHT -> Position(head.x + 1, head.y)
        }

        // Kollision: Wand
        if (nextHead.x < 0 || nextHead.x >= gridWidth || nextHead.y < 0 || nextHead.y >= gridHeight) {
            triggerGameOver(onGameOver)
            return
        }
        
        // Kollision: Hindernisse (Maze)
        if (obstacles.contains(nextHead)) {
            if (activeTrick?.type != TrickType.GHOST) {
                triggerGameOver(onGameOver)
                return
            }
        }

        // Kollision: Sich selbst (Ghost Trick ignoriert dies)
        if (snake.dropLast(1).contains(nextHead)) {
            if (activeTrick?.type != TrickType.GHOST) {
                 triggerGameOver(onGameOver)
                 return
            }
        }

        val newSnake = snake.toMutableList()
        newSnake.add(0, nextHead)

        // Futter gefressen?
        if (nextHead == food) {
            val multiplier = if (activeTrick?.type == TrickType.MIRROR) 3 else 1
            score += (10 * multiplier)
            food = generateFreePosition()
            onFoodEaten()
            
            // Zufälliger Trick Spawn (10% Chance, wenn keiner da und keiner aktiv)
            if (trickItem == null && activeTrick == null && Random.nextFloat() < 0.15f) {
                val trickType = if (Random.nextBoolean()) TrickType.GHOST else TrickType.MIRROR
                trickItem = TrickItem(generateFreePosition(), trickType)
            }
            
            // Im Trial Mode spawnen beim Fressen manchmal neue Hindernisse
            if (mode == GameMode.TRIAL && score % 50 == 0) {
                 val newObs = generateFreePosition()
                 obstacles = obstacles + newObs
            }
            
        } else if (trickItem != null && nextHead == trickItem!!.position) {
            // Trick eingesammelt!
            activeTrick = ActiveTrick(trickItem!!.type, System.currentTimeMillis() + trickItem!!.type.durationMs)
            trickItem = null
            onTrickPickedUp()
            newSnake.removeLast() // Wachsen nicht beim Trick
        } else {
            // Normale Bewegung
            newSnake.removeLast()
        }

        snake = newSnake
    }
    
    private fun triggerGameOver(onGameOver: () -> Unit) {
        isGameOver = true
        onGameOver()
    }

    private fun generateFreePosition(): Position {
        var newPos: Position
        var attempts = 0
        do {
            newPos = Position(Random.nextInt(gridWidth), Random.nextInt(gridHeight))
            attempts++
            if (attempts > 1000) break // Failsafe
        } while (snake.contains(newPos) || obstacles.contains(newPos) || trickItem?.position == newPos)
        return newPos
    }
    
    private fun generateObstacles() {
        val obsList = mutableListOf<Position>()
        val count = if (mode == GameMode.MAZE) 15 else 5
        for (i in 0 until count) {
            obsList.add(generateFreePosition())
        }
        obstacles = obsList
    }
}
