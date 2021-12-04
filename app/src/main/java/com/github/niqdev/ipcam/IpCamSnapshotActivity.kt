package com.github.niqdev.ipcam

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.niqdev.ipcam.databinding.ActivityIpcamSnapshotBinding
import com.github.niqdev.ipcam.settings.SettingsActivity
import com.github.niqdev.mjpeg.DisplayMode
import com.github.niqdev.mjpeg.Mjpeg
import com.github.niqdev.mjpeg.MjpegInputStream
import com.github.niqdev.mjpeg.MjpegRecordingHandler
import java.util.*

class IpCamSnapshotActivity : AppCompatActivity() {

    private var timer = Timer()
    var cnt = 0
    private lateinit var recordingHandler: MjpegRecordingHandler

    private lateinit var binding: ActivityIpcamSnapshotBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIpcamSnapshotBinding.inflate(layoutInflater)

        recordingHandler = MjpegRecordingHandler(this)
        binding.mjpegViewSnapshot.setOnFrameCapturedListener(recordingHandler)
        binding.recordText.text = "00:00:00"
    }

    private fun getPreference(key: String): String? = PreferenceManager
        .getDefaultSharedPreferences(this)
        .getString(key, "")

    private fun calculateDisplayMode(): DisplayMode {
        val orientation = resources.configuration.orientation
        return if (orientation == Configuration.ORIENTATION_LANDSCAPE)
            DisplayMode.FULLSCREEN
        else
            DisplayMode.BEST_FIT
    }

    private fun loadIpCam() {
        Mjpeg.newInstance()
            .credential(getPreference(SettingsActivity.PREF_AUTH_USERNAME), getPreference(SettingsActivity.PREF_AUTH_PASSWORD))
            .open(getPreference(SettingsActivity.PREF_IPCAM_URL), TIMEOUT)
            .subscribe(
                { inputStream: MjpegInputStream ->
                    binding.mjpegViewSnapshot.setSource(inputStream)
                    binding.mjpegViewSnapshot.setDisplayMode(calculateDisplayMode())
                    binding.mjpegViewSnapshot.showFps(true)
                }
            ) { throwable: Throwable ->
                Log.e(javaClass.simpleName, "mjpeg error", throwable)
                Toast.makeText(this, "Error ${throwable.javaClass.simpleName}\n${getPreference(SettingsActivity.PREF_IPCAM_URL)}", Toast.LENGTH_LONG)
                    .show()
            }
    }

    private fun getStringTime(cnt: Int): String {
        val hour = cnt / 3600
        val min = cnt % 3600 / 60
        val second = cnt % 60
        return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, min, second)
    }

    override fun onResume() {
        super.onResume()
        loadIpCam()
    }

    override fun onPause() {
        super.onPause()
        binding.mjpegViewSnapshot.stopPlayback()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_capture -> {
                runOnUiThread {
                    if (recordingHandler.lastBitmap != null) {
                        binding.imageView.visibility = View.VISIBLE
                        binding.imageView.setImageBitmap(recordingHandler.lastBitmap)
                        recordingHandler.saveBitmapToFile()
                    }
                }
                true
            }
            R.id.action_recording -> {
                if (!recordingHandler.isRecording) {
                    startRecording()
                } else {
                    stopRecording()
                }
                item.setIcon(if (recordingHandler.isRecording) R.drawable.recording else R.drawable.ic_videocam_white_48dp)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun stopRecording() {
        recordingHandler.stopRecording()
        timer.cancel()
        timer.purge()
        binding.recordText.visibility = View.GONE
    }

    @SuppressLint("SetTextI18n")
    private fun startRecording() {
        cnt = 0
        timer = Timer()
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    binding.recordText.visibility = View.VISIBLE
                    binding.recordText.text = getStringTime(cnt++)
                }
            }
        }
        timer.schedule(timerTask, 0, 1000)
        binding.recordText.visibility = View.VISIBLE
        binding.recordText.text = "00:00:00"
        recordingHandler.startRecording()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_capture, menu)
        return true
    }

    companion object {
        private const val TIMEOUT = 5
    }
}