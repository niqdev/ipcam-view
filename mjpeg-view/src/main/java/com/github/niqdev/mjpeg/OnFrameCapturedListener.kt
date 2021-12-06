package com.github.niqdev.mjpeg

import android.graphics.Bitmap

interface OnFrameCapturedListener {
    fun onFrameCaptured(bitmap: Bitmap)

    //return the byte data for recording
    fun onFrameCapturedWithHeader(bitmap: ByteArray, header: ByteArray)
}
