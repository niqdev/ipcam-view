package com.github.niqdev.ipcam;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegView;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.github.niqdev.ipcam.settings.SettingsActivity.PREF_AUTH_PASSWORD;
import static com.github.niqdev.ipcam.settings.SettingsActivity.PREF_AUTH_USERNAME;
import static com.github.niqdev.ipcam.settings.SettingsActivity.PREF_IPCAM_URL;

public class IpCamSnapshotActivity extends AppCompatActivity  {

    private static final int TIMEOUT = 5;
    private static final String TAG = "IpCamSnapshotActivity";

    @BindView(R.id.mjpegViewSnapshot)
    MjpegView mjpegView;
    @BindView(R.id.imageView)
    ImageView snapshotView;
    @BindView(R.id.record_text)
    TextView timerText;
    private Timer timer = new Timer();
    int cnt = 0;
    private MjpegRecordingHandler recordingHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipcam_snapshot);
        ButterKnife.bind(this);
        recordingHandler=new MjpegRecordingHandler(this);
        mjpegView.setOnFrameCapturedListener(recordingHandler);
        timerText.setText("00:00:00");
    }

    private String getPreference(String key) {
        return PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString(key, "");
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
                            mjpegView.showFps(true);
                        },
                        throwable -> {
                            Log.e(getClass().getSimpleName(), "mjpeg error", throwable);
                            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                        });
    }

    private String getStringTime(int cnt) {
        int hour = cnt / 3600;
        int min = cnt % 3600 / 60;
        int second = cnt % 60;
        return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, min, second);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_capture:
                runOnUiThread(() -> {
                    if (recordingHandler.getLastBitmap() != null) {
                        snapshotView.setVisibility(View.VISIBLE);
                        snapshotView.setImageBitmap(recordingHandler.getLastBitmap());
                        recordingHandler.saveBitmapToFile();
                    }
                });
                return true;
            case R.id.action_recording:
                if (!recordingHandler.isRecording()) {
                    startRecording();
                } else {
                    stopRecording();
                }
                item.setIcon(recordingHandler.isRecording() ? R.drawable.recording : R.drawable.ic_videocam_white_48dp);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void stopRecording() {
        recordingHandler.stopRecording();
        timer.cancel();
        timer.purge();
        timer = null;
        timerText.setVisibility(View.GONE);

    }

    private void startRecording() {
        cnt = 0;
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    timerText.setVisibility(View.VISIBLE);
                    timerText.setText(getStringTime(cnt++));
                });
            }
        };
        timer.schedule(timerTask, 0, 1000);
        timerText.setVisibility(View.VISIBLE);
        timerText.setText("00:00:00");
        recordingHandler.startRecording();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_capture, menu);
        return true;
    }
}