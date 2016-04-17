package x.contextualtriggers.MessageObjects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.DetectedActivity;

/**
 * Created by Sean on 16/04/2016.
 */
public class ActivityInfo implements IActivityInfo {
    private final int type, confidence;
    private final long timestamp;

    public ActivityInfo(int type, int confidence, long timestamp){
        this.type = type;
        this.confidence = confidence;
        this.timestamp = timestamp;
    }

    public ActivityInfo(Parcel in){
        this.type = in.readInt();
        this.confidence = in.readInt();
        this.timestamp = in.readLong();
    }

    public static boolean isUserOnFoot(IActivityInfo info, float desiredConfidence){
        boolean ret = false;
        if(info.getEstimatedActivityType() == DetectedActivity.ON_FOOT &&
                info.getActivityConfidence() > desiredConfidence){
            ret = true;
        }
        return ret;
    }

    public static boolean isUserInVehicle(IActivityInfo info, float desiredConfidence){
        boolean ret = false;
        if(info.getEstimatedActivityType() == DetectedActivity.IN_VEHICLE &&
                    info.getActivityConfidence() > desiredConfidence){
            ret = true;
        }
        return ret;
    }

    public static boolean isUserCycling(IActivityInfo info, float desiredConfidence){
        boolean ret = false;
        if(info.getEstimatedActivityType() == DetectedActivity.ON_BICYCLE &&
                info.getActivityConfidence() > desiredConfidence){
            ret = true;
        }
        return ret;
    }

    @Override
    public int getEstimatedActivityType() {
        return this.type;
    }

    @Override
    public int getActivityConfidence() {
        return this.confidence;
    }

    @Override
    public long getActivityTime() {
        return this.timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeInt(this.confidence);
        dest.writeLong(this.timestamp);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CalendarInfo createFromParcel(Parcel in) {
            return new CalendarInfo(in);
        }

        public CalendarInfo[] newArray(int size) {
            return new CalendarInfo[size];
        }
    };
}
