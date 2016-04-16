package x.contextualtriggers.Triggers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import x.contextualtriggers.Application.NotificationSender;
import x.contextualtriggers.R;

// TODO
public class PedometerPrompterTrigger extends BroadcastReceiver implements ITrigger {
    private final Context context;

    public PedometerPrompterTrigger(Context context){
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(getClass().getSimpleName(), "Received geofence trigger!");
        NotificationSender.sendNotification(context, 64002,
                R.drawable.ic_directions_walk_white_18dp,
                RouteRecommenderTrigger.class.getSimpleName(),
                "You passed a geofence!");
    }

    @Override
    public List<Pair<Class<?>, Integer>> getDependentServices() {
        final List<Pair<Class<?>, Integer>> ret = new ArrayList<>();
        return ret;
    }

    @Override
    public void registerReceivers(Context context) {
    }

    @Override
    public void unregisterReceivers(Context context) {
    }
}
