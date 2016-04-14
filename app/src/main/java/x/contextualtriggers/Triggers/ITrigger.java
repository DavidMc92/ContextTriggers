package x.contextualtriggers.Triggers;

import android.content.Context;

/**
 * Created by Sean on 14/04/2016.
 */
public interface ITrigger {
    void registerReceivers(Context context);
    void unregisterReceivers(Context context);
}
