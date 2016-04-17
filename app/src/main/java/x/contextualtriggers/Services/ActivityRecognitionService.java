package x.contextualtriggers.Services;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import x.contextualtriggers.MessageObjects.ActivityInfo;
import x.contextualtriggers.MessageObjects.IActivityInfo;

/**
 * Created by Sean on 16/04/2016.
 */
// Custom wrapper for ActivityRecognition Intents
public class ActivityRecognitionService extends BackgroundService {

    public static final String ACTIVITY_INTENT = "DATA_ACTIVITY", ACTIVITY_DATA = "ACTIVITY_INFO";

    public ActivityRecognitionService() {
        super(ActivityRecognitionService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            if(result != null){
                final DetectedActivity activity = result.getMostProbableActivity();
                final IActivityInfo info = new ActivityInfo(activity.getType(),
                        activity.getConfidence(), result.getElapsedRealtimeMillis());

                final Intent toSend = new Intent(ACTIVITY_INTENT);
                toSend.putExtra(ACTIVITY_DATA, info);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(toSend);
            }
        }
    }
}
