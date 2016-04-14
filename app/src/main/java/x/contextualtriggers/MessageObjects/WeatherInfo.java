package x.contextualtriggers.MessageObjects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sean on 14/04/2016.
 */
public class WeatherInfo implements IWeatherInfo {

    private final double temperature, humidity, pressure;
    private final String weatherDesc;

    public WeatherInfo(final double temperature, final double humidity, final double pressure,
                       final String weatherDesc){
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;

        this.weatherDesc = weatherDesc;
    }

    @Override
    public double getTemperature() {
        return this.temperature;
    }

    @Override
    public double getPressure() {
        return this.pressure;
    }

    @Override
    public double getHumidity() {
        return this.humidity;
    }

    @Override
    public String getWeatherDescription() {
        return this.weatherDesc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public WeatherInfo(Parcel in){
        this.temperature = in.readDouble();
        this.humidity = in.readDouble();
        this.pressure = in.readDouble();

        this.weatherDesc = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.temperature);
        dest.writeDouble(this.humidity);
        dest.writeDouble(this.pressure);
        dest.writeString(this.weatherDesc);
    }


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public WeatherInfo createFromParcel(Parcel in) {
            return new WeatherInfo(in);
        }

        public WeatherInfo[] newArray(int size) {
            return new WeatherInfo[size];
        }
    };

    // Builder design pattern
    public static final class WeatherInfoBuilder {
        // Assign bad defaults
        private double temp = -1.0f, hum = -1.0f, pres = -1.0f;
        private String desc = "";

        public WeatherInfoBuilder(){}

        public WeatherInfoBuilder setTemperature(double temperature){
            this.temp = temperature;
            return this;
        }

        public WeatherInfoBuilder setHumidity(double humidity){
            this.hum = humidity;
            return this;
        }

        public WeatherInfoBuilder setPressure(double pressure){
            this.pres = pressure;
            return this;
        }

        public WeatherInfoBuilder setWeatherDescription(String weatherDescription){
            this.desc = weatherDescription;
            return this;
        }

        public WeatherInfo build(){
            return new WeatherInfo(this.temp, this.hum, this.pres, this.desc);
        }
    }
}
