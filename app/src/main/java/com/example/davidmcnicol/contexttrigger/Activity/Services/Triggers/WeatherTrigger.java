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
import com.example.davidmcnicol.contexttrigger.Activity.Services.Services.WeatherService;
import com.example.davidmcnicol.contexttrigger.R;


/**
 * Created by davidmcnicol on 29/03/16.
 */
public class WeatherTrigger extends BroadcastReceiver {

    private int stepCount = 0;
    private Context context;

    public WeatherTrigger(Context context)
    {
        this.context = context;

        LocalBroadcastManager.getInstance(context).registerReceiver(
                this, new IntentFilter("weatherData"));

        context.startService(new Intent(context, WeatherService.class));

    }

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String message = intent.getStringExtra("Status");
            stepCount += Integer.parseInt(message);


//            sendNotification(context,"Weather",message);

            if(stepCount==5)
            {
                stepCount = 0;
                sendNotification(context,"Weather","Rain: Done 5 steps");
                context.stopService(new Intent(context, AccelerometerService.class));
                LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
            }

        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {


        String message = intent.getStringExtra("Status");

//        Log.d("Here", message);
        //status: clear sky, few clouds, scattered clouds, broken clouds, shower rain, rain, thunderstorm, snow, mist
//        if(message.contains("rain"))
//        {
//            context.startService(new Intent(context, AccelerometerService.class));
//
//            LocalBroadcastManager.getInstance(context).registerReceiver(
//                    mMessageReceiver, new IntentFilter("accData"));
//
//        }

        sendNotification(context,"Weather",message);

    }

    public void sendNotification(Context context, String title, String message) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_add_black_24dp)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setDefaults(Notification.DEFAULT_VIBRATE);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());

//        mId++;
    }

    public void stop()
    {
        context.stopService(new Intent(context, WeatherService.class));
    }

}
