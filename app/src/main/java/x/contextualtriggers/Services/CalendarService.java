package x.contextualtriggers.Services;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import x.contextualtriggers.MessageObjects.CalendarInfo;
import x.contextualtriggers.MessageObjects.Event;
import x.contextualtriggers.MessageObjects.ICalendarInfo;
import x.contextualtriggers.MessageObjects.IEvent;
import x.contextualtriggers.Misc.DateCreation;

public class CalendarService extends BackgroundService {

    public static final String CALENDAR_INTENT = "DATA_CALENDAR",
            CALENDAR_DATA = "CALENDAR_INFO";

    public CalendarService() {
        super(CalendarService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        broadcastTodaysInfo(getTodaysEvents());
    }

    private ICalendarInfo getTodaysEvents(){
        final List<IEvent> eventList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CALENDAR)
                == android.content.pm.PackageManager.PERMISSION_GRANTED){
            final ContentResolver contentResolver = getApplicationContext().getContentResolver();

            final String[] EVENT_PROJECTION = new String[]{CalendarContract.Events.TITLE,
                    CalendarContract.Events.EVENT_LOCATION,
                    CalendarContract.Instances.BEGIN,
                    CalendarContract.Instances.END,
                    CalendarContract.Events.ALL_DAY};

            final  Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();

            final Date now = new Date();
            final long endOfDay = DateCreation.getEndOfDay(now).getTime();
            ContentUris.appendId(eventsUriBuilder, now.getTime());
            ContentUris.appendId(eventsUriBuilder, endOfDay);

            final Cursor eventCursor = contentResolver.query(eventsUriBuilder.build(),
                    EVENT_PROJECTION,
                    null, null, null);
            while(eventCursor.moveToNext()){
                final Event.Builder builder = new Event.Builder()
                                                    .setTitle(eventCursor.getString(0))
                                                    .setLocation(eventCursor.getString(1))
                                                    .setIsAllDay(!eventCursor.getString(4).equals("0"));

                // Should always succeed but best to be careful
                try{
                    builder.setStart(new Date(Long.parseLong(eventCursor.getString(2))))
                            .setEnd(new Date(Long.parseLong(eventCursor.getString(3))));
                } catch(NumberFormatException nfe) {}

                eventList.add(builder.build());
            }
        }
        return new CalendarInfo(eventList);
    }

    private void broadcastTodaysInfo(ICalendarInfo info){
        final Intent intent = new Intent(CALENDAR_INTENT);
        intent.putExtra(CALENDAR_DATA, info);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}
