package x.contextualtriggers.MessageObjects;

import android.os.Parcel;

import com.google.android.gms.location.Geofence;

import java.util.HashMap;
import java.util.Map;

public class LocationInfo implements ILocationInfo {
    private final Map<String, Integer> locationInfo;

    public LocationInfo(final Map<String, Integer> locationInfo){
        this.locationInfo = locationInfo;
    }

    @Override
    public Map<String, Integer> getLocationInfo() {
        return this.locationInfo;
    }

    public static boolean isUserInside(Map<String, Integer> locInfo, String desc){
        boolean ret = false;
        if(locInfo.containsKey(desc)){
            final int transition = locInfo.get(desc);
            if(transition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                    transition == Geofence.GEOFENCE_TRANSITION_DWELL){
                ret = true;
            }
        }
        return ret;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public LocationInfo(Parcel in){
        this.locationInfo = new HashMap<>();
        in.readMap(this.locationInfo, null);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(this.locationInfo);
    }

    public static final Creator CREATOR = new Creator() {
        public LocationInfo createFromParcel(Parcel in) {
            return new LocationInfo(in);
        }

        public LocationInfo[] newArray(int size) {
            return new LocationInfo[size];
        }
    };

    // Builder design pattern
    public static final class Builder {
        // Assign bad defaults
        private Map<String, Integer> locInfo;

        public Builder(){
            this.locInfo = new HashMap<>();
        }

        public Builder addEntry(String desc, Integer status){
            this.locInfo.put(desc, status);
            return this;
        }

        public LocationInfo build(){
            return new LocationInfo(this.locInfo);
        }
    }
}
