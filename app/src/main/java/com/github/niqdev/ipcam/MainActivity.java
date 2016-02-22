package com.github.niqdev.ipcam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.niqdev.ipcam.Mjpeg;
import com.github.niqdev.ipcam.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void loadIpcam() {
        Mjpeg.init(Mjpeg.Type.DEFAULT)
            .read("")
            .subscribe();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
