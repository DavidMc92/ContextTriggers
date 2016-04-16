package x.contextualtriggers.MessageObjects;

import android.os.Parcelable;

/**
 * Created by Colin on 15/04/2016.
 */
public interface ILocationInfo extends Parcelable {
    String getLocationName();    // user defined name for location
    boolean getInside();       // entering or exiting

}

