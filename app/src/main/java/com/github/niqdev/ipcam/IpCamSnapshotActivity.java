package com.github.niqdev.ipcam;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegView;
import com.github.niqdev.mjpeg.OnFrameCapturedListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.github.niqdev.ipcam.settings.SettingsActivity.PREF_AUTH_PASSWORD;
import static com.github.niqdev.ipcam.settings.SettingsActivity.PREF_AUTH_USERNAME;
import static com.github.niqdev.ipcam.settings.SettingsActivity.PREF_IPCAM_URL;

public class IpCamSnapshotActivity extends AppCompatActivity implements OnFrameCapturedListener {

    private static final int TIMEOUT = 5;

    private Bitmap lastPreview = null;

    @BindView(R.id.mjpegViewSnapshot)
    MjpegView mjpegView;

    @BindView(R.id.imageView)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipcam_snapshot);
        ButterKnife.bind(this);
        mjpegView.setOnFrameCapturedListener(this);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (lastPreview != null) {
                            imageView.setImageBitmap(lastPreview);
                        }
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_capture, menu);
        return true;
    }

    @Override
    public void onFrameCaptured(Bitmap bitmap) {
        lastPreview = bitmap;
    }
}