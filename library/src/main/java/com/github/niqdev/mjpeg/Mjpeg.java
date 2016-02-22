package com.github.niqdev.mjpeg;

import android.text.TextUtils;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 *
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

    public static Mjpeg newInstance() {
        return new Mjpeg(Type.DEFAULT);
    }

    public static Mjpeg newInstance(Type type) {
        return new Mjpeg(type);
    }

    public Mjpeg credential(String username, String password) {
        // TODO
        return this;
    }

    public Observable<MjpegInputStream> read(String url) {
        return Observable.defer(() -> {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                switch (type) {
                    case DEFAULT: return Observable.just(new MjpegInputStreamDefault(inputStream));
                    case NATIVE: return Observable.just(new MjpegInputStreamNative(inputStream));
                }
                throw new IllegalStateException("invalid type");
            } catch (IOException e) {
                return Observable.error(e);
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
    }

    // TODO builder.xxx.native().build() {boolean}
    // - MjpegInputStream
    // - MjpegView

    public static MjpegInputStreamDefault readDefault(String url) {
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpResponse res = httpclient.execute(new HttpGet(URI.create(url)));
            return new MjpegInputStreamDefault(res.getEntity().getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // TODO deprecated
    private MjpegInputStreamNative readNative(String surl) {
        try {
            URL url = new URL(surl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            return new MjpegInputStreamNative(urlConnection.getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // TODO deprecated
    private HttpClient initHttpClient(String userName, String password) {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            return new DefaultHttpClient();
        }

        HttpParams httpParams = new BasicHttpParams();
        httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        // increased timeout
        HttpConnectionParams.setConnectionTimeout(httpParams, 5 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 5 * 1000);

        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userName, password);
        provider.setCredentials(AuthScope.ANY, credentials);

        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
        httpClient.setCredentialsProvider(provider);
        return httpClient;
    }

}
