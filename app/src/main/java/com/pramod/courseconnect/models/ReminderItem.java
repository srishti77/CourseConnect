package com.pramod.courseconnect.models;

/**
 * Created by User on 27/05/2018.
 */

public class ReminderItem
{

        public String mTitle;
        public String mDateTime;
        public String mRepeat;
        public String mRepeatNo;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int Id;
    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmDateTime() {
        return mDateTime;
    }

    public void setmDateTime(String mDateTime) {
        this.mDateTime = mDateTime;
    }

    public String getmRepeat() {
        return mRepeat;
    }

    public void setmRepeat(String mRepeat) {
        this.mRepeat = mRepeat;
    }

    public String getmRepeatNo() {
        return mRepeatNo;
    }

    public void setmRepeatNo(String mRepeatNo) {
        this.mRepeatNo = mRepeatNo;
    }

    public String getmRepeatType() {
        return mRepeatType;
    }

    public void setmRepeatType(String mRepeatType) {
        this.mRepeatType = mRepeatType;
    }

    public String getmActive() {
        return mActive;
    }

    public void setmActive(String mActive) {
        this.mActive = mActive;
    }

    public String mRepeatType;
        public String mActive;

        public ReminderItem(int Id, String Title, String DateTime, String Repeat, String RepeatNo, String RepeatType, String Active) {
            this.Id = Id;
            this.mTitle = Title;
            this.mDateTime = DateTime;
            this.mRepeat = Repeat;
            this.mRepeatNo = RepeatNo;
            this.mRepeatType = RepeatType;
            this.mActive = Active;
        }

}
