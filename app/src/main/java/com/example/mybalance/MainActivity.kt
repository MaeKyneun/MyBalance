package com.example.mybalance // Replace with your package name
import android.content.Context
import android.os.Bundle
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class LetterManager(private val context: Context) {
    private val letters = listOf("П", "О", "Н", "О", "М", "А", "Р", "Ч", "У", "К")
    private var currentIndex = 0

    fun createLetter(x: Int, y: Int) {
        val currentLetter = letters[currentIndex]
        currentIndex = (currentIndex + 1) % letters.size

        val textView = TextView(context).apply {
            text = currentLetter
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            // Удаляем вызов setBackgroundResource
            // setBackgroundResource(android.R.drawable.btn_default)

            layoutParams = FrameLayout.LayoutParams(50, 50).apply {
                leftMargin = x // Use leftMargin and topMargin
                topMargin = y
            }
        }

        val mainLayout = (context as? AppCompatActivity)?.findViewById<FrameLayout>(R.id.main_layout)
        mainLayout?.addView(textView)

        animateLetter(textView) // Directly call animateLetter, no need for Handler.postDelayed
    }

    private fun animateLetter(letter: TextView) {
        var velocity = 70f // Начальная скорость падения
        val gravity = 5f // Ускорение свободного падения
        val bounceFactor = 0.8f // Коэффициент уменьшения скорости после отскока
        val moveRightOffset = 5 // Смещение вправо при каждом прыжке

        val handler = Handler(Looper.getMainLooper())
        val screenHeight = letter.rootView.height

        val runnable = object : Runnable {
            override fun run() {
                val newY = letter.translationY + velocity
                val newX = letter.translationX + moveRightOffset

                letter.translationY = newY
                letter.translationX = newX

                if (newY >= screenHeight - letter.height) {
                    // Объект достиг нижней границы экрана, начинаем движение вверх
                    velocity = -velocity * bounceFactor
                } else {
                    // Продолжаем падение под действием гравитации
                    velocity += gravity
                }

                if (letter.translationY <= 0 && velocity < 0) {
                    // Если объект поднялся выше верхней границы экрана, возвращаемся к начальной скорости
                    velocity = 70f
                }

                handler.postDelayed(this, 20)
            }
        }
        handler.post(runnable)
    }
}

class MainActivity : AppCompatActivity() {
    private lateinit var letterManager: LetterManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        letterManager = LetterManager(this)

        findViewById<FrameLayout>(R.id.main_layout).setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val x = event.x.toInt()
                val y = event.y.toInt()
                letterManager.createLetter(x, y)
            }
            true
        }
    }
}