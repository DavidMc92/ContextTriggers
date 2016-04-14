package x.contextualtriggers.Triggers;

import android.content.Context;

/**
 * Created by Sean on 13/04/2016.
 */
public class TriggerManager {
    private ElevatorTrigger et;

    public void checkTriggerConditions(Context ctx, boolean accelEnabled, boolean barEnabled){
        manageElevatorTrigger(ctx, accelEnabled, barEnabled);
    }

    private void manageElevatorTrigger(Context ctx, boolean accelEnabled, boolean barEnabled){
        if(accelEnabled && barEnabled && et == null){
            et = new ElevatorTrigger(ctx);
        }
        else{
            et = null;
        }
    }
}
