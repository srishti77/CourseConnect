package com.pramod.courseconnect.activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pramod.courseconnect.R;
import com.pramod.courseconnect.db.ReminderDatabase;
import com.pramod.courseconnect.helpers.ReminderNotificationCreater;
import com.pramod.courseconnect.helpers.Utils;
import com.pramod.courseconnect.models.Reminder;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Calendar;

public class ReminderEditActivity extends CCBaseActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener{

    private EditText mTitleText, mDetailsText;
    private TextView mDateText, mTimeText, mRepeatText, mRepeatNoText, mRepeatTypeText,mNotifyText;
    private Switch mRepeatSwitch, mNotifySwitch;

    LinearLayout setDate, setTime;
    RelativeLayout mRepeatNoLayout, mRepeatTypeLayout;
    private ImageView mDateIcon, mTimeIcon;

    private String mTitle;
    private String mTime;
    private String mDate;
    private String mRepeatNo;
    private String mRepeatType;
    private String mActive;
    private String mRepeat;
    private String mDetails;
    private String[] mDateSplit;
    private String[] mTimeSplit;

    private int mReceivedID;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private long mRepeatTime;
    private Calendar mCalendar;
    private Reminder mReceivedReminder;
    private ReminderDatabase rb;
    private ReminderNotificationCreater reminderNotificationCreater;
    private ExpandableLayout expandableLayout0;
    private boolean expandableLayoutState;
    private ImageView notifyIcon;

    // Constant Intent String
    public static final String EXTRA_REMINDER_ID = "Reminder_ID";

    // Values for orientation change
    private static final String KEY_TITLE = "title_key";
    private static final String KEY_TIME = "time_key";
    private static final String KEY_DATE = "date_key";
    private static final String KEY_REPEAT = "repeat_key";
    private static final String KEY_REPEAT_NO = "repeat_no_key";
    private static final String KEY_REPEAT_TYPE = "repeat_type_key";
    private static final String KEY_ACTIVE = "active_key";
    private static final String KEY_DETAILS = "details_key";

