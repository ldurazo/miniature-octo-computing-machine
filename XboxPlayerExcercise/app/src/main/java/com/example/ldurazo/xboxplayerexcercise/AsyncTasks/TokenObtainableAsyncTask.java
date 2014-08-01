package com.example.ldurazo.xboxplayerexcercise.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ldurazo.xboxplayerexcercise.models.Constants;
import com.example.ldurazo.xboxplayerexcercise.models.AppSession;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class TokenObtainableAsyncTask extends AsyncTask<Void, Void, String>{
    private static final String TAG = "com.example.ldurazo.xboxplayerexcercise.asynctasks.tokenobtainableasynctask";
    private OnTokenTaskCallback callbacks;

    public TokenObtainableAsyncTask(OnTokenTaskCallback callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        AppSession.getInstance().setAccessToken(result);
        if(!result.equals(Constants.ERROR)){
            callbacks.onTokenReceived(AppSession.getInstance().getAccessToken());
        }else{
            callbacks.onTokenNotReceived();
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
                }
                Log.w(TAG,stringBuilder.toString());
                return retrieveToken(stringBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Constants.ERROR;
    }

    private InputStream establishConnection(){
        try {
            HttpParams httpParameters = new BasicHttpParams();
            int timeoutConnection = 5000; //Timeout until a connection is established.
            int timeoutSocket = 50000; //Timeout for waiting for data.
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            HttpClient client = new DefaultHttpClient(httpParameters);
            HttpPost request = new HttpPost(AppSession.SERVICE);
            request.setHeader("Content_type", AppSession.CONTENT_TYPE);
            request.setHeader("Accept", "application/json");
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
            nameValuePairs.add(new BasicNameValuePair("client_id", AppSession.CLIENT_ID));
            nameValuePairs.add(new BasicNameValuePair("client_secret", AppSession.CLIENT_SECRET));
            nameValuePairs.add(new BasicNameValuePair("scope", AppSession.SCOPE));
            nameValuePairs.add(new BasicNameValuePair("grant_type", AppSession.GRANT_TYPE));
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            return entity.getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String retrieveToken(String inputLine){
        try {
            JSONObject responseObject = new JSONObject(inputLine);
            return responseObject.getString("access_token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Constants.ERROR;
    }
}