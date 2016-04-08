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

import com.example.davidmcnicol.contexttrigger.Activity.Services.Services.BarometerService;
import com.example.davidmcnicol.contexttrigger.R;

import java.util.ArrayList;

/**
 * Created by davidmcnicol on 18/03/16.
 */
public class BarometerTrigger extends BroadcastReceiver {

    private float barValue = 0;
    private int mId = 0;
    private Context context;
    private float x, y, z = 0;
    private Boolean isAccValSmall, hasNotified = false;
    private static float defaultAltitude = 1016f; // Default air pressure at sea level.
    private ArrayList<Float> barValues = new ArrayList<>();
    private float currentAltitude = 0;
    private float prevAltitude = 0;
    private float altitude = 0;
    private int arrayPos;
    private int MAX_ARRAY_SIZE = 20;

    private ArrayList<Float> barometerValuesDifference = new ArrayList<>();

    public BarometerTrigger(Context context)
    {

        this.context = context;

        LocalBroadcastManager.getInstance(context).registerReceiver(
                this, new IntentFilter("barData"));

        LocalBroadcastManager.getInstance(context).registerReceiver(
                mMessageReceiver, new IntentFilter("accData"));

        context.startService(new Intent(context, BarometerService.class));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get extra data included in the Intent
        barValue = intent.getFloatExtra("BarVal",-1);

        barValues.add(barValue);

        currentAltitude = (defaultAltitude - currentAltitude) * 8;


        if(isAccValSmall)
        {
            altitude = prevAltitude - currentAltitude;
            if (altitude < 1 && altitude > -1) {
                //Log.v("altitude", "" + altitude);
                barometerValuesDifference.set(arrayPos, altitude);
                arrayPos++;
            }

            if (arrayPos >= MAX_ARRAY_SIZE) {
                //Log.v("HERE","RESET array pos");
                arrayPos = 0;
            }

            if (barometerValuesDifference.size() >= MAX_ARRAY_SIZE) {

                int sumBarValues = 0;
                for (int i = 0; i < barometerValuesDifference.size(); i++) {
                    //Log.v("averageBarometerDifference", "" + averageBarometerDifference);
                    sumBarValues += barometerValuesDifference.get(i);
                }

                int averageBarValues = sumBarValues / barometerValuesDifference.size();
                if (averageBarValues >= 0.02 || averageBarValues <= -0.02) {
                    //Lift Detected so send notification
                    if(!hasNotified) {
                        sendNotification(context, "Left Detected", "In future why not take the stairs to use more energy.");
                        hasNotified = true;
                    }
                    else
                    {
                        hasNotified = false;
                    }
                }

            }

        }


    }

        private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            x = intent.getFloatExtra("X",-1);
            y = intent.getFloatExtra("Y",-1);
            z = intent.getFloatExtra("Z",-1);

            if( x < 15 && y < 15 && z < 15)
            {
                isAccValSmall = true;
            }

        }
    };


    public void sendNotification(Context context, String title, String message)
    {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_add_black_24dp)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setDefaults(Notification.DEFAULT_VIBRATE);

//        // Creates an explicit intent for an Activity in your app
//        Intent resultIntent = new Intent(context, MainActivity.class);
//
//        // The stack builder object will contain an artificial back stack for the
//        // started Activity.
//        // This ensures that navigating backward from the Activity leads out of
//        // your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        // Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(MainActivity.class);
//        // Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());

        mId++;

    }

    public void stop()
    {
        context.stopService(new Intent(context, BarometerService.class));
    }
}
