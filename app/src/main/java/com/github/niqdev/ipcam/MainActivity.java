package com.github.niqdev.ipcam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;

import com.github.niqdev.ipcam.settings.SettingsActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.buttonDefault)
    Button buttonDefault;

    @Bind(R.id.buttonNative)
    Button buttonNative;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // load default values first time
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
        verifySettings();
    }

    private void verifySettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (TextUtils.isEmpty(prefs.getString(SettingsActivity.PREF_IPCAM_URL, ""))) {
            buttonDefault.setEnabled(false);
        }

        // TODO disabled
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
