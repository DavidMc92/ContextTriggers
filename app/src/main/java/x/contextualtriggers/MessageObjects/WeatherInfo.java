package x.contextualtriggers.MessageObjects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sean on 14/04/2016.
 */
public class WeatherInfo implements IWeatherInfo {
    private final double temperature, humidity, pressure,
                        windspeed, winddirection, cloudiness, rainvol;
    private final WeatherType weather;
    private final String weatherDesc;

    public WeatherInfo(final double temperature, final double humidity, final double pressure,
                       final double windspeed, final double winddirection, final double cloudiness,
                       final double rainvol, final WeatherType weather, final String weatherDesc){
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;

        this.windspeed = windspeed;
        this.winddirection = winddirection;
        this.cloudiness = cloudiness;

        this.rainvol = rainvol;
        this.weather = weather;

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
    public double getWindSpeed() {
        return this.windspeed;
    }

    @Override
    public double getWindDirection() {
        return this.winddirection;
    }

    @Override
    public double getCloudiness() {
        return this.cloudiness;
    }

    @Override
    public double getRainVolume() {
        return this.rainvol;
    }

    @Override
    public WeatherType getWeather() {
        return this.weather;
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

        this.windspeed = in.readDouble();
        this.winddirection = in.readDouble();
        this.cloudiness = in.readDouble();

        this.rainvol = in.readDouble();
        this.weather = (WeatherType)in.readSerializable();

        this.weatherDesc = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.temperature);
        dest.writeDouble(this.humidity);
        dest.writeDouble(this.pressure);

        dest.writeDouble(this.windspeed);
        dest.writeDouble(this.winddirection);
        dest.writeDouble(this.cloudiness);

        dest.writeDouble(this.rainvol);
        dest.writeSerializable(this.weather);

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
        private double temp = -1.0d, hum = -1.0d, pres = -1.0d, ws = -1.0d,
                    wd = -1.0d, cl = -1.0d, rv = -1.0d;
        private WeatherType type = WeatherType.OTHER;
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

        public WeatherInfoBuilder setWindSpeed(double windSpeed){
            this.ws = windSpeed;
            return this;
        }

        public WeatherInfoBuilder setWindDirection(double windDirection){
            this.wd = windDirection;
            return this;
        }

        public WeatherInfoBuilder setCloudiness(double cloudiness){
            this.cl = cloudiness;
            return this;
        }

        public WeatherInfoBuilder setRainVolume(double rainVolume){
            this.rv = rainVolume;
            return this;
        }

        public WeatherInfoBuilder setWeather(WeatherType weather){
            this.type = weather;
            return this;
        }

        public WeatherInfoBuilder setWeatherDescription(String weatherDescription){
            this.desc = weatherDescription;
            return this;
        }

        public WeatherInfo build(){
            return new WeatherInfo(this.temp, this.hum, this.pres, this.ws, this.wd,
                                this.cl, this.rv, this.type, this.desc);
        }
    }
}
