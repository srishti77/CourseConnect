package com.pramod.courseconnect.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;

import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.text.format.Time;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import android.widget.EditText;
import android.widget.Toast;

import com.pramod.courseconnect.R;
import com.pramod.courseconnect.fragments.CalendarFragment;
import com.pramod.courseconnect.helpers.CalendarDetails;
import com.pramod.courseconnect.helpers.CreateDirectories;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class CCBaseActivity extends AppCompatActivity {

    CalendarDetails getCalendarDetails = new CalendarDetails(CCBaseActivity.this);
    CreateDirectories createDirectories = new CreateDirectories();
    private static final int MY_PERMISSIONS = 1;

    public static String selectedCurrentEvent = "";
    Set<String> currentEvents = new HashSet<String>();
    public Set<String> selectedCalendars = new HashSet<String>();
    AlertDialog.Builder builder;
    AlertDialog alertForSettingsConfig;
    public static boolean eventCreated = false;

    public void createEvent() {
        Log.i("Create Event", "called");
        builder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.create_event_layout, null);
        final EditText title = (EditText) dialogView.findViewById(R.id.createEvent);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                     if(title.getText()+"" != ""){
                         Log.i("Ok pressed", "");
                         getCalendarDetails.createEvent(title.getText()+"");
                         selectedCurrentEvent = title.getText()+"";
                         Toast.makeText(getApplicationContext(), getString(R.string.create_event), Toast.LENGTH_SHORT).show();
                         eventCreated = true;
                         dialog.dismiss();
                     }

                    }
                })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                                dialog.dismiss();
                            }
                        });

        alertForSettingsConfig = builder.create();
        alertForSettingsConfig.show();

    }

    public void askPermissions(String permissions[]){

            if (!hasPermissions(this, permissions)){

                ActivityCompat.requestPermissions(this,
                    permissions, MY_PERMISSIONS);
            }

    }
    public static boolean hasPermissions(Context context, String[] permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS:
                for(int i=0; i< permissions.length;i++){
                    if (grantResults.length > 0
                            && grantResults[i] == PERMISSION_GRANTED) {

                        Log.i("Permission Granted", "Permission Granted");
                    } else  if(grantResults[i] == PERMISSION_DENIED){

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i] )) {
                            new AlertDialog.Builder(this)
                                    .setTitle(this.getString(R.string.Request_Permission_Grant))
                                    .setMessage(this.getString(R.string.retry_permission))
                                    .setCancelable(false)
                                    .setPositiveButton(this.getString(R.string.retry), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                           refresh();
                                        }
                                    }).show();
                        } else {
                           new AlertDialog.Builder(this)
                                    .setTitle(this.getString(R.string.important_permission_denied))
                                    .setMessage(this.getString(R.string.features_requirement))
                                    .setCancelable(false)
                                    .setPositiveButton(this.getString(R.string.grant_permission), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            dialog.cancel();
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                    Uri.parse("package:" + getPackageName()));
                                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);

                                        }
                                    })
                                   .setNegativeButton(this.getString(R.string.no), new DialogInterface.OnClickListener() {
                                       public void onClick(DialogInterface dialog, int id) {
                                           Intent intent1 = getIntent();
                                           finish();
                                       }
                                   }).show();

                        }
                    }
                }

                break;
        }

    }

    public boolean checkIfEventExists() {
        return true;
    }

    public String selectEventIfMultipleEventsOccur(Set<String> currentEvent){
        final ArrayList<String> currentEvents =  new ArrayList<String>(currentEvent);

        final AlertDialog.Builder builder = new AlertDialog.Builder(CCBaseActivity.this);

        builder.setTitle(R.string.more_than_one_event_msg);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                currentEvents);

        builder.setSingleChoiceItems(arrayAdapter, -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        selectedCurrentEvent = currentEvents.get(i);
                        Toast.makeText(getApplicationContext(), CCBaseActivity.this.getString(R.string.selected_event)+" " + selectedCurrentEvent, Toast.LENGTH_LONG).show();
                        dialogInterface.cancel();
                    }
                });

        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setLayout(700,600);
        alertDialog.show();

        return selectedCurrentEvent;
    }

    public void selectedCurrentEvent(){

        if((selectedCurrentEvent == null || selectedCurrentEvent.isEmpty())
                && (CCMainActivity.selectedCalendars != null && !CCMainActivity.selectedCalendars.isEmpty()) )
        {

            CalendarDetails calendarDetails = new CalendarDetails(this);
            currentEvents = calendarDetails.
                    getCurrentEvent(new ArrayList<String>(CCMainActivity.selectedCalendars));

            if(currentEvents.size() == 1){
                Log.i("No of Current Events ", currentEvents.size()+"");
                selectedCurrentEvent = currentEvents.iterator().next();
            }

            else if(currentEvents.size()>1)
            {
                Log.i("No of Current Events ", currentEvents.size()+"");

                selectEventIfMultipleEventsOccur(currentEvents);
                Log.i("Selected Event", selectedCurrentEvent+"");
                //show the alert Dialog when the two or more events exist
            }

            else if(currentEvents.size() < 1){
                createEvent();
            }
        }
    }


    public void setLocale(String o){
        String lang = null;
        switch(o+""){
            case "English":
                Log.i("English", "-----");
                lang = "en";
                break;
            case "German":
                Log.i("GErman", "-----");
                lang = "de";
                break;
            case "Italian":
                Log.i("Italian", "-----");
                lang = "it";
                break;
            default:
                lang = "en";
                break;
        }

        Locale locale = new Locale(lang);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

    }


    public void goToSettingsPage(){
        Log.i("GoTOSettingsPage", "yes");
        builder = new AlertDialog.Builder(CCBaseActivity.this);
        builder.setTitle(R.string.select_calendar_msg);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Intent intent = new Intent(CCBaseActivity.this, SettingsActivity.class);
                startActivity(intent);
                dialogInterface.dismiss();
                dialogInterface.cancel();
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                dialogInterface.cancel();
            }
        });
        alertForSettingsConfig = builder.create();
        alertForSettingsConfig.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ccmainactivity_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.setting:
                Intent intent = new Intent(CCBaseActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;

            case R.id.refresh:
                refresh();
                break;

            case R.id.delete_folder:
                deleteFolder();
                break;

            case R.id.create_event:
                createEvent();
                break;

            case R.id.delete_event:
                String[] events;
                Set<String> eventArray = getCalendarDetails.getEvents(CalendarFragment.calendarDay, CCMainActivity.selectedCalendars );
                events = eventArray.toArray(new String[eventArray.size()]);

                if(events.length!=0)
                    deleteEvent(events);
                else
                    Toast.makeText(getApplicationContext(), this.getString(R.string.no_events_delete), Toast.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void deleteFolder(){
        List<String> listOfDirectoryName = new ArrayList<String>();
        final AlertDialog.Builder builder = new AlertDialog.Builder(CCBaseActivity.this);


        listOfDirectoryName.addAll(createDirectories.readAllDirectoryOrFileName(null, null));
        final String[] directories = listOfDirectoryName.toArray(new String[listOfDirectoryName.size()]);

        final boolean[] checkedFolders = null;
        final ArrayList<String> selectedDirectories = new ArrayList<String>();
        builder.setTitle(R.string.select_delete_folders);
        builder.setMultiChoiceItems(directories, checkedFolders, new DialogInterface.OnMultiChoiceClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i, boolean b) {
              if(b){
                  selectedDirectories.add(directories[i]);
              }
              else{
                  if(selectedDirectories.contains(directories[i])){
                      selectedDirectories.remove(directories[i]);
                  }
              }
           }
       });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Log.i("selected Item", selectedDirectories.size()+"");
                if(selectedDirectories.size()>0)
                    deleteFolderConfirm(selectedDirectories);
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void deleteFolderConfirm(final ArrayList<String> selectedDirectories){

        final AlertDialog.Builder builderConfirm = new AlertDialog.Builder(CCBaseActivity.this);

        builderConfirm.setTitle(this.getString(R.string.delete_folder_confirm)+" "+selectedDirectories.size()+" "+this.getString(R.string.folders)+" ?");

        builderConfirm.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                createDirectories.deleteFolders(selectedDirectories);
                Toast.makeText(getApplicationContext(),getString(R.string.deleted_successfully), Toast.LENGTH_SHORT ).show();

            }
        });

        builderConfirm.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialogConfirm = builderConfirm.create();
        dialogConfirm.show();
    }

    public void deleteEvent(final String[] events){
       // List<String> listOfDirectoryName = new ArrayList<String>();
        final AlertDialog.Builder builder = new AlertDialog.Builder(CCBaseActivity.this);
        //listOfDirectoryName.addAll(createDirectories.readAllDirectoryOrFileName(null, null));


        final boolean[] checkedFolders = null;
        final ArrayList<String> selectedEvents = new ArrayList<String>();
        builder.setTitle(R.string.select_delete_folders);
        builder.setMultiChoiceItems(events, checkedFolders, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if(b){
                    selectedEvents.add(events[i]);
                }
                else{
                    if(selectedEvents.contains(events[i])){
                        selectedEvents.remove(events[i]);
                    }
                }
            }
        });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Log.i("selected Item", selectedEvents.size()+"");
                if(selectedEvents.size() > 0)
                    deleteEventConfirm(selectedEvents);
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void deleteEventConfirm(final ArrayList<String> selectedEvents){

        final AlertDialog.Builder builderConfirm = new AlertDialog.Builder(CCBaseActivity.this);

        builderConfirm.setTitle(this.getString(R.string.delete_folder_confirm)+" "+ selectedEvents.size()+" "+this.getString(R.string.event)+" ?");

        builderConfirm.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //
                getCalendarDetails.deleteEvent(selectedEvents);
                Toast.makeText(getApplicationContext(),getString(R.string.deleted_successfully), Toast.LENGTH_SHORT ).show();
            }
        });

        builderConfirm.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialogConfirm = builderConfirm.create();
        dialogConfirm.show();
    }

    public void refresh(){
        Intent intent1 = getIntent();
        finish();
        startActivity(intent1);
    }

}
