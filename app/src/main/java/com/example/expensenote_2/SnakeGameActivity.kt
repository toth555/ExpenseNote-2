package com.example.expensenote_2

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class SnakeGameActivity : AppCompatActivity() {

    private lateinit var snakeGameView: SnakeGameView
    private lateinit var tvScore: TextView
    private var currentScore = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snake_game)

        snakeGameView = findViewById(R.id.snakeGameView)
        tvScore = findViewById(R.id.tvScore)

        val btnBack = findViewById<Button>(R.id.btnBack)
        val btnUp = findViewById<Button>(R.id.btnUp)
        val btnDown = findViewById<Button>(R.id.btnDown)
        val btnLeft = findViewById<Button>(R.id.btnLeft)
        val btnRight = findViewById<Button>(R.id.btnRight)
        val btnRestart = findViewById<Button>(R.id.btnRestart)
        val btnClaim = findViewById<Button>(R.id.btnClaim)

        // ปุ่มควบคุมทิศทาง
        btnUp.setOnClickListener { snakeGameView.changeDirection(Direction.UP) }
        btnDown.setOnClickListener { snakeGameView.changeDirection(Direction.DOWN) }
        btnLeft.setOnClickListener { snakeGameView.changeDirection(Direction.LEFT) }
        btnRight.setOnClickListener { snakeGameView.changeDirection(Direction.RIGHT) }

        // ฟังคะแนนและเกมจบ
        snakeGameView.onScoreChangeListener = { score ->
            currentScore = score
            tvScore.text = String.format(Locale.getDefault(), "คะแนนสะสม: %d บาท 🍎", score)
        }

        snakeGameView.onGameOverListener = { finalScore ->
            currentScore = finalScore
            Toast.makeText(this, "GAME OVER! คะแนนรวมถอนเงินได้: $finalScore บาท 🍎", Toast.LENGTH_LONG).show()
        }

        // ปุ่มถอนเงินรางวัล
        btnClaim.setOnClickListener {
            if (currentScore <= 0) {
                Toast.makeText(this, "คุณยังไม่มีเงินรางวัล สะสมคะแนนด้วยการกินแอปเปิ้ลก่อนนะ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("expense_prefs", MODE_PRIVATE)
            val existingTotal = prefs.getFloat("total_amount", 0f).toDouble()
            val newTotal = existingTotal + currentScore.toDouble()

            prefs.edit()
                .putFloat("total_amount", newTotal.toFloat())
                .putString("last_note", "รางวัลจากเกมงูกินแอปเปิ้ล 🐍🍎")
                .putFloat("last_amount", currentScore.toFloat())
                .apply()

            Toast.makeText(this, "🎉 ถอนเงินรางวัล $currentScore บาท เข้ากระเป๋าเรียบร้อยแล้ว!", Toast.LENGTH_LONG).show()

            // รีเซ็ตเกมหลังจากถอนเงิน
            currentScore = 0
            snakeGameView.resetGame()
        }

        btnRestart.setOnClickListener {
            currentScore = 0
            snakeGameView.resetGame()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        snakeGameView.pauseGame()
    }

    override fun onResume() {
        super.onResume()
        snakeGameView.resumeGame()
    }
}
