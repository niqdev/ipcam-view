package com.github.niqdev.ipcam;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class MjpegView extends SurfaceView implements SurfaceHolder.Callback {

    public MjpegView(Context context) {
        super(context);
    }

    public MjpegView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
