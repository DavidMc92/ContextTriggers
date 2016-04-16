package x.contextualtriggers.Triggers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import x.contextualtriggers.Application.NotificationSender;
import x.contextualtriggers.MessageObjects.CalendarInfo;
import x.contextualtriggers.MessageObjects.ICalendarInfo;
import x.contextualtriggers.MessageObjects.ILocationInfo;
import x.contextualtriggers.MessageObjects.IWeatherInfo;
import x.contextualtriggers.MessageObjects.LocationInfo;
import x.contextualtriggers.MessageObjects.WeatherType;
import x.contextualtriggers.R;
import x.contextualtriggers.Services.CalendarService;
import x.contextualtriggers.Services.GeoFenceService;
import x.contextualtriggers.Services.WeatherService;

// TODO
public class RouteRecommenderTrigger extends GeofenceTrigger implements ITrigger, LocationListener {
    private static final int NOTIFICATION_ID = 64000;

    private final Context context;

    // Info from appropriate services allowing action
    private IWeatherInfo lastWeatherInfo;
    private ICalendarInfo lastCalendarInfo;
    private ILocationInfo lastGeofenceInfo;

    private Location lastKnownLocation;
    private boolean requestingLocationUpdates;

    public RouteRecommenderTrigger(Context context){
        super(context);
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Parse out the intents
        if(intent.getAction().equals(WeatherService.WEATHER_INTENT)){
            this.lastWeatherInfo = intent.getParcelableExtra(WeatherService.WEATHER_DATA);
        }
        else if(intent.getAction().equals(CalendarService.CALENDAR_INTENT)){
            this.lastCalendarInfo = intent.getParcelableExtra(CalendarService.CALENDAR_DATA);
        }
        else if(intent.getAction().equals(GeoFenceService.LOCATION_INTENT)){
            this.lastGeofenceInfo = intent.getParcelableExtra(GeoFenceService.LOCATION_DATA);
        }

        // Check if all needed info has been delivered
        if(this.lastWeatherInfo != null && this.lastCalendarInfo != null  &&
                this.lastGeofenceInfo != null){
            boolean isSuitableWeather = this.lastWeatherInfo.getWeather() == WeatherType.CLOUDS ||
                                        this.lastWeatherInfo.getWeather() == WeatherType.CLEAR;
            boolean isUserAvailable = CalendarInfo.isUserFree(this.lastCalendarInfo.getCalendarEvents(),
                    new Date().getTime());

            boolean isUserAtWork = LocationInfo.isUserInside(this.lastGeofenceInfo.getLocationInfo(),
                                        GEOFENCE_WORK),
                    isUserAtHome = LocationInfo.isUserInside(this.lastGeofenceInfo.getLocationInfo(),
                                        GEOFENCE_HOME);
            // First stage conditions are suitable
            if(isSuitableWeather && isUserAvailable && !isUserAtWork && !isUserAtHome){
                if(!this.requestingLocationUpdates){
                    this.startLocationUpdates();
                }
                NotificationSender.sendNotification(context, NOTIFICATION_ID,
                        R.drawable.ic_directions_walk_white_18dp,
                        RouteRecommenderTrigger.class.getSimpleName(),
                        "Do something, it's a nice day!");
            }
            else{
                this.stopLocationUpdates();
            }
        }
    }

    @Override
    public List<Pair<Class<?>, Integer>> getDependentServices() {
        final List<Pair<Class<?>, Integer>> ret = new ArrayList<>(super.getDependentServices());
        ret.add(new Pair(WeatherService.class, 30 * 1000));
        ret.add(new Pair(CalendarService.class, 30 * 1000));
        return ret;
    }

    @Override
    public void registerReceivers(Context context) {
        super.registerReceivers(context);

        LocalBroadcastManager.getInstance(context).registerReceiver(this,
                new IntentFilter(WeatherService.WEATHER_INTENT));
        LocalBroadcastManager.getInstance(context).registerReceiver(this,
                new IntentFilter(CalendarService.CALENDAR_INTENT));
    }

    @Override
    public void unregisterReceivers(Context context) {
        super.unregisterReceivers(context);

        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }

    private void startLocationUpdates(){
        if(client != null && client.isConnected() &&
            (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == android.content.pm.PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == android.content.pm.PackageManager.PERMISSION_GRANTED)) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client,
                    createLocationRequest(), this);
            this.requestingLocationUpdates = true;
        }
    }

    private void stopLocationUpdates(){
        if(this.requestingLocationUpdates && client != null && client.isConnected() &&
                (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                                == android.content.pm.PackageManager.PERMISSION_GRANTED)) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
            this.requestingLocationUpdates = false;
        }
    }

    private LocationRequest createLocationRequest(){
        return new LocationRequest()
                .setExpirationDuration(TimeUnit.HOURS.toMillis(1))
                .setInterval(TimeUnit.SECONDS.toMillis(15))
                .setFastestInterval(TimeUnit.SECONDS.toMillis(5))
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                ;
    }

    // Trigger conditions are suitable for location updates; check if user set distance
    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            this.lastKnownLocation = location;
        }
    }
}
