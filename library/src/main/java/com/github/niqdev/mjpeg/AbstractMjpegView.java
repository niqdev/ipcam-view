package com.github.niqdev.mjpeg;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class AbstractMjpegView extends SurfaceView
        implements SurfaceHolder.Callback, MjpegView {

    public final static int POSITION_UPPER_LEFT = 9;
    public final static int POSITION_UPPER_RIGHT = 3;
    public final static int POSITION_LOWER_LEFT = 12;
    public final static int POSITION_LOWER_RIGHT = 6;

    public final static int SIZE_STANDARD = 1;
    public final static int SIZE_BEST_FIT = 4;
    public final static int SIZE_FULLSCREEN = 8;

    public AbstractMjpegView(Context context) {
        super(context);
    }

    public AbstractMjpegView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // TODO refactor common

}
