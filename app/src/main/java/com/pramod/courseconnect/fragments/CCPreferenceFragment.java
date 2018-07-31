package com.pramod.courseconnect.fragments;


import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.DisplayMetrics;
import android.util.Log;

import com.pramod.courseconnect.R;
import com.pramod.courseconnect.activities.SettingsActivity;
import com.pramod.courseconnect.helpers.CalendarDetails;

import java.nio.file.Files;
import java.util.Locale;


public class CCPreferenceFragment extends PreferenceFragment {



    public CCPreferenceFragment() {
        // Required empty public constructor
    }

    static CalendarDetails calendarDetails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_settings);
        calendarDetails = new CalendarDetails(getActivity());
        final MultiSelectListPreference multiListPreference = (MultiSelectListPreference) findPreference("preferredCalendars");
        setListPreferenceData(multiListPreference);
        multiListPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {


                return false;
            }
        });

        final ListPreference listPreference = (ListPreference) findPreference("preferredLanguage");
        final Preference importCalFile = (Preference) findPreference("importCalendar");
        if(listPreference.getValue() == null){
            Log.i("the value is null", Locale.getDefault().getDisplayLanguage());
            switch(Locale.getDefault().getDisplayLanguage()){
                case "English":
                case "Englisch":
                case  "Inglese":
                    Log.i("English", "-----");
                    listPreference.setValueIndex(0);
                    break;

                case "German":
                case "Tedesco":
                case "Deutsch":
                    Log.i("GErman", "-----");
                    listPreference.setValueIndex(1);
                    break;

                case "Italian":
                case "Italiano":
                case "Italienisch":

                    Log.i("Italian", "-----");
                    listPreference.setValueIndex(2);
                    break;
                default:
                    listPreference.setValueIndex(0);
                    break;
            }
           
        }
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Log.i("Preferred Language", o+"");
                switch(o+""){
                    case "English":
                    case "Englisch":
                    case  "Inglese":
                        Log.i("English", "-----");
                        setLocale("en");
                        break;

                    case "German":
                    case "Tedesco":
                    case "Deutsch":
                        Log.i("GErman", "-----");
                        setLocale("de");
                        break;

                    case "Italian":
                    case "Italiano":
                    case "Italienisch":

                        Log.i("Italian", "-----");
                        setLocale("it");
                        break;
                    default:
                        setLocale("en");
                        break;
                }
                return true;
            }
        });



        importCalFile.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(Intent.createChooser(intent, getActivity().getString(R.string.select_file)), 123);
                return true;
            }
        });

    }
    protected static void setListPreferenceData(MultiSelectListPreference multiSelectListPreference) {

        int numberOfCalendars = calendarDetails.getAllCalendarNames().size();
        CharSequence[] entries = calendarDetails.getAllCalendarNames().values()
                .toArray(new CharSequence[numberOfCalendars]);
        multiSelectListPreference.setEntries(entries);
        multiSelectListPreference.setEntryValues(entries);
    }


    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent intent1 = getActivity().getIntent();
        getActivity().finish();
        startActivity(intent1);

    }


}
