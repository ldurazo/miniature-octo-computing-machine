package com.example.ldurazo.xboxplayerexcercise.adapters;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.ldurazo.xboxplayerexcercise.asynctasks.OnTokenTaskCallback;
import com.example.ldurazo.xboxplayerexcercise.asynctasks.TokenObtainableAsyncTask;

public class TokenRefreshBroadcastReceiver extends BroadcastReceiver implements OnTokenTaskCallback{
    private final static String TAG = "com.example.ldurazo.xboxplayerexcercise.adapters.tokenrefreshbroadcastreceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        new TokenObtainableAsyncTask(this).execute();
}

    @Override
    public void onTokenReceived(String response) {
        Log.w(TAG, "Token refreshed");
    }

    @Override
    public void onTokenNotReceived() {

    }
}