    // Constant values in milliseconds
    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;
    String selectedLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ReminderEditActivity.this);
        if(CCMainActivity.sharedPreferences != null){
            selectedLanguage = CCMainActivity.sharedPreferences.getString("preferredLanguage", null);
            if(selectedLanguage != null){
                setLocale(selectedLanguage);
            }
        }

        setContentView(R.layout.activity_add_reminder);

        // Initialize Views
        mTitleText = (EditText) findViewById(R.id.reminder_title);
        mDateText = (TextView) findViewById(R.id.set_date);
        mTimeText = (TextView) findViewById(R.id.set_time);
        mRepeatText = (TextView) findViewById(R.id.set_repeat);
        mRepeatNoText = (TextView) findViewById(R.id.set_repeat_no);
        mRepeatTypeText = (TextView) findViewById(R.id.set_repeat_type);
        mRepeatSwitch = (Switch) findViewById(R.id.repeat_switch);
        mNotifyText = (TextView) findViewById(R.id.notify_text);
        mNotifySwitch = (Switch) findViewById(R.id.notify_switch);
        expandableLayout0 = (ExpandableLayout) findViewById(R.id.expandable_layout_0);
        notifyIcon = (ImageView) findViewById(R.id.notify_icon);
        mDetailsText = (EditText) findViewById(R.id.reminder_details);

        mDateIcon = (ImageView) findViewById(R.id.date_icon);
        mTimeIcon = (ImageView) findViewById(R.id.time_icon);
        setDate = (LinearLayout) findViewById(R.id.setDate);
        setTime =(LinearLayout) findViewById(R.id.setTime);
        mRepeatNoLayout = (RelativeLayout) findViewById(R.id.RepeatNo);
        mRepeatTypeLayout = (RelativeLayout) findViewById(R.id.RepeatType);


        expandableLayout0.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                Log.d("ExpandableLayout0", "State: " + state);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        // Setup Toolbar
        
        Utils.darkenStatusBar(this, R.color.black);
        mNotifySwitch.setTextColor(getResources().getColor(R.color.black));
        mNotifySwitch.setHintTextColor(getResources().getColor(R.color.black));
        mNotifySwitch.setHighlightColor(getResources().getColor(R.color.black));

        mNotifySwitch.setOnClickListener(new View.OnClickListener() {
         @Override
            public void onClick(View view) {
             onSwitchNotify(view);
         }
        });

        mRepeatSwitch.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                   onSwitchRepeat(view);
        }
        });

        mRepeatNoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRepeatNo(view);
                  }
       });

        mRepeatTypeLayout.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             selectRepeatType(view);
              }
    });

    setDate.setOnClickListener(new View.OnClickListener() {
       @Override
      public void onClick(View view) {
           setDate(view);
         }
    });

    mDateIcon.setOnClickListener(new View.OnClickListener() {
       @Override
          public void onClick(View view) {
           setDate(view);
       }
       });

    mTimeIcon.setOnClickListener(new View.OnClickListener() {
        @Override
      public void onClick(View view) {

            setTime(view);
                   }
        });

    setTime.setOnClickListener(new View.OnClickListener() {
      @Override
        public void onClick(View view) {
         setTime(view);
   }
});


        // Setup Reminder Title EditText
        mTitleText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle = s.toString().trim();
                mTitleText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Setup Reminder Details EditText
        mDetailsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mDetails = s.toString().trim();
                mDetailsText.setError(null);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Get reminder id from intent
        mReceivedID = Integer.parseInt(getIntent().getStringExtra(EXTRA_REMINDER_ID));

        // Get reminder using reminder id
        rb = new ReminderDatabase(this);
        mReceivedReminder = rb.getReminder(mReceivedID);

        // Get values from reminder
        mTitle = mReceivedReminder.getrTitle();
        mDate = mReceivedReminder.getrDate();
        mTime = mReceivedReminder.getrTime();
        mRepeat = mReceivedReminder.getrRepeat();
        mRepeatNo = mReceivedReminder.getrRepeatNo();
        mRepeatType = mReceivedReminder.getrRepeatType();
        mActive = mReceivedReminder.getrActive();
        mDetails = mReceivedReminder.getrDetails();

        expandableLayoutState = Boolean.parseBoolean(mRepeat);
        notifyIcon.setImageResource(Boolean.parseBoolean(mActive)?R.drawable.ic_notifications_black_24dp:R.drawable.ic_notifications_off_black_24dp);


        // Setup TextViews using reminder values
        mTitleText.setText(mTitle);
        mDetailsText.setText(mDetails);
        mDateText.setText(mDate);
        mTimeText.setText(mTime);
        if (Boolean.parseBoolean(mRepeat)) {
            mRepeatNoText.setText(mRepeatNo);
            mRepeatTypeText.setText(mRepeatType);
            mRepeatText.setText(this.getString(R.string.every)+" " + mRepeatNo +
                    " " + mRepeatType + this.getString(R.string.plural_end));
        }
        else {
            mRepeatText.setText(R.string.repeat_off);
        }


        //Set Expandable Layout state
        if (expandableLayoutState) {
            expandableLayout0.expand();
        } else {
            expandableLayout0.collapse();
        }

        // To save state on device rotation
        if (savedInstanceState != null) {
            String savedTitle = savedInstanceState.getString(KEY_TITLE);
            mTitleText.setText(savedTitle);
            mTitle = savedTitle;

            String savedDetails = savedInstanceState.getString(KEY_DETAILS);
            mDetailsText.setText(savedDetails);
            mDetails = savedDetails;

            String savedTime = savedInstanceState.getString(KEY_TIME);
            mTimeText.setText(savedTime);
            mTime = savedTime;

            String savedDate = savedInstanceState.getString(KEY_DATE);
            mDateText.setText(savedDate);
            mDate = savedDate;

            String saveRepeat = savedInstanceState.getString(KEY_REPEAT);
            mRepeatText.setText(saveRepeat);
            mRepeat = saveRepeat;

            String savedRepeatNo = savedInstanceState.getString(KEY_REPEAT_NO);
            mRepeatNoText.setText(savedRepeatNo);
            mRepeatNo = savedRepeatNo;

            String savedRepeatType = savedInstanceState.getString(KEY_REPEAT_TYPE);
            mRepeatTypeText.setText(savedRepeatType);
            mRepeatType = savedRepeatType;

            mActive = savedInstanceState.getString(KEY_ACTIVE);
        }

        // Setup the notify switch
        if (mActive.equals("false")) {
            mNotifySwitch.setChecked(false);
            mNotifyText.setText(R.string.notify_off);

        } else if (mActive.equals("true")) {
            mNotifySwitch.setChecked(true);
            mNotifyText.setText(R.string.notify_on);
        }

        // Setup repeat switch
        if (mRepeat.equals("false")) {
            mRepeatSwitch.setChecked(false);
            mRepeatText.setText(R.string.notify_off);

        } else if (mRepeat.equals("true")) {
            mRepeatSwitch.setChecked(true);
        }

        // Obtain Date and Time details
        mCalendar = Calendar.getInstance();
        reminderNotificationCreater = new ReminderNotificationCreater();

        mDateSplit = mDate.split("/");
        mTimeSplit = mTime.split(":");

        mDay = Integer.parseInt(mDateSplit[0]);
        mMonth = Integer.parseInt(mDateSplit[1]);
        mYear = Integer.parseInt(mDateSplit[2]);
        mHour = Integer.parseInt(mTimeSplit[0]);
        mMinute = Integer.parseInt(mTimeSplit[1]);
    }

    // To save state on device rotation
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequence(KEY_TITLE, mTitleText.getText());
        outState.putCharSequence(KEY_TIME, mTimeText.getText());
        outState.putCharSequence(KEY_DATE, mDateText.getText());
        outState.putCharSequence(KEY_REPEAT, mRepeatText.getText());
        outState.putCharSequence(KEY_REPEAT_NO, mRepeatNoText.getText());
        outState.putCharSequence(KEY_REPEAT_TYPE, mRepeatTypeText.getText());
        outState.putCharSequence(KEY_ACTIVE, mActive);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    // On clicking Time picker
    public void setTime(View v){
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.setThemeDark(false);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    // On clicking Date picker
    public void setDate(View v){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }


    // Obtain date from date picker
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear ++;
        mDay = dayOfMonth;
        mMonth = monthOfYear;
        mYear = year;
        mDate = dayOfMonth + "/" + monthOfYear + "/" + year;
        mDateText.setText(mDate);
    }



    //On clicking the notify switch
    public void onSwitchNotify(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            mActive = "true";
            mNotifyText.setText(R.string.notify_on);
            notifyIcon.setImageResource(R.drawable.ic_notifications_black_24dp);


        } else {
            mActive = "false";
            mNotifyText.setText(R.string.notify_off);
            notifyIcon.setImageResource(R.drawable.ic_notifications_off_black_24dp);
        }
    }

    // On clicking the repeat switch
    public void onSwitchRepeat(View view) {

        if (expandableLayout0.isExpanded()) {
            expandableLayout0.collapse();
        } else {
            expandableLayout0.expand();
        }
        boolean on = ((Switch) view).isChecked();
        if (on) {
            mRepeat = "true";
            mRepeatText.setText(
                    this.getString(R.string.every)+" "+ mRepeatNo + " " + mRepeatType +
                            " "+this.getString(R.string.plural_end));
            mRepeatNoText.setText(mRepeatNo);
            mRepeatTypeText.setText(mRepeatType);

        } else {
            mRepeat = "false";
            mRepeatText.setText(R.string.repeat_off);

        }
    }


    // On clicking repeat type button
    public void selectRepeatType(View v){
        final String[] items = this.getResources().getStringArray(R.array.time);


        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_type);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                mRepeatType = items[item];
                mRepeatTypeText.setText(mRepeatType);
                mRepeatText.setText(ReminderEditActivity.this.getString(R.string.every)+" " + mRepeatNo + " " +
                        mRepeatType + " "+ReminderEditActivity.this.getString(R.string.plural_end));
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void setRepeatNo(View v){

        final android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_reminder_interval_number_picker, null);
        alert.setTitle(R.string.repeat_no);
        alert.setMessage(R.string.pick_a_number);
        alert.setView(dialogView);
        int finalNumber = 1;
        if (mRepeatNo!="") {
            finalNumber = Integer.parseInt(mRepeatNo);
        }

        final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);

        numberPicker.setMaxValue(10);
        numberPicker.setMinValue(1);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setValue(finalNumber);

        alert.setPositiveButton(R.string.done,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int i) {
                        int pickerValue = numberPicker.getValue();

                        mRepeatNo = Integer.toString(pickerValue);
                        mRepeatNoText.setText(mRepeatNo);
                        mRepeatText.setText(ReminderEditActivity.this.getString(R.string.every)+ mRepeatNo +
                                " " + mRepeatType + " "+ReminderEditActivity.this.getString(R.string.plural_end));
                    }
                });
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing
            }
        });

        android.support.v7.app.AlertDialog alertDialog = alert.create();

        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.blue));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
    }

    // On clicking the update button
    public void updateReminder(){
        // Set new values in the reminder
        mReceivedReminder.setrTitle(mTitle);
        mReceivedReminder.setrDate(mDate);
        mReceivedReminder.setrTime(mTime);
        mReceivedReminder.setrRepeat(mRepeat);
        mReceivedReminder.setrRepeatNo(mRepeatNo);
        mReceivedReminder.setrRepeatType(mRepeatType);
        mReceivedReminder.setrActive(mActive);
        mReceivedReminder.setrDetails(mDetails);

        // Update reminder
        rb.updateReminder(mReceivedReminder);
        rb.close();
        // Set up calender for creating the notification
        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        // Cancel existing notification of the reminder by using its ID
        reminderNotificationCreater.cancelAlarm(getApplicationContext(), mReceivedID);

        // Check repeat type
        if (mRepeatType.equals(this.getString(R.string.minute))) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milMinute;
        } else if (mRepeatType.equals(this.getString(R.string.hour))) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milHour;
        } else if (mRepeatType.equals(this.getString(R.string.day))) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milDay;
        } else if (mRepeatType.equals(this.getString(R.string.week))) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milWeek;
        } else if (mRepeatType.equals(this.getString(R.string.month))) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milMonth;
        }

        // Create a new notification
        if (mActive.equals("true")) {
            if (mRepeat.equals("true")) {
                reminderNotificationCreater.setRepeatAlarm(getApplicationContext(), mCalendar, mReceivedID, mRepeatTime);
            } else if (mRepeat.equals("false")) {
                reminderNotificationCreater.setAlarm(getApplicationContext(), mCalendar, mReceivedID);
            }
        }

        // Create toast to confirm update
        Toast.makeText(getApplicationContext(), this.getString(R.string.edited),
                Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    // On pressing the back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Creating the menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.newNotes).setVisible(false);
        // menu.findItem(R.id.save).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
    }
