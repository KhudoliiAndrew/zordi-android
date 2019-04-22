package com.example.admin.miplus.data_base.models;

import java.util.Date;

public class SleepData {
    private String startSleep;
    private String endSleep;
    private Date date;

    public String getStartSleep() {
        return startSleep;
    }

    public void setStartSleep(String startSleep) {
        this.startSleep = startSleep;
    }

    public String getEndSleep() {
        return endSleep;
    }

    public void setEndSleep(String endSleep) {
        this.endSleep = endSleep;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
