package x.contextualtriggers.Services;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

public class BarometerService extends SensorService {
    public static final String BAROMETER_INTENT = "DATA_BAROMETER";

    public static final String BAROM_KEY = "BAROMETER_VAL";

    @Override
    protected Intent sensorChanged(SensorEvent event) {
        final Intent ret = new Intent(BAROMETER_INTENT);
        if(event.values.length >= 1){
            ret.putExtra(BAROM_KEY, event.values[0]);
        }
        return ret;
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
