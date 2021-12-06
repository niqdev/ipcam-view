package com.github.niqdev.mjpeg

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class MjpegRecordingHandler(private val context: Context) : OnFrameCapturedListener {
    private var bos: BufferedOutputStream? = null
    var isRecording = false
    var lastBitmap: Bitmap? = null
        private set

    /**
     * start recording the live image
     */
    fun startRecording() {
        try {
            val mjpegFilePath = createMjpegFile()!!.absolutePath
            val fos = FileOutputStream(mjpegFilePath)
            bos = BufferedOutputStream(fos)
            Toast.makeText(context, "start recording, file path is:$mjpegFilePath", Toast.LENGTH_LONG).show()
            isRecording = true
        } catch (e: FileNotFoundException) {
            Log.e(TAG, e.message.toString())
        }
    }

    /**
     * stop recording the live image
     */
    fun stopRecording() {
        isRecording = false
    }

    /**
     * save the last acquired bitmap into jpg file.
     */
    fun saveBitmapToFile() {
        val fos: FileOutputStream
        val bos: BufferedOutputStream
        val imagePath = createJpgFile()!!.absolutePath
        try {
            fos = FileOutputStream(imagePath)
            bos = BufferedOutputStream(fos)
            val jpegByteArrayOutputStream = ByteArrayOutputStream()
            lastBitmap?.compress(Bitmap.CompressFormat.JPEG, 75, jpegByteArrayOutputStream)
            val jpegByteArray = jpegByteArrayOutputStream.toByteArray()
            bos.write(jpegByteArray)
            bos.flush()
            Toast.makeText(context, "saved image:$imagePath", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Log.e(TAG, e.message.toString())
        }
    }

    /**
     * Create jpg file in app external cache directory. the directory path is /sdcard/Android/data/com.github.niqdev.ipcam/files
     *
     * @return File
     */
    private fun createJpgFile(): File? {
        return createSavingFile("photo", "jpg")
    }

    private fun createSavingFile(prefix: String, extension: String): File? {
        val date = Date()

        @SuppressLint("SimpleDateFormat")
        val sdf = SimpleDateFormat("yyyyMMddHHmmss")
        val szFileName = prefix + "-" + sdf.format(date)
        try {
            val path = context.getExternalFilesDir(null)!!.path + "/" + szFileName + "." + extension
            val file = File(path)
            if (!file.exists()) {
                file.createNewFile()
            }
            Log.d(TAG, "file path is " + file.absolutePath)
            return file
        } catch (e: IOException) {
            Log.e(TAG, e.message.toString())
        }
        return null
    }

    /**
     * Create mjpeg file in app external cache directory. the directory path is /sdcard/Android/data/com.github.niqdev.ipcam/files
     *
     * @return File
     */
    private fun createMjpegFile(): File? {
        return createSavingFile("video", "mjpeg")
    }

    override fun onFrameCaptured(bitmap: Bitmap) {
        lastBitmap = bitmap
    }

    override fun onFrameCapturedWithHeader(bitmap: ByteArray, header: ByteArray) {
        if (isRecording) {
            try {
                bos!!.write(header)
                bos!!.write(bitmap)
                bos!!.flush()
            } catch (e: IOException) {
                Log.e(TAG, e.message!!)
            }
        }
    }

    companion object {
        private const val TAG = "MjpegRecordingHandler"
    }
}