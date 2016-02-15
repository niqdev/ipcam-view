package com.github.niqdev.mjpeg;

import android.text.TextUtils;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import rx.Observable;
import rx.functions.Func0;

/**
 *
 */
public class Mjpeg {

    private final boolean nativeStream;

    private Mjpeg(boolean nativeStream) {
        this.nativeStream = nativeStream;
    }

    public static Mjpeg init(boolean nativeStream) {
        return new Mjpeg(nativeStream);
    }

    public Observable<MjpegInputStream> read(String url) {
        return Observable.defer(new Func0<Observable<MjpegInputStream>>() {
            @Override
            public Observable<MjpegInputStream> call() {
                return null;
            }
        });
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

    public static MjpegInputStreamNative readNative(String surl) {
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

    // TODO
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
