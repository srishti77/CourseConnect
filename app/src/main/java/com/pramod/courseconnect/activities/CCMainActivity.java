package com.pramod.courseconnect.activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.pramod.courseconnect.R;
import com.pramod.courseconnect.adapters.MainScreenSlidePagerAdapter;

import com.pramod.courseconnect.db.ReminderDatabase;

import com.pramod.courseconnect.fragments.CalendarFragment;
import com.pramod.courseconnect.fragments.EventsListFragment;
import com.pramod.courseconnect.helpers.ActionReceiver;
import com.pramod.courseconnect.helpers.CalendarDetails;

import butterknife.BindView;
import butterknife.ButterKnife;
import java.util.ArrayList;

import java.util.HashSet;

import java.util.Set;

public class CCMainActivity extends CCBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener{

    @BindView(R.id.pager)
    ViewPager mPager;

    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;

    @BindView(R.id.next)
    ImageView imageView;

    private PagerAdapter mPagerAdapter;
   // private static final int MY_PERMISSIONS = 1;
    CalendarDetails calendarDetails = new CalendarDetails(this);
    public static Set<String> events = new HashSet<String>();
    public static SharedPreferences sharedPreferences;

    static String selectedLanguage;
    static public Set<String> currentEvents = new HashSet<String>();
    static public Set<String> selectedCalendars = new HashSet<String>();

    static boolean languageUpdate = false;
    static boolean calendarUpdate = false;
    static boolean goRight = true;

    String[] permissions = {
            Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CCMainActivity.this);
        selectedLanguage = sharedPreferences.getString("preferredLanguage", null);
        selectedCalendars = sharedPreferences.getStringSet("preferredCalendars", null);
        if(selectedLanguage != null){
            Log.i("Selected Language", selectedLanguage);
            setLocale(selectedLanguage);
        }

        setContentView(R.layout.activity_calendar);

        //Binds all the view elements specified above
        ButterKnife.bind(this);

        // Instantiate a ViewPager and a PagerAdapter.
        mPagerAdapter = new MainScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(this);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("CurrentItem", "Event");
                if(goRight){
                   viewEvents();
                }
                else{
                    goRight = true;
                    mPager.setCurrentItem(0);
                    imageView.setImageResource(R.drawable.ic_next);
                }

            }
        });

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;
            String currentEvent = "";

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    if(selectedCalendars!= null)
                        currentEvents = calendarDetails.getCurrentEvent(new ArrayList(selectedCalendars));

                    if(!currentEvents.isEmpty())
                        currentEvent = currentEvents.iterator().next();
                    collapsingToolbarLayout.setTitle(CCMainActivity.this.getString(R.string.event_list_header_eng)+": "+ currentEvent);
                    collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    //carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });
        //clearDB();
    }

    @Override
    protected void onStart() {
        super.onStart();
        askPermissions(permissions);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("On Resume", "called");
        if(hasPermissions(this, permissions)){
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);

            //selectedCalendars = sharedPreferences.getStringSet("preferredCalendars", null);
            if(selectedCalendars== null || selectedCalendars.size()==0 ){
                Log.i("Go to settings page", "executed");
                goToSettingsPage();
            }

            if(calendarUpdate){
                currentEvents = calendarDetails.getCurrentEvent(new ArrayList(selectedCalendars));
                calendarUpdate = false;
            }

            if(languageUpdate){
                setLocale(selectedLanguage);
                languageUpdate= false;
                recreate();
            }

            if(eventCreated){
                events = calendarDetails.getEvents(CalendarFragment.calendarDay, selectedCalendars);
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
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
        // Handle item selection
        switch (item.getItemId()) {


            case R.id.folder:
                Intent intent2 = new Intent(CCMainActivity.this, ViewMaterialsActivity.class);
                intent2.putExtra("SelectedEvent", selectedCurrentEvent);
                startActivity(intent2);
                break;

            case R.id.reload:
                refresh();
                break;

        }
        return super.onOptionsItemSelected(item);
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

    private void clearDB() {
        ReminderDatabase rb = new ReminderDatabase(this);
        rb.onUpgrade(rb.getWritableDatabase(), 1,2);
        rb.close();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position == 1){

            if(selectedCalendars != null && selectedCalendars.size() > 0){
                Log.i("Selected Day", CalendarFragment.calendarDay+"");
                events = calendarDetails.getEvents(CalendarFragment.calendarDay, selectedCalendars);
                Log.i("Update EventFragment ", events.size()+" size");
                EventsListFragment eventsListFragment = new EventsListFragment();
                eventsListFragment.update(new ArrayList<String>(events));
            }
        }
        if(position == 0){
            goRight = true;
            imageView.setImageResource(R.drawable.ic_next);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if( alertForSettingsConfig != null){
            alertForSettingsConfig.dismiss();
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        if(s.equals("preferredLanguage")){
            Log.i("Language Changed", sharedPreferences.getString(s,"null"));
            languageUpdate = true;
            selectedLanguage = sharedPreferences.getString(s,null);
        }

        if(s.equals("preferredCalendars")){
            Log.i("Calendar Pref", "changed");
            calendarUpdate =true;
            selectedCalendars = sharedPreferences.getStringSet(s, null);
        }
    }
    public void viewEvents(){
        mPager.setCurrentItem(1);
        imageView.setImageResource(R.drawable.ic_previous);
        goRight= false;
    }
}
