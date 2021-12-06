package com.github.niqdev.mjpeg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import androidx.annotation.NonNull;

/*
 * I don't really understand and want to know what the hell it does!
 * Maybe one day I will refactor it ;-)
 * <p/>
 * https://code.google.com/archive/p/android-camera-axis
 */
public class MjpegViewDefault extends AbstractMjpegView {
    private static final String TAG = MjpegViewDefault.class.getSimpleName();

    private final SurfaceHolder.Callback mSurfaceHolderCallback;
    private final SurfaceView mSurfaceView;
    private final boolean transparentBackground;

    private MjpegViewThread thread;
    private MjpegInputStreamDefault mIn = null;
    private boolean showFps = false;
    private boolean flipHorizontal = false;
    private boolean flipVertical = false;
    private float rotateDegrees = 0;
    private volatile boolean mRun = false;
    private volatile boolean surfaceDone = false;
    private Paint overlayPaint;
    private int overlayTextColor;
    private int overlayBackgroundColor;
    private int backgroundColor;
    private int ovlPos;
    private int dispWidth;
    private int dispHeight;
    private int displayMode;
    private boolean resume = false;

    private OnFrameCapturedListener onFrameCapturedListener;

    MjpegViewDefault(SurfaceView surfaceView, SurfaceHolder.Callback callback, boolean transparentBackground) {
        this.mSurfaceView = surfaceView;
        this.mSurfaceHolderCallback = callback;
        this.transparentBackground = transparentBackground;
        init();
    }

    Bitmap flip(Bitmap src) {
        Matrix m = new Matrix();
        float sx = flipHorizontal ? -1 : 1;
        float sy = flipVertical ? -1 : 1;
        m.preScale(sx, sy);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return dst;
    }

    Bitmap rotate(Bitmap src, float degrees) {
        Matrix m = new Matrix();
        m.setRotate(degrees);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
    }

    private void init() {

        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(mSurfaceHolderCallback);
        thread = new MjpegViewThread(holder);
        mSurfaceView.setFocusable(true);
        if (!resume) {
            resume = true;
            overlayPaint = new Paint();
            overlayPaint.setTextAlign(Paint.Align.LEFT);
            overlayPaint.setTextSize(12);
            overlayPaint.setTypeface(Typeface.DEFAULT);
            overlayTextColor = Color.WHITE;
            overlayBackgroundColor = Color.BLACK;
            backgroundColor = Color.BLACK;
            ovlPos = MjpegViewDefault.POSITION_LOWER_RIGHT;
            displayMode = MjpegViewDefault.SIZE_STANDARD;
            dispWidth = mSurfaceView.getWidth();
            dispHeight = mSurfaceView.getHeight();
        }
    }

    /* all methods/constructors below are no more accessible */

    void _startPlayback() {
        if (mIn != null && thread != null) {
            mRun = true;
            /*
             * clear canvas cache
             * @see https://github.com/niqdev/ipcam-view/issues/14
             */
            mSurfaceView.destroyDrawingCache();
            thread.start();
        }
    }

    void _resumePlayback() {
        mRun = true;
        init();
        thread.start();
    }

    /*
     * @see https://github.com/niqdev/ipcam-view/issues/14
     */
    synchronized void _stopPlayback() {
        mRun = false;
        boolean retry = true;
        while (retry) {
            try {
                // make sure the thread is not null
                if (thread != null) {
                    thread.join(500);
                }
                retry = false;
            } catch (InterruptedException e) {
                Log.e(TAG, "error stopping playback thread", e);
            }
        }

        // close the connection
        if (mIn != null) {
            try {
                mIn.close();
            } catch (IOException e) {
                Log.e(TAG, "error closing input stream", e);
            }
            mIn = null;
        }
    }

    void _surfaceChanged(int w, int h) {
        if (thread != null) {
            thread.setSurfaceSize(w, h);
        }
    }

    void _surfaceDestroyed() {
        surfaceDone = false;
        _stopPlayback();
        if (thread != null) {
            thread = null;
        }
    }

    void _frameCapturedWithByteData(byte[] imageByte, byte[] header) {
        if (onFrameCapturedListener != null) {
            onFrameCapturedListener.onFrameCapturedWithHeader(imageByte, header);
        }
    }

    void _frameCapturedWithBitmap(Bitmap bitmap) {
        if (onFrameCapturedListener != null) {
            onFrameCapturedListener.onFrameCaptured(bitmap);
        }
    }

    void _surfaceCreated() {
        surfaceDone = true;
    }

    void _showFps(boolean b) {
        showFps = b;
    }

    void _flipHorizontal(boolean b) {
        flipHorizontal = b;
    }

    void _flipVertical(boolean b) {
        flipVertical = b;
    }

    /*
     * @see https://github.com/niqdev/ipcam-view/issues/14
     */
    void _setSource(MjpegInputStreamDefault source) {
        mIn = source;
        // make sure resume is calling _resumePlayback()
        if (!resume) {
            _startPlayback();
        } else {
            _resumePlayback();
        }
    }

    void setOverlayPaint(Paint p) {
        overlayPaint = p;
    }

    void setOverlayTextColor(int c) {
        overlayTextColor = c;
    }

    void setOverlayBackgroundColor(int c) {
        overlayBackgroundColor = c;
    }

    void setOverlayPosition(int p) {
        ovlPos = p;
    }

    void setDisplayMode(int s) {
        displayMode = s;
    }

    @Override
    public void onSurfaceCreated(@NonNull SurfaceHolder holder) {
        _surfaceCreated();
    }

    /* override methods */

