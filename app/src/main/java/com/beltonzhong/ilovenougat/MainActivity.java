package com.beltonzhong.ilovenougat;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity {
    private Button searchButton;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText queryField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        queryField = (EditText) findViewById(R.id.edit_query);

        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = queryField.getText().toString();
                new ZapposRequestTask().execute(query);
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
            Log.e("MainActivity", e.getMessage());
        } catch (IOException e) {
            Log.e("MainActivity", e.getMessage());
        } finally {
            connection.disconnect();
        }
        return result;
    }

    public class ZapposRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return getRequest("https://api.zappos.com/Search?term=%3C" + params[0] + "%3E&key=b743e26728e16b81da139182bb2094357c31d331");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject resultObject = new JSONObject(result);
                JSONArray resultsArray = resultObject.getJSONArray("results");
                adapter = new MyAdapter(resultsArray);
                recyclerView.setAdapter(adapter);
            } catch (JSONException e) {
                Log.e("MainActivity", e.getMessage());
                e.printStackTrace();
            }

        }
    }
}
