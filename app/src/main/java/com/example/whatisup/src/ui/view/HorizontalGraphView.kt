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
import androidx.core.view.MotionEventCompat
import android.view.MotionEvent
import android.view.MotionEvent.INVALID_POINTER_ID
import com.example.whatisup.src.utils.checkType

private const val TAG = "HorizontalGraphView"

// with some modifications this and the GraphView could be made into generic
// graph with bar/line graph and horizontal/vertical modes
class HorizontalGraphView(context: Context, attrs: AttributeSet): View(context, attrs){

    private var activePointerId = INVALID_POINTER_ID
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val backgroundColor = context.getColor(R.color.colorPrimary)
    private val dotColor = Color.WHITE
    private val borderColor = Color.WHITE
    private val lineColor = context.getColor(R.color.colorAccent)

    private val goodColor = Color.GREEN
    private val badColor = Color.RED
    private val neutralColor = Color.YELLOW

    private val borderWidth = 6.0f
    private val rounding = 14.0f

    private var widthSize = 1000
    private var heightSize = 1000
    private var left = 0f
    private var top = 0f

    private var xInterval = widthSize / 8.6f
    private var yInterval = heightSize / 8f
    private var textSize = yInterval / 3.4f
    private var xDescriptionSize = heightSize / 10f
    private var yDescriptionSize = widthSize / 10f + textSize

    private var lineWidth = 10.0f
    private var xMargin = yInterval / 2

    private var yMargin = xInterval / 2

    private var data = mutableListOf<DataPoint>()
    private var targetPoints = HashMap<String, DataPoint>()

    private var showTargets: Boolean = true
    private var selectedTarget: DataPoint? = null
    private var showTargetInfo: Boolean = false

    var allowTargetMovement: Boolean = false

    private var targetInfoSide: Float = xInterval
    private var hoursInX = 6

    private var lastX = 0.0F
    private var lastY = 0.0F
//    private val yDescriptions = listOf("Still", )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBackground(canvas)
//        drawLines(canvas)
        drawBoxes(canvas)
//        drawDots(canvas)
        drawDescriptionAreas(canvas)
        if (showTargets) drawTargetPointers(canvas)
        drawTargetInfo(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = 1000
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

    fun showTargets(show: Boolean) {
        this.showTargets = show
        invalidate()
    }

    fun setDefaultTargetPoints() {
        val startX = xMargin + yDescriptionSize
        val baseX = startX + xInterval
        val startY = xDescriptionSize + yInterval / 8
        val baseY = startY
        targetPoints["still"] = DataPoint(baseX, baseY, 0)
        targetPoints["walking"] = DataPoint(baseX, baseY + yInterval, 0)
        targetPoints["onFoot"] = DataPoint(baseX, baseY + yInterval * 2, 0)
        targetPoints["bicycling"] = DataPoint(baseX, baseY + yInterval * 3, 0)
        targetPoints["running"] = DataPoint(baseX, baseY + yInterval * 4, 0)
        targetPoints["other"] = DataPoint(baseX, baseY + yInterval * 5, 0)
    }

    /**
     * Use this to set PhysicalActivities for the view.
     *
     * @param data
     *
     */
    fun setData(data: List<PhysicalActivity>) {
        this.data.clear()
        var count = 1
        data.map { this.data.add(mapMillisToData(it, count)); count += 1 }
    }


    override fun performClick(): Boolean {
//        Log.d(TAG, "perform click")
        return super.performClick()
    }

    // todo: still contains ctrl c+v code
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        performClick()

        val action = MotionEventCompat.getActionMasked(ev)

        if (allowTargetMovement) {
            when (action) {
                MotionEvent.ACTION_DOWN -> {

                    MotionEventCompat.getActionIndex(ev).also { pointerIndex ->
                        lastX = MotionEventCompat.getX(ev, pointerIndex)
                        lastY = MotionEventCompat.getY(ev, pointerIndex)

                    }

                    // Save the ID of this pointer (for dragging)
                    activePointerId = MotionEventCompat.getPointerId(ev, 0)
                }

                MotionEvent.ACTION_MOVE -> {
                    // Find the index of the active pointer and fetch its position
                    val (x: Float, y: Float) =
                        MotionEventCompat.findPointerIndex(ev, activePointerId).let { pointerIndex ->
                            // Calculate the distance moved
                            MotionEventCompat.getX(ev, pointerIndex) to
                                    MotionEventCompat.getY(ev, pointerIndex)
                        }

                    changeTarget(x, y) // this changes the target sliders location
                    invalidate()
                    lastX = x
                    lastY = y
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    activePointerId = INVALID_POINTER_ID
                    showTargetInfo = false
                    invalidate()
                }
                MotionEvent.ACTION_POINTER_UP -> {

                    MotionEventCompat.getActionIndex(ev).also { pointerIndex ->
                        MotionEventCompat.getPointerId(ev, pointerIndex)
                            .takeIf { it == activePointerId }
                            ?.run {
                                // This was our active pointer going up. Choose a new
                                // active pointer and adjust accordingly.
                                val newPointerIndex = if (pointerIndex == 0) 1 else 0
                                lastX = MotionEventCompat.getX(ev, newPointerIndex)
                                lastY = MotionEventCompat.getY(ev, newPointerIndex)
                                activePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex)
                            }
                    }
                }
            }
        }

