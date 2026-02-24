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
    val gridHeight: Int = 30
) {
    var snake by mutableStateOf(listOf(Position(10, 15), Position(10, 16), Position(10, 17)))
        private set

    var food by mutableStateOf(generateFood())
        private set

    var currentDirection by mutableStateOf(Direction.UP)
    var isGameOver by mutableStateOf(false)
        private set
    var score by mutableStateOf(0)
        private set

    private var nextDirection = currentDirection

    fun changeDirection(newDirection: Direction) {
        // Verhindern, dass die Schlange direkt in sich selbst umkehrt (180 Grad Drehung)
        if (!isGameOver && isOrthogonal(currentDirection, newDirection)) {
            nextDirection = newDirection
        }
    }

    private fun isOrthogonal(dir1: Direction, dir2: Direction): Boolean {
        return when (dir1) {
            Direction.UP, Direction.DOWN -> dir2 == Direction.LEFT || dir2 == Direction.RIGHT
            Direction.LEFT, Direction.RIGHT -> dir2 == Direction.UP || dir2 == Direction.DOWN
        }
    }

    suspend fun startGameLoop(onFoodEaten: () -> Unit, onGameOver: () -> Unit) {
        isGameOver = false
        score = 0
        snake = listOf(Position(10, 15), Position(10, 16), Position(10, 17))
        currentDirection = Direction.UP
        nextDirection = Direction.UP
        food = generateFood()

        while (!isGameOver) {
            // Speed wird dynamisch erhöht je nach Score für mehr "Challenge"
            val speedDelay = maxOf(50L, 200L - (score * 2L))
            delay(speedDelay)
            move(onFoodEaten, onGameOver)
        }
    }

    private fun move(onFoodEaten: () -> Unit, onGameOver: () -> Unit) {
        currentDirection = nextDirection
        val head = snake.first()
        val nextHead = when (currentDirection) {
            Direction.UP -> Position(head.x, head.y - 1)
            Direction.DOWN -> Position(head.x, head.y + 1)
            Direction.LEFT -> Position(head.x - 1, head.y)
            Direction.RIGHT -> Position(head.x + 1, head.y)
        }

        // Kollision mit den Wänden
        if (nextHead.x < 0 || nextHead.x >= gridWidth || nextHead.y < 0 || nextHead.y >= gridHeight) {
            isGameOver = true
            onGameOver()
            return
        }

        // Kollision mit sich selbst (außer dem letzten Segment, das sich im selben Tick wegbewegt)
        if (snake.dropLast(1).contains(nextHead)) {
            isGameOver = true
            onGameOver()
            return
        }

        val newSnake = snake.toMutableList()
        newSnake.add(0, nextHead)

        // Hat die Schlange das Futter gefressen?
        if (nextHead == food) {
            score += 10
            food = generateFood()
            onFoodEaten()
            // Schlange wächst (Tail wird nicht entfernt)
        } else {
            // Normale Bewegung (Tail rückt nach)
            newSnake.removeLast()
        }

        snake = newSnake
    }

    private fun generateFood(): Position {
        var newFood: Position
        do {
            newFood = Position(Random.nextInt(gridWidth), Random.nextInt(gridHeight))
        } while (snake.contains(newFood))
        return newFood
    }
}
