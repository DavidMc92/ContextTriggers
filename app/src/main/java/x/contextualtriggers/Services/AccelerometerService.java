package x.contextualtriggers.Services;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * Created by Sean on 13/04/2016.
 */
public class AccelerometerService extends SensorService  {
    public static final String ACCELEROMETER_X_KEY = "ACCELEROMETER_X_VAL",
            ACCELEROMETER_Y_KEY = "ACCELEROMETER_Y_VAL",
            ACCELEROMETER_Z_KEY = "ACCELEROMETER_Z_VAL";

    @Override
    protected void onSensorChanged(SensorEvent event, Intent intent) {
        if(event.values.length >= 3){
            intent.putExtra(ACCELEROMETER_X_KEY, event.values[0]);
            intent.putExtra(ACCELEROMETER_Y_KEY, event.values[1]);
            intent.putExtra(ACCELEROMETER_Z_KEY, event.values[2]);
        }
    }

    @Override
    protected void registerSensorListener(SensorManager sm) {
        final Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(sensor != null) {
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void unregisterSensorListener(SensorManager sm) {
        sm.unregisterListener(this);
    }
}
