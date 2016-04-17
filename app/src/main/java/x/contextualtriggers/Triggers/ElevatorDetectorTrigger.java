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
import x.contextualtriggers.R;
import x.contextualtriggers.Services.AccelerometerService;
import x.contextualtriggers.Services.BarometerService;

/**
 * Created by davidmcnicol on 18/03/16.
 */
public class ElevatorDetectorTrigger extends BroadcastReceiver implements ITrigger {
    private final Context context;
    private ArrayList<Float> barValues = new ArrayList<>();
    private ArrayList<Float> barometerValuesDifference = new ArrayList<>();
    private float currentAltitude = 0;
    private float prevAltitude = 0;
    private float altitude = 0;
    private int arrayPos;
    private int MAX_ARRAY_SIZE = 20;
    private Boolean isAccValSmall, hasNotified = false;
    private static float defaultAltitude = 1016f; // Default air pressure at sea level.
    private float x,y,z,barValue = 0;
    private static final int NOTIFICATION_ID = 64000;

    public ElevatorDetectorTrigger(Context context){
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().equals(AccelerometerService.ACCELEROMETER_INTENT)){
            x = intent.getFloatExtra(AccelerometerService.ACCELEROMETER_X_KEY, -1.0f);
            y = intent.getFloatExtra(AccelerometerService.ACCELEROMETER_Y_KEY, -1.0f);
            z = intent.getFloatExtra(AccelerometerService.ACCELEROMETER_Z_KEY, -1.0f);
        }
        if(intent.getAction().equals(BarometerService.BAROMETER_INTENT)) {
            barValue = intent.getFloatExtra(BarometerService.BAROM_KEY, -1.0f);
        }

        barValues.add(barValue);

        currentAltitude = (defaultAltitude - barValue) * 8;

        if (x < 15 && y < 15 && z < 15)
        {
            isAccValSmall = true;
        }

        if(isAccValSmall)
        {
            altitude = prevAltitude - currentAltitude;
            if (altitude < 1 && altitude > -1) {
                //Log.v("altitude", "" + altitude);
                barometerValuesDifference.set(arrayPos, altitude);
                arrayPos++;
            }

            if (arrayPos >= MAX_ARRAY_SIZE) {
                //Log.v("HERE","RESET array pos");
                arrayPos = 0;
            }

            if (barometerValuesDifference.size() >= MAX_ARRAY_SIZE) {

                int sumBarValues = 0;
                for (int i = 0; i < barometerValuesDifference.size(); i++) {
                    //Log.v("averageBarometerDifference", "" + averageBarometerDifference);
                    sumBarValues += barometerValuesDifference.get(i);
                }

                int averageBarValues = sumBarValues / barometerValuesDifference.size();
                if (averageBarValues >= 0.02 || averageBarValues <= -0.02) {
                    //Lift Detected so send notification
                    if(!hasNotified) {
//                        sendNotification(context, "Left Detected", "In future why not take the stairs to use more energy.");
                        NotificationSender.sendNotification(context, NOTIFICATION_ID,
                                R.drawable.elevator,
                                RouteRecommenderTrigger.class.getSimpleName(),
                                "It has been detected that you are using a lift, next time why not burn some calories by taking the stairs");
                        hasNotified = true;
                    }
                    else
                    {
                        hasNotified = false;
                    }
                }

            }

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
