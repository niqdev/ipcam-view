package com.github.niqdev.mjpeg;

public interface MjpegView {

    void setSource(MjpegInputStream stream);

    void setDisplayMode(DisplayMode mode);

    void showFps(boolean show);

    void stopPlayback();



    // NATIVE
    //isStreaming()
        //stopPlayback
    //freeCameraMemory() ------ on stopPlayback
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
