package com.github.niqdev.mjpeg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/*
 * I don't really understand and want to know what the hell it does!
 * Maybe one day I will refactor it ;-)
 * <p/>
 * https://code.google.com/archive/p/android-camera-axis
 */
public class MjpegInputStreamDefault extends MjpegInputStream {
    private static final String TAG = MjpegInputStream.class.getSimpleName();

    private final byte[] SOI_MARKER = {(byte) 0xFF, (byte) 0xD8};
    private final byte[] EOF_MARKER = {(byte) 0xFF, (byte) 0xD9};
    private final String CONTENT_LENGTH = "Content-Length";
    private final static int HEADER_MAX_LENGTH = 100;
    //private final static int FRAME_MAX_LENGTH = 40000 + HEADER_MAX_LENGTH;
    private final static int FRAME_MAX_LENGTH = 200000;
    private int mContentLength = -1;
    private byte[] header = null;
    private byte[] frameData = null;
    private int headerLen = -1;
    private int headerLenPrev = -1;

    // no more accessible
    MjpegInputStreamDefault(InputStream in) {
        super(new BufferedInputStream(in, FRAME_MAX_LENGTH));
    }

    private int getEndOfSeqeunce(DataInputStream in, byte[] sequence) throws IOException {
        int seqIndex = 0;
        byte c;
        for (int i = 0; i < FRAME_MAX_LENGTH; i++) {
            c = (byte) in.readUnsignedByte();
            if (c == sequence[seqIndex]) {
                seqIndex++;
                if (seqIndex == sequence.length) {
                    return i + 1;
                }
            } else {
                seqIndex = 0;
            }
        }
        return -1;
    }

    private int getStartOfSequence(DataInputStream in, byte[] sequence) throws IOException {
        int end = getEndOfSeqeunce(in, sequence);
        return (end < 0) ? (-1) : (end - sequence.length);
    }

    private int getEndOfSeqeunceSimplified(DataInputStream in, byte[] sequence) throws IOException {
        int startPos = mContentLength / 2;
        int endPos = 3 * mContentLength / 2;

        skipBytes(headerLen + startPos);
        
        int seqIndex = 0;
        byte c;
        for (int i = 0; i < endPos - startPos; i++) {
            c = (byte) in.readUnsignedByte();
            if (c == sequence[seqIndex]) {
                seqIndex++;
                if (seqIndex == sequence.length) {
                    return headerLen + startPos + i + 1;
                }
            } else seqIndex = 0;
        }

        return -1;
    }

    private int parseContentLength(byte[] headerBytes) throws IOException, IllegalArgumentException {
        ByteArrayInputStream headerIn = new ByteArrayInputStream(headerBytes);
        Properties props = new Properties();
        props.load(headerIn);
        return Integer.parseInt(props.getProperty(CONTENT_LENGTH));
    }

    // no more accessible
    Bitmap readMjpegFrame() throws IOException {
        mark(FRAME_MAX_LENGTH);
        headerLen = getStartOfSequence(this, SOI_MARKER);
        reset();
        
        if (header == null || headerLen != headerLenPrev) {
            header = new byte[headerLen]; // header renewed
        }
        headerLenPrev = headerLen;
        readFully(header);

        int ContentLengthNew = -1;
        try {
            ContentLengthNew = parseContentLength(header);
        } catch (IllegalArgumentException e) {
            ContentLengthNew = getEndOfSeqeunceSimplified(this, EOF_MARKER);
            if (ContentLengthNew < 0) { // Worst case for finding EOF_MARKER
                reset();
                ContentLengthNew = getEndOfSeqeunce(this, EOF_MARKER);
            }
        }
        mContentLength = ContentLengthNew;
        reset();
        
        if (frameData == null) {
            frameData = new byte[FRAME_MAX_LENGTH]; // frameData newed
        }
        if (mContentLength + HEADER_MAX_LENGTH > FRAME_MAX_LENGTH) {
            frameData = new byte[mContentLength + HEADER_MAX_LENGTH]; // frameData renewed
        }
        
        skipBytes(headerLen);
        readFully(frameData, 0, mContentLength);
        return BitmapFactory.decodeStream(new ByteArrayInputStream(frameData));
    }
}
