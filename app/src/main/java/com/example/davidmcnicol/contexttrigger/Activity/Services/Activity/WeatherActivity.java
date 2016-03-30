package com.example.davidmcnicol.contexttrigger.Activity.Services.Activity;

import android.app.Activity;
import android.os.Bundle;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.davidmcnicol.contexttrigger.R;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by davidmcnicol on 15/03/16.
 */
public class WeatherActivity extends Activity {


    private String a;
    private Bitmap icon = null;
    private TextView temperatureTV, descriptionTV, humidityTV, pressureTV;
    private ImageView image;
    private ArrayList<String> weather = new ArrayList<String>();
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather);

        descriptionTV = (TextView) findViewById(R.id.weather_description);
        temperatureTV = (TextView) findViewById(R.id.weather_temp);
        pressureTV = (TextView) findViewById(R.id.weather_pressure);
        humidityTV = (TextView) findViewById(R.id.weather_humidity);


        new retrieve_weatherTask().execute();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//    // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.weather, menu);
//        return true;
//    }



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
            Log.d("Here","2");
            String response = "";
            String urlString = "http://api.openweathermap.org/data/2.5/weather?q=glasgow,uk&APPID=326a256e75a2b049deb89119dfb778bf";

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

//                InputStream in = new BufferedInputStream(connection.getInputStream());
                BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String s = "";

                while ((s = buffer.readLine()) != null) {
                    response += s;
                }

            }catch (IOException e )
            {
                e.printStackTrace();
            }

//            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
//            HttpGetHC4 request = new HttpGetHC4(url);

//            try {
//                CloseableHttpResponse execute = httpClient.execute(request);
//
//                InputStream content = execute.getEntity().getContent();
//
//                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
//                String s = "";
//
//                while ((s = buffer.readLine()) != null) {
//                    response += s;
//                }
//
//            }catch (IOException e) {
//                e.printStackTrace();
//            }
            return response;
        }

        protected void onPostExecute(String result) {

            Log.d("Here","3   " + result);
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

// set all the fields in the activity from the parsed JSON
//                this.WeatherActivity.SetDescription(description);
//                this.WeatherActivity.SetTemperature(temperature);
//                this.WeatherActivity.SetPressure(pressure);
//                this.WeatherActivity.SetHumidity(humidity);

                descriptionTV.setText("Description: " + description);
                temperatureTV.setText("Temperature: " + temperature);
                pressureTV.setText("Pressure: " + pressure);
                humidityTV.setText("Humidity: " + humidity);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        private double ConvertTemperatureToFarenheit(double temperature) {
            return (temperature - 273) *(9/5) + 32;
        }
    }


    

}
