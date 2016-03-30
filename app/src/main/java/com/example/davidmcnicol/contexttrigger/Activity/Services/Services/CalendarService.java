package com.example.davidmcnicol.contexttrigger.Activity.Services.Services;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.CalendarContract;
import android.support.v4.content.LocalBroadcastManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by davidmcnicol on 29/03/16.
 */
public class CalendarService extends Service{

    /** interface for clients that bind */
//    IBinder mBinder;

    /** indicates how to behave if the service is killed */
    int mStartMode;

    /** indicates whether onRebind should be used */
    boolean mAllowRebind;

    private static Context context;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private ArrayList<String> calEvents = new ArrayList<>();
    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        context = CalendarService.this;
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        wakeLock.acquire();


    }

    public class LocalBinder extends Binder {
        public CalendarService getService() {
            // Return this instance of LocalService so clients can call public methods
            return CalendarService.this;
        }
    }

    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        readCalendar();
        return 1;
    }

    /** A client is binding to the service with bindService() */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** Called when all clients have unbound with unbindService() */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /** Called when a client is binding to the service with bindService()*/
    @Override
    public void onRebind(Intent intent) {

    }

    /** Called when The service is no longer used and is being destroyed */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        wakeLock.release();

    }

    public ArrayList readCalendar()
    {
        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = CalendarContract.Calendars.CONTENT_URI;

        final Cursor cursor = contentResolver.query(uri,
                (new String[]{"_id", CalendarContract.Calendars.ACCOUNT_NAME, CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.Calendars.OWNER_ACCOUNT}), CalendarContract.Calendars.ACCOUNT_TYPE + " LIKE ?", new String[]{"LOCAL"}, null);


        HashSet<String> calendarIds = new HashSet<String>();
        cursor.moveToFirst();

        final String _id = cursor.getString(0);
        final String displayName = cursor.getString(1);
        final String accType = cursor.getString(2);
        final String owner = cursor.getString(3);

//        Log.d("Calendar", "Id: " + cursor.getString(0) + " Display Name: " + cursor.getString(1) + " AccType: " + cursor.getString(2) + " owner: " + cursor.getString(3));


        Uri.Builder builder = uri.buildUpon();
        String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Events.TITLE, CalendarContract.Events.EVENT_LOCATION, CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Events.ALL_DAY,};

        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();

        long now = new Date().getTime();

        ContentUris.appendId(eventsUriBuilder, now);
        ContentUris.appendId(eventsUriBuilder, now + (DateUtils.DAY_IN_MILLIS/2));

        Uri eventUri = eventsUriBuilder.build();

        String selection = CalendarContract.Events.CALENDAR_ID + " LIKE ?";
        String[] selectionArgs = new String[]{_id};

        Cursor eventCursor = contentResolver.query(eventUri, EVENT_PROJECTION, null, null, null);

//        Log.d("Events","rows: " + eventCursor.getCount() + "  columns: " + eventCursor.getColumnCount());

        Date begin = new Date();
        Date end = new Date();
        String title = "";
        String location = "";
        Boolean allDay = false;

        while (eventCursor.moveToNext()) {
            title = eventCursor.getString(0);
            location = eventCursor.getString(1);
            begin = new Date(eventCursor.getLong(2));
            end = new Date(eventCursor.getLong(3));
            allDay = !eventCursor.getString(4).equals("0");

//            Log.d("Events","Title: " + title + " Location: " + location + " Begin: " + begin + " End: " + end +
//                    " All Day: " + allDay);

            String s = "Title: " + title + " Location: " + location + " Begin: " + begin + " End: " + end +
                    " All Day: " + allDay;

            calEvents.add(s);
        }

//        sendMessageToActivity(calEvents);
        return calEvents;

//        String[] myList = begin.toString().split(" ");
//        for(int i = 0; i < myList.length; i++) {
//            Log.d("list", myList[i]);
//        }

    }

    private static void sendMessageToActivity(ArrayList msg) {

        Intent intent = new Intent("calData");
        // You can also include some extra data.
        intent.putExtra("Status", msg);
        LocalBroadcastManager.getInstance(context.getApplicationContext()).sendBroadcast(intent);
    }
}
