package com.pramod.courseconnect.helpers;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Chronometer;
import android.widget.Toast;

import com.pramod.courseconnect.R;
import com.pramod.courseconnect.activities.RecorderActivityBG;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AudioService extends Service {



    static public MediaRecorder mediaRecorder = new MediaRecorder();
    static String selectedCurrentEvent;
    String fileLocation;
    static public long startTime = 0L;
    static long timeInMilliseconds = 0L;
    static long timeSwapBuff = 0L;
    public static long updatedTime = 0L;
    final public static Handler customHandler = new Handler();
    Chronometer chronometer;
    Timer timer;

    public AudioService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags,
                              int startId) {
        super.onStartCommand(intent, flags, startId);
        if(intent!= null){
            //selectedCurrentEvent = intent.getStringExtra("selectedCurrentEvent");
            fileLocation = intent.getStringExtra("fileLocation");
            startRecording("event");

        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecording();
    }

    public void startRecording(String event){
        Log.i("Start Recording", "called");
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        try{
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            mediaRecorder.setOutputFile(fileLocation);
            try {
                mediaRecorder.prepare();
            }
            catch(IOException e){
                // Toast.makeText(getApplicationContext(),"Something went wrong", Toast.LENGTH_SHORT).show();
                Log.e("Something went wrong", "");
            }

            mediaRecorder.start();
            startTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
            //isRecording = true;
            Log.i("Recording", "started");
        }
        catch (Exception e){
            Log.i("Recording", "started");
            e.printStackTrace();
        }
    }

    public void stopRecording(){
      Log.i("Recorder", "stopped");
      if(mediaRecorder!= null){
          mediaRecorder.stop();
          mediaRecorder.release();
          mediaRecorder = null;
      }
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        Toast.makeText(getApplicationContext(), getString(R.string.saved_successfully), Toast.LENGTH_SHORT).show();

        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
        startTime = 0L;
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;
        //showRecordingNamePrompt(showNamePromptToggle);
        stopSelf();

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void pauseRecorder(){
        Log.i("Recorder", "paused");
        mediaRecorder.pause();
        customHandler.removeCallbacks(updateTimerThread);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void resumeRecorder(){
        Log.i("Recorder", "resumed");
        mediaRecorder.resume();
        startTime = SystemClock.uptimeMillis()- updatedTime;
        customHandler.postDelayed(updateTimerThread, 0);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    static public Runnable updateTimerThread = new Runnable(){

        public void run(){
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            setTimer();
            customHandler.postDelayed(this, 0);
        }
    };

    public static void setTimer(){
        int secs = (int) (updatedTime/1000);
        int mins = secs/60;
        secs = secs % 60;
        int hours = mins/60;

        RecorderActivityBG.timerValue.setText(""+String.format("%02d", hours)+":"
                + String.format("%02d", mins)
                +":"
                +String.format("%02d", secs)
        );
    }


}
