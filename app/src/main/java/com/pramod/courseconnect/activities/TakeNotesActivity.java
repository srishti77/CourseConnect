package com.pramod.courseconnect.activities;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;
import com.pramod.courseconnect.R;

import com.pramod.courseconnect.helpers.CreateDirectories;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TakeNotesActivity extends CCBaseActivity {

    @BindView(R.id.titleNotes)
    EditText notesTitle;

    @BindView(R.id.notesBody)
    EditText notesBody;

    String notes_title;
    StringBuffer notes_body;

    CreateDirectories createDirectories = new CreateDirectories();
    String selectedLanguage;
    boolean needToSave = false;
    boolean exit = false;

    String permissions[] ={
            Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TakeNotesActivity.this);
        if(CCMainActivity.sharedPreferences!= null) {
            selectedLanguage = CCMainActivity.sharedPreferences.getString("preferredLanguage", null);
            if (selectedLanguage != null) {
                setLocale(selectedLanguage);
            }
        }
        setContentView(R.layout.activity_take_notes);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        notes_title = intent.getStringExtra("NotesName");
        selectedCurrentEvent = intent.getStringExtra("CurrentSelected");

        if(savedInstanceState !=  null){

            selectedCurrentEvent = (String) savedInstanceState.getSerializable("selectedCurrentEvent");
            needToSave = (Boolean) savedInstanceState.getSerializable("needToSave");
        }

        if (notes_title != null) {
            Log.i("Notes returned", notes_title);
            notes_body = createDirectories.readContentOfNotesFile(notes_title, selectedCurrentEvent);
            notesTitle.setText(notes_title.replace(".txt", ""));
            if(notes_body != null)
                notesBody.setText(notes_body);
        }

        notesTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                needToSave = true;
                Log.i("text", "changed");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        notesBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                needToSave = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.take_notes_menu, menu);
        return true;

    }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.folder:
                Intent intent = new Intent(TakeNotesActivity.this, ViewMaterialsActivity.class);
                intent.putExtra("SelectedEvent", selectedCurrentEvent);
                intent.putExtra("viewpager_position", 1);
                startActivity(intent);
                break;
            case R.id.newNotes:
                if(needToSave){
                    if((notesTitle != null && !notesTitle.getText().toString().isEmpty())||(notesBody != null && !notesBody.getText().toString().isEmpty()))
                    {
                        //save notes
                        askNeedToSaveTheNotes();
                    }
                }
                else{
                    //refresh the page
                    refresh();
                }
                break;
            case R.id.save:
                checkIfTheNotesCanBeSaved();
                break;

            case R.id.reload:
                selectedCurrentEvent();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkIfTheNotesCanBeSaved(){
        if(notesTitle == null || notesTitle.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), this.getString(R.string.title_not_empty), Toast.LENGTH_SHORT).show();
        }
        else {
            if (CCMainActivity.selectedCalendars == null || CCMainActivity.selectedCalendars.isEmpty()) {
                goToSettingsPage();
            } else {
                if (selectedCurrentEvent == null || selectedCurrentEvent.isEmpty()) {
                    //askPermissionToGoToCalendar();
                    createEvent();
                } else if (selectedCurrentEvent != null && !selectedCurrentEvent.isEmpty()) {
                    saveNotes();
                }
            }
        }
    }

    public void saveNotes(){
        String directory = selectedCurrentEvent + "/" + "Notes";
        File mediaStorageDir;
        mediaStorageDir = createDirectories.createFolder(directory);
        createDirectories.createNoMediaFile();

        if(mediaStorageDir != null){
            //create the file and write the content
            File notesFile = createDirectories.createFile(mediaStorageDir.getPath(),notesTitle.getText()+".txt");
            MediaScannerConnection.scanFile(TakeNotesActivity.this, new String[]{notesFile.toString()}, null, null);

            writeContentIntoFile(notesFile);
        }
    }

    public  void writeContentIntoFile(File file) {

        try {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                OutputStreamWriter out = new OutputStreamWriter(fileOutputStream);
                out.write(notesBody.getText().toString());
                out.flush();
                out.close();
                needToSave = false;
                if(exit)
                    finish();

            }
            Toast.makeText(this, this.getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, this.getString(R.string.could_not_save) ,Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        askPermissions(permissions);
    }

    @Override
    protected void onResume() {
        super.onResume();


        /*In case the user comes back from the google calendar app
        or when more than one events are present
         */
        if(hasPermissions(this, permissions)){
            if(CCMainActivity.selectedCalendars == null || CCMainActivity.selectedCalendars.isEmpty()){
                goToSettingsPage();
            }

            else if(selectedCurrentEvent== null){
                selectedCurrentEvent();
            }
        }

    }

    public void askNeedToSaveTheNotes(){
        AlertDialog.Builder builder = new AlertDialog.Builder(TakeNotesActivity.this);
        builder.setMessage(R.string.save_notes)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkIfTheNotesCanBeSaved();
                        if(!needToSave){
                            //refresh();
                            refresh();
                        }
                    }
                })
                .setNegativeButton(R.string.not_now, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //refresh the activity
                        refresh();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void refresh(){
        Log.i("Refresh", "called");
        notesTitle.setText("");
        notesBody.setText("");
        Intent intent1 =new Intent(TakeNotesActivity.this,TakeNotesActivity.class);
        finish();
        startActivity(intent1);

    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK)
        {

           if(needToSave){
               exit = false;
               AlertDialog.Builder builder = new AlertDialog.Builder(TakeNotesActivity.this);
               builder.setTitle("Do you want to save the notes?");

               builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                        exit = true;
                       checkIfTheNotesCanBeSaved();

                   }
               });

               builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {

                       dialogInterface.dismiss();
                       dialogInterface.cancel();
                       finish();

                   }
               });
               alertForSettingsConfig = builder.create();
               alertForSettingsConfig.show();


               return true;
           }
            return super.onKeyDown(keyCode, event);
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("selectedCurrentEvent", selectedCurrentEvent);
        outState.putBoolean("needToSave", needToSave);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(alertForSettingsConfig!= null)
            alertForSettingsConfig.dismiss();
    }
}

