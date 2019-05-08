package com.example.admin.miplus.data_base.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Profile {
    private int stepsTarget;
    private Date sleepTarget;
    private Date startSleep;
    private Date endSleep;
    private float startRadian;
    private float endRadian;
    private int height;
    private int weight;
    private boolean isStepsNotification;
    private boolean isSleepNotification;
    private boolean isNotifications;
    private boolean isLightTheme;
    private boolean isShowGeoposition;
    private int waterCount;
    private Date date;
    private boolean isSpeak;

    public int getStepsTarget() {
        return stepsTarget;
    }

    public void setStepsTarget(int stepsTarget) {
        this.stepsTarget = stepsTarget;
    }

    public Date getSleepTarget() {
        return sleepTarget;
    }

    public void setSleepTarget(Date sleepTarget) {
        this.sleepTarget = sleepTarget;
    }

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
        try {
            setEndSleep( new SimpleDateFormat("HH:mm").parse("06:00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            setStartSleep(new SimpleDateFormat("HH:mm").parse("23:00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            setSleepTarget(new SimpleDateFormat("HH:mm").parse("07:00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setStartRadian(5.763731f);
        setEndRadian(2.096111f);
        setHeight(160);
        setWeight(55);
        setStepsNotification(true);
        setSleepNotification(true);
        setNotifications(true);
        setLightTheme(true);
        setShowGeoposition(true);
        setWaterCount(0);
        setDate(new Date());
        setSpeak(true);
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

    public boolean getShowGeoposition() {
        return isShowGeoposition;
    }

    public void setShowGeoposition(boolean showGeoposition) {
        isShowGeoposition = showGeoposition;
    }


    public int getWaterCount() {
        return waterCount;
    }

    public void setWaterCount(int waterCount) {
        this.waterCount = waterCount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean getSpeak() {
        return isSpeak;
    }

    public void setSpeak(boolean speak) {
        isSpeak = speak;
    }
}