        return true
    }

    private fun changeTarget(x: Float, y: Float) {
        val value = targetPoints.filter { it.value.y < y && y < it.value.y + yInterval }
        value.forEach { it.value.x = x }
        if (value.isNotEmpty()) {
            selectedTarget = DataPoint(value.values.first().x, value.values.first().y, 0) // pretty stupid but w/e
            showTargetInfo = true
        }
    }

    private fun mapXtoHour(x: Float): String {
        val totalXMovement = xInterval * hoursInX // 100% or 6h is this much
        val correctedX = x - (yDescriptionSize + xMargin)
        val currentMovement = correctedX / totalXMovement

        var retVal = hoursInX * currentMovement
        if (retVal > 6) retVal = 6.0f
        if (retVal < 0) retVal = 0.0f

        return retVal.toString().take(4)
    }

    private fun drawTargetInfo(canvas: Canvas) {
        if (showTargetInfo) {
            selectedTarget?.let {
                val x = it.x
                val y = it.y + 20.0f // add some offset
                paint.color = context.getColor(R.color.colorAccent)
                paint.style = Paint.Style.FILL
                val rect = RectF((x - targetInfoSide / 2),
                    (y - targetInfoSide),
                    (x + targetInfoSide  / 2),
                    (y - targetInfoSide / 4))

                canvas.drawRoundRect(rect, rounding, rounding, paint)
                drawDescription(canvas, x , y - targetInfoSide / 1.8f, mapXtoHour(x))
            }
        }
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

    private fun drawTargetPointers(canvas: Canvas) {
        paint.strokeWidth = lineWidth
        paint.color = dotColor

        targetPoints.forEach {
            val x = it.value.x
            val y = it.value.y
            val x1 = x
            val y1 = it.value.y + yInterval - yInterval / 8

            canvas.drawLine(x, y, x1, y1, paint)
        }
    }

    private fun drawDescriptionAreas(canvas: Canvas) {
        // Y Descriptions
        var y = yMargin + xDescriptionSize + textSize / 2
        var x = yDescriptionSize - xMargin / 2
        for (i in this.data) {
            if (checkType(i.type)) {
                drawDescription(canvas, x, y, getActivityText(i.type, context))
                y += yInterval
            }
        }

        // X Descriptions
        y = yMargin + xDescriptionSize + yInterval * this.data.size + textSize
        x = xMargin + yDescriptionSize + xInterval
        var text = 1

        // could be fixed by better iteration
        drawDescription(canvas, x - xInterval, y, "0 h") // first draw 0
        canvas.drawLine(x - xInterval, y - textSize, x - xInterval, y - textSize - 12.0f, paint)

        for (i in this.data) {
            drawDescription(canvas, x, y, "$text h")
            canvas.drawLine(x, y - textSize, x, y - textSize - 12.0f, paint)
            x = xMargin + yDescriptionSize + xInterval + (xInterval * text)
            text += 1
            if (text <= 6) canvas.drawLine(x, y -textSize, x, y - textSize - 12.0f, paint)
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

    private fun drawBoxes(canvas: Canvas) {
        paint.style = Paint.Style.FILL

        val betweenBars = yInterval / 8

        val startX = xMargin + yDescriptionSize
        val startY = xDescriptionSize + betweenBars

        data.forEach {
            if (!checkType(it.type)) return@forEach
            val x = startX
            val x1 = it.x * xInterval + startX
            val y = it.y * yInterval + startY
            val y1 = y + yInterval - betweenBars

            drawDescription(canvas, x1 + xInterval / 2, y + yInterval / 2, "${it.x} h")

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
            val rect = RectF(x, y, x1, y1)
            canvas.drawRoundRect(rect, rounding, rounding, paint)
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
        paint.strokeWidth = lineWidth
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

    private data class DataPoint(var x: Float, var y: Float, val type: Int) {
        override fun toString(): String {
            return "$x:$y"
        }
    }
}