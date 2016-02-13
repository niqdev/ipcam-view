package com.github.niqdev.mjpeg.stream;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class MjpegAbstractStream extends DataInputStream {

    protected final byte[] SOI_MARKER = {(byte) 0xFF, (byte) 0xD8};
    protected final byte[] EOF_MARKER = {(byte) 0xFF, (byte) 0xD9};
    protected final String CONTENT_LENGTH = "Content-Length";
    protected final static int HEADER_MAX_LENGTH = 100;
    //protected final static int FRAME_MAX_LENGTH = 40000 + HEADER_MAX_LENGTH;
    protected final static int FRAME_MAX_LENGTH = 200000;
    protected int mContentLength = -1;

    MjpegAbstractStream(InputStream in) {
        super(new BufferedInputStream(in, FRAME_MAX_LENGTH));
    }

    protected int getEndOfSeqeunce(DataInputStream in, byte[] sequence) throws IOException {
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

    protected int getStartOfSequence(DataInputStream in, byte[] sequence) throws IOException {
        int end = getEndOfSeqeunce(in, sequence);
        return (end < 0) ? (-1) : (end - sequence.length);
    }

    protected int parseContentLength(byte[] headerBytes) throws IOException {
        ByteArrayInputStream headerIn = new ByteArrayInputStream(headerBytes);
        Properties props = new Properties();
        props.load(headerIn);
        return Integer.parseInt(props.getProperty(CONTENT_LENGTH));
    }

}
