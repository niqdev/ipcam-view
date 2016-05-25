package com.github.niqdev.ipcam;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IpCamNativeActivity extends AppCompatActivity {

    @BindView(R.id.mjpegViewNative)
    MjpegView mjpegView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipcam_native);
        ButterKnife.bind(this);

        // TODO if (mjpegView != null) mjpegView.setResolution(width, height);
        loadIpcam();
    }

    private void loadIpcam() {
        Mjpeg.newInstance(Mjpeg.Type.NATIVE)
            //.credential("", "")
            .open("http://wmccpinetop.axiscam.net/mjpg/video.mjpg")
            .subscribe(inputStream -> {
                mjpegView.setSource(inputStream);
                // TODO if (inputStream != null) inputStream.setSkip(1)
                mjpegView.setDisplayMode(DisplayMode.BEST_FIT);
                mjpegView.showFps(true);
            });
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* TODO
        if (mjpegView != null) {
            if (suspending) {
                new DoRead().execute(URL);
                suspending = false;
            }
        }
        */
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mjpegView != null) {
            if (mjpegView.isStreaming()) {
                mjpegView.stopPlayback();
                //suspending = true;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mjpegView != null) {
            mjpegView.freeCameraMemory();
        }
        super.onDestroy();
    }
}
