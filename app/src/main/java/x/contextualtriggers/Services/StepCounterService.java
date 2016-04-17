package x.contextualtriggers.Services;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by gregpatrick on 16/04/2016.
 */
public class StepCounterService extends SensorService {

    public static final String PEDOMETER_INTENT = "DATA_PEDOMETER";

    public static final String PEDOMETER_KEY = "PEDOMETER_VAL";


    @Override
    protected void registerSensorListener(SensorManager sm) {
        final Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(sensor != null) {
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        else{
            Toast.makeText(getApplicationContext(), "No pedometer on device!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void unregisterSensorListener(SensorManager sm) {
        sm.unregisterListener(this);
    }

    @Override
    protected Intent sensorChanged(SensorEvent event) {
        final Intent ret = new Intent(PEDOMETER_INTENT);
        if(event.values.length >= 1){
            ret.putExtra(PEDOMETER_KEY, event.values[0]);
        }
        return ret;
    }

}
