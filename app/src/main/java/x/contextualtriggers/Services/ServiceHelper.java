package x.contextualtriggers.Services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

    public static String performHTTP_GET(Context context, String validURL){
        String response = null;
        // Check if permitted to access the internet and network available
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED && ServiceHelper.isNetworkAvailable(context)) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                final URL url = new URL(validURL);

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                final StringBuilder builder = new StringBuilder();
                String s;
                while ((s = reader.readLine()) != null) {
                    builder.append(s);
                }
                response = builder.toString();
            } catch (IOException e) {
                Log.e(ServiceHelper.class.getSimpleName(), e.getMessage());
            } finally {   // Remember to release resources
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    Log.e(ServiceHelper.class.getSimpleName(), "Unable to release BufferedReader.");
                }
            }
        }
        return response;
    }
}
