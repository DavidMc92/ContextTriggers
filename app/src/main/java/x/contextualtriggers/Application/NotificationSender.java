package x.contextualtriggers.Application;

/**
 * Created by Sean on 13/04/2016.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
        mNotificationManager.notify(id, mBuilder.build());
    }

    public static void sendNotificationWithIntent(Context context, int id, int iconId, String title,
                                                  String message,PendingIntent callback){
        final NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message)
                                .setBigContentTitle(title)
                                .setSummaryText(""))
                        .setSmallIcon(iconId)
                        .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        .addAction(0,"Show",callback);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        // mId allows you to update the notification later on.
        mNotificationManager.notify(id, mBuilder.build());
    }
}

