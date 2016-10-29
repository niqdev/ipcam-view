package com.github.niqdev.ipcam;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREF_IPCAM_URL = "com.github.niqdev.ipcam.SettingsActivity.IPCAM_URL";
    public static final String PREF_AUTH_USERNAME = "com.github.niqdev.ipcam.SettingsActivity.PREF_AUTH_USERNAME";
    public static final String PREF_AUTH_PASSWORD = "com.github.niqdev.ipcam.SettingsActivity.PREF_AUTH_PASSWORD";

    @BindView(R.id.editTextUrl)
    EditText editTextUrl;

    @BindView(R.id.editTextUsername)
    EditText editTextUsername;

    @BindView(R.id.editTextPassword)
    EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.buttonSettings)
    public void onClickSave() {
        // no validation
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_IPCAM_URL, editTextUrl.getText().toString());
        editor.putString(PREF_AUTH_USERNAME, editTextUrl.getText().toString());
        editor.putString(PREF_AUTH_PASSWORD, editTextUrl.getText().toString());
        editor.apply();
    }
}
