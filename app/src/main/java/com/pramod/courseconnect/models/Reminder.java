package com.pramod.courseconnect.models;


public class Reminder {
    private int rID;
    private String rTitle;
    private String rDate;
    private String rTime;
    private String rRepeat;
    private String rRepeatNo;
    private String rRepeatType;
    private String rActive;
    private String rDetails;


    public Reminder(int ID, String Title, String Date, String Time, String Repeat, String RepeatNo, String RepeatType, String Active, String Details){
        rID = ID;
        rTitle = Title;
        rDate = Date;
        rTime = Time;
        rRepeat = Repeat;
        rRepeatNo = RepeatNo;
        rRepeatType = RepeatType;
        rActive = Active;
        rDetails = Details;
    }

    public Reminder(String Title, String Date, String Time, String Repeat, String RepeatNo, String RepeatType, String Active, String Details){
        rTitle = Title;
        rDate = Date;
        rTime = Time;
        rRepeat = Repeat;
        rRepeatNo = RepeatNo;
        rRepeatType = RepeatType;
        rActive = Active;
        rDetails = Details;
    }

    public Reminder() {

    }

    public int getrID() {
        return rID;
    }

    public void setrID(int rID) {
        this.rID = rID;
    }

    public String getrTitle() {
        return rTitle;
    }

    public void setrTitle(String rTitle) {
        this.rTitle = rTitle;
    }

    public String getrDate() {
        return rDate;
    }

    public void setrDate(String rDate) {
        this.rDate = rDate;
    }

    public String getrTime() {
        return rTime;
    }

    public void setrTime(String rTime) {
        this.rTime = rTime;
    }

    public String getrRepeat() {
        return rRepeat;
    }

    public void setrRepeat(String rRepeat) {
        this.rRepeat = rRepeat;
    }

    public String getrRepeatNo() {
        return rRepeatNo;
    }

    public void setrRepeatNo(String rRepeatNo) {
        this.rRepeatNo = rRepeatNo;
    }

    public String getrRepeatType() {
        return rRepeatType;
    }

    public void setrRepeatType(String rRepeatType) {
        this.rRepeatType = rRepeatType;
    }

    public String getrActive() {
        return rActive;
    }

    public void setrActive(String rActive) {
        this.rActive = rActive;
    }

    public String getrDetails() { return rDetails; }

    public void setrDetails(String rDetails) {  this.rDetails = rDetails; }
}
