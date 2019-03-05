package com.example.whatisup.src.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.whatisup.R
import com.example.whatisup.src.data.TYPE_OTHER
import com.example.whatisup.src.data.model.PhysicalActivity
import com.example.whatisup.src.utils.getActivityText
import com.google.android.gms.location.DetectedActivity
import java.util.concurrent.TimeUnit
import android.graphics.*

private const val TAG = "HorizontalGraphView"

// with some modifications this and the GraphView could be made into generic
// graph with bar/line graph and horizontal/vertical modes
class HorizontalGraphView(context: Context, attrs: AttributeSet): View(context, attrs){

    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundColor = context.getColor(R.color.colorPrimary)
    private val dotColor = Color.WHITE
    private val borderColor = Color.WHITE
    private val lineColor = Color.CYAN

    private val goodColor = Color.GREEN
    private val badColor = Color.RED
    private val neutralColor = Color.YELLOW

    private val borderWidth = 6.0f

    private var widthSize = 1000
    private var heightSize = 800
    private var left = 0f
    private var top = 0f

    private var xInterval = widthSize / 8f
    private var yInterval = heightSize / 8f
    private var textSize = yInterval / 4
    private var xDescriptionSize = heightSize / 10f
    private var yDescriptionSize = widthSize / 10f + textSize

    private var lineWidht = 10.0f
    private var xMargin = yInterval / 2

    private var yMargin = xInterval / 2

    private var data = mutableListOf<DataPoint>()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas)
        drawDescriptions(canvas)
//        drawLines(canvas)
        drawBoxes(canvas)
