package com.example.muhta.whereareyou;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

public class netChecker extends Activity {

    public BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("internetDebug","Network connectivity change");
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("internetDebug","Resumed");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("internetDebug","Paused");
        unregisterReceiver(networkChangeReceiver);
    }
}