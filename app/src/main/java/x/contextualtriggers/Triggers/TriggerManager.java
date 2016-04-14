package x.contextualtriggers.Triggers;

import android.content.Context;

public class TriggerManager {
    private ITrigger et;

    public void checkTriggerConditions(Context ctx, boolean accelEnabled, boolean barEnabled,
                                       boolean weatherEnabled){
        manageElevatorTrigger(ctx, accelEnabled, barEnabled);
        manageLunchTrigger(ctx);
        managePedometerTrigger(ctx);
        manageRouteTrigger(ctx);
    }

    private void manageElevatorTrigger(Context ctx, boolean accelEnabled, boolean barEnabled){
        if(accelEnabled && barEnabled && et == null){
            et = new ElevatorDetectorTrigger(ctx);
            et.registerReceivers(ctx);
        }
        else if(et != null){
            et.unregisterReceivers(ctx);
            et = null;
        }
    }

    // TODO
    private void manageLunchTrigger(Context ctx){

    }

    // TODO
    private void managePedometerTrigger(Context ctx){

    }

    // TODO
    private void manageRouteTrigger(Context ctx){

    }
}
