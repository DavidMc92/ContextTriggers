package x.contextualtriggers.Triggers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import x.contextualtriggers.Services.WeatherService;

// TODO
public class LunchTimeLocatorTrigger extends BroadcastReceiver implements ITrigger {
    private final Context context;

    public LunchTimeLocatorTrigger(Context context){
        this.context = context;
    }

    @Override
    public List<Pair<Class<?>, Integer>> getDependentServices() {
        final List<Pair<Class<?>, Integer>> ret = new ArrayList<>();
        ret.add(new Pair(WeatherService.class, 500));
        return ret;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }

    @Override
    public void registerReceivers(Context context) {

    }

    @Override
    public void unregisterReceivers(Context context) {

    }
}
