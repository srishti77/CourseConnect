package com.pramod.courseconnect.activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
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
import com.pramod.courseconnect.activities.CCBaseActivity;
import com.pramod.courseconnect.helpers.ActionReceiver;
import com.pramod.courseconnect.helpers.AudioService;
import com.pramod.courseconnect.helpers.CreateDirectories;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by User on 30/04/2018.
 */

public class RecorderActivityBG extends CCBaseActivity {

    private File mediaStorageDir;
    public static  String recordedFilePath;
    private SharedPreferences mSharedPreferences;
    private CreateDirectories directoryHelper = new CreateDirectories();
    private SharedPreferences.Editor mEditor;
    private static boolean showNamePromptToggle;
    private final String showNamePromptKey = "showNamePrompt";
    public static TextView timerValue;

    ImageView microphoneRecordImage, rotateImage, pauseReplayImage;
    AudioService audioService = new AudioService();
    public static boolean onRecordStart = true;
    public static boolean onPauseStart = true;

   // final static Handler customHandler = new Handler();
    CreateDirectories createDirectories = new CreateDirectories();
    Animation zoom_in, zoom_out;
    String selectedLanguage;
    static String fileName;
    String permissions[] ={
            Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(RecorderActivityBG.this);
        if(CCMainActivity.sharedPreferences != null) {
            selectedLanguage = CCMainActivity.sharedPreferences.getString("preferredLanguage", null);
            if (selectedLanguage != null) {
                setLocale(selectedLanguage);
            }
        }
        setContentView(R.layout.recorder_layout);
        timerValue = (TextView) findViewById(R.id.timerValue);
        microphoneRecordImage = (ImageView) findViewById(R.id.playOrpause);
        pauseReplayImage = (ImageView) findViewById(R.id.pauseOrreplay);

        rotateImage = (ImageView) findViewById(R.id.displayImage);
        zoom_in = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        zoom_out = AnimationUtils.loadAnimation(this, R.anim.zoom_out);
        pauseReplayImage.setVisibility(View.INVISIBLE);

        if(!onRecordStart){
            microphoneRecordImage.setImageResource(R.drawable.ic_stop_black_24dp);
            //customHandler.postDelayed(updateTimerThread, 0);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                pauseReplayImage.setVisibility(View.VISIBLE);

                if(!onPauseStart){
                    pauseReplayImage.setImageResource(R.drawable.ic_mic_black_24dp);
                    AudioService.customHandler.removeCallbacks(AudioService.updateTimerThread);
                    AudioService.setTimer();
                    AudioService.setTimer();
                }
                else{
                    AudioService.startTime = SystemClock.uptimeMillis()- AudioService.updatedTime;
                    AudioService.customHandler.postDelayed(AudioService.updateTimerThread, 0);
                    rotateImage.clearAnimation();
                    rotateImage.setAnimation(zoom_in);
                }
            }
        }
        microphoneRecordImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Current Drawable", microphoneRecordImage.getDrawable()+"");
                if(hasPermissions(RecorderActivityBG.this,permissions)){

                    if(CCMainActivity.selectedCalendars == null || CCMainActivity.selectedCalendars.isEmpty()){
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
                               Intent intent = new Intent(RecorderActivityBG.this, AudioService.class);
                                intent.putExtra("selectedCurrentEvent", selectedCurrentEvent);
                                intent.putExtra("fileLocation", getRecordingSavePath(getDefaultRecordingName()));
                                startService(intent);
                                onRecordStart = false;
                                Log.i("Record is started", "yayyy");
                                //Toast.makeText(getApplicationContext(), "Recording start", Toast.LENGTH_SHORT).show();
                            } else {
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                                    pauseReplayImage.setVisibility(View.INVISIBLE);
                                }
                                onRecordStart = true;

                                stopService(new Intent(getApplicationContext(),
                                        AudioService.class));
                                stopRecording();

                                Log.i("Record is stopped", "yayyy");
                            }
                        } else {
                            selectedCurrentEvent();
                        }
                    }
                }
                else{

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
                    audioService.pauseRecorder();
                    pauseReplayImage.setImageResource(R.drawable.ic_mic_black_24dp);
                    //customHandler.removeCallbacks(updateTimerThread);
                    rotateImage.setAnimation(null);
                    onPauseStart= false;

                }
                else{
                    Log.i("onReplay", "called");
                    pauseReplayImage.setImageResource(R.drawable.ic_pause_black_24dp);
                    audioService.resumeRecorder();
                   // startTime = SystemClock.uptimeMillis()- updatedTime;
                    //customHandler.postDelayed(updateTimerThread, 0);
                    rotateImage.clearAnimation();
                    rotateImage.setAnimation(zoom_in);
                    onPauseStart = true;

                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        askPermissions(permissions);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(hasPermissions(this, permissions)){

            if(CCMainActivity.selectedCalendars == null || CCMainActivity.selectedCalendars.isEmpty()  ){
                goToSettingsPage();
            }
            else{
                selectedCurrentEvent();
            }
            // setRecordingAttributes();
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);

        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.i("Paused", "activity");
        if(alertForSettingsConfig!= null)
            alertForSettingsConfig.dismiss();
        if(!onRecordStart){
            if(!onPauseStart){
                createNotification("replay");
            }
           else{
                createNotification("pause");
            }
        }


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
                Intent intent = new Intent(RecorderActivityBG.this, ViewMaterialsActivity.class);
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

        try{
            setRecordingAttributes();
            initializeSharedPreferences();
            Log.i("Recording", "started");

        }
        catch (Exception e){
            Log.i("Recording", "started");
            e.printStackTrace();
        }
    }

    public void stopRecording(){
        //rotateImage.setAnimation(null);
        mSharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        onRecordStart = true;
       // Log.i("timerValue", timerValue+"");
       // customHandler.removeCallbacks(updateTimerThread);
        if(timerValue != null) {

               Log.i("timerValue..", timerValue+"");
               timerValue.setText("00:00:00");
               microphoneRecordImage.setImageResource(R.drawable.ic_mic_black_24dp);
               pauseReplayImage.setImageResource(R.drawable.ic_pause_black_24dp);
               pauseReplayImage.setVisibility(View.GONE);
               rotateImage.setAnimation(null);
               if(fileName!= null && !fileName.isEmpty())
                    showRecordingNamePrompt(showNamePromptToggle);
        }
            //Toast.makeText(getApplicationContext(), getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show();
            // showRecordingNamePrompt(showNamePromptToggle);

    }


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

    public void createNotification(String pause){
        initChannels(this);
        Intent intentAction = new Intent(this,ActionReceiver.class);
        intentAction.setAction("stop");
        PendingIntent pIntentlogin = PendingIntent.getBroadcast(this, 1, intentAction, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "default");
        notificationBuilder.setSmallIcon(R.drawable.ic_mic_black_24dp);
        notificationBuilder.setContentTitle(this.getString(R.string.voice_record));
        notificationBuilder.setContentText(this.getString(R.string.tap_notification_recorder));
        notificationBuilder.addAction(R.drawable.ic_stop_black_24dp, "Stop", pIntentlogin);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            Intent intentAction1 = new Intent(this,ActionReceiver.class);
            intentAction1.setAction(pause);
            PendingIntent pauseIntent = PendingIntent.getBroadcast(this, 1, intentAction1, PendingIntent.FLAG_UPDATE_CURRENT);

            if(pause.equals("pause")){
                intentAction.putExtra("action2", pause);
                notificationBuilder.addAction(R.drawable.ic_pause_black_24dp, "Pause", pauseIntent);
            }
            else if(pause.equals("replay")){
                intentAction.putExtra("action2", pause);
                notificationBuilder.addAction(R.drawable.ic_mic_black_24dp, "Replay", pauseIntent);
            }
        }

        Intent intent1 = new Intent(RecorderActivityBG.this, RecorderActivityBG.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(RecorderActivityBG.class);
        stackBuilder.addNextIntent(intent1);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

    }

    public void initChannels(Context context) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("default",
                "Channel name",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Channel description");
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

}
