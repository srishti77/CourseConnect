package com.pramod.courseconnect.fragments;

import android.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.widget.CardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.pramod.courseconnect.R;
import com.pramod.courseconnect.activities.BuildCameraActivity;
import com.pramod.courseconnect.activities.CCMainActivity;
import com.pramod.courseconnect.activities.RecorderActivityBG;
import com.pramod.courseconnect.activities.ReminderListActivity;
import com.pramod.courseconnect.activities.TakeNotesActivity;
import com.pramod.courseconnect.helpers.CreateDirectories;

import java.io.File;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardFragment extends Fragment {
    @BindView(R.id.voiceRecordCardId)
    CardView recorderCardView;

    @BindView(R.id.cameraCardId)
    CardView cameraCardView;

    @BindView(R.id.remindersCardId)
    CardView remindersCardView;

    @BindView(R.id.notesCardId)
    CardView notesCardView;

    private ViewGroup rootView;
    private String selectedCurrentEvent;
    private CreateDirectories directoryHelper;
    private CCMainActivity mainActivity = (CCMainActivity) getActivity();
    private File mediaStorageDir;
    private String recordedFilePath;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private static boolean showNamePromptToggle;
    private final String showNamePromptKey = "showNamePrompt";

    Bundle bundle;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_dashboard, container, false);

        ButterKnife.bind(this, rootView);
        directoryHelper = new CreateDirectories();

        //"Event";
        addCardViewListeners();
        return rootView;
    }

    private void initializeSharedPreferences() {
        if (!mSharedPreferences.contains(showNamePromptKey)) {
            mEditor.putBoolean(showNamePromptKey,true);
            mEditor.apply();
        }
        else {
            showNamePromptToggle = mSharedPreferences.getBoolean(showNamePromptKey,true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mainActivity = (CCMainActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " Not MainActivity class instance");
        }
    }
    private void addCardViewListeners() {

        //On click listener for the record card
        recorderCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToRecorder = new Intent(getActivity(), RecorderActivityBG.class);
                startActivity(goToRecorder);
            }
        });

        //On click listener for the camera card
        cameraCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToCamera = new Intent(getActivity(), BuildCameraActivity.class);
                startActivity(goToCamera);

            }
        });

        //On click listener for the reminders card
        remindersCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent remindersList = new Intent(getActivity(), ReminderListActivity.class);
                startActivity(remindersList);

            }
        });

        //On click listener for the notes card
        notesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TakeNotesActivity.class);
                startActivity(intent);

            }
        });
    }

}
