package x.contextualtriggers.Triggers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import x.contextualtriggers.Application.NotificationSender;
import x.contextualtriggers.MessageObjects.CalendarInfo;
import x.contextualtriggers.MessageObjects.ICalendarInfo;
import x.contextualtriggers.MessageObjects.IWeatherInfo;
import x.contextualtriggers.MessageObjects.WeatherType;
import x.contextualtriggers.R;
import x.contextualtriggers.Services.CalendarService;
import x.contextualtriggers.Services.WeatherService;

// TODO
public class RouteRecommenderTrigger extends BroadcastReceiver implements ITrigger {
    private static final int NOTIFICATION_ID = 64000;

    private final Context context;

    // Info from appropriate services allowing action
    private IWeatherInfo lastWeatherInfo;
    private ICalendarInfo lastCalendarInfo;

    public RouteRecommenderTrigger(Context context){
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

        // Check if all needed info has been delivered
        if(this.lastWeatherInfo != null && this.lastCalendarInfo != null){
            boolean isSuitableWeather = this.lastWeatherInfo.getWeather() == WeatherType.CLOUDS ||
                                        this.lastWeatherInfo.getWeather() == WeatherType.CLEAR;
            boolean isUserAvailable = CalendarInfo.isUserFree(this.lastCalendarInfo.getCalendarEvents(),
                    new Date().getTime());

            if(isSuitableWeather && isUserAvailable){
                NotificationSender.sendNotification(context, NOTIFICATION_ID,
                        R.drawable.ic_directions_walk_white_18dp,
                        RouteRecommenderTrigger.class.getSimpleName(),
                        "Do something, it's a nice day!");
            }
        }
    }

    @Override
    public List<Pair<Class<?>, Integer>> getDependentServices() {
        final List<Pair<Class<?>, Integer>> ret = new ArrayList<>();
        ret.add(new Pair(WeatherService.class, 500));
        ret.add(new Pair(CalendarService.class, 1000));
        return ret;
    }

    @Override
    public void registerReceivers(Context context) {
        LocalBroadcastManager.getInstance(context).registerReceiver(this,
                new IntentFilter(WeatherService.WEATHER_INTENT));
        LocalBroadcastManager.getInstance(context).registerReceiver(this,
                new IntentFilter(CalendarService.CALENDAR_INTENT));
    }

    @Override
    public void unregisterReceivers(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }
}
