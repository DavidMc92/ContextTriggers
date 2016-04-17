package x.contextualtriggers.Triggers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
<<<<<<< HEAD
=======
import android.util.Log;
>>>>>>> Correcting merge
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import x.contextualtriggers.Application.NotificationSender;
<<<<<<< HEAD
=======
import x.contextualtriggers.R;
>>>>>>> Correcting merge
import x.contextualtriggers.MessageObjects.CalendarInfo;
import x.contextualtriggers.MessageObjects.ICalendarInfo;
import x.contextualtriggers.MessageObjects.IWeatherInfo;
import x.contextualtriggers.MessageObjects.WeatherType;
import x.contextualtriggers.Services.CalendarService;
import x.contextualtriggers.Services.StepCounterService;
import x.contextualtriggers.Services.WeatherService;
<<<<<<< HEAD
=======

>>>>>>> Correcting merge

// TODO
public class PedometerPrompterTrigger extends BroadcastReceiver implements ITrigger {
    private final Context context;
    private static final int NOTIFICATION_ID = 64002;
    private int steps;

    // Required services for trigger conditions
    private IWeatherInfo currWeather;
    private ICalendarInfo currCalendar;

    public PedometerPrompterTrigger(Context context){
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
<<<<<<< HEAD
<<<<<<< HEAD
=======

        Log.d(getClass().getSimpleName(), "Received geofence trigger!");
        NotificationSender.sendNotification(context, 64002,
                R.drawable.ic_directions_walk_white_18dp,
                RouteRecommenderTrigger.class.getSimpleName(),
                "You passed a geofence!");

>>>>>>> Correcting merge
=======
>>>>>>> Tidying up PedometerPrompter code to include step_counter intent. Need to fix preference for step target still
        // Parse out intents
        // WEATHER
        if(intent.getAction().equals(WeatherService.WEATHER_INTENT)){
            this.currWeather = intent.getParcelableExtra(WeatherService.WEATHER_DATA);
        }

        // CALENDAR
        else if(intent.getAction().equals(CalendarService.CALENDAR_INTENT)){
            this.currCalendar = intent.getParcelableExtra(CalendarService.CALENDAR_DATA);
        }

        // PEDOMETER
        if(intent.getAction().equals(StepCounterService.PEDOMETER_INTENT)) {
            this.steps = intent.getIntExtra(StepCounterService.PEDOMETER_KEY, 0);
        }

        // Check if all needed info has been delivered
        if(this.currWeather != null && this.currCalendar != null && this.steps != 0) {
            boolean isSuitableWeather = this.currWeather.getWeather() == WeatherType.CLOUDS ||
                    this.currWeather.getWeather() == WeatherType.CLEAR;

            boolean isUserAvailable = CalendarInfo.isUserFree(this.currCalendar.getCalendarEvents(),
                    new Date().getTime());

            // TODO: perform check for user to have met step target

            
            // Check weather, availability and step count
            if(isSuitableWeather && isUserAvailable) { // & !metStepTarget
                NotificationSender.sendNotification(context, NOTIFICATION_ID,
                        R.drawable.ic_pedometer_white,
                        PedometerPrompterTrigger.class.getSimpleName(),
                        "Let's get that step count up!");
            }
        }
<<<<<<< HEAD
=======

>>>>>>> Correcting merge
    }

    @Override
    public List<Pair<Class<?>, Integer>> getDependentServices() {
        final List<Pair<Class<?>, Integer>> ret = new ArrayList<>();
<<<<<<< HEAD
=======

>>>>>>> Correcting merge
        ret.add(new Pair(WeatherService.class, 500));
        ret.add(new Pair(CalendarService.class, 1000));
        //TODO Pedometer service dependence
        return ret;
    }

    @Override
    public void registerReceivers(Context context) {
        LocalBroadcastManager.getInstance(context).registerReceiver(this,
                new IntentFilter(WeatherService.WEATHER_INTENT));
        LocalBroadcastManager.getInstance(context).registerReceiver(this,
                new IntentFilter(CalendarService.CALENDAR_INTENT));
        // TODO Pedometer Intent Filter
    }

    @Override
    public void unregisterReceivers(Context context) {
<<<<<<< HEAD
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
=======

        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);

>>>>>>> Correcting merge
    }
}
