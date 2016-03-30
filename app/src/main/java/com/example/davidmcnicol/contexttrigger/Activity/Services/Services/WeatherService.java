package com.example.davidmcnicol.contexttrigger.Activity.Services.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGetHC4;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by davidmcnicol on 29/03/16.
 */
public class WeatherService extends Service {

    /** interface for clients that bind */
    IBinder mBinder;

    /** indicates how to behave if the service is killed */
    int mStartMode;

    /** indicates whether onRebind should be used */
    boolean mAllowRebind;

    private static Context context;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate() {

        context = WeatherService.this;
//        mySensorManager = (SensorManager)getSystemService(context.SENSOR_SERVICE);
//        countSensor = mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        mySensorManager.registerListener(this,countSensor,SensorManager.SENSOR_DELAY_NORMAL);
//
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();


    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
//        mStartMode = START_STICKY;
//        return mStartMode;
//        sendMessageToActivity();
        new retrieve_weatherTask().execute();
        return 1;
    }

    /** A client is binding to the service with bindService() */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** Called when all clients have unbound with unbindService() */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /** Called when a client is binding to the service with bindService()*/
    @Override
    public void onRebind(Intent intent) {

    }

    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        wakeLock.release();

    }

    private static void sendMessageToActivity(String msg) {

        Intent intent = new Intent("weatherData");
        // You can also include some extra data.
        intent.putExtra("Status", msg);
        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);
    }


    protected class retrieve_weatherTask extends AsyncTask<Void, String, String> {

//        protected void onPreExecute(){
//            Log.d("Here","1");
//            dialog = new ProgressDialog(WeatherActivity.this);
//            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            dialog.setMessage("Loading…");
//            dialog.setCancelable(false);
//            dialog.show();
//        }

        @Override
        protected String doInBackground(Void... arg0) {
            String response = "";
            String url = "http://api.openweathermap.org/data/2.5/weather?q=glasgow,uk&APPID=326a256e75a2b049deb89119dfb778bf";
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGetHC4 request = new HttpGetHC4(url);

            try {
                CloseableHttpResponse execute = httpClient.execute(request);

                InputStream content = execute.getEntity().getContent();

                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s = "";

                while ((s = buffer.readLine()) != null) {
                    response += s;
                }

            }catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        protected void onPostExecute(String result) {

//            Log.d("Here","3   " + result);
            String test = result;
            try {
                // parse the json result returned from the service
                JSONObject jsonResult = new JSONObject(test);

                // parse out the temperature from the JSON result
                double temperature = jsonResult.getJSONObject("main").getDouble("temp");
                temperature = ConvertTemperatureToFarenheit(temperature);

                // parse out the pressure from the JSON Result
                double pressure = jsonResult.getJSONObject("main").getDouble("pressure");

                // parse out the humidity from the JSON result
                double humidity = jsonResult.getJSONObject("main").getDouble("humidity");

                // parse out the description from the JSON result
                String description = jsonResult.getJSONArray("weather").getJSONObject(0).getString("description");

                sendMessageToActivity(description);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        private double ConvertTemperatureToFarenheit(double temperature) {
            return (temperature - 273) *(9/5) + 32;
        }
    }
}
