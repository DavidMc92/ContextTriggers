package x.contextualtriggers.MessageObjects;

import android.os.Parcelable;

import java.util.Map;

/**
 * Created by Colin on 15/04/2016.
 */
public interface ILocationInfo extends Parcelable {
    Map<String, Integer> getLocationInfo();
}