//        drawDots(canvas)
        drawDescriptions(canvas)
        drawDescriptionAreas(canvas)
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

    fun setData(data: List<PhysicalActivity>) {
        this.data.clear()
        var count = 1
        data.map { this.data.add(mapMillisToData(it, count)); count += 1 }
    }

    private fun mapMillisToData(activity: PhysicalActivity, count: Int): DataPoint {
        val y = getY(activity.type).toFloat()
        val millis = activity.duration
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - (hours * 60)
        var x = "$hours.$minutes".toFloat()
        if (x > 6.0) x = 6.0f
        return DataPoint(x, y, activity.type)
    }

    private fun drawDescriptionAreas(canvas: Canvas) {
        // Y Descriptions
        var y = yMargin + xDescriptionSize
        var x = yDescriptionSize - xMargin / 2
        for (i in this.data) {
            drawDescription(canvas, x, y, getActivityText(i.type, context))
            y += yInterval
        }

        // X Descriptions
        y = yMargin + xDescriptionSize + yInterval * this.data.size
        x = xMargin + yDescriptionSize + xInterval
        var text = 1

        drawDescription(canvas, x - xInterval, y, "0 h") // first draw 0

        for (i in this.data) {
            drawDescription(canvas, x, y, "$text h")
            x = xMargin + yDescriptionSize + xInterval + (xInterval * text)
            text += 1
        }
    }

    private fun drawDescription(canvas: Canvas, x: Float, y: Float, text: String) {

        paint.color = dotColor
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = textSize
        canvas.drawText(text, x, y, paint)
    }

    private fun drawDots(canvas: Canvas) {

        paint.color = dotColor
        paint.style = Paint.Style.FILL

        val iterator = this.data.iterator()
        for ((i, value) in iterator.withIndex()) {
            val y = value.y * yInterval + yMargin + yDescriptionSize
            val x = xInterval * value.x + xMargin + xDescriptionSize
            val radius = 10.0f
            canvas.drawCircle(x, y, radius, paint)
        }
    }

    private fun drawDescriptions(canvas: Canvas) {

    }

    private fun drawBoxes(canvas: Canvas) {
        paint.style = Paint.Style.FILL

        val betweenBars = yInterval / 8

        val startX = xMargin + yDescriptionSize
        val startY = xDescriptionSize + betweenBars

        data.forEach {
            val x = startX
            val x1 = it.x * xInterval + startX
            val y = it.y * yInterval + startY
            val y1 = y + yInterval - betweenBars

            drawDescription(canvas, x1 + xInterval / 3, y + yInterval / 2, "${it.x} h")

            if (it.type != DetectedActivity.STILL) {
                paint.color = when (it.x) {
                    in 0.0f..2.0f -> badColor
                    in 2.0f..4.0f -> neutralColor
                    in 4.0f..7.0f -> goodColor
                    else -> neutralColor
                }
            } else {
                paint.color = when (it.x) {
                    in 0.0f..2.0f -> goodColor
                    in 2.0f..4.0f -> neutralColor
                    in 4.0f..7.0f -> badColor
                    else -> neutralColor
                }
            }
            canvas.drawRect(x, y, x1, y1, paint)
        }
    }

    private fun drawLines(canvas: Canvas) {
        paint.color = lineColor
        paint.style = Paint.Style.STROKE
        val startY = yMargin / 2 + yDescriptionSize
        val endY = startY + yInterval * data.size
        val startX = xMargin + xDescriptionSize
        val endX = startX
        val firstValue = data.find { it.y == 0.0f }

        var count = 1
        firstValue?.let {
            val firstX = firstValue.x * xInterval + xMargin + xDescriptionSize
            val firsty = firstValue.y * yInterval + yMargin + yDescriptionSize
            Log.d(TAG, "going to : ${startX}, ${startY}, ${firstValue.x}, ${firstValue.y}")
            drawCurvedLine(canvas, startX, startY, firstX, firsty, count)
            count += 1
        }
        for ((i, value) in data.iterator().withIndex()) {
            val next = data.find { it.y == value.y + 1 }
            if (next != null) {
                val y = value.y * yInterval + yMargin + yDescriptionSize
                val x = value.x * xInterval + xMargin + xDescriptionSize
                val y1 = next.y * yInterval + yMargin + yDescriptionSize
                val x1 = next.x * xInterval + xMargin + xDescriptionSize

                drawCurvedLine(canvas, x, y, x1, y1, count)
                count += 1
            } else {
                val y = value.y * yInterval + yMargin + yDescriptionSize
                val x = value.x * xInterval + xMargin + xDescriptionSize
                drawCurvedLine(canvas, x, y, endX, endY, count)
                count += 1
            }
        }
        val lastValue = data.find { it.y == data.size.toFloat()}
        lastValue?.let {
            Log.d(TAG, "going to : ${lastValue.x}, ${lastValue.y}, ${endX}, $endY")
            drawCurvedLine(canvas, lastValue.x, lastValue.y, endX, endY, count)
        }
    }

    private fun drawCurvedLine(canvas: Canvas, x: Float, y: Float, x1: Float, y1: Float, count: Int) {
        Log.d(TAG, "drawing line $x, $y, $x1, $y1, $count")
        val path = Path()
        path.moveTo(x, y)
        val yDiff = y1 - y
        Log.d(TAG, "ydiff: $yDiff")
        val yOffSet = when (x1 < x){
            true -> y1 + yDiff
            false -> y1 - yDiff
        }
        val xOffSet = (x1 + x) / 2
        Log.d(TAG, "xoffset: $xOffSet, yoffset: $yOffSet")
        path.cubicTo(x, y, xOffSet, yOffSet, x1, y1)
        paint.color = lineColor
        paint.isAntiAlias = true
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = lineWidht
        canvas.drawPath(path, paint)
    }

    private fun getY(type: Int): Int {
        return when (type) {
            DetectedActivity.STILL -> 0
            DetectedActivity.ON_BICYCLE -> 1
            DetectedActivity.RUNNING -> 2
            DetectedActivity.WALKING -> 3
            TYPE_OTHER -> 4
            DetectedActivity.ON_FOOT -> 5
            else -> 6
        }
    }

    private data class DataPoint(val x: Float, val y: Float, val type: Int) {
        override fun toString(): String {
            return "$x:$y"
        }
    }
}