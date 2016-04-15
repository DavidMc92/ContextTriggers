package x.contextualtriggers.MessageObjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalendarInfo implements ICalendarInfo {

    private final List<IEvent> events;

    public CalendarInfo(List<IEvent> events){
        this.events = Collections.unmodifiableList(events);
    }

    public CalendarInfo(Parcel in){
        this.events = new ArrayList<>();
        in.readList(this.events, null);
    }

    @Override
    public List<IEvent> getCalendarEvents() {
        return this.events;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.events);
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
