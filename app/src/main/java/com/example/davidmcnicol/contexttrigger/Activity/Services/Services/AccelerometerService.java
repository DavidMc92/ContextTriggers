package com.example.davidmcnicol.contexttrigger.Activity.Services.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.util.List;

/**
 * Created by davidmcnicol on 18/03/16.
 */
public class AccelerometerService extends Service implements SensorEventListener{

    /** interface for clients that bind */
    IBinder mBinder;

    /** indicates how to behave if the service is killed */
    int mStartMode;

    /** indicates whether onRebind should be used */
    boolean mAllowRebind;

    private static SensorManager mySensorManager;
    List mySensors;
    private long lastUpdate = 0L;
    private float last_x, last_y, last_z = 0;
    private int SHAKE_THRESHOLD = 600;
    private int stepCount = 0;
    private static Context context;
    private Sensor countSensor;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;


    @Override
    public void onCreate() {

        context = AccelerometerService.this;
        mySensorManager = (SensorManager)getSystemService(context.SENSOR_SERVICE);
        countSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mySensorManager.registerListener(this,countSensor,SensorManager.SENSOR_DELAY_NORMAL);

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();


    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        mStartMode = START_STICKY;
        return mStartMode;
    }

    /** A client is binding to the service with bindService() */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** Called when all clients have unbound with unbindService() */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /** Called when a client is binding to the service with bindService()*/
    @Override
    public void onRebind(Intent intent) {

    }

    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        mySensorManager.unregisterListener(this);
        wakeLock.release();

    }


    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        long curTime = System.currentTimeMillis();

        if ((curTime - lastUpdate) > 100) {
            long diffTime = (curTime - lastUpdate);
            lastUpdate = curTime;

            float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

            if (speed > SHAKE_THRESHOLD) {
                stepCount++;
                sendMessageToActivity("1");
            }

            last_x = x;
            last_y = y;
            last_z = z;

            String s = "X: " + last_x + "  Y: " + last_y + "  Z: " + last_z;

//            Log.d("ACC vals", "X: " + last_x + "  Y: " + last_y + "  Z: " + last_z);

        }


    }

    private static void sendMessageToActivity(String msg) {
        Intent intent = new Intent("accData");
        // You can also include some extra data.
        intent.putExtra("Status", msg);
        Bundle b = new Bundle();
        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);
    }

}
