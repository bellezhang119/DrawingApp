package com.kotlinapps.drawingapp

import android.app.Dialog
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat.setLayerType

class ClickableColorPickerDialog (context : Context) : Dialog(context) {

//    private var right : Shader? = null
//    private var left : Shader? = null
//    private var composeShader : Shader? = null
//    private var ivColorPicker : ImageView? = null
//
//    private var layout : ConstraintLayout? = null
//
//    private var white : Int = Color.rgb(255, 255, 255)
//
//    private var rainbowSeekBar : SeekBar? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        setContentView(R.layout.dialog_color_picker)
//        ivColorPicker = findViewById(R.id.iv_color_picker)
//
//        rainbowSeekBar = findViewById(R.id.sb_rainbow)
//        setRainbowSeekBar()
//
//        rainbowSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                var hsvColor = floatArrayOf(0f, 1f, 1f)
//
//                hsvColor[0] = 360f * progress.toFloat() / seekBar!!.max.toFloat()
//
//                updateColor(Color.HSVToColor(hsvColor))
//                rainbowSeekBar?.thumb?.setTint(Color.HSVToColor(hsvColor))
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
//
//        })
//        //updateColor(Color.rgb(255, 0, 0))
//    }
//
//    fun updateColor(color : Int) {
//        println(ivColorPicker?.width)
//        println(ivColorPicker?.height)
//
//        val bitmap : Bitmap = ivColorPicker!!.drawable.toBitmap(ivColorPicker!!.width, ivColorPicker!!.height)
//        val newBitmap : Bitmap = addGradient(bitmap, color)
//
//        ivColorPicker!!.setImageDrawable(BitmapDrawable(context.resources, newBitmap))
//    }
//
//    fun addGradient(ogBitmap : Bitmap, color : Int) : Bitmap {
//        val width : Int = ogBitmap.width
//        val height : Int = ogBitmap.height
//
//        val updatedBitmap : Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
//        val canvas : Canvas = Canvas(updatedBitmap)
//
//        canvas.drawBitmap(ogBitmap, 0f, 0f, null)
//
//        val paint : Paint = Paint()
//        right = LinearGradient(0f, 0f, width.toFloat(), 0f, white, color,
//            Shader.TileMode.CLAMP)
//        left = LinearGradient(0f, height.toFloat(), 0f, 0f, Color.rgb(0, 0, 0), Color.rgb(0, 0, 0),
//            Shader.TileMode.CLAMP)
//
//        composeShader = ComposeShader(left as LinearGradient,
//            right as LinearGradient, Xfermode())
//
//        paint.shader = composeShader
//        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
//        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
//
//        return updatedBitmap
//    }
//
//    private fun setRainbowSeekBar() {
//        val colorArray : IntArray = intArrayOf(Color.rgb(255, 0, 0), Color.rgb(255, 127, 0), Color.rgb(255, 255, 0),
//            Color.rgb(0, 255, 0), Color.rgb(0, 0, 255), Color.rgb(75, 0, 130), Color.rgb(148, 0, 211))
//        val rainbow : GradientDrawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
//            colorArray)
//
//        rainbowSeekBar?.progressDrawable = rainbow
//    }
}