*/
    // On clicking menu buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // On clicking the back arrow
            // Discard any changes
            case android.R.id.home:
                onBackPressed();
                return true;

            // On clicking save reminder button
            // Update reminder
            case R.id.save:
                mTitleText.setText(mTitle);
                if (mTitleText.getText().toString().length() == 0)

                    mTitleText.setError(Html.fromHtml("<font color='#FFFFFF'>" + getResources().getString(R.string.reminder_not_blank) + "</font>"));
                else {
                    updateReminder();
                }
                return true;

            // On clicking discard reminder button
            // Discard any changes
            case R.id.discard_reminder:
                Toast.makeText(getApplicationContext(), this.getString(R.string.discard_changes),
                        Toast.LENGTH_SHORT).show();

                onBackPressed();
                return true;
            case R.id.folder:
                Intent intent2 = new Intent(ReminderEditActivity.this, ViewMaterialsActivity.class);
                intent2.putExtra("SelectedEvent", selectedCurrentEvent);
                startActivity(intent2);
                return true;
            case R.id.reload:
                selectedCurrentEvent();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        mHour = hourOfDay;
        mMinute = minute;
        if (minute < 10) {
            mTime = hourOfDay + ":" + "0" + minute;
        } else {
            mTime = hourOfDay + ":" + minute;
        }
        mTimeText.setText(mTime);
    }

    @Override
    protected void onStop() {
        super.onStop();
      //  rb.close();
    }
}