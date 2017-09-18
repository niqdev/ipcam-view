package com.github.niqdev.ipcam;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity to show the possibilities of transparent stream background
 * and the actions which need to be done when hiding and showing the
 * stream with transparent background
 */
public class IpCamCustomAppearanceActivity extends AppCompatActivity {

    private static final int TIMEOUT = 5;

    @BindView(R.id.mjpegViewCustomAppearance)
    MjpegView mjpegView;

    @BindView(R.id.layoutProgressWrapper)
    LinearLayout progressWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ipcam_custom_appearance);
        ButterKnife.bind(this);
    }

    private void loadIpCam() {
        progressWrapper.setVisibility(View.VISIBLE);

        Mjpeg.newInstance()
            .open("http://cascam.ou.edu/axis-cgi/mjpg/video.cgi?resolution=320x240", TIMEOUT)
            .subscribe(
                inputStream -> {
                    progressWrapper.setVisibility(View.GONE);
                    mjpegView.setFpsOverlayBackgroundColor(Color.DKGRAY);
                    mjpegView.setFpsOverlayTextColor(Color.WHITE);
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
            case R.id.action_toggleStream:
                if (((View) mjpegView).getVisibility() == View.VISIBLE) {
                    mjpegView.stopPlayback();
                    mjpegView.clearStream();
                    mjpegView.resetTransparentBackground();

                    ((View) mjpegView).setVisibility(View.GONE);

                    item.setIcon(R.drawable.ic_videocam_white_48dp);
                    item.setTitle(getString(R.string.menu_toggleStreamOn));
                } else {
                    mjpegView.setTransparentBackground();
                    ((View) mjpegView).setVisibility(View.VISIBLE);

                    item.setIcon(R.drawable.ic_videocam_off_white_48dp);
                    item.setTitle(getString(R.string.menu_toggleStreamOff));

                    loadIpCam();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_custom_appearance, menu);
        return true;
    }
}
