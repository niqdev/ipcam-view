package com.github.niqdev.mjpeg;

import android.view.SurfaceView;

public interface MjpegView {

    void setSource(MjpegInputStream stream);

    void setDisplayMode(DisplayMode mode);

    void showFps(boolean show);

    void flipSource(boolean flip);

    void flipHorizontal(boolean flip);

    void flipVertical(boolean flip);

    void stopPlayback();

    boolean isStreaming();

    void setResolution(int width, int height);

    void freeCameraMemory();

    void setOnFrameCapturedListener(OnFrameCapturedListener onFrameCapturedListener);

    void setCustomBackgroundColor(int backgroundColor);

    void setFpsOverlayBackgroundColor(int overlayBackgroundColor);

    void setFpsOverlayTextColor(int overlayTextColor);

    SurfaceView getSurfaceView();

    void resetTransparentBackground();

    void setTransparentBackground();

    void clearStream();
}
