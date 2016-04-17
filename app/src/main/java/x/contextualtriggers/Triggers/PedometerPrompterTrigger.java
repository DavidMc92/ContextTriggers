package x.contextualtriggers.Triggers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import x.contextualtriggers.Application.NotificationSender;
import x.contextualtriggers.R;
import x.contextualtriggers.MessageObjects.CalendarInfo;
import x.contextualtriggers.MessageObjects.ICalendarInfo;
import x.contextualtriggers.MessageObjects.IWeatherInfo;
import x.contextualtriggers.MessageObjects.WeatherType;
import x.contextualtriggers.Services.CalendarService;
import x.contextualtriggers.Services.StepCounterService;
import x.contextualtriggers.Services.WeatherService;

// TODO
public class PedometerPrompterTrigger extends BroadcastReceiver implements ITrigger {
    private final Context context;
    private static final int NOTIFICATION_ID = 64002;
    private int steps;
    private Boolean hasNotified = false;
    private Calendar c = Calendar.getInstance();
    int hours = c.get(Calendar.HOUR_OF_DAY);

    // Required services for trigger conditions
    private IWeatherInfo currWeather;
    private ICalendarInfo currCalendar;

    public PedometerPrompterTrigger(Context context) {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Parse out intents
        // WEATHER
        if (intent.getAction().equals(WeatherService.WEATHER_INTENT)) {
            this.currWeather = intent.getParcelableExtra(WeatherService.WEATHER_DATA);
        }

        // CALENDAR
        else if (intent.getAction().equals(CalendarService.CALENDAR_INTENT)) {
            this.currCalendar = intent.getParcelableExtra(CalendarService.CALENDAR_DATA);
        }

        // PEDOMETER
        if (intent.getAction().equals(StepCounterService.PEDOMETER_INTENT)) {
            this.steps = intent.getIntExtra(StepCounterService.PEDOMETER_KEY, 0);
        }

        // Check if all needed info has been delivered
        if (this.currWeather != null && this.currCalendar != null && this.steps != 0) {
            boolean isSuitableWeather = this.currWeather.getWeather() == WeatherType.CLOUDS ||
                    this.currWeather.getWeather() == WeatherType.CLEAR;

            boolean isUserAvailable = CalendarInfo.isUserFree(this.currCalendar.getCalendarEvents(),
                    new Date().getTime());

            // TODO: perform check for user to have met step target
            int target = 100;
            boolean hasMetTarget = steps >= target;

            // Check that time of day is between 12pm and 8pm
            boolean activeTime = hours < 12 && hours > 20;

            // Check weather, availability and step count
            if (isSuitableWeather && isUserAvailable && !hasMetTarget && activeTime) {
                NotificationSender.sendNotification(context, NOTIFICATION_ID,
                        R.drawable.ic_pedometer_white,
                        PedometerPrompterTrigger.class.getSimpleName(),
                        "You've not met your target today!Let's get that step count up!");
                hasNotified = true;
            } else {
                hasNotified = false;
            }
        }
    }


    @Override
    public List<Pair<Class<?>, Integer>> getDependentServices() {
        final List<Pair<Class<?>, Integer>> ret = new ArrayList<>();
        ret.add(new Pair(WeatherService.class, 500));
        ret.add(new Pair(CalendarService.class, 1000));
        ret.add(new Pair(StepCounterService.class, 500));
        return ret;
    }

    @Override
    public void registerReceivers(Context context) {
        LocalBroadcastManager.getInstance(context).registerReceiver(this,
                new IntentFilter(WeatherService.WEATHER_INTENT));
        LocalBroadcastManager.getInstance(context).registerReceiver(this,
                new IntentFilter(CalendarService.CALENDAR_INTENT));
        LocalBroadcastManager.getInstance(context).registerReceiver(this,
                new IntentFilter(StepCounterService.PEDOMETER_INTENT));
    }

    @Override
    public void unregisterReceivers(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }
}
