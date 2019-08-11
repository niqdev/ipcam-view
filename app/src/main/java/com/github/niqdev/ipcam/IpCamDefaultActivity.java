package com.github.niqdev.ipcam;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.github.niqdev.ipcam.settings.SettingsActivity.PREF_AUTH_PASSWORD;
import static com.github.niqdev.ipcam.settings.SettingsActivity.PREF_AUTH_USERNAME;
import static com.github.niqdev.ipcam.settings.SettingsActivity.PREF_FLIP_HORIZONTAL;
import static com.github.niqdev.ipcam.settings.SettingsActivity.PREF_FLIP_VERTICAL;
import static com.github.niqdev.ipcam.settings.SettingsActivity.PREF_IPCAM_URL;
import static com.github.niqdev.ipcam.settings.SettingsActivity.PREF_ROTATE_DEGREES;

public class IpCamDefaultActivity extends AppCompatActivity {

    private static final int TIMEOUT = 5;

    @BindView(R.id.mjpegViewDefault)
    MjpegView mjpegView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipcam_default);
        ButterKnife.bind(this);
    }

    private SharedPreferences getSharedPreferences() {
        return PreferenceManager
                .getDefaultSharedPreferences(this);
    }

    private String getPreference(String key) {
        return getSharedPreferences()
            .getString(key, "");
    }

    private Boolean getBooleanPreference(String key) {
        return getSharedPreferences()
                .getBoolean(key, false);
    }

    private DisplayMode calculateDisplayMode() {
        int orientation = getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_LANDSCAPE ?
            DisplayMode.FULLSCREEN : DisplayMode.BEST_FIT;
    }

    private void loadIpCam() {
        Mjpeg.newInstance()
            .credential(getPreference(PREF_AUTH_USERNAME), getPreference(PREF_AUTH_PASSWORD))
            .open(getPreference(PREF_IPCAM_URL), TIMEOUT)
            .subscribe(
                inputStream -> {
                    mjpegView.setSource(inputStream);
                    mjpegView.setDisplayMode(calculateDisplayMode());
                    mjpegView.flipHorizontal(getBooleanPreference(PREF_FLIP_HORIZONTAL));
                    mjpegView.flipVertical(getBooleanPreference(PREF_FLIP_VERTICAL));
                    mjpegView.setRotate(Float.parseFloat(getPreference(PREF_ROTATE_DEGREES)));
                    mjpegView.showFps(true);
                },
                throwable -> {
                    Log.e(getClass().getSimpleName(), "mjpeg error", throwable);
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIpCam();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mjpegView.stopPlayback();
    }

}
