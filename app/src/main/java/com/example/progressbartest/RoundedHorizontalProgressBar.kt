package com.example.progressbartest

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator


class RoundedHorizontalProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private lateinit var animator: ValueAnimator
    private var animationDuration: Long = 0

    private val progressPaint: Paint = Paint()
    private val progressBgPaint: Paint = Paint()

    private var radius: Float = 0f
    private var center: Float = 0f

    private var compensateLowProgress: Boolean = true
    private var progress: Float = 0f

    init {
        val typedArray =
            getContext().theme.obtainStyledAttributes(
                attrs,
                R.styleable.RoundedHorizontalProgressBar,
                0,
                0
            )
        try {
            val bgColor = typedArray.getColor(R.styleable.RoundedHorizontalProgressBar_progressBackgroundColor, Color.BLACK)
            progressBgPaint.color = bgColor

            val progressColor = typedArray.getColor(R.styleable.RoundedHorizontalProgressBar_progressColor, Color.WHITE)
            progressPaint.color = progressColor

            animationDuration = typedArray.getInt(R.styleable.RoundedHorizontalProgressBar_animationDurationInMillis, 300).toLong()
            compensateLowProgress = typedArray.getBoolean(R.styleable.RoundedHorizontalProgressBar_compensateWhenProgressIsLow, false)

            progress = typedArray.getInt(R.styleable.RoundedHorizontalProgressBar_progress, 0).toFloat()
        } finally {
            typedArray.recycle()
        }

        progressBgPaint.strokeCap = Paint.Cap.ROUND
        progressBgPaint.isAntiAlias = true

        progressPaint.strokeCap = Paint.Cap.ROUND
        progressPaint.isAntiAlias = true
        progressPaint.strokeWidth = 20f

        setProgress(progress)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        radius = min(width, height) / 2f
        center = height / 2f
        progressBgPaint.strokeWidth = 2 * radius

        canvas?.drawLine(radius, center, width - radius, center, progressBgPaint)

        if (compensateLowProgress) {
            val compensationRegion = radius
            val endPoint = (width - radius) * (progress / 100f)

            if (endPoint > compensationRegion) {
                progressPaint.strokeWidth = 2 * radius
                canvas?.drawLine(radius, center, endPoint, center, progressPaint)
            } else {
                progressPaint.strokeWidth = 2 * endPoint
                canvas?.drawLine(endPoint, center, endPoint, center, progressPaint)
            }
        } else {
            progressPaint.strokeWidth = 2 * radius
            val endPoint = radius + (width - 2 * radius) * (progress / 100f)
            canvas?.drawLine(radius, center, endPoint, center, progressPaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    fun setProgress(value: Int, animated: Boolean = true) {
        setProgress(clamp(value,0,100).toFloat(), animated)
    }

    private fun setProgress(value: Float, animated: Boolean = true) {
        val clampedValue = clamp(value, 0f, 100f)

        if (animated) {
            animator = ValueAnimator.ofFloat(progress, clampedValue)
            animator.duration = animationDuration
            animator.interpolator = DecelerateInterpolator()
            animator.addUpdateListener { animation ->
                val interpolation = animation.animatedValue as Float
                setProgress(interpolation, false)
            }

            if (!animator.isStarted) {
                animator.start()
            }
        } else {
            progress = clampedValue
            postInvalidate()
        }
    }

    private fun min(a: Int, b: Int): Int {
        return (if (a < b) return a else b)
    }

    private fun clamp(value: Int, low: Int, high: Int): Int {
        return (if (value < low) low else (if (value > high) high else value))
    }
    private fun clamp(value: Float, low: Float, high: Float): Float {
        return (if (value < low) low else (if (value > high) high else value))
    }


    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putInt("progress", progress.toInt())
        bundle.putParcelable("superState", super.onSaveInstanceState())
        return bundle
    }
    override fun onRestoreInstanceState(state: Parcelable?) {
        var state = state
        if (state is Bundle) {
            val bundle = state
            setProgress(bundle.getInt("progress"))
            state = bundle.getParcelable("superState")
        }
        super.onRestoreInstanceState(state)
    }
}