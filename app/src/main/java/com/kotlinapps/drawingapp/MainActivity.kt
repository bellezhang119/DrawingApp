package com.kotlinapps.drawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    private var layout : ConstraintLayout? = null

    private var drawingView : DrawingView? = null

    private var brushButton : ImageButton? = null
    private var brushDialog: Dialog? = null
    private var brushSeekBar : SeekBar? = null

    private var colorButton : ImageButton? = null
    private var colorPickerDialog : ColorPickerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        layout = findViewById(R.id.layout_main)

        drawingView = findViewById(R.id.drawing_view)
        drawingView?.setBrushSize(20f)
        brushButton = findViewById(R.id.brush_button)

        brushDialog = Dialog(this)
        brushDialog?.setContentView(R.layout.dialog_brush_size)
        brushSeekBar = brushDialog?.findViewById(R.id.brush_seekBar)

        colorButton = findViewById(R.id.color_button)
        colorPickerDialog = ColorPickerDialog(this, colorButton, drawingView)

        brushButton?.setOnClickListener {
            showBrushSizeDialog()
        }

        colorButton?.setOnClickListener {
            showColorPickerDialog()
        }
    }

    private fun showBrushSizeDialog() {
        println("show brush")

        brushDialog?.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        brushSeekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.progress?.let { drawingView?.setBrushSize(it.toFloat()) }
                brushSeekBar?.progress = seekBar?.progress!!
                println("seek bar")
            }

        })

        brushDialog?.show()
    }

    private fun showColorPickerDialog() {
        colorPickerDialog?.show()
    }
}

