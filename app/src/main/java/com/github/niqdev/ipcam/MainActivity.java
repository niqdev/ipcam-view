package com.github.niqdev.ipcam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.buttonDefault)
    public void onClickDefault() {
        startActivity(new Intent(this, IpCamDefaultActivity.class));
    }

    @OnClick(R.id.buttonNative)
    public void onClickNative() {
        startActivity(new Intent(this, IpCamNativeActivity.class));
    }

}
