package x.contextualtriggers.Application;

/**
 * Created by Sean on 13/04/2016.
 */

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import x.contextualtriggers.R;

public class NotificationSender {
    private static final int NOTIFICATION_ID = 64000;

    public static void sendNotification(Activity activity, String title, String message){
        final NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(activity)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setColor(ContextCompat.getColor(activity, R.color.colorPrimary))
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message));

        // Entirely taken from android guide on notifications, exception of sound
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(activity, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(activity);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    /*private static void sendSound(Activity activity){
        final MediaPlayer mp = MediaPlayer.create(activity, R.raw.final_fantasy_victory_snippet);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(final MediaPlayer mp) {
                mp.reset();
                mp.release();
            }
        });
        mp.start();
    }*/
}

