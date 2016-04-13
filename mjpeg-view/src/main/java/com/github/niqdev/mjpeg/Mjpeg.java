package com.github.niqdev.mjpeg;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A library wrapper for handle mjpeg streams.
 *
 * @see
 * <ul>
 *     <li><a href="https://bitbucket.org/neuralassembly/simplemjpegview">simplemjpegview</a></li>
 *     <li><a href="https://code.google.com/archive/p/android-camera-axis">android-camera-axis</a></li>
 * </ul>
 */
public class Mjpeg {

    /**
     * Library implementation type
     */
    public enum Type {
        DEFAULT, NATIVE
    }

    private final Type type;

    private Mjpeg(Type type) {
        if (type == null) {
            throw new IllegalArgumentException("null type not allowed");
        }
        this.type = type;
    }

    /**
     * Uses {@link Type#DEFAULT} implementation.
     *
     * @return Mjpeg instance
     */
    public static Mjpeg newInstance() {
        return new Mjpeg(Type.DEFAULT);
    }

    /**
     * Choose among {@link com.github.niqdev.mjpeg.Mjpeg.Type} implementations.
     *
     * @return Mjpeg instance
     */
    public static Mjpeg newInstance(Type type) {
        return new Mjpeg(type);
    }

    /**
     * Configure authentication.
     *
     * @param username credential
     * @param password credential
     * @return Mjpeg instance
     */
    public Mjpeg credential(String username, String password) {
        if (username != null && password != null) {
            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password.toCharArray());
                }
            });
        }
        return this;
    }

    /**
     * Connect to a Mjpeg stream.
     *
     * @param url source
     * @return Observable Mjpeg stream
     */
    public Observable<MjpegInputStream> open(String url) {
        return Observable.defer(() -> {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                switch (type) {
                    // handle multiple implementations
                    case DEFAULT:
                        return Observable.just(new MjpegInputStreamDefault(inputStream));
                    case NATIVE:
                        return Observable.just(new MjpegInputStreamNative(inputStream));
                }
                throw new IllegalStateException("invalid type");
            } catch (IOException e) {
                return Observable.error(e);
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
    }

}
