package com.github.niqdev.mjpeg;

import android.graphics.Bitmap;

public interface OnFrameCapturedListener {
    void onEvent(Bitmap bitmap);
}