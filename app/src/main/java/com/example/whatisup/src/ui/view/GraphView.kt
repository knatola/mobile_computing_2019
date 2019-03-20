package com.example.whatisup.src.ui.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.whatisup.R
import com.example.whatisup.src.data.model.DayActivity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v4.content.res.ResourcesCompat
import com.example.whatisup.src.utils.TimeUtils

private const val TAG = "GraphView"

class GraphView(context: Context, attrs: AttributeSet): View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val lineColor = context.getColor(R.color.colorAccent)
    private val backgroundColor = context.getColor(R.color.colorPrimary)
    private val dotColor = Color.WHITE
    private val borderColor = Color.WHITE
    private val borderWidth = 6.0f

    private var left = 0f
    private var top = 0f
    private var widthSize = 1000
    private var heightSize = 800
    private var emojiX = widthSize / 6 + 20
    private var dataSet: List<DayActivity> = listOf()
    private var dotPositions: MutableList<DotPosition> = mutableListOf()

    private val xInterval = (widthSize - emojiX) / 7
    private val yInterval = (heightSize - emojiX) / 4.6f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas)
        drawLines(canvas)
        drawDots(canvas)
        drawSmileys(canvas)
        drawXDescriptions(canvas)

        //drawGraph(canvas)
        //drawDots(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = 800
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthMeasure = MeasureSpec.getSize(widthMeasureSpec)
        val heightMeasure = MeasureSpec.getSize(heightMeasureSpec)

        widthSize = when (widthMode) {
            View.MeasureSpec.EXACTLY -> widthMeasure
            View.MeasureSpec.AT_MOST -> Math.min(desiredSize, widthSize)
            MeasureSpec.UNSPECIFIED -> desiredSize
            else -> desiredSize
        }

        heightSize = when (heightMode) {
            View.MeasureSpec.EXACTLY -> heightMeasure
            View.MeasureSpec.AT_MOST -> Math.min(desiredSize, heightMeasure)
            MeasureSpec.UNSPECIFIED -> desiredSize
            else -> desiredSize
        }

        setMeasuredDimension(widthSize, heightSize)
    }


    private fun drawBackground(canvas: Canvas) {
        paint.color = backgroundColor
        paint.style = Paint.Style.FILL

        val rect = Rect(left.toInt(), top.toInt(), widthSize, heightSize)
        canvas.drawRect(left, top, widthSize.toFloat(), heightSize.toFloat(), paint)

        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth
        canvas.drawRect(rect, paint)
    }

    private fun drawDots(canvas: Canvas) {
        val iterator = dataSet.iterator()
        for ((i, value) in iterator.withIndex()) {
            val y = heightSize - (emojiX + value.emoji.toFloat() * yInterval)
            val x = (emojiX + i.toFloat() * xInterval)
            dotPositions.add(DotPosition(x, y))
            drawDot(canvas, x, y)
        }
    }

    private fun drawDot(canvas: Canvas, x: Float, y: Float) {
        paint.color = dotColor
        paint.style = Paint.Style.FILL
        val radius = 10.0f //todo: pull into constant
        canvas.drawCircle(x, y, radius, paint)
    }

    private fun drawLines(canvas: Canvas) {
        val iterator = dataSet.iterator()
        paint.color = lineColor
        paint.style = Paint.Style.STROKE

        for ((i, value) in iterator.withIndex()) {
            if (i < dataSet.size - 1) {
                val nextValue = dataSet[i + 1]
                val y = heightSize - (emojiX + value.emoji.toFloat() * yInterval)
                val x = (emojiX + i.toFloat() * xInterval)
                val y1 = heightSize - (emojiX + nextValue.emoji.toFloat() * yInterval)
                val x1 = (emojiX + i.toFloat() * xInterval) + xInterval
                canvas.drawLine(x, y, x1, y1, paint)
            } else {
                Log.i(TAG, "Not drawing line here -> i = $i")
            }
        }
    }

    private fun drawSmileys(canvas: Canvas) {
        for (i in 0..4) {
            val correction = 48
            val x = 16f
            val y = heightSize - correction - (emojiX + i.toFloat() * yInterval)
            drawSmiley(canvas, i, x, y)
        }
    }

    private fun drawSmiley(canvas: Canvas, type: Int, x:Float, y:Float) {
        val d = when (type) {
            0 -> ResourcesCompat.getDrawable(context.resources, R.drawable.unhappy, null)!!
            1 -> ResourcesCompat.getDrawable(context.resources, R.drawable.sad, null)!!
            2 -> ResourcesCompat.getDrawable(context.resources, R.drawable.confused, null)!!
            3 -> ResourcesCompat.getDrawable(context.resources, R.drawable.happy, null)!!
            4 -> ResourcesCompat.getDrawable(context.resources, R.drawable.smiling, null)!!
            else -> context.resources.getDrawable(R.drawable.sad)
        }

        canvas.drawBitmap(drawableToBitmap(d), x, y, paint)
    }

    private fun drawXDescriptions(canvas: Canvas) {
        for ((i, value) in dataSet.iterator().withIndex()) {
            val correction = 0f
            val x = emojiX - correction + (i.toFloat() * xInterval)
            val y = heightSize - (emojiX / 2).toFloat()
            drawDescription(canvas, x, y, value.date.toString())
        }
    }

    private fun drawDescription(canvas: Canvas, x:Float, y:Float, date: String) {
        paint.color = dotColor
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 30f //todo: Hardcoded magic number

        val formatted = TimeUtils.formatDate(date)

        canvas.drawText(formatted, x, y, paint)
    }

    private fun drawGraph(canvas: Canvas) {
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.color = lineColor

        val path = Path()
        path.setFillType(Path.FillType.EVEN_ODD)
        val startPosition = dotPositions[0]
        path.moveTo(startPosition.x, startPosition.y)
        for (value in dotPositions) {
            path.lineTo(value.x, value.y)
        }
        path.lineTo(startPosition.x, startPosition.y)
        path.close()
        canvas.drawPath(path, paint)
    }

    fun setDataSet(set: List<DayActivity>) {
        Log.i(TAG, "Received new dataset: ${set.size}")
        this.dataSet = set
        this.invalidate()
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {

        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private class DotPosition(val x: Float, val y: Float)
}