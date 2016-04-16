package x.contextualtriggers.Triggers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import x.contextualtriggers.Application.NotificationSender;
import x.contextualtriggers.MessageObjects.CalendarInfo;
import x.contextualtriggers.MessageObjects.ICalendarInfo;
import x.contextualtriggers.MessageObjects.ILocationInfo;
import x.contextualtriggers.MessageObjects.IWeatherInfo;
import x.contextualtriggers.MessageObjects.WeatherType;
import x.contextualtriggers.R;
import x.contextualtriggers.Services.CalendarService;
import x.contextualtriggers.Services.GeoFenceService;
import x.contextualtriggers.Services.WeatherService;

// TODO
public class LunchTimeLocatorTrigger extends BroadcastReceiver implements ITrigger {

    private static final int NOTIFICATION_ID = 64001;

    private final Context context;

    // Info from appropriate services allowing action
    private IWeatherInfo lastWeatherInfo;
    private ICalendarInfo lastCalendarInfo;
    private ILocationInfo lastLocationInfo;
    private boolean hasUserLeftWork = false;

    public LunchTimeLocatorTrigger(Context context){
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
            this.lastLocationInfo = intent.getParcelableExtra(GeoFenceService.LOCATION_DATA);
            Log.d(LunchTimeLocatorTrigger.class.getSimpleName(),"GeoFenceIntent");
            if(this.lastLocationInfo.getLocationName().equals("Work")&&!this.lastLocationInfo.getInside()){
                Calendar calendar = new GregorianCalendar();
                if ( 11 < calendar.get(Calendar.HOUR_OF_DAY) && calendar.get(Calendar.HOUR_OF_DAY) < 14){
                    hasUserLeftWork = true;
                }

            }
        }

        // Check if all needed info has been delivered
        if(this.lastWeatherInfo != null && this.lastCalendarInfo != null  && this.lastLocationInfo != null){
            boolean isSuitableWeather = this.lastWeatherInfo.getWeather() == WeatherType.CLOUDS ||
                    this.lastWeatherInfo.getWeather() == WeatherType.CLEAR;
            boolean isUserAvailable = CalendarInfo.isUserFree(this.lastCalendarInfo.getCalendarEvents(),
                    new Date().getTime());
            boolean isUserAtWork = (lastLocationInfo.getLocationName().equals("Work") && lastLocationInfo.getInside());

            if(isSuitableWeather && isUserAvailable && isUserAtWork && !hasUserLeftWork){
                NotificationSender.sendNotification(context, NOTIFICATION_ID,
                        R.drawable.ic_restaurant_white_18dp,
                        RouteRecommenderTrigger.class.getSimpleName(),
                        "Maybe you should take a walk its lunch time");
            }
        }
    }

    @Override
    public List<Pair<Class<?>, Integer>> getDependentServices() {
        final List<Pair<Class<?>, Integer>> ret = new ArrayList<>();
        ret.add(new Pair(WeatherService.class, 5 *6000));
        ret.add(new Pair(CalendarService.class, 5 *6000));
        ret.add(new Pair(GeoFenceService.class, -1));
        return ret;
    }

    @Override
    public void registerReceivers(Context context) {
        LocalBroadcastManager.getInstance(context).registerReceiver(this,
                new IntentFilter(WeatherService.WEATHER_INTENT));
        LocalBroadcastManager.getInstance(context).registerReceiver(this,
                new IntentFilter(CalendarService.CALENDAR_INTENT));
        LocalBroadcastManager.getInstance(context).registerReceiver(this,
                new IntentFilter(GeoFenceService.LOCATION_INTENT));
    }

    @Override
    public void unregisterReceivers(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }
}
