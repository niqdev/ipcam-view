package com.github.niqdev.mjpeg;

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
import java.net.URI;
import java.net.URL;

/**
 *
 */
public class Mjpeg {

    private Mjpeg() {}

    // TODO builder.xxx.native().build() {boolean}
    // - MjpegInputStream
    // - MjpegView

    public static MjpegInputStreamDefault readDefault(String url) {
        HttpResponse res;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            res = httpclient.execute(new HttpGet(URI.create(url)));
            return new MjpegInputStreamDefault(res.getEntity().getContent());
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
        return null;
    }

    public static MjpegInputStreamNative readNative(String surl) {
        try {
            URL url = new URL(surl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            return new MjpegInputStreamNative(urlConnection.getInputStream());
        } catch (Exception e) {
        }

        return null;
    }

    private HttpClient initHttpClient(String userName, String password) {
        //return new DefaultHttpClient();

        HttpParams httpParams = new BasicHttpParams();
        httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        // increased timeout
        HttpConnectionParams.setConnectionTimeout(httpParams, 100000);
        HttpConnectionParams.setSoTimeout(httpParams, 100000);

        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userName, password);
        provider.setCredentials(AuthScope.ANY, credentials);

        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
        httpClient.setCredentialsProvider(provider);
        return httpClient;
    }

}
