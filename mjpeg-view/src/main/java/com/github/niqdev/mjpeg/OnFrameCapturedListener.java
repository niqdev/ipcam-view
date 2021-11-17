package com.github.niqdev.mjpeg;

import android.graphics.Bitmap;

public interface OnFrameCapturedListener {

    void onFrameCaptured(Bitmap bitmap);
    //return the byte data for recording
    void onFrameCapturedWithHeader(byte[] bitmap,byte[] header);
}