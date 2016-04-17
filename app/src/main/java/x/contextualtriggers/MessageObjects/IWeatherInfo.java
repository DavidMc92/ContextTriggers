package x.contextualtriggers.MessageObjects;

import android.os.Parcelable;

/**
 * Created by Colin on 15/04/2016.
 */
public interface IWeatherInfo extends Parcelable {
    double getTemperature();    // Celsius
    double getPressure();       // HectoPascals

    double getHumidity();       // Percentage

    double getWindSpeed();      // Metres / seconds
    double getWindDirection();  // Degress (meteorological)

    double getCloudiness();     // Percentage

    double getRainVolume();     // Volume over last three hours

    WeatherType getWeather();   // Enum representation of weather

    String getWeatherDescription(); // For now; will work on better description
}

