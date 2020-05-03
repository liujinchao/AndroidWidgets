package com.liujc.androidwidgets

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.Surface
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.cc.camera.camera.CameraThreadPool
import com.cc.camera.camera.ICameraControl
import com.cc.camera.camera.PermissionCallback
import com.cc.camera.view.CcCameraView
import com.cc.camera.view.CcMaskView
import com.cc.camera.view.crop.CropView
import com.cc.camera.view.crop.OverlayCameraView
import com.cc.camera.view.crop.RectangleOverlayView
import kotlinx.android.synthetic.main.activity_camera_layout.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * @ClassName  CameraActivity
 * @author liujc
 * @date 2019/11/26
 * @Description 自定义拍照页面
 */
class CameraActivity : AppCompatActivity(){

    companion object {
        private const val PERMISSIONS_REQUEST_CAMERA = 1000

        @JvmStatic
        fun intentTo(context: Context?) {
            val intent = Intent(context, CameraActivity::class.java)
            context?.startActivity(intent)
        }
    }

    private lateinit var cameraView: CcCameraView
    private lateinit var overlayView: OverlayCameraView
    private lateinit var cropView: CropView
    private lateinit var cropMaskView: CcMaskView
    private var outputFile: File? = null
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_layout)
        initViews()
    }

    private fun initViews() {
        outputFile = File(application.getFilesDir(), "pic.jpg")

        cameraView = findViewById<CcCameraView>(R.id.camera_view)
        cameraView.setPermissionCallback(permissionCallback)
        cameraView.setMaskType(CcMaskView.MASK_TYPE_NONE, this)
        cameraView.setEnableScan(true)

        overlayView = findViewById<OverlayCameraView>(R.id.overlay_view)
        overlayView.isFixed = true
        overlayView.setOverlayView(RectangleOverlayView())

        cropView = findViewById<CropView>(R.id.crop_view)
        cropMaskView = findViewById<CcMaskView>(R.id.crop_mask_view)
        cropMaskView.maskType = CcMaskView.MASK_TYPE_NONE
        setOrientation(resources.configuration)
        findViewById<ImageView>(R.id.confirm_button).setOnClickListener(takeButtonOnClickListener);
    }

    private val permissionCallback = PermissionCallback {
        ActivityCompat.requestPermissions(this@CameraActivity,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSIONS_REQUEST_CAMERA)
        false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cameraView.refreshPermission()
                } else {
                    Toast.makeText(applicationContext, R.string.camera_permission_required, Toast.LENGTH_LONG)
                            .show()
                }
            }
            else -> {
            }
        }
    }

    private val takeButtonOnClickListener = View.OnClickListener { cameraView.takePicture(outputFile, takePictureCallback) }

    private val takePictureCallback = CcCameraView.OnTakePictureCallback {
        handler.post(Runnable {
            cropView.setFilePath(outputFile?.getAbsolutePath())
            showCrop()
        })
    }

    private fun showCrop() {


        val maskType = cropMaskView.maskType
        val rect: Rect
        when (maskType) {
            CcMaskView.MASK_TYPE_NONE -> rect = overlayView.frameRect
            else -> rect = overlayView.frameRect
        }
        val cropped = cropView.crop(rect)
        iv_display_img.setImageBitmap(cropped)
        cameraView.cameraControl.pause()
        updateFlashMode()

        CameraThreadPool.execute {
            try {
                val fileOutputStream = FileOutputStream(outputFile)
                cropped.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                fileOutputStream.close()
//                getRecognitionResultByImage(cropped)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun updateFlashMode() {
        val flashMode = cameraView.cameraControl.flashMode
        if (flashMode == ICameraControl.FLASH_MODE_TORCH) {
//            lightButton.setImageResource(R.drawable.bd_ocr_light_on)
        } else {
//            lightButton.setImageResource(R.drawable.bd_ocr_light_off)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setOrientation(newConfig)
    }

    private fun setOrientation(newConfig: Configuration) {
        val rotation = windowManager.defaultDisplay.rotation
        var cameraViewOrientation = CcCameraView.ORIENTATION_PORTRAIT
        when (newConfig.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                cameraViewOrientation = CcCameraView.ORIENTATION_PORTRAIT
            }
            Configuration.ORIENTATION_LANDSCAPE -> {
                if (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_90) {
                    cameraViewOrientation = CcCameraView.ORIENTATION_HORIZONTAL
                } else {
                    cameraViewOrientation = CcCameraView.ORIENTATION_INVERT
                }
            }
            else -> {
                cameraView.setOrientation(CcCameraView.ORIENTATION_PORTRAIT)
            }
        }
        cameraView.setOrientation(cameraViewOrientation)
    }

    override fun onDestroy() {
        super.onDestroy()
        CameraThreadPool.cancelAutoFocusTimer()
    }
}