package com.pramod.courseconnect.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.format.Time;
import android.util.EventLog;
import android.util.Log;


import com.pramod.courseconnect.activities.CCBaseActivity;
import com.pramod.courseconnect.activities.CCMainActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;



public class CalendarDetails {

    private Activity context;

    public CalendarDetails(Activity context) {
        this.context = context;
    }

    public static Map<String, String> list_CalendarNames = new HashMap<String, String>();
    public static Map<String, String> list_EventNames = new HashMap<String, String>();
    Set<String> list_eventNames = new HashSet<String>();
    Set<String> currentEventName = new HashSet<String>();
    private static final int MY_PERMISSIONS = 1;

    public Map<String, String> getAllCalendarNames() {
        Cursor cursor;
        ContentResolver contentResolver = context.getContentResolver();
        Uri.Builder uri = CalendarContract.Calendars.CONTENT_URI.buildUpon();
        Uri calendarUri = uri.build();
        String projections[] = new String[]{
                CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        };
        cursor = contentResolver
                .query(calendarUri, projections, null, null, null);

        list_CalendarNames.clear();

        if (cursor.getCount() > 0) {

            cursor.moveToFirst();
            Log.i("No of calendars", cursor.getCount() + "");
            for (int i = 0; i < cursor.getCount(); i++) {
               // Log.i(cursor.getString(0)+" ", cursor.getString(1));
             if (cursor.getString(1).contains(".")) {
                    if (list_CalendarNames.containsValue(cursor.getString(1))) {
                        for (Map.Entry<String, String> entry : list_CalendarNames.entrySet()) {
                            if (entry.getValue().equals(cursor.getString(1))) {
                                list_CalendarNames.remove(entry.getKey());
                                list_CalendarNames.put(cursor.getString(0), cursor.getString(1));
                            }
                        }
                        //
                    } else if (!list_CalendarNames.containsValue(cursor.getString(1))) {
                        list_CalendarNames.put(cursor.getString(0), cursor.getString(1));
                        Log.i(cursor.getString(0), cursor.getString(1));
                    }



                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        return list_CalendarNames;
    }

    //Go to google calendar when no event exist
    public void goToGoogleCalendar(Context context) {
        long startMills = System.currentTimeMillis();
        Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
        builder.appendPath("time");
        ContentUris.appendId(builder, startMills);
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(builder.build());
        context.startActivity(intent);
    }

    public Set<String> getEvents(CalendarDay date, Set<String> selectedCalendar) {
        Log.i("Get event", "event");
        Time time = new Time();
        long dtStart, dtEnd;

        /*
        when no date in calendar is selected the calendarday from EventsListFragment returns null value.
        By Default, Today's event will be shown if no date in calendar is selected.
         */
        //setting up start and end time
        if (date == null) {
            time.setToNow();
            time.set(00, 00, 01, time.monthDay, time.month, time.year);
            dtStart = time.toMillis(false);
            time.set(59, 59, 23, time.monthDay, time.month, time.year);
            dtEnd = time.toMillis(false);
        } else {
            time.set(00, 00, 00, date.getDay(), date.getMonth(), date.getYear());
            dtStart = time.toMillis(false);
            time.set(59, 59, 23, date.getDay(), date.getMonth(), date.getYear());
            dtEnd = time.toMillis(false);
        }

        Cursor cursor = null;
        ContentResolver contentResolver = context.getContentResolver();
        String[] selectionArgs = selectedCalendar.toArray(new String[selectedCalendar.size()]);
        String questionMark = adjustSelectionArguments(selectedCalendar.size());
        String selection = CalendarContract.Instances.CALENDAR_DISPLAY_NAME + " IN(" + questionMark + ")";
        Uri eventsUri = getUri(dtStart, dtEnd);
        cursor = contentResolver.query(eventsUri, null, selection, selectionArgs,
                CalendarContract.Instances.DTSTART + " ASC");
        cursor.moveToFirst();
        list_eventNames.clear();
        list_EventNames.clear();

        for (int i = 0; i < cursor.getCount(); i++) {
            list_eventNames.add(cursor.getString(cursor.getColumnIndex("title")));
            list_EventNames.put(cursor.getString(cursor.getColumnIndex("event_id")), cursor.getString(cursor.getColumnIndex("title")));
            cursor.moveToNext();
        }
        cursor.close();
        return list_eventNames;

    }

    public Set<String> getCurrentEvent(ArrayList<String> selectedCalendar) {
        Log.i("Inside", "getCurrentEvent");

        Time t = new Time();
        t.setToNow();
        long dtStart = t.toMillis(false);
        t.set(59, 59, 23, t.monthDay, t.month, t.year);
        long dtEnd = t.toMillis(false);

        Cursor cur = null;
        ContentResolver cr = context.getContentResolver();
        String questionMark = adjustSelectionArguments(selectedCalendar.size());
        //had to do this because the selection Argument has a variable length :D
        selectedCalendar.add(dtStart + "");
        selectedCalendar.add(dtStart + "");
        String[] selectionArgs = selectedCalendar.toArray(new String[selectedCalendar.size()]);
        String selection = CalendarContract.Instances.CALENDAR_DISPLAY_NAME + " IN (" + questionMark + ")"
                + " AND (" + CalendarContract.Instances.BEGIN + " <= ? ) AND (" + CalendarContract.Instances.END + " >= ? )";
        Uri eventsUri = getUri(dtStart, dtEnd);
        cur = cr.query(eventsUri, null, selection, selectionArgs, CalendarContract.Instances.DTSTART + " ASC");
        cur.moveToFirst();
        int colIndex = cur.getColumnIndex("title");
        for (int i = 0; i < cur.getCount(); i++) {
            Log.i("CurrentEvent",cur.getString(colIndex).toString() );
            currentEventName.add(cur.getString(colIndex).toString());
            cur.moveToNext();
        }
        cur.close();
        return currentEventName;

    }

    public Uri getUri(long startTime, long endTime) {
        Uri.Builder uriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(uriBuilder, startTime);
        ContentUris.appendId(uriBuilder, endTime);
        Uri uri = uriBuilder.build();
        return uri;
    }

    public String adjustSelectionArguments(int length) {
        String questionMark = "?";
        //had to do this because the selection Argument has a variable length :D
        for (int i = 1; i < length; i++) {
            questionMark = questionMark + ",?";
        }

        return questionMark;
    }

    public void createEvent(String title) {

        List<String> selectedCal = new ArrayList<String>();
        selectedCal.addAll(CCMainActivity.selectedCalendars);
        if(selectedCal != null && selectedCal.size() >0)
            Log.i("Create E in", selectedCal.get(0));
        ContentResolver cr = context.getContentResolver();
        Time time = new Time();
        time.setToNow();
        // time.set(00, 00, 01, time.monthDay, time.month, time.year);
        long dtStart = time.toMillis(false);

        time.set(time.second,time.minute,time.hour+1, time.monthDay, time.month, time.year);
        long dtEnd = time.toMillis(false);
        ContentValues values = new ContentValues();
        getAllCalendarNames();
        for (Map.Entry<String, String> entry : list_CalendarNames.entrySet()) {
            if (entry.getValue().equals(selectedCal.get(0))) {
                Log.i("Inside CE:", title);
                values.put(CalendarContract.Events.CALENDAR_ID, entry.getKey());

                values.put(CalendarContract.Events.DTSTART, dtStart);
                values.put(CalendarContract.Events.TITLE, title);
                values.put(CalendarContract.Events.DTEND, dtEnd);

                Log.i("TimeZone:", dtStart+" "+ dtEnd+"");
                values.put (CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

               /* CCBaseActivity ccBaseActivity = new CCBaseActivity();
                ccBaseActivity.askPermissions(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);*/
                if ((ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)
                    && (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)) {
                    // ODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    /* for ActivityCompat#requestPermissions for more details.
                    String[] permissions = {
                            Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR
                    };
                    ccBaseActivity.askPermissions(permissions);
                    */
                    Log.i("ask", "permission");
                    return;
                }
                try{
                    Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
                }
              catch (Exception e){
                    Log.i("Exceptionn", CalendarContract.Events.CONTENT_URI+"");
                    e.printStackTrace();

              }
              finally{
                    Log.i("Done", "done");
                }

            }

        }

    }

    public void deleteEvent(ArrayList<String> eventName){
        Uri deleteUri = null;
        Log.i("Delete Event", "called");
        for (Map.Entry<String, String> entry : list_EventNames.entrySet()) {
            Log.i("Delete Id:", entry.getKey());
            if (eventName.contains(entry.getValue())) {
                deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, Long.parseLong(entry.getKey()));
                int rows = context.getContentResolver().delete(deleteUri, null, null);
            }
        }
    }

}
