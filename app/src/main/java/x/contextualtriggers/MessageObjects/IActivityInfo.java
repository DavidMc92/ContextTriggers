package x.contextualtriggers.MessageObjects;

import android.os.Parcelable;

/**
 * Created by Sean on 16/04/2016.
 */
public interface IActivityInfo extends Parcelable {
    int getEstimatedActivityType();
    int getActivityConfidence();
    long getActivityTime();
}
