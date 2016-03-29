package com.example.davidmcnicol.contexttrigger.Activity.Services.Triggers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by davidmcnicol on 29/03/16.
 */
public class CalendarTrigger extends BroadcastReceiver{

    private ArrayList<String> events = new ArrayList<>();

    public CalendarTrigger(Context context)
    {
        LocalBroadcastManager.getInstance(context).registerReceiver(
                this, new IntentFilter("calData"));


    }

    @Override
    public void onReceive(Context context, Intent intent) {

        events = intent.getStringArrayListExtra("Status");

        for(int i = 0; i < events.size(); i ++) {
            Log.d("events ct", events.get(0).toString());
        }
    }
}
