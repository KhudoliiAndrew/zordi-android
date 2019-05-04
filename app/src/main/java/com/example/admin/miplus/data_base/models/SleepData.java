package com.example.admin.miplus.data_base.models;

import java.util.Date;

public class SleepData {
    private Date startSleep;
    private Date endSleep;
    private Date date;

    public Date getStartSleep() {
        return startSleep;
    }

    public void setStartSleep(Date startSleep) {
        this.startSleep = startSleep;
    }

    public Date getEndSleep() {
        return endSleep;
    }

    public void setEndSleep(Date endSleep) {
        this.endSleep = endSleep;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
