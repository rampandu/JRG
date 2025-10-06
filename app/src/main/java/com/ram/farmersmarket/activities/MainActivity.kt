package com.ram.farmersmarket.activities

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {  // ← Changed from AppCompatActivity to Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(100, 100, 100, 100)
        }

        val tvStatus = TextView(this).apply {
            text = "✅ MainActivity is working!"
            textSize = 20f
        }

        val btnBack = Button(this).apply {
            text = "Back to Login"
            setOnClickListener {
                finish()
            }
        }

        layout.addView(tvStatus)
        layout.addView(btnBack)
        setContentView(layout)
    }
}