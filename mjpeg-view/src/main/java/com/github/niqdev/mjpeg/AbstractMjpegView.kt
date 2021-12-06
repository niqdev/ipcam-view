package com.github.niqdev.mjpeg

import android.view.SurfaceHolder

abstract class AbstractMjpegView : MjpegView {
    abstract fun onSurfaceCreated(holder: SurfaceHolder)
    abstract fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int)
    abstract fun onSurfaceDestroyed(holder: SurfaceHolder)

    companion object {
        protected const val POSITION_UPPER_LEFT = 9
        protected const val POSITION_UPPER_RIGHT = 3
        protected const val POSITION_LOWER_LEFT = 12
        protected const val POSITION_LOWER_RIGHT = 6
        protected const val SIZE_STANDARD = 1
        protected const val SIZE_BEST_FIT = 4
        protected const val SIZE_SCALE_FIT = 16
        protected const val SIZE_FULLSCREEN = 8
    }
}
