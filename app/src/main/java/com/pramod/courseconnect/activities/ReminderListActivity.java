package com.pramod.courseconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.pramod.courseconnect.R;
import com.pramod.courseconnect.adapters.ReminderListAdapter;
import com.pramod.courseconnect.db.ReminderDatabase;
import com.pramod.courseconnect.fragments.ViewListOfNotesFragment;
import com.pramod.courseconnect.helpers.DateTimeSorter;
import com.pramod.courseconnect.helpers.ReminderNotificationCreater;
import com.pramod.courseconnect.models.Reminder;
import com.pramod.courseconnect.models.ReminderItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by User on 27/05/2018.
 */

public class ReminderListActivity extends CCBaseActivity implements AbsListView.MultiChoiceModeListener{

    private ListView mList;
    private ReminderListAdapter mAdapterFirst;
    private TextView mNoReminderView;
    private FloatingActionButton mAddReminderButton;
    private ReminderDatabase rb;

    private ReminderNotificationCreater reminderNotificationCreater;

    private ArrayList<Reminder> mItems = new ArrayList<Reminder>();
    ArrayList<Reminder> selectedList= new ArrayList<Reminder>();
    ActionMode actionMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders_list_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        rb = new ReminderDatabase(getApplicationContext());

        mAddReminderButton = (FloatingActionButton) findViewById(R.id.add_reminder);
        mList = (ListView) findViewById(R.id.reminder_list);
        mNoReminderView = (TextView) findViewById(R.id.no_reminder_text);

        // Create recycler view
       // mList.setLayoutManager(getLayoutManager());
        mItems.clear();
        mItems.addAll(getData());

        if (mItems.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);
        }

        // registerForContextMenu(mList);
        mAdapterFirst = new ReminderListAdapter(ReminderListActivity.this, mItems);
       // mAdapterFirst.setItemCount(getDefaultItemCount());
        mList.setAdapter(mAdapterFirst);

        mAddReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ReminderAddActivity.class);
                startActivity(intent);
            }
        });

        mList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mList.setMultiChoiceModeListener(this);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int mReminderClickID = mItems.get(i).getrID();
                editReminder(mReminderClickID);
            }
        });


        reminderNotificationCreater = new ReminderNotificationCreater();

    }

    public List<Reminder> getData(){
        ArrayList<Reminder> items = new ArrayList<>();

        items.addAll(rb.getAllReminders());
        return items;
    }

    public List<ReminderItem> generateData() {
        ArrayList<ReminderItem> items = new ArrayList<>();

        // Get all reminders from the database
        List<Reminder> reminders = rb.getAllReminders();
        Log.i("NoReminders", reminders.size()+"");
        // Initialize lists
        List<String> Titles = new ArrayList<>();
        List<String> Repeats = new ArrayList<>();
        List<String> RepeatNos = new ArrayList<>();
        List<String> RepeatTypes = new ArrayList<>();
        List<String> Actives = new ArrayList<>();
        List<String> DateAndTime = new ArrayList<>();
        List<Integer> IDList= new ArrayList<>();
        List<DateTimeSorter> DateTimeSortList = new ArrayList<>();

        // Add details of all reminders in their respective lists
        for (Reminder r : reminders) {
            Titles.add(r.getrTitle());
            DateAndTime.add(r.getrDate() + " " + r.getrTime());
            Repeats.add(r.getrRepeat());
            RepeatNos.add(r.getrRepeatNo());
            RepeatTypes.add(r.getrRepeatType());
            Actives.add(r.getrActive());
            IDList.add(r.getrID());
        }

        int key = 0;

        // Add date and time as DateTimeSorter objects
        for(int k = 0; k<Titles.size(); k++){
            DateTimeSortList.add(new DateTimeSorter(key, DateAndTime.get(k)));
            key++;
        }

        // Sort items according to date and time in ascending order
        Collections.sort(DateTimeSortList, new DateTimeComparator());

        int k = 0;

        // Add data to each recycler view item
        items.clear();
        //IDmap.clear();

        for (DateTimeSorter item:DateTimeSortList) {
            int i = item.getIndex();
            items.add(new ReminderItem(IDList.get(i),Titles.get(i), DateAndTime.get(i), Repeats.get(i),
                    RepeatNos.get(i), RepeatTypes.get(i), Actives.get(i)));
            //IDmap.put(k, IDList.get(i));
            k++;
        }
        return items;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
        Menu menu = actionMode.getMenu();
        if(b){
            selectedList.add(mItems.get(i));
        }
        else{
            selectedList.remove(selectedList.indexOf(mItems.get(i)));
        }

        actionMode.setTitle(selectedList.size() + " "
                +ReminderListActivity.this.getString(R.string.selected_list));
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        this.actionMode = actionMode;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_reminders_list, menu);
        if(selectedList!= null && selectedList.size()>0){
            actionMode.setTitle(selectedList.size() + " "+ ReminderListActivity.this.getString(R.string.selected_list));
        }
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
                actionMode.finish();
                for (Reminder list : selectedList) {

                    int id = list.getrID();
                    // Get reminder from reminder database using id
                    Reminder temp = rb.getReminder(id);
                    // Delete reminder
                    rb.deleteReminder(temp);
                    Log.i("After delete", mItems.size()+"");
                   // IDmap.remove(id);
                    mItems.remove(mItems.indexOf(list));
                   /* mItems.clear();
                    mItems.addAll(getData());*/
                    mAdapterFirst.notifyDataSetChanged();
                    reminderNotificationCreater.cancelAlarm(getApplicationContext(), id);

                }
                selectedList.clear();
                break;

        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {

    }

    public class DateTimeComparator implements Comparator {
        DateFormat f = new SimpleDateFormat("dd/mm/yyyy hh:mm");

        public int compare(Object a, Object b) {
            String o1 = ((DateTimeSorter)a).getDateTime();
            String o2 = ((DateTimeSorter)b).getDateTime();

            try {
                return f.parse(o1).compareTo(f.parse(o2));
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mItems.clear();
        mItems.addAll(getData());
        mAdapterFirst.notifyDataSetChanged();
        if(actionMode != null){
            actionMode.finish();
            selectedList.clear();
        }

        if (mItems.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);
        }
    }


    private void editReminder(int mClickID) {
        String mStringClickID = Integer.toString(mClickID);

        // Create intent to edit the reminder
        // Put reminder id as extra
        Intent i = new Intent(this, ReminderEditActivity.class);
        i.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, mStringClickID);
        startActivityForResult(i, 1);
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

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.folder:
                Intent intent2 = new Intent(ReminderListActivity.this, ViewMaterialsActivity.class);
                intent2.putExtra("SelectedEvent", selectedCurrentEvent);
                startActivity(intent2);
                break;

            case R.id.reload:
                refresh();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
