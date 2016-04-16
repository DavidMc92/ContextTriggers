package x.contextualtriggers.Triggers;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import x.contextualtriggers.Application.NotificationSender;
import x.contextualtriggers.Application.PreferenceContainer;
import x.contextualtriggers.MessageObjects.CalendarInfo;
import x.contextualtriggers.MessageObjects.ICalendarInfo;
import x.contextualtriggers.MessageObjects.ILocationInfo;
import x.contextualtriggers.MessageObjects.IWeatherInfo;
import x.contextualtriggers.MessageObjects.LocationInfo;
import x.contextualtriggers.MessageObjects.WeatherType;
import x.contextualtriggers.R;
import x.contextualtriggers.Services.CalendarService;
import x.contextualtriggers.Services.GeoFenceService;
import x.contextualtriggers.Services.ServiceHelper;
import x.contextualtriggers.Services.WeatherService;

// TODO
public class LunchTimeLocatorTrigger extends GeofenceTrigger implements ITrigger {

    private static final int NOTIFICATION_ID = 64001;

    private final Context context;

    // Info from appropriate services allowing action
    private IWeatherInfo lastWeatherInfo;
    private ICalendarInfo lastCalendarInfo;
    private ILocationInfo lastLocationInfo;
    private boolean hasUserLeftWork = false;

    public LunchTimeLocatorTrigger(Context context){
        super(context);
        this.context = context;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        // Parse out the intents
        if (intent.getAction().equals(WeatherService.WEATHER_INTENT)) {
            this.lastWeatherInfo = intent.getParcelableExtra(WeatherService.WEATHER_DATA);
        } else if (intent.getAction().equals(CalendarService.CALENDAR_INTENT)) {
            this.lastCalendarInfo = intent.getParcelableExtra(CalendarService.CALENDAR_DATA);
        } else if (intent.getAction().equals(GeoFenceService.LOCATION_INTENT)) {
            this.lastLocationInfo = intent.getParcelableExtra(GeoFenceService.LOCATION_DATA);
            Log.d(LunchTimeLocatorTrigger.class.getSimpleName(), "GeoFenceIntent");
            if(!LocationInfo.isUserInside(this.lastLocationInfo.getLocationInfo(), "Work")){
                Calendar calendar = new GregorianCalendar();
                if (11 < calendar.get(Calendar.HOUR_OF_DAY) && calendar.get(Calendar.HOUR_OF_DAY) < 14) {
                    hasUserLeftWork = true;
                }
            }
        }

        new NearbyRestaurantFinder().execute("");

        // Check if all needed info has been delivered
        if (this.lastWeatherInfo != null && this.lastCalendarInfo != null && this.lastLocationInfo != null) {
            boolean isSuitableWeather = this.lastWeatherInfo.getWeather() == WeatherType.CLOUDS ||
                    this.lastWeatherInfo.getWeather() == WeatherType.CLEAR;
            boolean isUserAvailable = CalendarInfo.isUserFree(this.lastCalendarInfo.getCalendarEvents(),
                    new Date().getTime());
            boolean isUserAtWork = (LocationInfo.isUserInside(this.lastLocationInfo.getLocationInfo(), "Work"));
            Calendar calendar = new GregorianCalendar();
            boolean isEndOfLunchTime = (13 < calendar.get(Calendar.HOUR_OF_DAY) && calendar.get(Calendar.HOUR_OF_DAY) < 14);
            if (isSuitableWeather && isUserAvailable && isUserAtWork && !hasUserLeftWork && isEndOfLunchTime) {
                new NearbyRestaurantFinder().execute("");
            }
        }
    }

    @Override
    public List<Pair<Class<?>, Integer>> getDependentServices() {
        final List<Pair<Class<?>, Integer>> ret = new ArrayList<>();
        ret.add(new Pair(WeatherService.class, 6000));
        ret.add(new Pair(CalendarService.class, 6000));
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

    public static  class MapOpenCallbackReceiver extends BroadcastReceiver {

        public static void setLocationForCallBack(double lat, double lon){
            MapOpenCallbackReceiver.lat = lat;
            MapOpenCallbackReceiver.lon = lon;
        }
        private static double lat = 0,lon = 0;
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent mapintent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr="+lat+","+lon));
            mapintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            context.startActivity(mapintent);
        }

    }
    private class NearbyRestaurantFinder extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            String res =fetchNearby();
            Log.d(NearbyRestaurantFinder.class.getSimpleName(),res);
            if(!res.equals("none")){
                Intent showReceive = new Intent();
                showReceive.setAction("SHOW_ON_MAP_ACTION");
                PendingIntent pendingIntentShow = PendingIntent.getBroadcast(context, 12345, showReceive, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationSender.sendNotificationWithIntent(context, NOTIFICATION_ID,
                        R.drawable.ic_restaurant_white_18dp,
                        LunchTimeLocatorTrigger.class.getSimpleName(),
                        ("Why not try: " + res + " for Lunch its just a short walk away"),pendingIntentShow);
            }else {

                NotificationSender.sendNotification(context, NOTIFICATION_ID,
                        R.drawable.ic_restaurant_white_18dp,
                        LunchTimeLocatorTrigger.class.getSimpleName(),
                        "Maybe you should take a walk its lunch time");
            }
            return null;
        }



        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }


        private static final String GOOGLE_PLACES_API_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?",
                COORD_QUERY = "location=%f,%f&radius=1000",
                TYPE_QUERY = "&type=restaurant&opennow",
                KEY_QUERY = "&key=AIzaSyD06mJGSBw5K6r9Qf4Lo5DA7FZ7Fhe7jcw";


        private URL buildURL() throws IOException {
            final StringBuilder builder = new StringBuilder(GOOGLE_PLACES_API_URL);

            builder.append(String.format(COORD_QUERY, PreferenceContainer.getInstance(context).getWorkLat(), PreferenceContainer.getInstance(context).getWorkLong()));
            builder.append(TYPE_QUERY);
            builder.append(KEY_QUERY);
            return new URL(builder.toString());
        }

        private String fetchNearby() {
            String response = null;
            // Check if permitted to access the internet and network available
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
                    == android.content.pm.PackageManager.PERMISSION_GRANTED && ServiceHelper.isNetworkAvailable(context)) {

                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    final URL url = buildURL();

                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.connect();

                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    final StringBuilder builder = new StringBuilder();
                    String s;
                    while ((s = reader.readLine()) != null) {
                        builder.append(s);
                    }
                    response = builder.toString();
                } catch (IOException e) {
                    Log.e(WeatherService.class.getSimpleName(), e.getMessage());
                } finally {   // Remember to release resources
                    if (connection != null) {
                        connection.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        Log.e(WeatherService.class.getSimpleName(), "Unable to release BufferedReader.");
                    }
                }
            }
            return processResults(response);
        }

        private String processResults(final String data) {
            if (data != null) {
                try {
                    // parse the json result returned from the service
                    final JSONArray jsonResult = new JSONObject(data).getJSONArray("results");

                    // parse out the temperature from the JSON result
                    if (jsonResult.length() > 0) {

                        JSONObject target =jsonResult.getJSONObject(new Random().nextInt(jsonResult.length()));
                        JSONObject loc = target.getJSONObject("geometry").getJSONObject("location");
                        MapOpenCallbackReceiver.setLocationForCallBack(loc.getDouble("lat"),loc.getDouble("lng"));
                        return target.getString("name") + " at " + target.getString("vicinity");
                    } else {
                        return "none";
                    }
                } catch (JSONException json) {
                }
            }
            return "none";
        }
    }

}
