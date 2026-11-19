package com.example.expensenote_2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.random.Random

enum class Direction { UP, DOWN, LEFT, RIGHT }

class SnakeGameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val gridWidth = 20
    private val gridHeight = 20

    private val snake = mutableListOf<Point>()
    private var apple = Point(5, 5)
    private var currentDirection = Direction.RIGHT
    var isGameOver = false
        private set
    private var score = 0

    private val snakePaint = Paint().apply {
        color = Color.parseColor("#00E676") // Neon Green Body
        style = Paint.Style.FILL
    }
    private val snakeHeadPaint = Paint().apply {
        color = Color.parseColor("#FFD700") // Gold Head
        style = Paint.Style.FILL
    }
    private val applePaint = Paint().apply {
        color = Color.parseColor("#FF1744") // Red Apple
        style = Paint.Style.FILL
    }
    private val bgPaint = Paint().apply {
        color = Color.parseColor("#1D1B2A") // Dark Card BG
        style = Paint.Style.FILL
    }

    private val handler = Handler(Looper.getMainLooper())
    private val gameRunnable = object : Runnable {
        override fun run() {
            if (!isGameOver) {
                updateGame()
                invalidate()
                handler.postDelayed(this, 180)
            }
        }
    }

    var onScoreChangeListener: ((Int) -> Unit)? = null
    var onGameOverListener: ((Int) -> Unit)? = null

    private var startX = 0f
    private var startY = 0f

    init {
        resetGame()
    }

    fun resetGame() {
        snake.clear()
        snake.add(Point(10, 10))
        snake.add(Point(9, 10))
        snake.add(Point(8, 10))
        currentDirection = Direction.RIGHT
        isGameOver = false
        score = 0
        spawnApple()
        onScoreChangeListener?.invoke(score)
        handler.removeCallbacks(gameRunnable)
        handler.post(gameRunnable)
    }

    fun changeDirection(newDir: Direction) {
        if (isGameOver) return
        if ((currentDirection == Direction.UP && newDir != Direction.DOWN) ||
            (currentDirection == Direction.DOWN && newDir != Direction.UP) ||
            (currentDirection == Direction.LEFT && newDir != Direction.RIGHT) ||
            (currentDirection == Direction.RIGHT && newDir != Direction.LEFT)
        ) {
            currentDirection = newDir
        }
    }

    private fun spawnApple() {
        while (true) {
            val rx = Random.nextInt(gridWidth)
            val ry = Random.nextInt(gridHeight)
            val p = Point(rx, ry)
            if (!snake.contains(p)) {
                apple = p
                break
            }
        }
    }

    private fun updateGame() {
        val head = snake[0]
        val newHead = when (currentDirection) {
            Direction.UP -> Point(head.x, head.y - 1)
            Direction.DOWN -> Point(head.x, head.y + 1)
            Direction.LEFT -> Point(head.x - 1, head.y)
            Direction.RIGHT -> Point(head.x + 1, head.y)
        }

        // Wall collision
        if (newHead.x < 0 || newHead.x >= gridWidth || newHead.y < 0 || newHead.y >= gridHeight) {
            isGameOver = true
            onGameOverListener?.invoke(score)
            return
        }

        // Self collision
        if (snake.contains(newHead)) {
            isGameOver = true
            onGameOverListener?.invoke(score)
            return
        }

        snake.add(0, newHead)

        // Eat Apple
        if (newHead == apple) {
            score += 10
            onScoreChangeListener?.invoke(score)
            spawnApple()
        } else {
            snake.removeAt(snake.size - 1)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width == 0 || height == 0) return

        val cellSize = width.coerceAtMost(height) / gridWidth.toFloat()

        // Background
        canvas.drawRect(0f, 0f, cellSize * gridWidth, cellSize * gridHeight, bgPaint)

        // Apple
        val appleX = apple.x * cellSize + cellSize / 2f
        val appleY = apple.y * cellSize + cellSize / 2f
        canvas.drawCircle(appleX, appleY, cellSize / 2f - 4f, applePaint)

        // Snake Body
        for (i in snake.indices) {
            val p = snake[i]
            val left = p.x * cellSize + 2f
            val top = p.y * cellSize + 2f
            val right = (p.x + 1) * cellSize - 2f
            val bottom = (p.y + 1) * cellSize - 2f
            val paint = if (i == 0) snakeHeadPaint else snakePaint
            canvas.drawRoundRect(left, top, right, bottom, 8f, 8f, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                return true
            }
            MotionEvent.ACTION_UP -> {
                performClick()
                val deltaX = event.x - startX
                val deltaY = event.y - startY

                if (abs(deltaX) > abs(deltaY)) {
                    if (abs(deltaX) > 40) {
                        if (deltaX > 0) changeDirection(Direction.RIGHT)
                        else changeDirection(Direction.LEFT)
                    }
                } else {
                    if (abs(deltaY) > 40) {
                        if (deltaY > 0) changeDirection(Direction.DOWN)
                        else changeDirection(Direction.UP)
                    }
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun pauseGame() {
        handler.removeCallbacks(gameRunnable)
    }

    fun resumeGame() {
        if (!isGameOver) {
            handler.removeCallbacks(gameRunnable)
            handler.post(gameRunnable)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        pauseGame()
    }
}
