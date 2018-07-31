package com.pramod.courseconnect.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;

import com.pramod.courseconnect.R;

public class SplashActivity extends Activity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 300;
    private boolean firstRun = true;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.layout_splashscreen);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                SharedPreferences sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
               // sharedPreferences.edit().clear().commit();
                /* Create an Intent that will start the Menu-Activity. */
                firstRun = sharedPreferences.getBoolean("firstRun", true);
                Intent i;
                if (firstRun) {
                    Log.i("Inside", "");
                    firstRun =false;
                  sharedPreferences.edit().putBoolean("firstRun", firstRun).commit();
                   i = new Intent(SplashActivity.this, IntroActivity.class);

                } else {
                    i = new Intent(SplashActivity.this, CCMainActivity.class);
                }
                //Intent mainIntent = new Intent(SplashActivity.this,CCMainActivity.class);
                startActivity(i);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}