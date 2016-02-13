package com.github.niqdev.mjpeg;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 *
 */
public class Mjpeg {

    /*
    public static MjpegInputStream read(String url) {
        HttpResponse res;

        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            res = httpclient.execute(new HttpGet(URI.create(url)));
            return new MjpegInputStream(res.getEntity().getContent());
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
        return null;
    }

    public static MjpegInputStreamNative read(String surl) {
        try {
            URL url = new URL(surl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            return new MjpegInputStreamNative(urlConnection.getInputStream());
        } catch (Exception e) {
        }

        return null;
    }
    */
}
