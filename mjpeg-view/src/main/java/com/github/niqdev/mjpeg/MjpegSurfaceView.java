package com.github.niqdev.mjpeg;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.StyleableRes;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MjpegSurfaceView extends SurfaceView implements SurfaceHolder.Callback, MjpegView {

    private MjpegView mMjpegView;

    private static final int DEFAULT_TYPE = 0;

    // issue in attrs.xml - verify reserved keywords
    private static final SparseArray<Mjpeg.Type> TYPE;
    static {
        TYPE = new SparseArray<>();
        TYPE.put(0, Mjpeg.Type.DEFAULT);
        TYPE.put(1, Mjpeg.Type.NATIVE);
    }

    public MjpegSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        switch (getPropertyType(attrs, R.styleable.MjpegSurfaceView, R.styleable.MjpegSurfaceView_type)) {
            case DEFAULT:
                mMjpegView = new MjpegViewDefault(this, this);
                break;
            case NATIVE:
                mMjpegView = new MjpegViewNative(this, this);
                break;
        }
    }

    public Mjpeg.Type getPropertyType(AttributeSet attributeSet, @StyleableRes int[] attrs, int attrIndex) {
        TypedArray typedArray = getContext().getTheme()
            .obtainStyledAttributes(attributeSet, attrs, 0, 0);
        try {
            int typeIndex = typedArray.getInt(attrIndex, DEFAULT_TYPE);
            Mjpeg.Type type = TYPE.get(typeIndex);
            return type != null ? type : TYPE.get(DEFAULT_TYPE);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        ((AbstractMjpegView) mMjpegView).onSurfaceCreated(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        ((AbstractMjpegView) mMjpegView).onSurfaceChanged(holder, format, width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        ((AbstractMjpegView) mMjpegView).onSurfaceDestroyed(holder);
    }

    @Override
    public void setSource(MjpegInputStream stream) {
        mMjpegView.setSource(stream);
    }

    @Override
    public void setDisplayMode(DisplayMode mode) {
        mMjpegView.setDisplayMode(mode);
    }

    @Override
    public void showFps(boolean show) {
        mMjpegView.showFps(show);
    }

    @Override
    public void stopPlayback() {
        mMjpegView.stopPlayback();
    }

    @Override
    public boolean isStreaming() {
        return mMjpegView.isStreaming();
    }

    @Override
    public void setResolution(int width, int height) {
        mMjpegView.setResolution(width, height);
    }

    @Override
    public void freeCameraMemory() {
        mMjpegView.freeCameraMemory();
    }

}
