package com.kotlinapps.drawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class DrawingView(context : Context, attrs : AttributeSet) : View(context, attrs) {

    private var drawPath : CustomPath? = null
    private var canvasBitMap : Bitmap? = null
    private var drawPaint : Paint? = null
    private var canvasPaint : Paint? = null
    private var brushSize : Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas : Canvas? = null
    private val paths = ArrayList<CustomPath>()

    init {
        drawPath = CustomPath(color, brushSize)
        drawPaint = Paint()
        drawPaint?.color = color
        drawPaint?.style = Paint.Style.STROKE
        drawPaint?.strokeJoin = Paint.Join.ROUND
        drawPaint?.strokeCap = Paint.Cap.ROUND
        canvasPaint = Paint(Paint.DITHER_FLAG)
        brushSize = 20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        Log.d("Dimension", "$w $h")
        canvasBitMap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(canvasBitMap!!)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("Touch", "Touch")
        val touchX = event?.x
        val touchY = event?.y

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                drawPath?.color = color
                drawPath?.brushThickness = brushSize

                drawPath?.reset()
                if (touchX != null && touchY != null) {
                    drawPath?.moveTo(touchX, touchY)
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (touchX != null && touchY != null) {
                    drawPath?.lineTo(touchX, touchY)
                }
            }

            MotionEvent.ACTION_UP -> {
                paths.add(drawPath!!)
                drawPath = CustomPath(color, brushSize)
            }

            else -> return false
        }
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawBitmap(canvasBitMap!!, 0f, 0f, canvasPaint)

        for (path in paths) {
            drawPaint?.strokeWidth = path.brushThickness
            drawPaint?.color = path.color
            canvas.drawPath(path, drawPaint!!)
        }

        if (!drawPath!!.isEmpty) {
            drawPaint?.strokeWidth = drawPath!!.brushThickness
            drawPaint?.color = drawPath!!.color
            canvas.drawPath(drawPath!!, drawPaint!!)
        }
    }


    internal inner class CustomPath(var color : Int, var brushThickness : Float) : Path() {

    }
}