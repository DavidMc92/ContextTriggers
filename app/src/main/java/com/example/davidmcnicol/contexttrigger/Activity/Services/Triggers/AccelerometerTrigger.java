package com.example.davidmcnicol.contexttrigger.Activity.Services.Triggers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.example.davidmcnicol.contexttrigger.Activity.Services.Services.AccelerometerService;
import com.example.davidmcnicol.contexttrigger.R;

/**
 * Created by davidmcnicol on 18/03/16.
 */
public class AccelerometerTrigger extends BroadcastReceiver {

    private int stepCount = 0;
    private Context context;
    private int mId = 0;
    private static String GROUP = "Group";

    public AccelerometerTrigger(Context context)
    {
        this.context = context;
        LocalBroadcastManager.getInstance(context).registerReceiver(
                this, new IntentFilter("accData"));

        context.startService(new Intent(context, AccelerometerService.class));
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        // Get extra data included in the Intent
        String message = intent.getStringExtra("Status");
        stepCount += Integer.parseInt(message);

        if(stepCount==5)
        {
            stepCount = 0;
            sendNotification(context,"Steps","Done 5 steps");
        }
//            Log.d("MSG", message);
        // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

//    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // Get extra data included in the Intent
//            String message = intent.getStringExtra("Status");
//            stepCount += Integer.parseInt(message);
//
//            if(stepCount==20)
//            {
//                stepCount = 0;
//                sendNotification(context,"Steps","Done 20 steps");
//            }
////            Log.d("MSG", message);
//            // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//        }
//    };


    public void sendNotification(Context context, String title, String message)
    {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_directions_walk_black_24dp)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setGroup(GROUP)
                        .setGroupSummary(true)
                        .setNumber(mId);

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

        mId++;

    }

    public void stop()
    {
        context.stopService(new Intent(context, AccelerometerService.class));
    }
}
