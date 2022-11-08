package com.kotlinapps.drawingapp

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.*

class ColorPickerDialog (context : Context, imageButton : ImageButton?, drawingView : DrawingView?) : Dialog(context), SeekBar.OnSeekBarChangeListener {

    private var colorView : ImageView? = null
    private var colorButton : ImageButton? = null
    private var rgbTextView : TextView? = null
    private var redSeekBar : SeekBar? = null
    private var greenSeekBar : SeekBar? = null
    private var blueSeekBar : SeekBar? = null
    private var alphaSeekBar : SeekBar? = null
    private var drawingView : DrawingView? = null
    var rValue = 0
    var gValue = 0
    var bValue = 0
    var aValue = 255

    init {
        colorButton = imageButton
        this.drawingView = drawingView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.dialog_color_picker)
        window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        colorView = findViewById(R.id.iv_color)

//        val layout : LinearLayout? = findViewById(R.id.layout_main)
//        colorButton = layout?.findViewById(R.id.color_button)
        rgbTextView = findViewById(R.id.tv_rgb_value)
        redSeekBar = findViewById(R.id.seekbar_red)
        greenSeekBar = findViewById(R.id.seekbar_green)
        blueSeekBar = findViewById(R.id.seekbar_blue)
        alphaSeekBar = findViewById(R.id.seekbar_alpha)

        redSeekBar?.setOnSeekBarChangeListener(this)
        greenSeekBar?.setOnSeekBarChangeListener(this)
        blueSeekBar?.setOnSeekBarChangeListener(this)
        alphaSeekBar?.setOnSeekBarChangeListener(this)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        when (seekBar?.id) {
            R.id.seekbar_red -> rValue = progress
            R.id.seekbar_green -> gValue = progress
            R.id.seekbar_blue -> bValue = progress
            R.id.seekbar_alpha -> aValue = progress
        }

        val rgbString = "($rValue,$gValue,$bValue)"
        rgbTextView?.text = rgbString
        rgbTextView?.setTextColor(Color.argb(aValue, rValue, gValue, bValue))

        colorView?.drawable?.setTint(Color.argb(aValue, rValue, gValue, bValue))
        colorButton?.drawable?.setTint(Color.argb(aValue, rValue, gValue, bValue))
        drawingView?.setBrushColor(aValue, rValue, gValue, bValue)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

}