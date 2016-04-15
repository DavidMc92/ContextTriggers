package x.contextualtriggers.Application;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.lang.ref.WeakReference;

/**
 * Created by Sean on 14/04/2016.
 */
public class PreferenceContainer implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static PreferenceContainer instance;
    private static WeakReference<Context> contextRef;


    public static PreferenceContainer getInstance(Context context){
        if(instance == null || contextRef == null || contextRef.get() == null){
            instance = new PreferenceContainer(context);
        }
        return instance;
    }

    private PreferenceContainer(Context context){
        contextRef = new WeakReference<>(context);

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        if(sp != null){

        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
