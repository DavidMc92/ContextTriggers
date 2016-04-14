package x.contextualtriggers.Triggers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import x.contextualtriggers.Services.BarometerService;

/**
 * Created by davidmcnicol on 18/03/16.
 */
public class ElevatorTrigger extends BroadcastReceiver {
    private final Context context;

    public ElevatorTrigger(Context context){
        this.context = context;
        LocalBroadcastManager.getInstance(context).registerReceiver(this,
                new IntentFilter(BarometerService.BAROMETER_INTENT));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        float barValue = intent.getFloatExtra(BarometerService.BAROM_KEY, -1.0f);

        return;
    }
}
