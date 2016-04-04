package com.github.niqdev.ipcam;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.niqdev.ipcam.settings.SettingsActivity;
import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegView;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IpCamDefaultActivity extends AppCompatActivity {

    @Bind(R.id.mjpegViewDefault)
    MjpegView mjpegView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipcam_default);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIpcam();
    }

    private void loadIpcam() {
        Mjpeg.newInstance()
            .open(PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString(SettingsActivity.PREF_IPCAM_URL, ""))
            .timeout(2, TimeUnit.SECONDS)
            .subscribe(
                inputStream -> {
                    mjpegView.setSource(inputStream);
                    mjpegView.setDisplayMode(DisplayMode.BEST_FIT);
                    mjpegView.showFps(true);
                },
                throwable -> {
                    Log.e(getClass().getSimpleName(), "mjpeg error", throwable);
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mjpegView.stopPlayback();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
