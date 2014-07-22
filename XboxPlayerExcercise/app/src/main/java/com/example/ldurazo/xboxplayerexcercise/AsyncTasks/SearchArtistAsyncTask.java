package com.example.ldurazo.xboxplayerexcercise.AsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ldurazo.xboxplayerexcercise.Models.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SearchArtistAsyncTask extends AsyncTask<Void, Void, String> {
    private String token;
    public SearchArtistAsyncTask(String token) {
        try {
            token = URLEncoder.encode(token, "UTF-8");
            this.token = token;
        } catch (UnsupportedEncodingException e) {
            this.token = Constants.ERROR;
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            InputStream inputStream = establishConnection();
            if(inputStream!=null){
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"),8);
                String inputLine;
                while((inputLine=bufferedReader.readLine())!=null){
                    stringBuilder.append(inputLine);
                    Log.w(Constants.TAG, inputLine);
                }
                return retrieveArtist(stringBuilder.toString());
            }else {
                return Constants.ERROR;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Constants.ERROR;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

    private InputStream establishConnection(){
        try {
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 10000; //Timeout until a connection is established.
            int timeoutSocket = 10000; //Timeout for waiting for data.
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpClient client = new DefaultHttpClient(httpParameters);
            String query = Constants.SCOPE_SERVICE+"/1/content/music/search?q=daft+punk&accessToken=Bearer+"+token;
            Log.w(Constants.TAG,query);
            HttpGet request = new HttpGet(query);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            return entity.getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String retrieveArtist(String jsonString){
        try {
            //TODO please implement the jsonarray as it should be dude.
            JSONObject parentData = new JSONObject(jsonString);
            JSONObject artists = parentData.getJSONObject("Artists");
            JSONObject items = artists.getJSONArray("Items").getJSONObject(0);
            Log.w(Constants.TAG, items.get("Name").toString());
            return items.getString("Name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Constants.ERROR;
    }
}