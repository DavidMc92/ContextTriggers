package x.contextualtriggers.Triggers;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TriggerManager {
    private final Context context;
    private final AlarmManager alarmManager;
    private final ActivityManager activityManager;

    // Cache a list of all triggers to allow service checking
    private final Set<ITrigger> activeTriggers;

    public TriggerManager(Context context){
        this.context = context;
        this.alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        this.activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        this.activeTriggers = new HashSet<>();
    }

    public void enableTrigger(ITrigger trigger){
        this.activeTriggers.add(trigger);

        final List<Pair<Class<?>, Integer>> dependentServices = trigger.getDependentServices();
        for(Pair<Class<?>, Integer> p : dependentServices){
            enableService(p.first, p.second);
        }
        trigger.registerReceivers(context);
    }

    public void disableTrigger(ITrigger trigger){
        this.activeTriggers.remove(trigger);

        final List<Pair<Class<?>, Integer>> dependentServices = trigger.getDependentServices();
        for(Pair<Class<?>, Integer> p : dependentServices){
            disableService(p.first, p.second, false);
        }
        trigger.unregisterReceivers(context);
    }

    private void enableService(Class<?> clz, int period){
        if(!isServiceRunning(clz)){
            // Period flag indicates if alarm necessary or it is otherwise handled
            if(period > 0){
                scheduleAlarm(clz, period);
            }
            else{
                this.context.startService(new Intent(context.getApplicationContext(), clz));
            }
        }
    }

    private void disableService(Class<?> clz, int period, boolean definite){
        if(isServiceRunning(clz)){
            boolean canDisable = true;
            // Check if any other active triggers rely on this service
            if(!definite){
                outerloop:
                for(ITrigger trigger : this.activeTriggers){
                    final List<Pair<Class<?>, Integer>> servs = trigger.getDependentServices();
                    for(Pair<Class<?>, Integer> p : servs){
                            if(p.first.equals(clz)){
                                canDisable = false;
                                break outerloop;
                            }
                        }
                }
            }

            if(canDisable){
                if(period > 0){
                    unscheduleAlarm(clz);
                }
                else{
                    this.context.stopService(new Intent(context.getApplicationContext(), clz));
                }
            }
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        boolean ret = false;
        for(ActivityManager.RunningServiceInfo service : this.activityManager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                ret = true;
                break;
            }
        }
        return ret;
    }

    private void scheduleAlarm(Class clz, long period){
        if(this.alarmManager != null) {
            final Intent intent = new Intent(context, clz);
            final PendingIntent pi = PendingIntent.getService(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            long trigger = System.currentTimeMillis() + 500;
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, trigger, period, pi);
        }
    }

    private void unscheduleAlarm(Class clz){
        if(this.alarmManager != null){
            final Intent intent = new Intent(context, clz);
            final PendingIntent pi = PendingIntent.getService(context, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pi);
        }
    }
}
