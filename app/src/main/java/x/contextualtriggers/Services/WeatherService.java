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
import x.contextualtriggers.MessageObjects.WeatherType;

public class WeatherService extends BackgroundService {
    public static final String WEATHER_INTENT = "DATA_WEATHER",
                                WEATHER_DATA = "WEATHER_INFO";

    private static final String OPEN_WEATHER_MAP_API_KEY = "326a256e75a2b049deb89119dfb778bf";

    private static final String WEATHER_API_URL =
            "http://api.openweathermap.org/data/2.5/weather?q=glasgow,uk&APPID=" + OPEN_WEATHER_MAP_API_KEY ;

    public WeatherService() {
        super(WeatherService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(WeatherService.class.getSimpleName(), "Fetching the weather.");
        broadcastWeather(processWeather(fetchWeather()));
    }

    private String fetchWeather() {
        String response = null;
        // Check if permitted to access the internet and network available
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET)
                == android.content.pm.PackageManager.PERMISSION_GRANTED && ServiceHelper.isNetworkAvailable(this)) {

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
                Log.e(WeatherService.class.getSimpleName(), e.getMessage());
            } finally {   // Remember to release resources
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    Log.e(WeatherService.class.getSimpleName(), "Unable to release BufferedReader.");
                }
            }
        }
        return response;
    }

    // JSON Information:        http://openweathermap.org/current
    // WeatherType Information: http://openweathermap.org/weather-conditions
    private IWeatherInfo processWeather(final String weather){
        final WeatherInfo.WeatherInfoBuilder builder = new WeatherInfo.WeatherInfoBuilder();
        if(weather != null) {
            try {
                // parse the json result returned from the service
                final JSONObject jsonResult = new JSONObject(weather);

                // parse out the temperature from the JSON result
                double val = jsonResult.getJSONObject("main").getDouble("temp") - 273.0;
                builder.setTemperature(val);

                // parse out the pressure from the JSON Result
                val = jsonResult.getJSONObject("main").getDouble("pressure");
                builder.setPressure(val);

                // parse out the humidity from the JSON result
                val = jsonResult.getJSONObject("main").getDouble("humidity");
                builder.setHumidity(val);

                // parse out the current weather from the JSON result
                final WeatherType type = processWeatherType(
                        jsonResult.getJSONArray("weather").getJSONObject(0).getString("main"));
                builder.setWeather(type);

                // parse out the detailed weather description from the JSON result
                final String desc = jsonResult.getJSONArray("weather").getJSONObject(0).
                        getString("description");
                builder.setWeatherDescription(desc);

                // parse out the wind speed from the JSON result
                val = jsonResult.getJSONObject("wind").getDouble("speed");
                builder.setWindSpeed(val);

                // parse out the wind direction from the JSON result
                val = jsonResult.getJSONObject("wind").getDouble("deg");
                builder.setWindDirection(val);

                // Either of the next parameters could result in a JSONException if they have not
                // occurred in the local area for the day

                // parse out the cloudiness from the JSON result
                val = jsonResult.getJSONObject("clouds").getDouble("all");
                builder.setCloudiness(val);

                // parse out the rain volume over the past three hours from the JSON result
                val = jsonResult.getJSONObject("rain").getDouble("3h");
                builder.setRainVolume(val);
            }
            catch(JSONException json){}
        }
        return builder.build();
    }

    private WeatherType processWeatherType(final String description){
        WeatherType ret = WeatherType.OTHER;
        switch(description){
            case "Thunderstorm":    ret = WeatherType.THUNDERSTORM;
                                    break;

            case "Drizzle":         ret = WeatherType.DRIZZLE;
                                    break;

            case "Rain":            ret = WeatherType.RAIN;
                                    break;

            case "Snow":            ret = WeatherType.SNOW;
                                    break;

            case "Atmosphere":      ret = WeatherType.ATMOSPHERE;
                                    break;

            case "Clear":           ret = WeatherType.CLEAR;
                                    break;

            case "Clouds":          ret = WeatherType.CLOUDS;
                                    break;

            case "Extreme":         ret = WeatherType.EXTREME;
                                    break;
        }
        return ret;
    }

    private void broadcastWeather(IWeatherInfo info){
        final Intent intent = new Intent(WEATHER_INTENT);
        intent.putExtra(WEATHER_DATA, info);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}
