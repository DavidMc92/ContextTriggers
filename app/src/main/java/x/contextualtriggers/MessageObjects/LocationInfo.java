package x.contextualtriggers.MessageObjects;

import android.os.Parcel;

/**
 * Created by Sean on 14/04/2016.
 */
public class LocationInfo implements ILocationInfo {
    private final boolean entering;
    private final String locationDesc;

    public LocationInfo(final String locationDesc, final boolean entering){
        this.entering = entering;
        this.locationDesc = locationDesc;

    }
    @Override
    public String getLocationName(){return this.locationDesc;}

    @Override
    public  boolean getEnterExit(){return this.entering;}

    @Override
    public int describeContents() {
        return 0;
    }

    public LocationInfo(Parcel in){
        this.entering = true;
        this.locationDesc = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeValue(this.entering);
        //dest.writeString(this.locationDesc);
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
    public static final class LocationInfoBuilder {
        // Assign bad defaults
        private boolean entering = true;
        private String desc = "";

        public LocationInfoBuilder(){}

        public LocationInfoBuilder setEntering(boolean entering){
            this.entering = entering;
            return this;
        }

        public LocationInfoBuilder setLocationDescription(String locationDescription){
            this.desc = locationDescription;
            return this;
        }

        public LocationInfo build(){
            return new LocationInfo( this.desc,this.entering);
        }
    }
}
