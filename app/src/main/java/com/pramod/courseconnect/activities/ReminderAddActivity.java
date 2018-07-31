package com.pramod.courseconnect.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
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
import java.util.Calendar;
import java.util.Locale;

import net.cachapa.expandablelayout.ExpandableLayout;

public class ReminderAddActivity extends CCBaseActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {
    private EditText mTitleText, mDetailsText;
    private TextView mDateText, mTimeText, mRepeatText, mRepeatNoText, mRepeatTypeText, mNotifyText;
    private ImageView mDateIcon, mTimeIcon;
    private Calendar mCalendar;
    private Switch mNotifySwitch, mRepeatSwitch;
    LinearLayout setDate, setTime;
    RelativeLayout mRepeatNoLayout, mRepeatTypeLayout;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private long mRepeatTime;
    private String mTitle;
    private String mTime;
    private String mDate;
    private String mRepeat;
    private String mRepeatNo;
    private String mRepeatType;
    private String mActive;
    private String mDetails;
    private ExpandableLayout expandableLayout0;
    private ImageView notifyIcon;


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
        //sharedPreferences =
        if(CCMainActivity.sharedPreferences != null){
            selectedLanguage = CCMainActivity.sharedPreferences.getString("preferredLanguage", null);
            if(selectedLanguage != null){
                setLocale(selectedLanguage);
            }
        }

        setContentView(R.layout.activity_add_reminder);

        mTitleText = (EditText) findViewById(R.id.reminder_title);
        mDateText = (TextView) findViewById(R.id.set_date);
        mDateIcon = (ImageView) findViewById(R.id.date_icon);
        mTimeIcon = (ImageView) findViewById(R.id.time_icon);
        setDate = (LinearLayout) findViewById(R.id.setDate);
        setTime =(LinearLayout) findViewById(R.id.setTime);

        mTimeText = (TextView) findViewById(R.id.set_time);
        mRepeatText = (TextView) findViewById(R.id.set_repeat);
        mRepeatNoText = (TextView) findViewById(R.id.set_repeat_no);

        mRepeatNoLayout = (RelativeLayout) findViewById(R.id.RepeatNo);
        mRepeatTypeLayout = (RelativeLayout) findViewById(R.id.RepeatType);

        mRepeatTypeText = (TextView) findViewById(R.id.set_repeat_type);
        mNotifyText = (TextView) findViewById(R.id.notify_text);
        mNotifySwitch = (Switch) findViewById(R.id.notify_switch);
        mRepeatSwitch = (Switch) findViewById(R.id.repeat_switch);

        expandableLayout0 = (ExpandableLayout) findViewById(R.id.expandable_layout_0);
        notifyIcon = (ImageView) findViewById(R.id.notify_icon);
        mDetailsText = (EditText) findViewById(R.id.reminder_details);


        expandableLayout0.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction, int state) {
                Log.d("ExpandableLayout0", "State: " + state);
            }
        });

       // getSupportActionBar().setTitle(R.string.title_activity_add_reminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
       // getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.blue)));
        Utils.darkenStatusBar(this, R.color.black);


        // Initialize default values
        mActive = "true";
        mRepeat = "true";
        mRepeatNo = Integer.toString(1);
        mRepeatType = this.getString(R.string.hour);
        mCalendar = Calendar.getInstance();
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mDay = mCalendar.get(Calendar.DATE);

        mDate = mDay + "/" + mMonth + "/" + mYear;
        mTime = mHour + ":" + mMinute;

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


        // Setup TextViews using reminder values
        mDateText.setText(mDate);
        mTimeText.setText(mTime);
        mRepeatNoText.setText(mRepeatNo);
        mRepeatTypeText.setText(mRepeatType);
        mRepeatText.setText(this.getString(R.string.every)+" " + mRepeatNo + " " +
                mRepeatType +this.getString(R.string.plural_end));

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
        outState.putCharSequence(KEY_DETAILS, mDetails);
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
        dpd.show(getFragmentManager(), "Datepickerdialog");
        dpd.setLocale(Locale.GERMAN);
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
            mRepeatText.setText(this.getString(R.string.every)+ " " + mRepeatNo + " " + mRepeatType + " "+
                    this.getString(R.string.plural_end));

        } else {
            mRepeat = "false";
            mRepeatText.setText(R.string.repeat_off);

        }
    }

    // On clicking repeat type button
    public void selectRepeatType(View v){
        final String[] items = this.getResources().getStringArray(R.array.time);

        /*items[0] = "Minute";
        items[1] = "Hour";
        items[2] = "Day";
        items[3] = "Week";
        items[4] = "Month";
*/
        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_type);
        builder.setItems(R.array.time, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                mRepeatType = items[item];
                mRepeatTypeText.setText(mRepeatType);
                mRepeatText.setText(ReminderAddActivity.this.getString(R.string.every)+" " + mRepeatNo + " " +
                        mRepeatType + " "+ReminderAddActivity.this.getString(R.string.plural_end));
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    // On clicking repeat interval button
    public void setRepeatNo(View v){

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
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
                        mRepeatText.setText(ReminderAddActivity.this.getString(R.string.every)+" " + mRepeatNo + " " + mRepeatType +
                                " "+ReminderAddActivity.this.getString(R.string.plural_end));
                    }
                });
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing
            }
        });

        AlertDialog alertDialog = alert.create();

        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.blue));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
    }

    // On clicking the save button
    public void saveReminder(){
        ReminderDatabase rb = new ReminderDatabase(this);

        // Creating Reminder
        int ID = rb.addReminder(new Reminder(mTitle, mDate, mTime, mRepeat, mRepeatNo, mRepeatType, mActive, mDetails));

        // Set up calender for creating the notification
        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        // Check repeat type
        if (mRepeatType.equals(this.getString(R.string.minute)) ) {
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
                new ReminderNotificationCreater().setRepeatAlarm(getApplicationContext(), mCalendar, ID, mRepeatTime);
            } else if (mRepeat.equals("false")) {
                new ReminderNotificationCreater().setAlarm(getApplicationContext(), mCalendar, ID);
            }
        }

        // Create toast to confirm new reminder
        Toast.makeText(getApplicationContext(), this.getString(R.string.saved_successfully),
                Toast.LENGTH_SHORT).show();

        onBackPressed();
        rb.close();
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
                    saveReminder();
                }
                return true;

            // On clicking discard reminder button
            // Discard any changes
            case R.id.discard_reminder:
                Toast.makeText(getApplicationContext(), this.getString(R.string.discard),
                        Toast.LENGTH_SHORT).show();

                onBackPressed();
                return true;

            case R.id.reload:
                selectedCurrentEvent();
                return true;
            case R.id.folder:
                Intent intent2 = new Intent(ReminderAddActivity.this, ViewMaterialsActivity.class);
                intent2.putExtra("SelectedEvent", selectedCurrentEvent);
                startActivity(intent2);
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
}
