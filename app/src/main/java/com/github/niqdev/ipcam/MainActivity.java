package com.github.niqdev.ipcam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.github.niqdev.ipcam.settings.SettingsActivity;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindString(R.string.action_bar_title)
    String actionBarTitle;

    @Bind(R.id.buttonDefault)
    Button buttonDefault;

    @Bind(R.id.buttonNative)
    Button buttonNative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle(actionBarTitle);
        verifySettings();
    }

    private void verifySettings() {
        buttonDefault.setEnabled(false);
        buttonNative.setEnabled(false);
    }

    @OnClick(R.id.buttonDefault)
    public void onClickDefault() {
        startActivity(new Intent(this, IpCamDefaultActivity.class));
    }

    @OnClick(R.id.buttonNative)
    public void onClickNative() {
        startActivity(new Intent(this, IpCamNativeActivity.class));
    }

    @OnClick(R.id.buttonSettings)
    public void onClickSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

}
