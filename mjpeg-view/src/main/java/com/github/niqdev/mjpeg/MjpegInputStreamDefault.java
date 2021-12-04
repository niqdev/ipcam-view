package com.github.niqdev.mjpeg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
    private final static int HEADER_MAX_LENGTH = 100;
    private final static int FRAME_MAX_LENGTH = 40000 + HEADER_MAX_LENGTH;
    private final byte[] SOI_MARKER = {(byte) 0xFF, (byte) 0xD8};
    private final byte[] EOF_MARKER = {(byte) 0xFF, (byte) 0xD9};
    private final String CONTENT_LENGTH = "Content-Length";
    private int mContentLength = -1;

    // no more accessible
    MjpegInputStreamDefault(InputStream in) {
        super(new BufferedInputStream(in, FRAME_MAX_LENGTH));
    }

    private int getEndOfSequence(DataInputStream in, byte[] sequence) throws IOException {
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
        int end = getEndOfSequence(in, sequence);
        return (end < 0) ? (-1) : (end - sequence.length);
    }

    private int parseContentLength(byte[] headerBytes) throws IOException, IllegalArgumentException {
        ByteArrayInputStream headerIn = new ByteArrayInputStream(headerBytes);
        Properties props = new Properties();
        props.load(headerIn);
        return Integer.parseInt(props.getProperty(CONTENT_LENGTH));
    }

    byte[] readHeader() throws IOException {
        mark(FRAME_MAX_LENGTH);
        int headerLen = getStartOfSequence(this, SOI_MARKER);
        reset();
        byte[] header = new byte[headerLen];
        readFully(header);
        return header;
    }

    // no more accessible
    byte[] readMjpegFrame(byte[] header) throws IOException {
        try {
            mContentLength = parseContentLength(header);
        } catch (IllegalArgumentException iae) {
            mContentLength = getEndOfSequence(this, EOF_MARKER);
        }
        reset();
        byte[] frameData = new byte[mContentLength];
        skipBytes(header.length);
        readFully(frameData);
        return frameData;
    }

    // no more accessible
    Bitmap readMjpegFrame() throws IOException {
        mark(FRAME_MAX_LENGTH);
        int headerLen = getStartOfSequence(this, SOI_MARKER);
        reset();
        byte[] header = new byte[headerLen];
        readFully(header);
        try {
            mContentLength = parseContentLength(header);
        } catch (IllegalArgumentException iae) {
            mContentLength = getEndOfSequence(this, EOF_MARKER);
        }
        reset();
        byte[] frameData = new byte[mContentLength];
        skipBytes(headerLen);
        readFully(frameData);
        return BitmapFactory.decodeStream(new ByteArrayInputStream(frameData));
    }

}
