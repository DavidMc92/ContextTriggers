package x.contextualtriggers.MessageObjects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Sean on 15/04/2016.
 */
public class Event implements IEvent {
    private final String title, location;
    private final Date start, end;
    private final boolean isAllDay;

    public Event(String title, String location, Date start, Date end, boolean isAllDay){
        this.title = title;
        this.location = location;
        this.start = start;
        this.end = end;
        this.isAllDay = isAllDay;
    }

    public Event(Parcel in){
        this.title = in.readString();
        this.location = in.readString();
        this.start = new Date(in.readLong());
        this.end = new Date(in.readLong());
        this.isAllDay = in.readByte() == 1;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getLocation() {
        return this.location;
    }

    @Override
    public Date getStartDate() {
        return this.start;
    }

    @Override
    public Date getEndDate() {
        return this.end;
    }

    @Override
    public boolean isAllDay() {
        return this.isAllDay;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.location);
        dest.writeLong(this.start.getTime());
        dest.writeLong(this.end.getTime());
        dest.writeByte((byte)((this.isAllDay) ? 1 : 0));
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public static final class Builder {
        private String title, location;
        private Date start, end;
        private boolean isAllDay;

        // Assign bad defaults
        public Builder(){
            this.title = this.location = "";
            this.start = this.end = new Date();
            this.isAllDay = false;
        }

        public Builder setTitle(String title){
            this.title = title;
            return this;
        }

        public Builder setLocation(String location){
            this.location = location;
            return this;
        }

        public Builder setStart(Date start){
            this.start = start;
            return this;
        }

        public Builder setEnd(Date end){
            this.end = end;
            return this;
        }

        public Builder setIsAllDay(boolean isAllDay){
            this.isAllDay = isAllDay;
            return this;
        }

        public Event build(){
            return new Event(this.title, this.location, this.start, this.end, this.isAllDay);
        }
    }
}
