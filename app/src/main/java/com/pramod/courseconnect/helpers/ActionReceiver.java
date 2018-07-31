package com.pramod.courseconnect.helpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.pramod.courseconnect.R;
import com.pramod.courseconnect.activities.RecorderActivityBG;

/**
 * Created by User on 24/05/2018.
 */

public class ActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        String action = intent.getAction();
        if(action.equals("stop")){
            Log.i("Do something","stop");
            context.stopService(new Intent(context,
                    AudioService.class));
            RecorderActivityBG.onRecordStart = true;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
        }
        else if(action.equals("pause")){
            Log.i("Pause", "paused");
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                AudioService.pauseRecorder();
                RecorderActivityBG.onPauseStart = false;
                createNotification("replay", context);
            }


        }
        else if(action.equals("replay")){
            Log.i("replay", "replay");
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                AudioService.resumeRecorder();
                RecorderActivityBG.onPauseStart = true;
               // RecorderActivityBG.onPauseStart = false;
                createNotification("pause", context);
            }


        }

    }


    public void createNotification(String pause, Context context){
        initChannels(context);

        Intent intentAction = new Intent(context,ActionReceiver.class);
        intentAction.setAction("stop");
        PendingIntent pIntentlogin = PendingIntent.getBroadcast(context, 1, intentAction, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "default");
        notificationBuilder.setSmallIcon(R.drawable.ic_mic_black_24dp);
        notificationBuilder.setContentTitle(context.getString(R.string.voice_record));
        notificationBuilder.setContentText(context.getString(R.string.tap_notification_recorder));
        notificationBuilder.addAction(R.drawable.ic_stop_black_24dp, "Stop", pIntentlogin);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            Intent intentAction1 = new Intent(context,ActionReceiver.class);
            intentAction1.setAction(pause);
            PendingIntent pauseIntent = PendingIntent.getBroadcast(context, 1, intentAction1, PendingIntent.FLAG_UPDATE_CURRENT);

            if(pause.equals("pause")){
                intentAction.putExtra("action2", pause);
                notificationBuilder.addAction(R.drawable.ic_pause_black_24dp, "Pause", pauseIntent);
            }
            else if(pause.equals("replay")){
                intentAction.putExtra("action2", pause);
                notificationBuilder.addAction(R.drawable.ic_mic_black_24dp, "Replay", pauseIntent);
            }
        }

        Intent intent1 = new Intent(context, RecorderActivityBG.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(RecorderActivityBG.class);
        stackBuilder.addNextIntent(intent1);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
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

}
