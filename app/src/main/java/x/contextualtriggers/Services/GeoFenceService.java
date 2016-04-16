package x.contextualtriggers.Services;


import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;

import java.util.ArrayList;
import java.util.List;

import x.contextualtriggers.Application.PreferenceContainer;
import x.contextualtriggers.MessageObjects.ILocationInfo;
import x.contextualtriggers.MessageObjects.LocationInfo;

/**
 * Created by Colin on 15/04/16.
 */
public class GeoFenceService extends BackgroundService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback  {

        public static final String LOCATION_INTENT = "DATA_LOCATION",
                LOCATION_DATA = "LOCATION_INFO";


    private static final int GEOFENCE_RADIUS = 30;

    private GoogleApiClient client;

    private List<Geofence> geofenceList;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(GeoFenceService.class.getSimpleName(), "Creating CLient.");
        this.client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        this.geofenceList = new ArrayList<>();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int ret = super.onStartCommand(intent, flags, startId);
        this.client.connect();
        if(!PreferenceContainer.getInstance(getApplicationContext()).getHomeAddress().equals("")){
            Log.d(GeoFenceService.class.getSimpleName(), "Adding Home.");

            this.geofenceList.add(new Geofence.Builder()
                    .setRequestId("Home")
                    .setCircularRegion(PreferenceContainer.getInstance(getApplicationContext()).getHomeLat(),
                            PreferenceContainer.getInstance(getApplicationContext()).getHomeLong(),
                            GEOFENCE_RADIUS)
                    .setExpirationDuration(-1)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
        if(!PreferenceContainer.getInstance(getApplicationContext()).getWorkAddress().equals("")){
            Log.d(GeoFenceService.class.getSimpleName(), "Adding Work.");
            this.geofenceList.add(new Geofence.Builder()
                    .setRequestId("Work")
                    .setCircularRegion(PreferenceContainer.getInstance(getApplicationContext()).getWorkLat(),
                            PreferenceContainer.getInstance(getApplicationContext()).getWorkLong(),
                            GEOFENCE_RADIUS)
                    .setExpirationDuration(-1)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());
        }
        return ret;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(GeoFenceService.class.getSimpleName(), "Starting fences.");
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            PendingResult<Status> result = LocationServices.GeofencingApi.addGeofences(this.client, getGeofencingRequest(), getGeofencePendingIntent());
            Log.d(GeoFenceService.class.getSimpleName(), "Started Fences.");
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(this.geofenceList);
        return builder.build();
    }
    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeoFenceService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    public GeoFenceService() {
            super(GeoFenceService.class.getSimpleName());
        }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(GeoFenceService.class.getSimpleName(), "Fency.");
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event != null) {
            if (event.hasError()) {

            } else {
                int transition = event.getGeofenceTransition();
                if (transition == Geofence.GEOFENCE_TRANSITION_ENTER || transition == Geofence.GEOFENCE_TRANSITION_DWELL || transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    List<String> geofenceIds = new ArrayList<>();
                    final LocationInfo.LocationInfoBuilder builder = new LocationInfo.LocationInfoBuilder();
                    builder.setLocationDescription(event.getTriggeringGeofences().get(0).getRequestId());
                    if (transition == Geofence.GEOFENCE_TRANSITION_ENTER || transition == Geofence.GEOFENCE_TRANSITION_DWELL) {

                        builder.setInside(true);
                    }else {
                        builder.setInside(false);

                    }
                    broadcastLocationInfo(builder.build());

                }
            }
        }
    }


        private void broadcastLocationInfo(ILocationInfo info){
            final Intent intent = new Intent(LOCATION_INTENT);
            intent.putExtra(LOCATION_DATA, info);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
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
    }

