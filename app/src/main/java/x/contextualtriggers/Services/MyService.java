package x.contextualtriggers.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Sean on 14/04/2016.
 */
public class MyService extends Service {
    private static final String TAG = "MyService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        android.os.Debug.waitForDebugger();  // this line is key
        Toast.makeText(this, "My Service Created", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");

    }
}
