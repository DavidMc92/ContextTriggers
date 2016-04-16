package x.contextualtriggers.Services;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import x.contextualtriggers.MessageObjects.ILocationInfo;
import x.contextualtriggers.MessageObjects.LocationInfo;

/**
 * Created by Sean on 16/04/2016.
 */
public class GeoFenceService extends BackgroundService {
    public static final String LOCATION_INTENT = "DATA_LOCATION",
            LOCATION_DATA = "LOCATION_INFO";

    public GeoFenceService() {
        super(GeoFenceService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(GeoFenceService.class.getSimpleName(), "Handling geofence information.");
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if (event != null) {
            if (!event.hasError()) {
                int transition = event.getGeofenceTransition();
                if (transition == Geofence.GEOFENCE_TRANSITION_ENTER || transition == Geofence.GEOFENCE_TRANSITION_DWELL ||
                        transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    final LocationInfo.Builder builder = new LocationInfo.Builder();
                    for (Geofence geofence : event.getTriggeringGeofences()) {
                        builder.addEntry(geofence.getRequestId(), transition);
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
}
