package com.pramod.courseconnect.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pramod.courseconnect.R;
import com.pramod.courseconnect.helpers.CreateDirectories;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
/**
 * Created by User on 30/04/2018.
 */

public class RecorderActivity extends CCBaseActivity{

    private File mediaStorageDir;
    private String recordedFilePath;
    private SharedPreferences mSharedPreferences;
    private CreateDirectories directoryHelper = new CreateDirectories();
    private SharedPreferences.Editor mEditor;
    private static boolean showNamePromptToggle;
    private final String showNamePromptKey = "showNamePrompt";
    TextView timerValue;
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    ImageView microphoneRecordImage, rotateImage, pauseReplayImage;

    static MediaRecorder mediaRecorder;
    boolean onRecordStart = true;
    boolean onPauseStart = true;

    final Handler customHandler = new Handler();
    CreateDirectories createDirectories = new CreateDirectories();
    String fileName;
    Animation zoom_in, zoom_out;
    String selectedLanguage;
    boolean isRecording = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(RecorderActivity.this);
        selectedLanguage = CCMainActivity.sharedPreferences.getString("preferredLanguage", null);
        if(selectedLanguage != null){
            setLocale(selectedLanguage);
        }
        setContentView(R.layout.recorder_layout);
        timerValue = (TextView) findViewById(R.id.timerValue);
        microphoneRecordImage = (ImageView) findViewById(R.id.playOrpause);
        pauseReplayImage = (ImageView) findViewById(R.id.pauseOrreplay);

        rotateImage = (ImageView) findViewById(R.id.displayImage);
        zoom_in = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        zoom_out = AnimationUtils.loadAnimation(this, R.anim.zoom_out);
        pauseReplayImage.setVisibility(View.INVISIBLE);

        microphoneRecordImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Current Drawable", microphoneRecordImage.getDrawable()+"");

