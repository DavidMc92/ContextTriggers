package com.example.davidmcnicol.contexttrigger.Activity.Services.Activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.davidmcnicol.contexttrigger.Activity.Services.Model.CalendarTest;
import com.example.davidmcnicol.contexttrigger.Activity.Services.Services.BarometerService;
import com.example.davidmcnicol.contexttrigger.Activity.Services.Triggers.BarometerTrigger;
import com.example.davidmcnicol.contexttrigger.Activity.Services.Triggers.TestTrigger;
import com.example.davidmcnicol.contexttrigger.R;

import com.example.davidmcnicol.contexttrigger.Activity.Services.Services.TestService;

/**
 * Created by davidmcnicol on 18/03/16.
 */
public class MainActivity extends Activity{

    private int stepCount = 0;
    private int mId = 1;
    private Switch accSwitch;
    private Switch barSwitch;
    private Switch calSwitch;
    private Boolean isAccOn = false;
    private Boolean isBarOn = false;
    private Boolean isCalOn = false;
    private final Context context = MainActivity.this;
    private Boolean test = true;
    private Boolean test2 = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.davidmcnicol.contexttrigger.R.layout.activity_main);

//        LocalBroadcastManager.getInstance(this).registerReceiver(
//                mMessageReceiver, new IntentFilter("accData"));

        accSwitch = (Switch) findViewById(R.id.switchAcc);
        accSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    isAccOn = true;
                    accSwitch.setChecked(isAccOn);
                    startServiceAcc(buttonView);
                } else {
                    isAccOn = false;
                    accSwitch.setChecked(isAccOn);
                    stopServiceAcc(buttonView);
                }

            }
        });

        barSwitch = (Switch) findViewById(R.id.switchBar);
        barSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    isBarOn = true;
                    barSwitch.setChecked(isBarOn);
                    startServiceBar(buttonView);
                } else {
                    isBarOn = false;
                    barSwitch.setChecked(isBarOn);
                    stopServiceBar(buttonView);
                }

            }
        });

        final CalendarTest ct = new CalendarTest();

        calSwitch = (Switch) findViewById(R.id.switchCal);
        calSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    isCalOn = true;
                    calSwitch.setChecked(isCalOn);
                    Log.d("Calendar", "Here");
                    ct.readCalendar(context);

                } else {
                    isCalOn = false;
                    calSwitch.setChecked(isCalOn);
                }

            }
        });
    }


    // Method to start the service
    public void startServiceAcc(View view) {
        startService(new Intent(getBaseContext(), TestService.class));
        TestTrigger ts = new TestTrigger(getBaseContext());
    }

    // Method to stop the service
    public void stopServiceAcc(View view) {
        stopService(new Intent(getBaseContext(), TestService.class));

    }

    // Method to start the service
    public void startServiceBar(View view) {
        startService(new Intent(getBaseContext(), BarometerService.class));
        BarometerTrigger bt = new BarometerTrigger(getBaseContext());
    }

    // Method to stop the service
    public void stopServiceBar(View view) {
        stopService(new Intent(getBaseContext(), BarometerService.class));

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


    public void sendNotification(Context context, String goalName, String action)
    {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_directions_walk_black_24dp)
                        .setContentTitle(goalName)
                        .setContentText(action)
                        .setDefaults(Notification.DEFAULT_VIBRATE);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
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
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());

        mId++;

    }
}
