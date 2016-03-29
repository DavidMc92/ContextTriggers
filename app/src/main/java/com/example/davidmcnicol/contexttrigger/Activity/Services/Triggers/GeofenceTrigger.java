package com.example.davidmcnicol.contexttrigger.Activity.Services.Triggers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by davidmcnicol on 29/03/16.
 */
public class GeofenceTrigger extends BroadcastReceiver{

    public GeofenceTrigger(Context context)
    {
        LocalBroadcastManager.getInstance(context).registerReceiver(
                this, new IntentFilter("geoData"));


    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String events = intent.getStringExtra("Status");

        Log.d("events ct", events);
    }
}
