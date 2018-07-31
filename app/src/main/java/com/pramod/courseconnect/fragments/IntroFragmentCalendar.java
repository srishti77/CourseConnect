package com.pramod.courseconnect.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pramod.courseconnect.R;
import com.pramod.courseconnect.activities.CCBaseActivity;
import com.pramod.courseconnect.activities.CCMainActivity;
import com.pramod.courseconnect.activities.IntroActivity;

import agency.tango.materialintroscreen.SlideFragment;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class IntroFragmentCalendar extends SlideFragment {

    Context context = getActivity();
    private static final int MY_PERMISSIONS = 1;
    CCBaseActivity ccBaseActivity = new CCBaseActivity();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = (View) inflater
                .inflate(R.layout.fragment_intro_calendar, container, false);

        Button button = (Button) view.findViewById(R.id.grantCalendarPermission);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if ((ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CALENDAR) != PERMISSION_GRANTED) ||
                        (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_CALENDAR) != PERMISSION_GRANTED))

                {
                    Log.i("Permission",  Manifest.permission.READ_CALENDAR+"");
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{
                                    Manifest.permission.READ_CALENDAR,
                                    Manifest.permission.WRITE_CALENDAR
                            }, MY_PERMISSIONS);


                }

               // String[] permissions = {Manifest.permission.READ_CALENDAR, Manifest.permission.READ_CALENDAR};
                //ccBaseActivity.askPermissions(permissions);

            }
        });

       /* Button importButton = (Button) view.findViewById(R.id.importICS);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

                intent.setType("file/*");
                startActivityForResult(Intent.createChooser(intent, getActivity().getString(R.string.select_file)), 123);
            }
        });

        Button skipButton = (Button) view.findViewById(R.id.button_skip);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(getActivity(),CCMainActivity.class);
                startActivity(mainIntent);
            }
        });
       */
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public int backgroundColor() {
        return R.color.yellow;
    }

    @Override
    public int buttonsColor() {
        return R.color.colorPrimaryDark;
    }

    @Override
    public boolean canMoveFurther() {
        return true;
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return "Error";
    }

  @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch(requestCode){
            case MY_PERMISSIONS:
                if (grantResults.length > 0
                        && grantResults[0] == PERMISSION_GRANTED) {

                    Log.i("Permission Granted", "Permission Granted");
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CALENDAR)) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Request Permission Grant")
                                .setMessage("You need to grant all Permission. Press the \"Refresh\" button").show();
                    } else {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Permission Denied")
                                .setMessage("You need to grant Permission that are Denied").show();
                    }
                }
                break;
            default:
                break;
        }

    }


}