                 if(CCMainActivity.selectedCalendars == null || CCMainActivity.selectedCalendars.isEmpty()  ){
                    goToSettingsPage();
                }
                else {
                    if (selectedCurrentEvent != null && !selectedCurrentEvent.isEmpty()) {
                        if (onRecordStart) {
                            rotateImage.setAnimation(zoom_in);
                            microphoneRecordImage.setImageResource(R.drawable.ic_stop_black_24dp);
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                                pauseReplayImage.setVisibility(View.VISIBLE);
                            }

                            startRecording(selectedCurrentEvent);
                            Log.i("Record is started", "yayyy");
                            //Toast.makeText(getApplicationContext(), "Recording start", Toast.LENGTH_SHORT).show();
                        } else {
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                                pauseReplayImage.setVisibility(View.INVISIBLE);
                            }

                            stopRecording();
                            Log.i("Record is stopped", "yayyy");
                        }
                    } else {
                        selectedCurrentEvent();
                    }
                }

                }
        });

        zoom_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rotateImage.startAnimation(zoom_out);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        zoom_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rotateImage.startAnimation(zoom_in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        pauseReplayImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                if(onPauseStart){
                    Log.i("OnPause", "called");
                    pauseReplayImage.setImageResource(R.drawable.ic_mic_black_24dp);
                    mediaRecorder.pause();
                    customHandler.removeCallbacks(updateTimerThread);
                    rotateImage.setAnimation(null);
                    onPauseStart= false;
                }
                else{
                    Log.i("onReplay", "called");
                    pauseReplayImage.setImageResource(R.drawable.ic_pause_black_24dp);
                    mediaRecorder.resume();
                    startTime = SystemClock.uptimeMillis()- updatedTime;
                    customHandler.postDelayed(updateTimerThread, 0);
                    rotateImage.clearAnimation();
                    rotateImage.setAnimation(zoom_in);
                    onPauseStart = true;
                }
            }
        });

    }

 @Override
    protected void onResume() {
        super.onResume();

        String permissions[] ={
             Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR,
             Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
             Manifest.permission.RECORD_AUDIO
     };
        askPermissions(permissions);
        Log.i("ON Resume", "called");
        if(CCMainActivity.selectedCalendars == null || CCMainActivity.selectedCalendars.isEmpty()  ){
            goToSettingsPage();
        }
        else{
            selectedCurrentEvent();
        }
       // setRecordingAttributes();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(alertForSettingsConfig!= null)
            alertForSettingsConfig.dismiss();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.newNotes).setVisible(false);
        menu.findItem(R.id.save).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.folder:
                Intent intent = new Intent(RecorderActivity.this, ViewMaterialsActivity.class);
                intent.putExtra("SelectedEvent", selectedCurrentEvent);
                intent.putExtra("viewpager_position", 2);
                startActivity(intent);
                break;
            case R.id.reload:
                selectedCurrentEvent();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startRecording(String event){
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        try{
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
            onRecordStart = false;
            setRecordingAttributes();
            initializeSharedPreferences();
            mediaRecorder.setOutputFile(getRecordingSavePath(getDefaultRecordingName()));

            try {
                mediaRecorder.prepare();
            }
            catch(IOException e){
                // Toast.makeText(getApplicationContext(),"Something went wrong", Toast.LENGTH_SHORT).show();
                Log.e("Something went wrong", "");
            }
            mediaRecorder.start();
            isRecording = true;
            Log.i("Recording", "started");
        }
        catch (Exception e){
            Log.i("Recording", "started");
            e.printStackTrace();
        }
    }

    public void stopRecording(){
        rotateImage.setAnimation(null);
        onRecordStart = true;
        timerValue.setText("00:00:00");
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
        timeSwapBuff = 0L;
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        isRecording = false;
        microphoneRecordImage.setImageResource(R.drawable.ic_mic_black_24dp);
        pauseReplayImage.setImageResource(R.drawable.ic_pause_black_24dp);
        pauseReplayImage.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show();
        showRecordingNamePrompt(showNamePromptToggle);
    }

    Runnable updateTimerThread = new Runnable(){

        public void run(){
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime/1000);
            int mins = secs/60;
            secs = secs % 60;
            int hours = mins/60;

            timerValue.setText(""+String.format("%02d", hours)+":"
                    + String.format("%02d", mins)
                    +":"
                    +String.format("%02d", secs)
            );
            customHandler.postDelayed(this, 0);
        }
    };

  private void initializeSharedPreferences() {

            showNamePromptToggle = mSharedPreferences.getBoolean(showNamePromptKey,true);

    }
    private void setRecordingAttributes() {

        mSharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        if (selectedCurrentEvent != null && !selectedCurrentEvent.equals("")) {
            Log.i("Get recording path", selectedCurrentEvent);
            String directory = selectedCurrentEvent + "/" + "Recordings";
            mediaStorageDir = directoryHelper.createFolder(directory);

        }
        else{

           createEvent();
        }
    }


    public String getDefaultRecordingName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        Date now = new Date();
        return formatter.format(now)+ ".wav";
    }

    public String getRecordingSavePath(String recordingName) {
        fileName= recordingName;
        recordedFilePath = mediaStorageDir.getPath() + "/" + recordingName;
        return mediaStorageDir.getPath() + "/" + recordingName;
    }




    private void showRecordingNamePrompt(final boolean showNamePromptToggle) {

        if (showNamePromptToggle) {
            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.prompt_recording_name, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                   this);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.editTextDialogUserInput);

            final CheckBox showToggle = (CheckBox) promptsView.findViewById(R.id.show_toggle);
            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    // get user input and set it to result
                                    // edit text
                                    //setRecordingName(userInput.getText().toString());
                                    if(!(userInput.getText()+"").isEmpty()){
                                        createDirectories.isFileRenamed(fileName,(userInput.getText()+""),"Recordings",selectedCurrentEvent);
                                    }
                                   mEditor.putBoolean(showNamePromptKey,!showToggle.isChecked());
                                    mEditor.apply();
                                }
                            })
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    mEditor.putBoolean(showNamePromptKey,!showToggle.isChecked());
                                    mEditor.apply();
                                    dialog.cancel();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));



        }

    }


  @Override
    protected void onStop() {
        super.onStop();
        //stopRecording();
        if(mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        Log.i("Activity", "onStop called");

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {

            if(isRecording){
               // exit = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(RecorderActivity.this);
                builder.setTitle("Do you want to save the recordings?");

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       stopRecording();

                    }
                });

                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        mediaRecorder.stop();
                        mediaRecorder.release();
                        mediaRecorder = null;
                        createDirectories.deleteFiles(selectedCurrentEvent+"/Recordings", fileName);
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



}
