package x.contextualtriggers.Application;

/**
 * Created by Sean on 13/04/2016.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import x.contextualtriggers.R;

public class NotificationSender {
    public static void sendNotification(Context context, int id, int iconId, String title, String message){
        final NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message)
                                .setBigContentTitle(title)
                                .setSummaryText(""))
                        .setSmallIcon(iconId)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .setContentTitle(title);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(id, mBuilder.build());
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

