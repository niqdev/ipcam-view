package com.github.niqdev.ipcam;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;
import com.github.niqdev.mjpeg.OnFrameCapturedListener;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MjpegRecordingHandler implements OnFrameCapturedListener {
    private static final String TAG = "MjpegRecordingHandler";
    private Context context;
    private BufferedOutputStream bos;
    private boolean isRecording = false;
    private Bitmap lastBitmap = null;

    public MjpegRecordingHandler(Context context) {
        this.context = context;
    }

    /**
     * start recording the live image
     */
    public void startRecording() {
        try {
            String mjpegFilePath = createMjpegFile().getAbsolutePath();
            FileOutputStream fos = new FileOutputStream(mjpegFilePath);
            bos = new BufferedOutputStream(fos);
            Toast.makeText(context, "start recording, file path is:" + mjpegFilePath, Toast.LENGTH_LONG).show();
            isRecording = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * stop recording the live image
     */
    public void stopRecording() {
        isRecording = false;
    }
    public boolean isRecording() {
        return  isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }

    public Bitmap getLastBitmap() {
        return lastBitmap;
    }

    /**
     * save the last acquired bitmap into jpg file.
     */
    public void saveBitmapToFile() {
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        String imagePath = createJpgFile().getAbsolutePath();
        try {
            fos = new FileOutputStream(imagePath);
            bos = new BufferedOutputStream(fos);
            ByteArrayOutputStream jpegByteArrayOutputStream = new ByteArrayOutputStream();
            lastBitmap.compress(Bitmap.CompressFormat.JPEG, 75, jpegByteArrayOutputStream);
            byte[] jpegByteArray = jpegByteArrayOutputStream.toByteArray();
            bos.write(jpegByteArray);
            bos.flush();
            Toast.makeText(context, "saved image:" + imagePath, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create jpg file in app external cache directory. the directory path is /sdcard/Android/data/com.github.niqdev.ipcam/files
     *
     * @return File
     */
    private File createJpgFile() {
        Date T = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String szFileName = "photo-" + sdf.format(T);
        try {
            String path = context.getExternalFilesDir(null).getPath() + "/" + szFileName + ".jpg";
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            Log.d(TAG, "file path is " + file.getAbsolutePath());
            return file;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }


    /**
     * Create mjpeg file in app external cache directory. the directory path is /sdcard/Android/data/com.github.niqdev.ipcam/files
     *
     * @return File
     */
    private File createMjpegFile() {
        Date T = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String szFileName = "video-" + sdf.format(T);
        try {
            String path = context.getExternalFilesDir(null).getPath() + "/" + szFileName + ".mjpeg";
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            Log.d(TAG, "file path is " + file.getAbsolutePath());
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onFrameCaptured(Bitmap bitmap) {
        lastBitmap = bitmap;
    }

    @Override
    public void onFrameCapturedWithHeader(byte[] bitmapData, byte[] header) {
        if (isRecording) {
            try {
                bos.write(header);
                bos.write(bitmapData);
                bos.flush();
            } catch (IOException e) {
               Log.e(TAG, e.getMessage());
            }
        }
    }
}
