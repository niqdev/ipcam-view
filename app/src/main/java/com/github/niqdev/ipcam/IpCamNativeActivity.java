package com.github.niqdev.ipcam;

import android.os.Bundle;
import android.view.View;

import com.github.niqdev.ipcam.databinding.ActivityIpcamNativeBinding;
import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;

import androidx.appcompat.app.AppCompatActivity;

public class IpCamNativeActivity extends AppCompatActivity {

    ActivityIpcamNativeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIpcamNativeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // TODO if (mjpegView != null) mjpegView.setResolution(width, height);
        loadIpcam();
    }

    private void loadIpcam() {
        Mjpeg.newInstance(Mjpeg.Type.NATIVE)
                //.credential("", "")
                .open("http://wmccpinetop.axiscam.net/mjpg/video.mjpg")
                .subscribe(inputStream -> {
                    binding.mjpegViewNative.setSource(inputStream);
                    // TODO if (inputStream != null) inputStream.setSkip(1)
                    binding.mjpegViewNative.setDisplayMode(DisplayMode.BEST_FIT);
                    binding.mjpegViewNative.showFps(true);
                });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (binding.mjpegViewNative.isStreaming()) {
            binding.mjpegViewNative.stopPlayback();
            //suspending = true;
        }
    }

    @Override
    protected void onDestroy() {
        binding.mjpegViewNative.freeCameraMemory();
        super.onDestroy();
    }
}
