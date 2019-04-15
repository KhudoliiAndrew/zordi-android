package com.example.admin.miplus.data_base.models;

import java.util.Date;

public class GeoData {

    private double latitude;
    private double longitude;
    private Date date;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setUserPosition(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
