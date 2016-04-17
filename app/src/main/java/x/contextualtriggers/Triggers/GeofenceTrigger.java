package x.contextualtriggers.Triggers;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import x.contextualtriggers.Application.PreferenceContainer;
import x.contextualtriggers.Services.GeoFenceService;

/**
 * Created by Sean on 16/04/2016.
 */
public abstract class GeofenceTrigger extends BroadcastReceiver implements ITrigger, GoogleApiClient.ConnectionCallbacks,
                GoogleApiClient.OnConnectionFailedListener, ResultCallback {

    protected static final String GEOFENCE_HOME = "Home", GEOFENCE_WORK = "Work";

    private static final int GEOFENCE_RADIUS = 50;

    protected final GoogleApiClient client;
    private final Context context;
    private final List<Geofence> geofenceList;

    public GeofenceTrigger(Context context){
        this.context = context;
        this.client = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        this.geofenceList = new ArrayList<>();
    }

    @Override
    public List<Pair<Class<?>, Integer>> getDependentServices() {
        return new ArrayList<>();   // Don't want TM managing the GeoFenceService
    }

    @Override
    public void registerReceivers(Context context) {
        this.client.connect();
        initGeofences();
        LocalBroadcastManager.getInstance(context).registerReceiver(this,
                new IntentFilter(GeoFenceService.LOCATION_INTENT));
    }

    private void initGeofences(){
        if(!PreferenceContainer.getInstance(context).getHomeAddress().equals("")){
            Log.d(GeofenceTrigger.class.getSimpleName(), "Adding Home.");

            this.geofenceList.add(new Geofence.Builder()
                    .setRequestId(GEOFENCE_HOME)
                    .setCircularRegion(PreferenceContainer.getInstance(context).getHomeLat(),
                            PreferenceContainer.getInstance(context).getHomeLong(),
                            GEOFENCE_RADIUS)
                    .setExpirationDuration(-1)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
        if(!PreferenceContainer.getInstance(context).getWorkAddress().equals("")){
            Log.d(GeofenceTrigger.class.getSimpleName(), "Adding Work.");
            this.geofenceList.add(new Geofence.Builder()
                    .setRequestId(GEOFENCE_WORK)
                    .setCircularRegion(PreferenceContainer.getInstance(context).getWorkLat(),
                            PreferenceContainer.getInstance(context).getWorkLong(),
                            GEOFENCE_RADIUS)
                    .setExpirationDuration(-1)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
    }

    @Override
    public void unregisterReceivers(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
        if(client.isConnected()) {
            LocationServices.GeofencingApi.removeGeofences(
                    this.client, getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(GeoFenceService.class.getSimpleName(), "Retrieving fences.");
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            com.google.android.gms.common.api.PendingResult<Status> result = LocationServices.GeofencingApi.addGeofences(this.client, getGeofencingRequest(), getGeofencePendingIntent());
            Log.d(GeoFenceService.class.getSimpleName(), "Call to start GeoFenceService.");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(Result result) {

    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT);
        builder.addGeofences(this.geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(context, GeoFenceService.class);
        return PendingIntent.getService(context, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }
}
