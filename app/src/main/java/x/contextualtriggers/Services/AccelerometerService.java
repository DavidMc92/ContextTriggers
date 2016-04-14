package x.contextualtriggers.Services;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

public class AccelerometerService extends SensorService  {
    public static final String ACCELEROMETER_INTENT = "DATA_ACCELEROMETER";

    public static final String ACCELEROMETER_X_KEY = "ACCELEROMETER_X_VAL",
            ACCELEROMETER_Y_KEY = "ACCELEROMETER_Y_VAL",
            ACCELEROMETER_Z_KEY = "ACCELEROMETER_Z_VAL";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected Intent sensorChanged(SensorEvent event) {
        final Intent ret = new Intent(ACCELEROMETER_INTENT);
        if(event.values.length >= 3){
            ret.putExtra(ACCELEROMETER_X_KEY, event.values[0]);
            ret.putExtra(ACCELEROMETER_Y_KEY, event.values[1]);
            ret.putExtra(ACCELEROMETER_Z_KEY, event.values[2]);
        }
        return ret;
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
