package com.github.niqdev.mjpeg.stream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MjpegInputStreamNative extends MjpegAbstractStream {

    private static final String TAG = "MJPEG";
    private static final boolean DEBUG = false;

    byte[] header = null;
    byte[] frameData = null;
    int headerLen = -1;
    int headerLenPrev = -1;

    int skip = 1;
    int count = 0;

    static {
        System.loadLibrary("ImageProc");
    }

    MjpegInputStreamNative(InputStream in) {
        super(in);
    }

    public native int pixeltobmp(byte[] jp, int l, Bitmap bmp);

    public native void freeCameraMemory();

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
            } else {
                seqIndex = 0;
            }
        }
        return -1;
    }

    public Bitmap readMjpegFrame() throws IOException {
        mark(FRAME_MAX_LENGTH);
        int headerLen;
        try {
            headerLen = getStartOfSequence(this, SOI_MARKER);
        } catch (IOException e) {
            if (DEBUG) Log.d(TAG, "IOException in betting headerLen.");
            reset();
            return null;
        }
        reset();

        if (header == null || headerLen != headerLenPrev) {
            header = new byte[headerLen];
            if (DEBUG) Log.d(TAG, "header renewed " + headerLenPrev + " -> " + headerLen);
        }
        headerLenPrev = headerLen;
        readFully(header);

        int ContentLengthNew = -1;
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
            return null;
        }
        mContentLength = ContentLengthNew;
        reset();

        if (frameData == null) {
            frameData = new byte[FRAME_MAX_LENGTH];
            if (DEBUG) Log.d(TAG, "frameData newed cl=" + FRAME_MAX_LENGTH);
        }
        if (mContentLength + HEADER_MAX_LENGTH > FRAME_MAX_LENGTH) {
            frameData = new byte[mContentLength + HEADER_MAX_LENGTH];
            if (DEBUG) Log.d(TAG, "frameData renewed cl=" + (mContentLength + HEADER_MAX_LENGTH));
        }

        skipBytes(headerLen);

        readFully(frameData, 0, mContentLength);

        if (count++ % skip == 0) {
            return BitmapFactory.decodeStream(new ByteArrayInputStream(frameData, 0, mContentLength));
        } else {
            return null;
        }
    }

    public int readMjpegFrame(Bitmap bmp) throws IOException {
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

        int ContentLengthNew = -1;
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

        if (frameData == null) {
            frameData = new byte[FRAME_MAX_LENGTH];
            if (DEBUG) Log.d(TAG, "frameData newed cl=" + FRAME_MAX_LENGTH);
        }
        if (mContentLength + HEADER_MAX_LENGTH > FRAME_MAX_LENGTH) {
            frameData = new byte[mContentLength + HEADER_MAX_LENGTH];
            if (DEBUG) Log.d(TAG, "frameData renewed cl=" + (mContentLength + HEADER_MAX_LENGTH));
        }

        skipBytes(headerLen);

        readFully(frameData, 0, mContentLength);

        if (count++ % skip == 0) {
            return pixeltobmp(frameData, mContentLength, bmp);
        } else {
            return 0;
        }
    }

    public void setSkip(int s) {
        skip = s;
    }
}