package x.contextualtriggers.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public abstract class SensorService extends BackgroundService implements SensorEventListener {
    private static final int SCREEN_OFF_RECEIVER_DELAY = 500;

    private SensorManager sensorManager;
    private BroadcastReceiver broadcastReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.broadcastReceiver = new CustomBroadcastReceiver();
        registerReceiver(this.broadcastReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

        Toast.makeText(getApplicationContext(), "Starting service!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        LocalBroadcastManager.getInstance(getApplicationContext()).
                                            sendBroadcast(sensorChanged(event));
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {}

    // Defer registration of a particular sensor to the child service
    protected abstract void registerSensorListener(SensorManager sm);
    protected abstract void unregisterSensorListener(SensorManager sm);
    protected abstract Intent sensorChanged(SensorEvent event);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(this.sensorManager != null){
            registerSensorListener(this.sensorManager);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(this.sensorManager != null){
            unregisterSensorListener(this.sensorManager);
        }

        Toast.makeText(getApplicationContext(), "Stopping service!", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final class CustomBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                Log.d(this.getClass().getSimpleName(), "Screen off action!");
                final Runnable toRun = new Runnable() {
                    @Override
                    public void run() {
                        unregisterSensorListener(sensorManager);
                        registerSensorListener(sensorManager);
                    }
                };
                new Handler().postDelayed(toRun, SCREEN_OFF_RECEIVER_DELAY);
            }
        }
    }
}
