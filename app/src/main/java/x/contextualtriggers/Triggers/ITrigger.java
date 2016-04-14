package x.contextualtriggers.Triggers;

import android.content.Context;
import android.util.Pair;

import java.util.List;

/**
 * Created by Sean on 14/04/2016.
 */
public interface ITrigger {
    void registerReceivers(Context context);
    void unregisterReceivers(Context context);

    List<Pair<Class<?>, Integer>> getDependentServices();
}
