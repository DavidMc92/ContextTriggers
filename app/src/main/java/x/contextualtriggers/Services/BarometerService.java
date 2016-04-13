package x.contextualtriggers.Services;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * Created by Sean on 13/04/2016.
 */
public class BarometerService extends SensorService {
    public static final String BAROM_KEY = "BAROMETER_VAL";

    @Override
    protected void onSensorChanged(SensorEvent event, Intent intent) {
        if(event.values.length >= 1){
            intent.putExtra(BAROM_KEY, event.values[0]);
        }
    }

    @Override
    protected void registerSensorListener(SensorManager sm) {
        final Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if(sensor != null) {
            sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void unregisterSensorListener(SensorManager sm) {
        sm.unregisterListener(this);
    }
}
