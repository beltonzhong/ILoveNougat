package com.beltonzhong.ilovenougat;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText queryField = (EditText) findViewById(R.id.edit_query);
                String query = queryField.getText().toString();
                new RequestTask().execute("zappos", query, "%3E&key=b743e26728e16b81da139182bb2094357c31d331");
            }
        });
    }

    public static String getRequest(String requestURL) {
        URL url = null;
        HttpURLConnection connection = null;
        String result = "";
        try {
            url = new URL(requestURL);
            connection = (HttpURLConnection) url.openConnection();
            InputStream input = new BufferedInputStream(connection.getInputStream());
            StringBuilder stringBuilder = new StringBuilder();
            int byteRead;
            while((byteRead = input.read()) != -1) {
                char c = (char) byteRead;
                stringBuilder.append(c);
            }
            result = stringBuilder.toString();
        } catch (MalformedURLException e) {
            Log.e("MainActivity", "Bad URL.");
        } catch (IOException e) {
            Log.e("MainActivity", "Connection failed.");
        } finally {
            connection.disconnect();
        }
        return result;
    }

    private class RequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return getRequest("https://api." + params[0] + ".com/Search?term=%3C" + params[1] + params[2]);
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("MainActivity", result);
        }
    }
}