    @Override
    public void onSurfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        _surfaceChanged(width, height);
    }

    @Override
    public void onSurfaceDestroyed(@NonNull SurfaceHolder holder) {
        _surfaceDestroyed();
    }

    @Override
    public void setSource(@NonNull MjpegInputStream stream) {
        if (!(stream instanceof MjpegInputStreamDefault)) {
            throw new IllegalArgumentException("stream must be an instance of MjpegInputStreamDefault");
        }
        _setSource((MjpegInputStreamDefault) stream);
    }

    @Override
    public void setDisplayMode(DisplayMode mode) {
        setDisplayMode(mode.getValue());
    }

    @Override
    public void showFps(boolean show) {
        _showFps(show);
    }

    @Override
    public void flipSource(boolean flip) {
        _flipHorizontal(flip);
    }

    @Override
    public void flipHorizontal(boolean flip) {
        _flipHorizontal(flip);
    }

    @Override
    public void flipVertical(boolean flip) {
        _flipVertical(flip);
    }

    @Override
    public void setRotate(float degrees) {
        rotateDegrees = degrees;
    }

    @Override
    public void stopPlayback() {
        _stopPlayback();
    }

    @Override
    public boolean isStreaming() {
        return mRun;
    }

    @Override
    public void setResolution(int width, int height) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void freeCameraMemory() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void setOnFrameCapturedListener(@NonNull OnFrameCapturedListener onFrameCapturedListener) {
        this.onFrameCapturedListener = onFrameCapturedListener;
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

    @NonNull
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

    // no more accessible
    class MjpegViewThread extends Thread {
        private final SurfaceHolder mSurfaceHolder;
        private int frameCounter = 0;
        private Bitmap ovl;

        // no more accessible
        MjpegViewThread(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;
        }

        private Rect destRect(int bmw, int bmh) {

            int tempx;
            int tempy;
            if (displayMode == MjpegViewDefault.SIZE_STANDARD) {
                tempx = (dispWidth / 2) - (bmw / 2);
                tempy = (dispHeight / 2) - (bmh / 2);
                return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
            }
            if (displayMode == MjpegViewDefault.SIZE_BEST_FIT) {
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
            if (displayMode == MjpegViewDefault.SIZE_SCALE_FIT) {
                float bmasp = ((float) bmw / (float) bmh);
                tempx = 0;
                tempy = 0;
                if (bmw < dispWidth) {
                    bmw = dispWidth;
                    // cross-multiplication using aspect ratio
                    bmh = (int) (dispWidth / bmasp);
                    // set it to the center height
                    tempy = (dispHeight - bmh) / 4;
                }
                return new Rect(tempx, tempy, bmw, bmh + tempy);
            }
            if (displayMode == MjpegViewDefault.SIZE_FULLSCREEN)
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

        private Bitmap makeFpsOverlay(Paint p, String text) {
            Rect b = new Rect();
            p.getTextBounds(text, 0, text.length(), b);
            int bwidth = b.width() + 2;
            int bheight = b.height() + 2;
            Bitmap bm = Bitmap.createBitmap(bwidth, bheight,
                    Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bm);
            p.setColor(overlayBackgroundColor);
            c.drawRect(0, 0, bwidth, bheight, p);
            p.setColor(overlayTextColor);
            c.drawText(text, -b.left + 1,
                    (bheight / 2) - ((p.ascent() + p.descent()) / 2) + 1, p);
            return bm;
        }

        public void run() {
            long start = System.currentTimeMillis();
            PorterDuffXfermode mode = new PorterDuffXfermode(
                    PorterDuff.Mode.DST_OVER);
            Bitmap bm;
            int width;
            int height;
            Rect destRect;
            Canvas c = null;
            Paint p = new Paint();
            String fps;
            while (mRun) {
                if (surfaceDone) {
                    try {
                        c = mSurfaceHolder.lockCanvas();

                        if (c == null) {
                            Log.w(TAG, "null canvas, skipping render");
                            continue;
                        }
                        synchronized (mSurfaceHolder) {
                            try {
                                byte[] header = mIn.readHeader();
                                byte[] imageData = mIn.readMjpegFrame(header);
                                bm = BitmapFactory.decodeStream(new ByteArrayInputStream(imageData));
                                if (flipHorizontal || flipVertical)
                                    bm = flip(bm);
                                if (rotateDegrees != 0)
                                    bm = rotate(bm, rotateDegrees);

                                _frameCapturedWithByteData(imageData, header);
                                _frameCapturedWithBitmap(bm);
                                destRect = destRect(bm.getWidth(),
                                        bm.getHeight());

                                if (transparentBackground) {
                                    c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                                } else {
                                    c.drawColor(backgroundColor);
                                }

                                c.drawBitmap(bm, null, destRect, p);

                                if (showFps) {
                                    p.setXfermode(mode);
                                    if (ovl != null) {
                                        height = ((ovlPos & 1) == 1) ? destRect.top
                                                : destRect.bottom
                                                - ovl.getHeight();
                                        width = ((ovlPos & 8) == 8) ? destRect.left
                                                : destRect.right
                                                - ovl.getWidth();
                                        c.drawBitmap(ovl, width, height, null);
                                    }
                                    p.setXfermode(null);
                                    frameCounter++;
                                    if ((System.currentTimeMillis() - start) >= 1000) {
                                        fps = frameCounter
                                                + "fps";
                                        frameCounter = 0;
                                        start = System.currentTimeMillis();
                                        ovl = makeFpsOverlay(overlayPaint, fps);
                                    }
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "encountered exception during render", e);
                            }
                        }
                    } finally {
                        if (c != null) {
                            mSurfaceHolder.unlockCanvasAndPost(c);
                        } else {
                            Log.w(TAG, "couldn't unlock surface canvas");
                        }
                    }
                }
            }
        }
    }
}
