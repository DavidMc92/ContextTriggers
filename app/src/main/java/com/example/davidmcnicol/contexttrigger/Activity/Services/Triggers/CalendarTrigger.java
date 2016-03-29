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

import com.example.davidmcnicol.contexttrigger.R;

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

    public void sendNotification(Context context, String goalName, String action)
    {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_directions_walk_black_24dp)
                        .setContentTitle(goalName)
                        .setContentText(action)
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
        mNotificationManager.notify(0, mBuilder.build());

    }
}
