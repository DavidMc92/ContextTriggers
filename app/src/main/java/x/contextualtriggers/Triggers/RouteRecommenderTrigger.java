package x.contextualtriggers.Triggers;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import x.contextualtriggers.Application.NotificationSender;
import x.contextualtriggers.Application.PreferenceContainer;
import x.contextualtriggers.MessageObjects.ActivityInfo;
import x.contextualtriggers.MessageObjects.CalendarInfo;
import x.contextualtriggers.MessageObjects.CommuteDetection;
import x.contextualtriggers.MessageObjects.CommuteStatus;
import x.contextualtriggers.MessageObjects.IActivityInfo;
import x.contextualtriggers.MessageObjects.ICalendarInfo;
import x.contextualtriggers.MessageObjects.ILocationInfo;
import x.contextualtriggers.MessageObjects.IWeatherInfo;
import x.contextualtriggers.MessageObjects.LocationInfo;
import x.contextualtriggers.MessageObjects.WeatherType;
import x.contextualtriggers.Misc.DateCreation;
import x.contextualtriggers.R;
import x.contextualtriggers.Services.ActivityRecognitionService;
import x.contextualtriggers.Services.CalendarService;
import x.contextualtriggers.Services.GeoFenceService;
import x.contextualtriggers.Services.ServiceHelper;
import x.contextualtriggers.Services.WeatherService;

