package x.contextualtriggers.MessageObjects;

import android.os.Parcelable;

/**
 * Created by Sean on 14/04/2016.
 */
public interface IWeatherInfo extends Parcelable {
    double getTemperature();    // Celsius
    double getPressure();       // Pascals
    double getHumidity();       // Grams / metre cubed ?

    String getWeatherDescription(); // For now; will work on better description
}
