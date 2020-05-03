package com.cc.camera

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.android.ocrcamera.R
import com.cc.camera.camera.CameraThreadPool
import com.cc.camera.camera.ICameraControl
import com.cc.camera.view.CcCameraView
import com.cc.camera.view.CcMaskView

import com.cc.camera.view.crop.CropView
import com.cc.camera.view.crop.OverlayCameraView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * @ClassName  OcrActivity
 * @author liujc
 * @date 2019/11/22
 * @Description (这里用一句话描述这个类的作用)
 */
class OcrActivity : AppCompatActivity() {

    private val REQUEST_CODE_GENERAL = 105

    private var hasGotToken = false

    private var alertDialog: AlertDialog.Builder? = null

    private val API_KEY = "lxzk2psYSGg444QxIgHMAec7"
    private val SECRET_KEY = "ElOK4taVLMdFW1M4SHpQGTv1RyzFKMHC"

    companion object {
        @JvmStatic
        fun intentTo(context: Context?) {
            val intent = Intent(context, OcrActivity::class.java)
            context?.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ocr_activity_layout)
    }


    fun initViewsAndEvents() {
        initOcrViews()
        alertDialog = AlertDialog.Builder(this)
    }

    private fun checkTokenStatus(): Boolean {
        if (!hasGotToken) {
            Toast.makeText(applicationContext, "token还未成功获取", Toast.LENGTH_LONG).show()
        }
        return hasGotToken
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun infoPopText(result: String?) {
        alertText("", result)
    }

    private fun alertText(title: String, message: String?) {
        this.runOnUiThread {
            alertDialog?.setTitle(title)
                    ?.setMessage(message)
                    ?.setPositiveButton("确定", null)
                    ?.show()
        }
    }

    private fun bitmapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val bytes = baos.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private lateinit var cameraView: CcCameraView
    private lateinit var lightButton: ImageView
    private lateinit var takePhotoBtn: ImageView
    private var outputFile: File? = null
    private val handler = Handler()
    private lateinit var overlayView: OverlayCameraView
    private lateinit var cropView: CropView
    private lateinit var cropMaskView: CcMaskView
    private fun initOcrViews() {
//        outputFile = File(FileUtil.getSaveFile(application).absolutePath)

        cameraView = findViewById<View>(R.id.camera_view) as CcCameraView
        lightButton = findViewById<View>(R.id.light_button) as ImageView
        lightButton.setOnClickListener(lightButtonOnClickListener)
        takePhotoBtn = findViewById<View>(R.id.take_photo_button) as ImageView
        takePhotoBtn.setOnClickListener(takeButtonOnClickListener)
        cameraView.setAutoPictureCallback(autoTakePictureCallback)
        overlayView = findViewById<View>(R.id.overlay_view) as OverlayCameraView
//        overlayView.isFixed = true
//        overlayView.setTypeWide()
        cropView = findViewById<View>(R.id.crop_view) as CropView
        cropMaskView = findViewById<CcMaskView>(R.id.crop_mask_view)
        cameraView.setMaskType(CcMaskView.MASK_TYPE_NONE, this)
        cropMaskView.maskType = CcMaskView.MASK_TYPE_NONE

//        findViewById<ImageView>(R.id.confirm_button).setOnClickListener(cropConfirmButtonListener);
        findViewById<ImageView>(R.id.confirm_button).setOnClickListener(takeButtonOnClickListener);
    }

    private val lightButtonOnClickListener = View.OnClickListener {
        if (cameraView.getCameraControl().flashMode == ICameraControl.FLASH_MODE_OFF) {
            cameraView.getCameraControl().flashMode = ICameraControl.FLASH_MODE_TORCH
        } else {
            cameraView.getCameraControl().flashMode = ICameraControl.FLASH_MODE_OFF
        }
        updateFlashMode()
    }

    private val takeButtonOnClickListener = View.OnClickListener { cameraView.takePicture(outputFile, takePictureCallback) }

    private fun updateFlashMode() {
        val flashMode = cameraView.cameraControl.flashMode
        if (flashMode == ICameraControl.FLASH_MODE_TORCH) {
            lightButton.setImageResource(R.drawable.bd_ocr_light_on)
        } else {
            lightButton.setImageResource(R.drawable.bd_ocr_light_off)
        }
    }

    private val autoTakePictureCallback = CcCameraView.OnTakePictureCallback { bitmap ->
        CameraThreadPool.execute {
            try {
                val fileOutputStream = FileOutputStream(outputFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
                bitmap.recycle()
                fileOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

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
//        displayImageView.setImageBitmap(cropped)
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

    private val cropConfirmButtonListener = View.OnClickListener {
        val maskType = cropMaskView.maskType
        val rect: Rect
        when (maskType) {
            CcMaskView.MASK_TYPE_NONE -> rect = overlayView.frameRect
            else -> rect = overlayView.frameRect
        }
        val cropped = cropView.crop(rect)
//        displayImageView.setImageBitmap(cropped)
        cropAndConfirm()
    }

    private fun cropAndConfirm() {
        cameraView.cameraControl.pause()
        updateFlashMode()
    }

    override fun onDestroy() {
        super.onDestroy()
        CameraThreadPool.cancelAutoFocusTimer()
    }
}