public class RouteRecommenderTrigger extends GeofenceTrigger implements ITrigger, LocationListener,
        ITaskCallback<RouteRecommenderTrigger.RouteContainer> {
    private static final int NOTIFICATION_ID = 64003;
    // Callback information for the Map display
    private static final String CALLBACK_MAP = "SHOW_ROUTE_ON_MAP_ACTION",
            CALLBACK_EXTRA = "QueryString";

    private final Context context;

    // Info from appropriate services allowing action
    private IWeatherInfo lastWeatherInfo;
    private ICalendarInfo lastCalendarInfo;
    private ILocationInfo lastGeofenceInfo;
    private IActivityInfo lastActivityInfo;

    private Location lastKnownLocation, currentLocation;
    private boolean requestingGoogleAPIUpdates;
    private long nextPossibleNotificationTime = -1;

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
        else if(intent.getAction().equals(ActivityRecognitionService.ACTIVITY_INTENT)){
            this.lastActivityInfo = intent.getParcelableExtra(ActivityRecognitionService.ACTIVITY_DATA);
        }

        // Check if all needed info has been delivered
        if(this.lastWeatherInfo != null && this.lastCalendarInfo != null  &&
                this.lastGeofenceInfo != null){
            boolean isSuitableWeather = this.lastWeatherInfo.getWeather() == WeatherType.CLEAR ||
                    (this.lastWeatherInfo.getWeather() == WeatherType.CLOUDS &&
                            this.lastWeatherInfo.getRainVolume() < 10); // Clear or cloudy but dry
            boolean isUserAvailable = CalendarInfo.isUserFree(this.lastCalendarInfo.getCalendarEvents(),
                    new Date().getTime());

            boolean isUserAtWork = LocationInfo.isUserInside(this.lastGeofenceInfo.getLocationInfo(),
                    GEOFENCE_WORK),
                    isUserAtHome = LocationInfo.isUserInside(this.lastGeofenceInfo.getLocationInfo(),
                            GEOFENCE_HOME);
            // First stage conditions are suitable
            if(true || (isSuitableWeather && isUserAvailable && !isUserAtWork && !isUserAtHome &&
                    nextPossibleNotificationTime < new Date().getTime())){
                if(!this.requestingGoogleAPIUpdates){
                    this.startGoogleAPIUpdates();
                }
            }
            else{
                this.stopGoogleAPIUpdates();
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
        LocalBroadcastManager.getInstance(context).registerReceiver(this,   // For consistency
                new IntentFilter(ActivityRecognitionService.ACTIVITY_INTENT));
    }

    @Override
    public void unregisterReceivers(Context context) {
        super.unregisterReceivers(context);
        // Shutdown services
        if(this.requestingGoogleAPIUpdates){
            stopGoogleAPIUpdates();
        }
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }

    private void startGoogleAPIUpdates(){
        if(client != null && client.isConnected() &&
                (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                                == android.content.pm.PackageManager.PERMISSION_GRANTED)) {

            LocationServices.FusedLocationApi.requestLocationUpdates(client,
                    createLocationRequest(), this);

            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(client,
                    TimeUnit.SECONDS.toMillis(15), PendingIntent.getService(context, 0,
                            new Intent(context, ActivityRecognitionService.class),
                            PendingIntent.FLAG_UPDATE_CURRENT));

            this.requestingGoogleAPIUpdates = true;
        }
    }

    private void stopGoogleAPIUpdates(){
        if(this.requestingGoogleAPIUpdates && client != null && client.isConnected() &&
                (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED)) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);

            // Can't seem to check the ACTIVITY_RECOGNITION permission dynamically?
            ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(client,
                    PendingIntent.getService(context, 0,
                            new Intent(context, ActivityRecognitionService.class),
                            PendingIntent.FLAG_UPDATE_CURRENT));

            this.requestingGoogleAPIUpdates = false;
        }
    }

    private LocationRequest createLocationRequest(){
        return new LocationRequest()
                .setExpirationDuration(TimeUnit.HOURS.toMillis(1))
                .setInterval(TimeUnit.SECONDS.toMillis(15))
                .setFastestInterval(TimeUnit.SECONDS.toMillis(5))
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    // Trigger conditions are suitable for location updates; now perform extensive checks
    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            updateLocations(location);
            // If we have two valid locations and check notification time again
            // Sometimes unregistering for the location service doesn't execute if
            // the expiration date is long for some reason...
            if(this.currentLocation != null && this.lastKnownLocation != null &&
                    this.nextPossibleNotificationTime < new Date().getTime()){
                decideOnGoogleQuery();
            }
        }
    }

    private void decideOnGoogleQuery(){
        // Use a rough estimation to detect if the user is commuting between home and work
        final CommuteDetection commuteDetection = new CommuteDetection(CommuteStatus.NOT_COMMUTING);
        isCommuting(commuteDetection);
        if(true || commuteDetection.getCommuteStatus() != CommuteStatus.NOT_COMMUTING){
            // Fetch the travel distance
            final boolean goingHome = commuteDetection.getCommuteStatus() == CommuteStatus.COMMUTING_TO_HOME;
            final float distanceToTravel = goingHome ?
                    commuteDetection.getCurrentDistanceToHome() :
                    commuteDetection.getCurrentDistanceToWork();
            // If the shortest route is below a threshold of 5 kilometres
            if(true || distanceToTravel < 5){
                // Make a request to GoogleMaps for the travel-time between the two-points based on
                // their mode of transport
                new GoogleMapsQueryTask(this, (float)currentLocation.getLatitude(),
                        (float)currentLocation.getLongitude(),
                        goingHome ? PreferenceContainer.getInstance(context).getHomeLat() :
                                PreferenceContainer.getInstance(context).getWorkLat(),
                        goingHome ? PreferenceContainer.getInstance(context).getHomeLong() :
                                PreferenceContainer.getInstance(context).getWorkLong(),
                        goingHome).execute();
            }
        }
    }

    private void updateLocations(Location location){
        if(this.currentLocation == null){
            this.currentLocation = location;
        }
        else{
            this.lastKnownLocation = this.currentLocation;
            this.currentLocation = location;
        }
    }

    private void isCommuting(CommuteDetection ret){
        if(!PreferenceContainer.getInstance(context).getHomeAddress().equals("") &&
                !PreferenceContainer.getInstance(context).getWorkAddress().equals("")) {
            ret.setCurrentDistanceToHome(getDistance((float) this.currentLocation.getLatitude(),
                    (float) this.currentLocation.getLongitude(),
                    PreferenceContainer.getInstance(context).getHomeLat(),
                    PreferenceContainer.getInstance(context).getHomeLong()));

            ret.setCurrentDistanceToWork(getDistance((float) this.currentLocation.getLatitude(),
                    (float) this.currentLocation.getLongitude(),
                    PreferenceContainer.getInstance(context).getWorkLat(),
                    PreferenceContainer.getInstance(context).getWorkLong()));

            ret.setPreviousDistanceToHome(getDistance((float) this.lastKnownLocation.getLatitude(),
                    (float) this.lastKnownLocation.getLongitude(),
                    PreferenceContainer.getInstance(context).getHomeLat(),
                    PreferenceContainer.getInstance(context).getHomeLong()));

            ret.setPreviousDistanceToWork(getDistance((float) this.lastKnownLocation.getLatitude(),
                    (float) this.lastKnownLocation.getLongitude(),
                    PreferenceContainer.getInstance(context).getWorkLat(),
                    PreferenceContainer.getInstance(context).getWorkLong()));
            // Getting closer to home
            // TODO need threshold checking like 0.25 km
            if(ret.getCurrentDistanceToHome() < ret.getPreviousDistanceToHome() &&
                    ret.getCurrentDistanceToWork() > ret.getPreviousDistanceToWork()){
                ret.setCommuteStatus(CommuteStatus.COMMUTING_TO_HOME);
            }// Getting closer to work
            else if(ret.getCurrentDistanceToHome() > ret.getPreviousDistanceToHome() &&
                    ret.getCurrentDistanceToWork() < ret.getPreviousDistanceToWork()){
                ret.setCommuteStatus(CommuteStatus.COMMUTING_TO_WORK);
            }
        }
    }

    // Returns distance in Kilometres
    private static final float EARTH_RADIUS = 6371;
    private float getDistance(float lat1, float lon1, float lat2, float lon2){
        float dLat = (float) Math.toRadians(lat2 - lat1);
        float dLon = (float) Math.toRadians(lon2 - lon1);
        float a =
                (float) (Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2));
        float c = (float) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
        return EARTH_RADIUS * c;
    }

    private class GoogleMapsQueryTask extends AsyncTask<Void, Void, RouteContainer>{
        private static final String GOOGLE_MAPS_API_URL = "https://maps.googleapis.com/maps/api/directions/json?",
                COORD_SPEC = "origin=%.5f,%.5f&destination=%.5f,%.5f",
                MODE_SPEC = "&mode=walking",
                TIME_SPEC = "&departure_time=now",
                API_KEY = "&key=AIzaSyDg0VW8SZY4CnHl9dHYU5x3iDlGiIeCJU8";

        private static final String GOOGLE_MAPS_DEST_URL = "https://maps.google.com/maps?",
                DEST_SPEC = "daddr=%.5f,%.5f",
                DEST_MODE_SPEC = "&directions&mode=walking";

        private final ITaskCallback callback;
        private final String queryString, destString;
        private final boolean userGoingHome;

        public GoogleMapsQueryTask(ITaskCallback callback, float currentLat, float currentLong,
                                   float destLat, float destLong, boolean userGoingHome){
            this.callback = callback;
            this.queryString = new StringBuilder(GOOGLE_MAPS_API_URL)
                    .append(String.format(COORD_SPEC, currentLat, currentLong,
                            destLat, destLong))
                    .append(MODE_SPEC)
                    .append(TIME_SPEC)
                    .append(API_KEY)
                    .toString();
            this.destString = new StringBuilder(GOOGLE_MAPS_DEST_URL)
                    .append(String.format(DEST_SPEC, destLat, destLong))
                    .append(DEST_MODE_SPEC)
                    .toString();
            this.userGoingHome = userGoingHome;
        }

        @Override
        protected RouteContainer doInBackground(Void... params) {
            final String result = ServiceHelper.performHTTP_GET(context, this.queryString);
            final long durationSeconds = processJSON(result);
            return new RouteContainer(this.destString, durationSeconds, userGoingHome);
        }

        private long processJSON(final String queryResult){
            long duration = -1;
            if(queryResult != null) {
                try {
                    // Very awkward to parse
                    final JSONObject jsonResult = new JSONObject(queryResult);
                    final JSONArray temp1 = jsonResult.getJSONArray("routes");
                    final JSONObject temp2 = (JSONObject)temp1.get(0);

                    final JSONArray temp3 = temp2.getJSONArray("legs");
                    final JSONObject temp4 = (JSONObject)temp3.get(0);
                    final JSONObject temp5 = temp4.getJSONObject("duration");

                    duration = temp5.getLong("value");
                }
                catch(JSONException json){}
            }
            return duration;
        }

        @Override
        protected void onPostExecute(RouteContainer result) {
            super.onPostExecute(result);
            callback.executeCallback(result);
        }
    }

    public class RouteContainer {
        private final String destinationURL;
        private final long duration;
        private final boolean isUserGoingHome;

        public RouteContainer(String query, long duration, boolean isUserGoingHome){
            this.destinationURL = query;
            this.duration = duration;
            this.isUserGoingHome = isUserGoingHome;
        }

        public String getDestinationURL() {
            return destinationURL;
        }

        public long getDuration() {
            return duration;
        }

        public boolean isUserGoingHome() {
            return isUserGoingHome;
        }
    }

    // Query returned from Google; final level of checks for notification
    @Override
    public void executeCallback(RouteContainer queryResult) {
        // If valid duration and user free for estimated travel time duration
        if(queryResult.getDuration()!= -1 && CalendarInfo.isUserFree(this.lastCalendarInfo.getCalendarEvents(),
                TimeUnit.SECONDS.toMillis(queryResult.getDuration()))){
            String message = new StringBuilder("It's a nice day and your calendar indicates no urgent events.\n")
                    .append("It should take approximately ")
                    .append(timeMillisToString(queryResult.getDuration()*1000))
                    .append(" to reach your ")
                    .append((queryResult.isUserGoingHome()) ? "home " : "workplace ")
                    .append("by walking.\n")
                    .toString();
            boolean notify = false;
            // Check if user is travelling by vehicle with at least 50% probability
            if(true || ActivityInfo.isUserInVehicle(this.lastActivityInfo, 50)){
                message = new StringBuilder(message)
                        .append("We know you are in a vehicle but walking is good for your health!\n")
                        .append("Click to see the best walking route!")
                        .toString();
                // TODO
                final PendingIntent callBackIntent = PendingIntent.getBroadcast(context, 123456,
                        new Intent().setAction(CALLBACK_MAP).putExtra(CALLBACK_EXTRA, queryResult.getDestinationURL()),
                        PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationSender.sendNotificationWithIntent(context, NOTIFICATION_ID,
                        R.drawable.ic_directions_walk_white_18dp,
                        "Route Recommender",
                        message,
                        callBackIntent
                );
                notify = true;
            }// Check if user is travelling on foot with at least 50% probability
            else if(ActivityInfo.isUserOnFoot(this.lastActivityInfo, 50)){
                NotificationSender.sendNotification(context, NOTIFICATION_ID,
                        R.drawable.ic_directions_walk_white_18dp,
                        "Route Recommender",
                        message + "Burn those calories!");
                notify = true;
            }
            else if(ActivityInfo.isUserCycling(this.lastActivityInfo, 50)){
                NotificationSender.sendNotification(context, NOTIFICATION_ID,
                        R.drawable.ic_directions_walk_white_18dp,
                        "Route Recommender",
                        message + "We're sure you can defeat that target on bicycle!");
                notify = true;
            }

            if(notify){
                this.nextPossibleNotificationTime = DateCreation.addHours(new Date(), 6).getTime();
            }
        }
    }

    private String timeMillisToString(long millis){
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    public static class MapCallBackReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(CALLBACK_MAP)){
                final String query = intent.getStringExtra(CALLBACK_EXTRA);
                if(query != null){
                    final Intent mapintent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(query));
                    mapintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    context.startActivity(mapintent);
                }
            }
        }
    }


}
