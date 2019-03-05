package com.example.admin.miplus.data_base.models;

public class GeoPoint {
    private String geoPoint;

    public String getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(com.google.firebase.firestore.GeoPoint geoPoint) {
       // if(this.geoPoint != null){
       //     this.geoPoint = this.geoPoint + String.valueOf(geoPoint) + "*";
      //  } else {
            this.geoPoint = String.valueOf(geoPoint) + "*";
     //   }

    }
}
