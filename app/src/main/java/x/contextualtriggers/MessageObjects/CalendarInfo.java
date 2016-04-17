package x.contextualtriggers.MessageObjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CalendarInfo implements ICalendarInfo {

    private final List<IEvent> events;

    public CalendarInfo(List<IEvent> events){
        this.events = events;
    }

    public static boolean isUserFree(List<IEvent> events, long time){
        return isUserFree(events, time, 0);
    }

    public static boolean isUserFree(List<IEvent> events, long startTime, long periodMs){
        boolean ret = true;
        if(!events.isEmpty()){
            final List<IEvent> copy = new ArrayList<>(events);
            Collections.sort(copy, new Comparator<IEvent>() {   // Sort by Start date then End Date
                @Override
                public int compare(IEvent lhs, IEvent rhs) {
                    int ret = lhs.getStartDate().compareTo(rhs.getStartDate());
                    if (ret == 0) {
                        ret = lhs.getEndDate().compareTo(rhs.getEndDate());
                    }
                    return ret;
                }
            });

            final Date start = new Date(startTime), end = new Date(startTime + periodMs);
            for(IEvent e : copy){
                if(start.after(e.getStartDate()) && start.before(e.getEndDate()) ||
                        end.after(e.getStartDate()) && end.before(e.getEndDate())){
                    ret = false;
                    break;
                }
                else if(end.before(e.getStartDate())){  // Since sorted list
                    break;
                }
            }
        }
        return ret;
    }

    public CalendarInfo(Parcel in){
        this.events = new ArrayList<>();
        in.readList(this.events, null);
    }

    @Override
    public List<IEvent> getCalendarEvents() {
        return Collections.unmodifiableList(this.events);
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
