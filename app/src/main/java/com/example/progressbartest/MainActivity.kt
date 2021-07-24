package com.example.progressbartest

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    var value = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val s = GradientDrawable()
        s.shape = GradientDrawable.RECTANGLE
        s.cornerRadius = 20f
        s.setColor(Color.WHITE)


        findViewById<Button>(R.id.button).setOnClickListener {
            value = Random.nextInt(100)
            findViewById<RoundedHorizontalProgressBar>(R.id.cprogress).setProgress(value, true)
        }
    }
}