package com.golapp.mybooklist.ApiRequests;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class GoogleApiRequest {

    //The base url to query from Google Books API
    String url = "https://www.googleapis.com/books/v1/volumes?q=";

    //Queries the given isbn from Google Books API
    public JSONObject getBook(BigInteger isbn){
        String queryUrl = url + "isbn:" + isbn;
        try {
            return new GetBookInfo().execute(queryUrl).get();
        } catch (ExecutionException e) {
            Log.e(getClass().getSimpleName(), "Exception", e);
        } catch (InterruptedException e) {
            Log.e(getClass().getSimpleName(), "Exception", e);
        }
        return null;
    }

    private class GetBookInfo extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... queryUrl) {
            JSONObject json = null;
            try {
                //Connects to Url
                HttpURLConnection connection = (HttpURLConnection)new URL(queryUrl[0]).openConnection();
                InputStream in = connection.getInputStream();

                //Reads the input stream
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                //Gets the first JSON object in the JSON array from the string builder
                json = new JSONObject(builder.toString()).getJSONArray("items").getJSONObject(0);
                //Gets the corresponding volumeInfo on the object
                json = json.getJSONObject("volumeInfo");
                reader.close();
            }
            catch (IOException e) {
                Log.e(getClass().getSimpleName(), "Exception parsing JSON", e);
            } catch (JSONException e) {
                Log.e(getClass().getSimpleName(), "Exception parsing JSON", e);
                e.printStackTrace();
            }
            return json;
        }
    }
}
