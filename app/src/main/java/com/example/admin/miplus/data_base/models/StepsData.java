package com.example.admin.miplus.data_base.models;

import java.util.Date;

public class StepsData {
    private int steps;
    private Date date;

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDefaultInstance(){
        setSteps(1);
        setDate(new Date());
    }
}
