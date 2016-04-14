package x.contextualtriggers.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

public abstract class IntensiveService extends Service {
    private PowerManager.WakeLock mWakeLock;

    @Override
    public void onCreate() {
        super.onCreate();
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if(pm != null){
            this.mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    this.getClass().getSimpleName());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(this.mWakeLock != null){
            this.mWakeLock.acquire();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(this.mWakeLock != null){
            this.mWakeLock.release();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
