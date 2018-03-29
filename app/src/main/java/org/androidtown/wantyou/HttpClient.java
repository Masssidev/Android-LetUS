package org.androidtown.wantyou;

public class HttpClient {
    private volatile static org.apache.http.impl.client.DefaultHttpClient httpClient=new org.apache.http.impl.client.DefaultHttpClient();

    private HttpClient() {

    }

    public static org.apache.http.impl.client.DefaultHttpClient getInstance() {
        if(httpClient == null) {
            synchronized(HttpClient.class) {
                if(httpClient == null) {
                    httpClient = new org.apache.http.impl.client.DefaultHttpClient() {

                    };
                }
            }
        }
        return httpClient;
    }
}

