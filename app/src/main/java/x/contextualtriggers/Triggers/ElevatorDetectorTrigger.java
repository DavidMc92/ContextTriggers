package x.contextualtriggers.Triggers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import x.contextualtriggers.Services.AccelerometerService;
import x.contextualtriggers.Services.BarometerService;

/**
 * Created by davidmcnicol on 18/03/16.
 */
public class ElevatorDetectorTrigger extends BroadcastReceiver implements ITrigger {
    private final Context context;

    public ElevatorDetectorTrigger(Context context){
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(AccelerometerService.ACCELEROMETER_INTENT)){
            float x = intent.getFloatExtra(AccelerometerService.ACCELEROMETER_X_KEY, -1.0f),
                    y = intent.getFloatExtra(AccelerometerService.ACCELEROMETER_Y_KEY, -1.0f),
                    z = intent.getFloatExtra(AccelerometerService.ACCELEROMETER_Z_KEY, -1.0f);
        }
        else{
            float barValue = intent.getFloatExtra(BarometerService.BAROM_KEY, -1.0f);
        }
    }

    @Override
    public List<Pair<Class<?>, Integer>> getDependentServices() {
        final List<Pair<Class<?>, Integer>> ret = new ArrayList<>();
        ret.add(new Pair(AccelerometerService.class, -1));
        ret.add(new Pair(BarometerService.class, -1));
        return ret;
    }

    @Override
    public void registerReceivers(Context context) {
        // Require accelerometer and barometer updates to perform processing
        LocalBroadcastManager.getInstance(context).registerReceiver(this,
                new IntentFilter(AccelerometerService.ACCELEROMETER_INTENT));
        LocalBroadcastManager.getInstance(context).registerReceiver(this,
                new IntentFilter(BarometerService.BAROMETER_INTENT));
    }

    @Override
    public void unregisterReceivers(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }
}
