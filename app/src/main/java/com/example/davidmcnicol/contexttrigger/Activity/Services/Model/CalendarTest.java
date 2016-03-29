package com.example.davidmcnicol.contexttrigger.Activity.Services.Model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by davidmcnicol on 22/03/16.
 */
public class CalendarTest {

    public static void readCalendar(Context context) {

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = CalendarContract.Calendars.CONTENT_URI;
//Uri.parse("content://calendar/calendars")
//        final Cursor cursor = contentResolver.query(uri,
//                (new String[]{"_id", CalendarContract.Calendars.ACCOUNT_NAME, CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.Calendars.OWNER_ACCOUNT}), null, null, null);

        final Cursor cursor = contentResolver.query(uri,
                (new String[]{"_id", CalendarContract.Calendars.ACCOUNT_NAME, CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.Calendars.OWNER_ACCOUNT}), CalendarContract.Calendars.ACCOUNT_TYPE + " LIKE ?", new String[]{"LOCAL"}, null);


        HashSet<String> calendarIds = new HashSet<String>();
        cursor.moveToFirst();

        final String _id = cursor.getString(0);
        final String displayName = cursor.getString(1);
        final String accType = cursor.getString(2);
        final String owner = cursor.getString(3);

        Log.d("Calendar", "Id: " + cursor.getString(0) + " Display Name: " + cursor.getString(1) + " AccType: " + cursor.getString(2) + " owner: " + cursor.getString(3));


//        while (cursor.moveToNext()) {
//
//            final String _id = cursor.getString(0);
//            final String displayName = cursor.getString(1);
//            final String accType = cursor.getString(2);
//            final String owner = cursor.getString(3);
//
////            System.out.println("Id: " + _id + " Display Name: " + displayName + " Selected: " + selected);
//            Log.d("Calendar", "Id: " + _id + " Display Name: " + displayName + " AccType: " + accType + " owner: " + owner );
//            calendarIds.add(_id);
//        }


        Uri.Builder builder = uri.buildUpon();
//        Cursor eventCursor = contentResolver.query(builder.build(),
//                new String[]{CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.ALL_DAY}, "Calendars._id=" + _id,
//                null, "startDay ASC, startMinute ASC");



        String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Events.TITLE, CalendarContract.Events.EVENT_LOCATION, CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Events.ALL_DAY,};

        Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();

        long now = new Date().getTime();

        ContentUris.appendId(eventsUriBuilder, now - DateUtils.WEEK_IN_MILLIS);
        ContentUris.appendId(eventsUriBuilder, now + DateUtils.WEEK_IN_MILLIS);

        Uri eventUri = eventsUriBuilder.build();

        String selection = CalendarContract.Events.CALENDAR_ID + " LIKE ?";
        String[] selectionArgs = new String[]{_id};

        Cursor eventCursor = contentResolver.query(eventUri, EVENT_PROJECTION, null, null, null);

//        Cursor eventCursor = contentResolver.query(builder.build(),
//                new String[] { CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.ALL_DAY}, "Calendars._id=" + _id,
//                null, "startDay ASC, startMinute ASC");

        Log.d("Events","rows: " + eventCursor.getCount() + "  columns: " + eventCursor.getColumnCount());

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

            Log.d("Events","Title: " + title + " Location: " + location + " Begin: " + begin + " End: " + end +
                    " All Day: " + allDay);


//            Log.d("Events","Title: " + title);
        }

        String[] myList = begin.toString().split(" ");
        for(int i = 0; i < myList.length; i++) {
            Log.d("list", myList[i]);
        }

    }
}
