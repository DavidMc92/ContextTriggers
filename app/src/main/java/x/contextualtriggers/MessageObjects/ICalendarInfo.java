package x.contextualtriggers.MessageObjects;

import android.os.Parcelable;

import java.util.List;

/**
 * Created by Sean on 15/04/2016.
 */
public interface ICalendarInfo extends Parcelable {
    List<IEvent> getCalendarEvents();
}
