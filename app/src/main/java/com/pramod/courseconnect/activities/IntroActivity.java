package com.pramod.courseconnect.activities;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.FloatRange;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.pramod.courseconnect.R;
import com.pramod.courseconnect.fragments.IntroFragmentCalendar;
import com.pramod.courseconnect.fragments.IntroFragmentCamera;
import com.pramod.courseconnect.fragments.IntroFragmentNotes;
import com.pramod.courseconnect.fragments.IntroFragmentRecorder;
import com.pramod.courseconnect.fragments.IntroFragmentReminder;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class IntroActivity extends MaterialIntroActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_intro);
        enableLastSlideAlphaExitTransition(true);
        Log.i("Inside", "Intro");
        getBackButtonTranslationWrapper()
                .setEnterTranslation(new IViewTranslation() {
                    @Override
                    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                        view.setAlpha(percentage);
                    }
                });
        addSlide(new IntroFragmentCamera()
        );
        addSlide(new IntroFragmentRecorder());
        addSlide(new IntroFragmentNotes());
        addSlide(new IntroFragmentReminder());
        addSlide(new IntroFragmentCalendar());



    }
    @Override
    public void onFinish() {
        super.onFinish();
        Intent mainIntent = new Intent(IntroActivity.this,CCMainActivity.class);
        startActivity(mainIntent);
    }

}
