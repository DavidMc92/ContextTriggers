package x.contextualtriggers.MessageObjects;

import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Sean on 15/04/2016.
 */
public interface IEvent extends Parcelable {
    String getTitle();
    String getLocation();

    Date getStartDate();
    Date getEndDate();

    boolean isAllDay();
}
