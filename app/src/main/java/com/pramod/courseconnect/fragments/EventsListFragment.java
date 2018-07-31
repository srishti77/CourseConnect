package com.pramod.courseconnect.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import android.widget.ListView;

import com.pramod.courseconnect.R;
import com.pramod.courseconnect.activities.CCBaseActivity;
import com.pramod.courseconnect.activities.CCMainActivity;
import com.pramod.courseconnect.activities.ViewMaterialsActivity;
import com.pramod.courseconnect.adapters.EventsAdapter;
import com.pramod.courseconnect.helpers.CalendarDetails;
import com.pramod.courseconnect.helpers.CreateDirectories;

import android.text.format.Time;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


import butterknife.BindView;
import butterknife.ButterKnife;



public class EventsListFragment extends Fragment
        implements IUIFragment {

   // @BindView(R.id.eventsListView)
    ListView eventsList;
    public static ArrayList<String> todayEvents = new ArrayList<String>();
    CreateDirectories createDirectories = new CreateDirectories();
    ArrayList<String> selectedEvents = new ArrayList<String>();
    //static because if I swipe for the second time, it was null. It was throwing Null PointerExcep.
    public static EventsAdapter adapter;
    public static  View rootView;
    public static  TextView header;
    static Activity context;
    ActionMode actionModeDiscard;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (View) inflater.inflate(
                R.layout.fragment_events_list, container, false);
        //ButterKnife.bind(this, rootView);
        eventsList = (ListView) rootView.findViewById(R.id.eventsListView);
        header = (TextView) rootView.findViewById(R.id.header);

        Log.i("check CalendarDay--",CalendarFragment.calendarDay+"" );
       // todayEvents.add("No Events Available");
        todayEvents.addAll(CCMainActivity.events);

        Log.i("List of events", todayEvents.size()+"");
        context = getActivity();
        adapter = new EventsAdapter(getActivity(), todayEvents);
        eventsList.setAdapter(adapter);
        checkIfNoEventOccurs();
        eventsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        eventsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!createDirectories.readAllDirectoryOrFileName(todayEvents.get(i), "Images").isEmpty()
                        ||!createDirectories.readAllDirectoryOrFileName(todayEvents.get(i), "Notes").isEmpty()
                        || !createDirectories.readAllDirectoryOrFileName(todayEvents.get(i), "Recordings").isEmpty()) {
                    Log.i("Item selected", todayEvents.get(i));
                    Intent intent = new Intent(getActivity(), ViewMaterialsActivity.class);
                    intent.putExtra("SelectedEvent", todayEvents.get(i));
                    startActivity(intent);
                    }
                else {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.no_materials_available), Toast.LENGTH_SHORT).show();
                }
            }
        });

        eventsList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {

                MenuInflater inflater = context.getMenuInflater();
                inflater.inflate(R.menu.menu_reminders_list, menu);
                if(selectedEvents!= null && selectedEvents.size()>0){
                    actionMode.setTitle(selectedEvents.size() + " "+ context.getString(R.string.event));
                }
                actionModeDiscard = actionMode;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.discard_reminder:

                        for(String event: selectedEvents){
                            todayEvents.remove(event);
                        }
                        adapter.notifyDataSetChanged();
                        CalendarDetails calendarDetails = new CalendarDetails(context);
                        calendarDetails.deleteEvent(selectedEvents);
                        selectedEvents.clear();
                        actionMode.finish();
                        if(todayEvents.isEmpty()){
                            header.setText(context.getString(R.string.no_event));
                        }
                        break;
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                selectedEvents.clear();
            }

            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

                Menu menu = actionMode.getMenu();
                if(b){
                    selectedEvents.add(todayEvents.get(i));
                }
                else{
                    selectedEvents.remove(todayEvents.get(i));
                }


                actionMode.setTitle(selectedEvents.size() + " "
                        +context.getString(R.string.event));
            }

        });
        return rootView;
    }

  
    public void checkIfNoEventOccurs(){
       if(todayEvents.isEmpty())
       {
           if(header != null)
           header.setText(context.getString(R.string.no_event));
       }

        else{
           if(header != null)
           header.setText(context.getString(R.string.todays_event)+" "+
                   new SimpleDateFormat("dd/MM/yyyy").format(CalendarFragment.calendarDay.getDate()));
       }

    }

    public void update(ArrayList<String> events){
           todayEvents.clear();
           todayEvents.addAll(events);
           Log.i("event List----", todayEvents.size()+"");
            adapter.notifyDataSetChanged();
            checkIfNoEventOccurs();

    }

    @Override
    public void onPause() {
        super.onPause();
        if(actionModeDiscard != null){
            selectedEvents.clear();
            actionModeDiscard.finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
