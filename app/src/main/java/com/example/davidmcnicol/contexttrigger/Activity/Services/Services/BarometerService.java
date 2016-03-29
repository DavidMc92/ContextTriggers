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
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by davidmcnicol on 18/03/16.
 */
public class BarometerService extends Service implements SensorEventListener {

    /** interface for clients that bind */
    IBinder mBinder;

    /** indicates how to behave if the service is killed */
    int mStartMode;

    /** indicates whether onRebind should be used */
    boolean mAllowRebind;

    private static SensorManager mySensorManager;
    private static Context context;
    private Sensor pressureSensor;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {

        context = BarometerService.this;
        mySensorManager = (SensorManager)getSystemService(context.SENSOR_SERVICE);

        List<Sensor> list = mySensorManager.getSensorList(Sensor.TYPE_ALL);
        for(Sensor sensor: list){
            Log.d("Sensors", sensor.getName());
        }

        if(mySensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) == null)
        {
            Log.d("Sensors", "pressure sensor is null");
        }

        pressureSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mySensorManager.registerListener(this,pressureSensor,SensorManager.SENSOR_DELAY_NORMAL);

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();

        Log.d("HERE","HERE");

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

        float value = event.values[0];

        sendMessageToActivity(String.valueOf(value));

    }

    private static void sendMessageToActivity(String msg) {
        Intent intent = new Intent("barData");
        // You can also include some extra data.
        intent.putExtra("Status", msg);
        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);
    }
}

