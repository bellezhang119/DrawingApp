package com.kotlinapps.drawingapp

import android.Manifest
import androidx.appcompat.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private var layout : ConstraintLayout? = null

    private var drawingView : DrawingView? = null

    private var brushButton : ImageButton? = null
    private var brushDialog: Dialog? = null
    private var brushSeekBar : SeekBar? = null

    private var colorButton : ImageButton? = null
    private var colorPickerDialog : ColorPickerDialog? = null

    private var imageButton : ImageButton? = null

    private var settings : SharedPreferences? = null

    private var editor : SharedPreferences.Editor? = null

    private val openGalleryLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) {
        result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val imageBackground : ImageView = findViewById(R.id.iv_background)
            imageBackground.setImageURI(result.data?.data)
        }
    }

    private val requestPermission : ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissions -> permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                val firstEverRequest : Boolean = settings!!.getBoolean("firstEverRequest", true)

                if (isGranted) {
                    if (firstEverRequest) {
                        Toast.makeText(
                            this@MainActivity,
                            "Permission granted for gallery",
                            Toast.LENGTH_LONG
                        ).show()
                        editor?.putBoolean("firstEverRequest", false)
                        editor?.apply()
                    }

                    val pickIntent = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                    openGalleryLauncher.launch(pickIntent)

                } else {
                    if (permissionName==Manifest.permission.READ_EXTERNAL_STORAGE) {
                        Toast.makeText(
                            this@MainActivity,
                            "Permission denied for gallery",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

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

        imageButton = findViewById(R.id.image_button)

        settings = getSharedPreferences("firstEverRequest", 0)
        editor = settings?.edit()

        brushButton?.setOnClickListener {
            showBrushSizeDialog()
        }

        colorButton?.setOnClickListener {
            showColorPickerDialog()
        }

        imageButton?.setOnClickListener {
            requestStoragePermission()
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

    private fun requestStoragePermission() {
        println(ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE))
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showRationaleDialog("Drawing App",
                "Please give Drawing App permission to access your files and media in the settings")

        } else {
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }
    }

    private fun showRationaleDialog(title : String, message : String) {
        val builder : AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss()}
        builder.setPositiveButton("Accept") {dialog, _ ->
            val intent : Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri : Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        builder.create().show()
    }
}

