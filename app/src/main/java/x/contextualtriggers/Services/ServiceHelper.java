package x.contextualtriggers.Services;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;

/**
 * Created by Sean on 14/04/2016.
 */
public class ServiceHelper {

    public static boolean isNetworkAvailable(Context context){
        boolean ret = false;
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)
                == android.content.pm.PackageManager.PERMISSION_GRANTED){
            final ConnectivityManager connectivityManager =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if(connectivityManager != null){
                final NetworkInfo info = connectivityManager.getActiveNetworkInfo();
                ret = (info != null && info.isConnected());
            }
        }
        return ret;
    }

}
