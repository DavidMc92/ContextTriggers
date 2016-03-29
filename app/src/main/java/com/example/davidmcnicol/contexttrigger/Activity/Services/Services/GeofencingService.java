package com.example.davidmcnicol.contexttrigger.Activity.Services.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.SyncStateContract;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by davidmcnicol on 29/03/16.
 */
public class GeofencingService extends Service {

    /** interface for clients that bind */
    IBinder mBinder;

    /** indicates how to behave if the service is killed */
    int mStartMode;

    /** indicates whether onRebind should be used */
    boolean mAllowRebind;

    private static Context context;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    private ArrayList mGeofenceList = new ArrayList();



    @Override
    public void onCreate() {

        context = GeofencingService.this;
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();

        GoogleApiClient client = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .setAccountName("davmcnicol@gmail.com")
                .build();

        client.connect();



        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("DavidHome")
                .setCircularRegion(
                        55.895089,
                        -4.194424,
                        50
                )
                .setExpirationDuration(-1)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());


        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);

    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List triggeringGeofences = geofencingEvent.getTriggeringGeofences();


            sendMessageToActivity(triggeringGeofences.get(0).toString());


        }
        return 1;
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
        wakeLock.release();

    }

    private static void sendMessageToActivity(String msg) {

        Intent intent = new Intent("calData");
        // You can also include some extra data.
        intent.putExtra("Status", msg);
        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);
    }
}
