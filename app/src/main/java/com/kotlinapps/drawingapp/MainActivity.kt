package com.kotlinapps.drawingapp

import android.Manifest
import androidx.appcompat.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

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

    private var undoButton : ImageButton? = null
    private var redoButton : ImageButton? = null

    private var diskButton : ImageButton? = null

    var customProgressDialog : Dialog? = null

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
        brushButton = findViewById(R.id.ib_brush)

        brushDialog = Dialog(this)
        brushDialog?.setContentView(R.layout.dialog_brush_size)
        brushSeekBar = brushDialog?.findViewById(R.id.brush_seekBar)

        colorButton = findViewById(R.id.ib_color)
        colorPickerDialog = ColorPickerDialog(this, colorButton, drawingView)

        imageButton = findViewById(R.id.ib_image)

        settings = getSharedPreferences("firstEverRequest", 0)
        editor = settings?.edit()

        undoButton = findViewById(R.id.ib_undo)
        redoButton = findViewById(R.id.ib_redo)

        diskButton = findViewById(R.id.ib_disk)

        brushButton?.setOnClickListener {
            showBrushSizeDialog()
        }

        colorButton?.setOnClickListener {
            showColorPickerDialog()
        }

        imageButton?.setOnClickListener {
            requestStoragePermission()
        }

        undoButton?.setOnClickListener {
            drawingView?.onClickUndo()
        }

        redoButton?.setOnClickListener {
            drawingView?.onClickRedo()
        }

        diskButton?.setOnClickListener {
            if (isReadStorageAllowed()) {
                showProgressDialog()

                lifecycleScope.launch {
                    val flDrawingView : FrameLayout = findViewById(R.id.fl_drawing_view_container)

                    saveBitmapFile(getBitmap(flDrawingView))
                }
            }
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

    private fun getBitmap(view : View) : Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val background = view.background

        if (background != null) {
            background.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }

        view.draw(canvas)

        return bitmap
    }

    private suspend fun saveBitmapFile(bitmap : Bitmap?) : String {
        var result = ""

        withContext(Dispatchers.IO) {
            if (bitmap != null) {
                try {
                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
                    
                    val file = File(externalCacheDir?.absoluteFile.toString() 
                            + File.separator + "DrawingApp_" + System.currentTimeMillis()/1000
                            + ".png")

                    val fileOutput = FileOutputStream(file)
                    fileOutput.write(bytes.toByteArray())
                    fileOutput.close()

                    result = file.absolutePath

                    runOnUiThread {
                        cancelProgressDialog()
                        if (result.isNotEmpty()) {
                            Toast.makeText(this@MainActivity, "File saved successfully: $result",
                            Toast.LENGTH_SHORT).show()
                            shareImage(result)
                        } else {
                            Toast.makeText(this@MainActivity, "Something went wrong",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e : Exception) {
                    result = ""
                    e.printStackTrace()
                }
            }
        }

        return result
    }

    private fun isReadStorageAllowed() : Boolean {
        val result = ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE)

        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun isManageStorageAllowed() : Boolean {
        val result = ContextCompat.checkSelfPermission(this,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE)

        return result == PackageManager.PERMISSION_GRANTED
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
            requestPermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE))
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

    private fun showProgressDialog() {
        customProgressDialog = Dialog(this@MainActivity)
        customProgressDialog?.setContentView(R.layout.dialog_custom_progress)

        customProgressDialog?.show()

    }

    private fun cancelProgressDialog() {
        if (customProgressDialog != null) {
            customProgressDialog?.dismiss()
            customProgressDialog = null
        }
    }

    private fun shareImage(result : String) {
        MediaScannerConnection.scanFile(this, arrayOf(result), null) {
            path, uri ->
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.type = "image/png"
            startActivity(Intent.createChooser(shareIntent, "share"))
        }
    }
}

