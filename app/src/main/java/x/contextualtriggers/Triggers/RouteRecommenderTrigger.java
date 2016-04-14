package x.contextualtriggers.Triggers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import x.contextualtriggers.Application.NotificationSender;
import x.contextualtriggers.MessageObjects.WeatherInfo;
import x.contextualtriggers.R;
import x.contextualtriggers.Services.WeatherService;

// TODO
public class RouteRecommenderTrigger extends BroadcastReceiver implements ITrigger {
    private static final String NOTIFICATION_TITLE = "Route Recommender";
    private static final int NOTIFICATION_ID = 64000;

    private final Context context;

    private WeatherInfo lastWeatherInfo;

    public RouteRecommenderTrigger(Context context){
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Parse out the intents
        if(intent.getAction().equals(WeatherService.WEATHER_INTENT)){
            this.lastWeatherInfo = intent.getParcelableExtra(WeatherService.WEATHER_DATA);

        }

        // Check if the conditions are valid
        if(lastWeatherInfo != null){
            NotificationSender.sendNotification(context, NOTIFICATION_ID,
                    R.drawable.ic_directions_walk_white_18dp,
                    NOTIFICATION_TITLE, "Rar");
        }
    }

    @Override
    public List<Pair<Class<?>, Integer>> getDependentServices() {
        final List<Pair<Class<?>, Integer>> ret = new ArrayList<>();
        ret.add(new Pair(WeatherService.class, 500));
        return ret;
    }

    @Override
    public void registerReceivers(Context context) {
        LocalBroadcastManager.getInstance(context).registerReceiver(this,
                new IntentFilter(WeatherService.WEATHER_INTENT));
    }

    @Override
    public void unregisterReceivers(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }
}
