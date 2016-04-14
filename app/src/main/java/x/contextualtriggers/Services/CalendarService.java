package x.contextualtriggers.Services;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarService extends BackgroundService implements ICalendarService {

    private final IBinder mBinder = new LocalBinder();

    /** A client is binding to the service with bindService() */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public List<String> getCalendarEvents() {
        final List<String> ret = new ArrayList<>();

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALENDAR)
                == android.content.pm.PackageManager.PERMISSION_GRANTED){
            final ContentResolver contentResolver = getApplicationContext().getContentResolver();

            final String[] EVENT_PROJECTION = new String[]{CalendarContract.Events.TITLE,
                    CalendarContract.Events.EVENT_LOCATION,
                    CalendarContract.Instances.BEGIN,
                    CalendarContract.Instances.END,
                    CalendarContract.Events.ALL_DAY};

            final  Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
            final long now = new Date().getTime();
            ContentUris.appendId(eventsUriBuilder, now);
            ContentUris.appendId(eventsUriBuilder, now + (DateUtils.DAY_IN_MILLIS / 2));

            final Cursor eventCursor = contentResolver.query(eventsUriBuilder.build(),
                                                                EVENT_PROJECTION,
                                                                    null, null, null);
            while(eventCursor.moveToNext()){
                ret.add(new StringBuilder().append("Title: " + eventCursor.getString(0))
                        .append(" Location: " + eventCursor.getString(1))
                        .append(" Begin: " + new Date(eventCursor.getLong(2)).toString())
                        .append(" End: " + new Date(eventCursor.getLong(3)).toString())
                        .append(" All Day: " + !eventCursor.getString(4).equals("0"))
                        .toString());
            }
        }
        return ret;
    }

    public final class LocalBinder extends Binder {
        public CalendarService getService() {
            // Return this instance of LocalService so clients can call public methods
            return CalendarService.this;
        }
    }
}
