package x.contextualtriggers.Services;

import android.Manifest;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import x.contextualtriggers.MessageObjects.IWeatherInfo;
import x.contextualtriggers.MessageObjects.WeatherInfo;

public class WeatherService extends BackgroundService {
    public static final String WEATHER_INTENT = "DATA_WEATHER",
                                WEATHER_DATA = "WEATHERINFO";


    private static final String NAME = "WeatherService";
    private static final String OPEN_WEATHER_MAP_API_KEY = "326a256e75a2b049deb89119dfb778bf";

    private static final String WEATHER_API_URL =
            "http://api.openweathermap.org/data/2.5/weather?q=glasgow,uk&APPID=" + OPEN_WEATHER_MAP_API_KEY ;

    public WeatherService() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(NAME, "Fetching the weather.");
        broadcastWeather(processWeather(fetchWeather()));
    }

    private String fetchWeather() {
        String response = null;
        // TODO Check if currently have network access as well!
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                final URL url = new URL(WeatherService.WEATHER_API_URL);

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
                Log.e(NAME, e.getMessage());
            } finally {   // Remember to release resources
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    Log.e(NAME, "Unable to release BufferedReader.");
                }
            }
        }
        return response;
    }

    private IWeatherInfo processWeather(final String weather){
        final WeatherInfo.WeatherInfoBuilder builder = new WeatherInfo.WeatherInfoBuilder();
        if(weather != null) {
            try {
                // parse the json result returned from the service
                JSONObject jsonResult = new JSONObject(weather);

                // parse out the temperature from the JSON result
                double temperature = jsonResult.getJSONObject("main").getDouble("temp");
                builder.setTemperature(temperature);

                // parse out the pressure from the JSON Result
                double pressure = jsonResult.getJSONObject("main").getDouble("pressure");
                builder.setPressure(pressure);

                // parse out the humidity from the JSON result
                double humidity = jsonResult.getJSONObject("main").getDouble("humidity");
                builder.setHumidity(humidity);

                String description = jsonResult.getJSONArray("weather").getJSONObject(0).getString("description");
                builder.setWeatherDescription(description);
            }
            catch(JSONException json){}
        }
        return builder.build();
    }

    private void broadcastWeather(IWeatherInfo info){
        final Intent intent = new Intent(WEATHER_INTENT);
        intent.putExtra(WEATHER_DATA, info);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}
