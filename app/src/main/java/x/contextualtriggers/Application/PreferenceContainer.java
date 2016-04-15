package x.contextualtriggers.Application;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.lang.ref.WeakReference;

import x.contextualtriggers.R;

/**
 * Created by Sean on 14/04/2016.
 */
public class PreferenceContainer implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static PreferenceContainer instance;
    private static WeakReference<Context> contextRef;

    private String homeAddress, workAddress;
    private float homeLat, homeLong, workLat, workLong;

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
            homeAddress = sp.getString(context.getString(R.string.preference_home_key), "");
            homeLat = sp.getFloat(context.getString(R.string.preference_home_lat), -1.0f);
            homeLong = sp.getFloat(context.getString(R.string.preference_home_long), -1.0f);

            workAddress = sp.getString(context.getString(R.string.preference_work_key), "");
            workLat = sp.getFloat(context.getString(R.string.preference_work_lat), -1.0f);
            workLong = sp.getFloat(context.getString(R.string.preference_work_long), -1.0f);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        if(contextRef != null && contextRef.get() != null){
            final Context ctx = contextRef.get();   // Prevent discard temp
            if(key.equals(ctx.getString(R.string.preference_home_key))){
                homeAddress = sp.getString(ctx.getString(R.string.preference_home_key), "");
            }
            else if(key.equals(ctx.getString(R.string.preference_home_lat))){
                homeLat = sp.getFloat(ctx.getString(R.string.preference_home_lat), -1.0f);
            }
            else if(key.equals(ctx.getString(R.string.preference_home_long))){
                homeLong = sp.getFloat(ctx.getString(R.string.preference_home_long), -1.0f);
            }
            else if(key.equals(ctx.getString(R.string.preference_work_key))){
                workAddress = sp.getString(ctx.getString(R.string.preference_work_key), "");
            }
            else if(key.equals(ctx.getString(R.string.preference_work_lat))){
                workLat = sp.getFloat(ctx.getString(R.string.preference_work_lat), -1.0f);
            }
            else if(key.equals(ctx.getString(R.string.preference_work_long))){
                workLong = sp.getFloat(ctx.getString(R.string.preference_work_long), -1.0f);
            }
        }
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public float getHomeLat() {
        return homeLat;
    }

    public float getHomeLong() {
        return homeLong;
    }

    public float getWorkLat() {
        return workLat;
    }

    public float getWorkLong() {
        return workLong;
    }
}
