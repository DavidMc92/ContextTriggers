package com.example.davidmcnicol.contexttrigger.Activity.Services.Triggers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.davidmcnicol.contexttrigger.Activity.Services.Services.AccelerometerService;
import com.example.davidmcnicol.contexttrigger.R;


/**
 * Created by davidmcnicol on 29/03/16.
 */
public class WeatherTrigger extends BroadcastReceiver {

    private int stepCount = 0;

    public WeatherTrigger(Context context)
    {
        LocalBroadcastManager.getInstance(context).registerReceiver(
                this, new IntentFilter("weatherData"));


    }

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("Status");
            stepCount += Integer.parseInt(message);

            if(stepCount==5)
            {
                stepCount = 0;
                sendNotification(context,"Rain: Done 5 steps");
                context.stopService(new Intent(context, AccelerometerService.class));
                LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
            }

        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("Here", "RECEIVED 2");
        String message = intent.getStringExtra("Status");

        if(message.contains("rain"))
        {
            context.startService(new Intent(context, AccelerometerService.class));

            LocalBroadcastManager.getInstance(context).registerReceiver(
                    mMessageReceiver, new IntentFilter("accData"));

        }

//        sendNotification(context,message);

    }

    public void sendNotification(Context context, String weather) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_directions_walk_black_24dp)
                        .setContentTitle("Weather")
                        .setContentText(weather)
                        .setDefaults(Notification.DEFAULT_VIBRATE);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());

//        mId++;
    }

    }
