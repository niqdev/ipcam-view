package com.github.niqdev.mjpeg;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/*
 * I don't really understand and want to know what the hell it does!
 * Maybe one day I will refactor it ;-)
 * <p/>
 * https://bitbucket.org/neuralassembly/simplemjpegview
 */
public class MjpegViewNative extends AbstractMjpegView {
    private static final String TAG = MjpegViewDefault.class.getSimpleName();

    private SurfaceHolder.Callback mSurfaceHolderCallback;
    private SurfaceView mSurfaceView;
    private boolean transparentBackground;

    private SurfaceHolder holder;

    private MjpegViewThread thread;
    private MjpegInputStreamNative mIn = null;
    private boolean showFps = false;
    private boolean mRun = false;
    private boolean surfaceDone = false;

    private Paint overlayPaint;
    private int overlayTextColor;
    private int overlayBackgroundColor;
    private int backgroundColor;
    private int ovlPos;
    private int dispWidth;
    private int dispHeight;
    private int displayMode;

    private boolean suspending = false;

    private Bitmap bmp = null;

    private int IMG_WIDTH = 640;
    private int IMG_HEIGHT = 480;

    // no more accessible
    class MjpegViewThread extends Thread {
        private SurfaceHolder mSurfaceHolder;
        private int frameCounter = 0;
        private long start;
        private String fps = "";

        // no more accessible
        MjpegViewThread(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;
        }

        private Rect destRect(int bmw, int bmh) {
            int tempx;
            int tempy;
            if (displayMode == MjpegViewNative.SIZE_STANDARD) {
                tempx = (dispWidth / 2) - (bmw / 2);
                tempy = (dispHeight / 2) - (bmh / 2);
                return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
            }
            if (displayMode == MjpegViewNative.SIZE_BEST_FIT) {
                float bmasp = (float) bmw / (float) bmh;
                bmw = dispWidth;
                bmh = (int) (dispWidth / bmasp);
                if (bmh > dispHeight) {
                    bmh = dispHeight;
                    bmw = (int) (dispHeight * bmasp);
                }
                tempx = (dispWidth / 2) - (bmw / 2);
                tempy = (dispHeight / 2) - (bmh / 2);
                return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
            }
            if (displayMode == MjpegViewNative.SIZE_FULLSCREEN)
                return new Rect(0, 0, dispWidth, dispHeight);
            return null;
        }

        // no more accessible
        void setSurfaceSize(int width, int height) {
            synchronized (mSurfaceHolder) {
                dispWidth = width;
                dispHeight = height;
            }
        }

        private Bitmap makeFpsOverlay(Paint p) {
            Rect b = new Rect();
            p.getTextBounds(fps, 0, fps.length(), b);

            // false indentation to fix forum layout
            Bitmap bm = Bitmap.createBitmap(b.width(), b.height(), Bitmap.Config.ARGB_8888);

            Canvas c = new Canvas(bm);
            p.setColor(overlayBackgroundColor);
            c.drawRect(0, 0, b.width(), b.height(), p);
            p.setColor(overlayTextColor);
            c.drawText(fps, -b.left, b.bottom - b.top - p.descent(), p);
            return bm;
        }

        public void run() {
            start = System.currentTimeMillis();
            PorterDuffXfermode mode = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);

            int width;
            int height;
            Paint p = new Paint();
            Bitmap ovl = null;

