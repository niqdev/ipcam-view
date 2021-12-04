package com.github.niqdev.mjpeg;

import android.graphics.Bitmap;
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
 * https://bitbucket.org/neuralassembly/simplemjpegview
 */
public class MjpegInputStreamNative extends MjpegInputStream {

    //private final static int FRAME_MAX_LENGTH = 40000 + HEADER_MAX_LENGTH;
    private final static int FRAME_MAX_LENGTH = 200000;
    private static final String TAG = "MJPEG";
    private static final boolean DEBUG = false;

    static {
        System.loadLibrary("ImageProc");
    }

    private final byte[] SOI_MARKER = {(byte) 0xFF, (byte) 0xD8};
    private final byte[] EOF_MARKER = {(byte) 0xFF, (byte) 0xD9};
    private final String CONTENT_LENGTH = "Content-Length";
    byte[] header = null;
    byte[] frameData = null;
    int headerLen = -1;
    int headerLenPrev = -1;
    int skip = 1;
    int count = 0;
    private int mContentLength = -1;

    // no more accessible
    MjpegInputStreamNative(InputStream in) {
        super(new BufferedInputStream(in, FRAME_MAX_LENGTH));
    }

    public native int pixeltobmp(byte[] jp, int l, Bitmap bmp);

    public native void freeCameraMemory();

    private int getEndOfSeqeunce(DataInputStream in, byte[] sequence)
            throws IOException {

        int seqIndex = 0;
        byte c;
        for (int i = 0; i < FRAME_MAX_LENGTH; i++) {
            c = (byte) in.readUnsignedByte();
            if (c == sequence[seqIndex]) {
                seqIndex++;
                if (seqIndex == sequence.length) {

                    return i + 1;
                }
            } else seqIndex = 0;
        }

        return -1;
    }

    private int getStartOfSequence(DataInputStream in, byte[] sequence)
            throws IOException {
        int end = getEndOfSeqeunce(in, sequence);
        return (end < 0) ? (-1) : (end - sequence.length);
    }

    private int getEndOfSeqeunceSimplified(DataInputStream in, byte[] sequence)
            throws IOException {
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

    private int parseContentLength(byte[] headerBytes)
            throws IOException, IllegalArgumentException {
        ByteArrayInputStream headerIn = new ByteArrayInputStream(headerBytes);
        Properties props = new Properties();
        props.load(headerIn);
        return Integer.parseInt(props.getProperty(CONTENT_LENGTH));
    }

    // no more accessible
    int readMjpegFrame(Bitmap bmp) throws IOException {
        mark(FRAME_MAX_LENGTH);
        int headerLen;
        try {
            headerLen = getStartOfSequence(this, SOI_MARKER);
        } catch (IOException e) {
            if (DEBUG) Log.d(TAG, "IOException in betting headerLen.");
            reset();
            return -1;
        }
        reset();

        if (header == null || headerLen != headerLenPrev) {
            header = new byte[headerLen];
            if (DEBUG) Log.d(TAG, "header renewed " + headerLenPrev + " -> " + headerLen);
        }
        headerLenPrev = headerLen;
        readFully(header);

        int ContentLengthNew;
        try {
            ContentLengthNew = parseContentLength(header);
        } catch (NumberFormatException nfe) {
            ContentLengthNew = getEndOfSeqeunceSimplified(this, EOF_MARKER);

            if (ContentLengthNew < 0) {
                if (DEBUG) Log.d(TAG, "Worst case for finding EOF_MARKER");
                reset();
                ContentLengthNew = getEndOfSeqeunce(this, EOF_MARKER);
            }
        } catch (IllegalArgumentException e) {
            if (DEBUG) Log.d(TAG, "IllegalArgumentException in parseContentLength");
            ContentLengthNew = getEndOfSeqeunceSimplified(this, EOF_MARKER);

            if (ContentLengthNew < 0) {
                if (DEBUG) Log.d(TAG, "Worst case for finding EOF_MARKER");
                reset();
                ContentLengthNew = getEndOfSeqeunce(this, EOF_MARKER);
            }
        } catch (IOException e) {
            if (DEBUG) Log.d(TAG, "IOException in parseContentLength");
            reset();
            return -1;
        }
        mContentLength = ContentLengthNew;
        reset();

        if (frameData == null || mContentLength > frameData.length) {
            frameData = new byte[mContentLength]; // + HEADER_MAX_LENGTH];
            if (DEBUG) Log.d(TAG, "frameData renewed cl=" + mContentLength);
        }

        skipBytes(headerLen);

        readFully(frameData, 0, mContentLength);

        if (count++ % skip == 0) {
            return pixeltobmp(frameData, mContentLength, bmp);
        } else {
            return 0;
        }
    }

    // no more accessible
    void setSkip(int s) {
        skip = s;
    }
}
