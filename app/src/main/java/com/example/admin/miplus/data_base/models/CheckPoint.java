package com.example.admin.miplus.data_base.models;

import java.util.Date;

public class CheckPoint {

    private boolean isGone;
    private Date date;
    private int numOfDoc;

    public void setNum(int num) {
        numOfDoc = num;
    }

    public int getNum(){
        return numOfDoc;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setGone(boolean gone) {
        isGone = gone;
    }

    public boolean getGone() {
        return isGone;
    }

    public void setDefaultInstance(){
        isGone = false;
        date = new Date();
    }

}
