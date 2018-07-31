package com.pramod.courseconnect.activities;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import com.pramod.courseconnect.R;
import com.pramod.courseconnect.adapters.ViewMaterialSlidePagerAdapter;
import com.pramod.courseconnect.fragments.ViewListOfImagesFragment;
import com.pramod.courseconnect.fragments.ViewListOfNotesFragment;
import com.pramod.courseconnect.fragments.ViewListOfRecordingsFragment;
import com.pramod.courseconnect.helpers.CreateDirectories;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewMaterialsActivity extends CCBaseActivity {


    @BindView(R.id.bottom_nav_bar)
    BottomNavigationView bottomNavigationView;

    @BindView(R.id.viewMaterialsViewPager)
    ViewPager viewPager;

    @BindView(R.id.spinner)
    Spinner chooseEvent;

    boolean imageSpinnerChanged;
    boolean notesSpinnerChanged;
    boolean recordingsSpinnerChanged;

    MenuItem prevMenuItem;
    String selectedEvent;
    List<String> listOfDirectoryName = new ArrayList<String>();
    CreateDirectories createDirectories = new CreateDirectories();
    ViewListOfImagesFragment imagesFragment;
    ViewListOfRecordingsFragment recordingsFragment;
    ViewListOfNotesFragment notesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_materials);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
       // loadingPanel = (ProgressBar) findViewById(R.id.loadingPanel);
        Intent intent = getIntent();
        int position = 0;
        if(intent.getIntExtra("viewpager_position", -1) != -1){
            position = intent.getIntExtra("viewpager_position", -1);
        }

        selectedEvent = intent.getStringExtra("SelectedEvent");

        listOfDirectoryName.addAll(createDirectories.readAllDirectoryOrFileName(null, null));
        ArrayAdapter<String> arrayAdapterListOfDirectories = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listOfDirectoryName);
        arrayAdapterListOfDirectories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseEvent.setAdapter(arrayAdapterListOfDirectories);

        if(selectedEvent != null){
            Log.i("All List Activity", selectedEvent);
            if(listOfDirectoryName.contains(selectedEvent))
                chooseEvent.setSelection(arrayAdapterListOfDirectories.getPosition(selectedEvent));
        }

        chooseEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("Spinner", "Changed");
                //loadingPanel.setVisibility(View.VISIBLE);
                imageSpinnerChanged = true;
                notesSpinnerChanged = true;
                recordingsSpinnerChanged = true;
                selectedEvent = chooseEvent.getSelectedItem()+"";

                if(viewPager.getCurrentItem() == 0){
                    imagesFragment.onSpinnerChanged(selectedEvent);
                    imageSpinnerChanged = false;
                }
                else if(viewPager.getCurrentItem() == 1){
                    notesFragment.onSpinnerChanged(selectedEvent);
                    notesSpinnerChanged = false;
                }
                else{
                    recordingsFragment.onSpinnerChanged(selectedEvent);
                    recordingsSpinnerChanged = false;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.images:
                                viewPager.setCurrentItem(0);
                                if(imageSpinnerChanged){
                                    updateList(0);
                                }

                                break;
                            case R.id.notes:
                                viewPager.setCurrentItem(1);
                                if(notesSpinnerChanged){
                                   updateList(1);
                                }

                                break;
                            case R.id.recordings:
                                viewPager.setCurrentItem(2);
                                if(recordingsSpinnerChanged){
                                   updateList(2);
                                }
                                break;
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
                updateList(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setupViewPager(viewPager);
        viewPager.setCurrentItem(position);

    }

    public void updateList(int position){
        if(position == 0){

            imagesFragment.onSpinnerChanged(selectedEvent);
            imageSpinnerChanged= false;

        }
        else if(position == 1){

            notesFragment.onSpinnerChanged(selectedEvent);
            notesSpinnerChanged = false;

        }
        else if(position ==2)
        {
            recordingsFragment.onSpinnerChanged(selectedEvent);
            recordingsSpinnerChanged = false;
        }
    }
    private void setupViewPager(ViewPager viewPager)
    {
        ViewMaterialSlidePagerAdapter adapter = new ViewMaterialSlidePagerAdapter(getSupportFragmentManager());
        imagesFragment=new ViewListOfImagesFragment();
        notesFragment=new ViewListOfNotesFragment();
        recordingsFragment=new ViewListOfRecordingsFragment();
        adapter.addFragment(imagesFragment);
        adapter.addFragment(notesFragment);
        adapter.addFragment(recordingsFragment);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.newNotes).setVisible(false);
        menu.findItem(R.id.save).setVisible(false);
        menu.findItem(R.id.folder).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {


        super.onDestroy();
        Log.i("View Materials", "destroyed");


    }
}
