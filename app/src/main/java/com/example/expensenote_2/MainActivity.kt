package com.example.expensenote_2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private  lateinit var etAmount: EditText
    private  lateinit var etNote: EditText
    private  lateinit var btnClear: Button
    private  lateinit var btnSave: Button
    private  lateinit var tvTotalAmount: TextView

    private  lateinit var tvLastItem: TextView


    private var total: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        etAmount = findViewById<EditText>(R.id.etAmount)
        etNote = findViewById<EditText>(R.id.etNote)
        btnClear = findViewById<Button>(R.id.btnClear)
        btnSave = findViewById<Button>(R.id.btnSave)
        tvLastItem = findViewById<TextView>(R.id.tvLastItem)
        tvTotalAmount = findViewById<TextView>(R.id.tvTotalAmount)

        btnSave.setOnClickListener {
            val amountText = etAmount.text.toString().trim()
            val noteText = etNote.text.toString().trim()

            if (amountText.isEmpty()){
                Toast.makeText(this, "กรุณากรอกจำนวนเงิน", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null){

                Toast.makeText(this, "กรุณากรอกเฉพาะตัวเลข ในช่องกรอกจำนวนเงิน", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
             }

            total += amount

            val showNote = if (noteText.isNotEmpty()) noteText else "ไม่ระบุ"

            tvLastItem.text = "รายการล่าสุด: $showNote - %.2f บาท".format(amount)
            tvTotalAmount.text = "ยอดรวมวันนี้: %.2f บาท".format(total)

            etAmount.text.clear()
            etNote.text.clear()
        }
        btnClear.setOnClickListener {
            total = 0.0

            etAmount.text.clear()
            etNote.text.clear()

            tvLastItem.text = "รายการล่าสุด: -บาท"
            tvTotalAmount.text = "ยอดรวมวันนี้: 0.00 บาท"
        }
    }
}
