package com.github.niqdev.mjpeg;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class MjpegView extends SurfaceView implements SurfaceHolder.Callback {

    public final static int POSITION_UPPER_LEFT = 9;
    public final static int POSITION_UPPER_RIGHT = 3;
    public final static int POSITION_LOWER_LEFT = 12;
    public final static int POSITION_LOWER_RIGHT = 6;

    public final static int SIZE_STANDARD = 1;
    public final static int SIZE_BEST_FIT = 4;
    public final static int SIZE_FULLSCREEN = 8;

    public MjpegView(Context context) {
        super(context);
    }

    public MjpegView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // TODO attribute

    // NATIVE
    //isStreaming()
    //stopPlayback
        //freeCameraMemory()
        //setResolution(width, height)
    //setSource(MjpegInputStream)
    //setDisplayMode(MjpegView.SIZE_BEST_FIT)
    //showFps(false)

    // DEFAULT
        //new MjpegView(this)
    //stopPlayback
    //setSource(MjpegInputStream)
    //setDisplayMode(MjpegView.SIZE_BEST_FIT)
    //showFps(true)
}
