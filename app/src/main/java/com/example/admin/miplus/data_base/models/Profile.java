package com.example.admin.miplus.data_base.models;

public class Profile {
    private int stepsTarget;
    private int steps;
    private String sleepTarget;
    private String startSleep;
    private String endSleep;
    private float startRadian;
    private float endRadian;
    private int height;
    private int weight;
    private boolean isStepsNotification;
    private boolean isSleepNotification;
    private boolean isNotifications;
    private boolean isLightTheme;

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

    public void setDefaultInstance() {
        setStepsTarget(8000);
        setEndSleep("6:00");
        setStartSleep("23:00");
        setSleepTarget("7:00");
        setStartRadian(5.763731f);
        setEndRadian(2.096111f);
        setSteps(0);
        setHeight(160);
        setWeight(55);
        setStepsNotification(true);
        setSleepNotification(true);
        setNotifications(true);
        setLightTheme(true);
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean getStepsNotification() {
        return isStepsNotification;
    }

    public void setStepsNotification(boolean stepsNotification) {
        isStepsNotification = stepsNotification;
    }

    public boolean getSleepNotification() {
        return isSleepNotification;
    }

    public void setSleepNotification(boolean sleepNotification) {
        isSleepNotification = sleepNotification;
    }

    public boolean getNotifications() {
        return isNotifications;
    }

    public void setNotifications(boolean notifications) {
        isNotifications = notifications;
    }

    public boolean getLightTheme() {
        return isLightTheme;
    }

    public void setLightTheme(boolean lightTheme) {
        isLightTheme = lightTheme;
    }
}
