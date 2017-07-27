package com.github.niqdev.mjpeg;

public interface MjpegView {

    void setSource(MjpegInputStream stream);

    void setDisplayMode(DisplayMode mode);

    void showFps(boolean show);

    void flipSource(boolean flip);

    void stopPlayback();

    boolean isStreaming();

    void setResolution(int width, int height);

    void freeCameraMemory();

    void setOnFrameCapturedListener(OnFrameCapturedListener onFrameCapturedListener);

}
