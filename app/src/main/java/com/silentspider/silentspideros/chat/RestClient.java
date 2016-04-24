package com.silentspider.silentspideros.chat;


import android.os.AsyncTask;
import android.widget.EditText;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class RestClient extends AsyncTask<String, Void, String> {

    private RestCallback callback;

    public RestClient(RestCallback callback) {
        super();
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... uri) {
        StringBuffer sb = new StringBuffer();
        try {

            URLConnection connection = new URL(uri[0]).openConnection();
            InputStream response = connection.getInputStream();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    sb.append(line);
                }
            }

        } catch (Exception e) {

        }
        return sb.toString();
    }


    protected void onPostExecute(String results) {
        callback.result(results);
    }
}