            while (mRun) {

                Rect destRect = null;
                Canvas c = null;

                if (surfaceDone) {
                    try {
                        if (bmp == null) {
                            bmp = Bitmap.createBitmap(IMG_WIDTH, IMG_HEIGHT, Bitmap.Config.ARGB_8888);
                        }
                        int ret = mIn.readMjpegFrame(bmp);

                        if (ret == -1) {
                            // TODO error
                            //((MjpegActivity) saved_context).setImageError();
                            return;
                        }

                        destRect = destRect(bmp.getWidth(), bmp.getHeight());

                        c = mSurfaceHolder.lockCanvas();
                        synchronized (mSurfaceHolder) {
                            if (transparentBackground) {
                                c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                            } else {
                                c.drawColor(backgroundColor);
                            }

                            c.drawBitmap(bmp, null, destRect, p);

                            if (showFps) {
                                p.setXfermode(mode);
                                if (ovl != null) {

                                    // false indentation to fix forum layout
                                    height = ((ovlPos & 1) == 1) ? destRect.top : destRect.bottom - ovl.getHeight();
                                    width = ((ovlPos & 8) == 8) ? destRect.left : destRect.right - ovl.getWidth();

                                    c.drawBitmap(ovl, width, height, null);
                                }
                                p.setXfermode(null);
                                frameCounter++;
                                if ((System.currentTimeMillis() - start) >= 1000) {
                                    fps = String.valueOf(frameCounter) + "fps";
                                    frameCounter = 0;
                                    start = System.currentTimeMillis();
                                    if (ovl != null) ovl.recycle();

                                    ovl = makeFpsOverlay(overlayPaint);
                                }
                            }


                        }

                    } catch (IOException e) {

                    } finally {
                        if (c != null) mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }

    private void init() {

        //SurfaceHolder holder = getHolder();
        holder = mSurfaceView.getHolder();
        holder.addCallback(mSurfaceHolderCallback);
        thread = new MjpegViewThread(holder);
        mSurfaceView.setFocusable(true);
        overlayPaint = new Paint();
        overlayPaint.setTextAlign(Paint.Align.LEFT);
        overlayPaint.setTextSize(12);
        overlayPaint.setTypeface(Typeface.DEFAULT);
        overlayTextColor = Color.WHITE;
        overlayBackgroundColor = Color.BLACK;
        backgroundColor = Color.BLACK;
        ovlPos = MjpegViewNative.POSITION_LOWER_RIGHT;
        displayMode = MjpegViewNative.SIZE_STANDARD;
        dispWidth = mSurfaceView.getWidth();
        dispHeight = mSurfaceView.getHeight();
    }

    /* all methods/constructors below are no more accessible */

    void _startPlayback() {
        if (mIn != null) {
            mRun = true;
            if (thread == null) {
                thread = new MjpegViewThread(holder);
            }
            thread.start();
        }
    }

    void _resumePlayback() {
        if (suspending) {
            if (mIn != null) {
                mRun = true;
                SurfaceHolder holder = mSurfaceView.getHolder();
                holder.addCallback(mSurfaceHolderCallback);
                thread = new MjpegViewThread(holder);
                thread.start();
                suspending = false;
            }
        }
    }

    void _stopPlayback() {
        if (mRun) {
            suspending = true;
        }
        mRun = false;
        if (thread != null) {
            boolean retry = true;
            while (retry) {
                try {
                    thread.join();
                    retry = false;
                } catch (InterruptedException e) {
                }
            }
            thread = null;
        }
        if (mIn != null) {
            try {
                mIn.close();
            } catch (IOException e) {
            }
            mIn = null;
        }

    }

    void _freeCameraMemory() {
        if (mIn != null) {
            mIn.freeCameraMemory();
        }
    }

    void _surfaceChanged(SurfaceHolder holder, int f, int w, int h) {
        if (thread != null) {
            thread.setSurfaceSize(w, h);
        }
    }

    void _surfaceDestroyed(SurfaceHolder holder) {
        surfaceDone = false;
        _stopPlayback();
        if (thread != null) {
            thread = null;
        }
    }

    MjpegViewNative(SurfaceView surfaceView, SurfaceHolder.Callback callback, boolean transparentBackground) {
        this.mSurfaceView = surfaceView;
        this.mSurfaceHolderCallback = callback;
        this.transparentBackground = transparentBackground;
        init();
    }

    void _surfaceCreated(SurfaceHolder holder) {
        surfaceDone = true;
    }

    void _showFps(boolean b) {
        showFps = b;
    }

    void _setSource(MjpegInputStreamNative source) {
        mIn = source;
        if (!suspending) {
            _startPlayback();
        } else {
            _resumePlayback();
        }
    }

    void _setOverlayPaint(Paint p) {
        overlayPaint = p;
    }

    void _setOverlayTextColor(int c) {
        overlayTextColor = c;
    }

    void _setOverlayBackgroundColor(int c) {
        overlayBackgroundColor = c;
    }

    void _setOverlayPosition(int p) {
        ovlPos = p;
    }

    void _setDisplayMode(int s) {
        displayMode = s;
    }

    void _setResolution(int w, int h) {
        IMG_WIDTH = w;
        IMG_HEIGHT = h;
    }

    boolean _isStreaming() {
        return mRun;
    }

    /* override methods */

    @Override
    public void onSurfaceCreated(SurfaceHolder holder) {
        _surfaceCreated(holder);
    }

    @Override
    public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        _surfaceChanged(holder, format, width, height);
    }

    @Override
    public void onSurfaceDestroyed(SurfaceHolder holder) {
        _surfaceDestroyed(holder);
    }

    @Override
    public void setSource(MjpegInputStream stream) {
        if (!(stream instanceof MjpegInputStreamNative)) {
            throw new IllegalArgumentException("stream must be an instance of MjpegInputStreamNative");
        }
        _setSource((MjpegInputStreamNative) stream);
    }

    @Override
    public void setDisplayMode(DisplayMode mode) {
        _setDisplayMode(mode.getValue());
    }

    @Override
    public void showFps(boolean show) {
        _showFps(show);
    }

    @Override
    public void flipSource(boolean flip) {
        flipHorizontal(flip);
    }

    @Override
    public void flipHorizontal(boolean flip) {

    }

    @Override
    public void flipVertical(boolean flip) {

    }

    @Override
    public void stopPlayback() {
        _stopPlayback();
    }

    @Override
    public boolean isStreaming() {
        return _isStreaming();
    }

    @Override
    public void setResolution(int width, int height) {
        _setResolution(width, height);
    }

    @Override
    public void freeCameraMemory() {
        _freeCameraMemory();
    }


    @Override
    public void setOnFrameCapturedListener(OnFrameCapturedListener onFrameCapturedListener) {
        throw new UnsupportedOperationException("Not implemented yet!");
    }

    @Override
    public void setCustomBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void setFpsOverlayBackgroundColor(int overlayBackgroundColor) {
        this.overlayBackgroundColor = overlayBackgroundColor;
    }

    @Override
    public void setFpsOverlayTextColor(int overlayTextColor) {
        this.overlayTextColor = overlayTextColor;
    }

    @Override
    public SurfaceView getSurfaceView() {
        return mSurfaceView;
    }

    @Override
    public void resetTransparentBackground() {
        mSurfaceView.setZOrderOnTop(false);
        mSurfaceView.getHolder().setFormat(PixelFormat.OPAQUE);
    }

    @Override
    public void setTransparentBackground() {
        mSurfaceView.setZOrderOnTop(true);
        mSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
    }

    @Override
    public void clearStream() {
        Canvas c = null;

        try {
            c = mSurfaceView.getHolder().lockCanvas();
            c.drawColor(0, PorterDuff.Mode.CLEAR);
        } finally {
            if (c != null) {
                mSurfaceView.getHolder().unlockCanvasAndPost(c);
            } else {
                Log.w(TAG, "couldn't unlock surface canvas");
            }
        }
    }
}
