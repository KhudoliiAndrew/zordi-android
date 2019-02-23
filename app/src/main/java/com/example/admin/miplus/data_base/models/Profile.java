package com.example.admin.miplus.data_base.models;

public class Profile {
    private int stepsTarget;
    private String sleepTarget;
    private String startSleep;
    private String endSleep;
    private float startRadian;
    private float endRadian;

    public int getStepsTarget() {
        return stepsTarget;
    }

    public void setStepsTarget(int stepsTarget) {
        this.stepsTarget = stepsTarget;
    }

    public String getSleepTarget() {
        return sleepTarget;
    }

    public void setSleepTarget(String sleepTarget) {
        this.sleepTarget = sleepTarget;
    }

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

    public float getStartRadian() {
        return startRadian;
    }

    public void setStartRadian(float startRadian) {
        this.startRadian = startRadian;
    }

    public float getEndRadian() {
        return endRadian;
    }

    public void setEndRadian(float endRadian) {
        this.endRadian = endRadian;
    }

    public void setDefaultInstance(){
        setStepsTarget(8000);
        setEndSleep("6:00");
        setStartSleep("23:00");
        setSleepTarget("7:00");
        setStartRadian(5.763731f);
        setEndRadian(2.096111f);
    }
}
