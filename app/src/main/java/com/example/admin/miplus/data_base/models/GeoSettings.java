package com.example.admin.miplus.data_base.models;

public class GeoSettings {
    private String mapType;
    private String markerColor;
    private String polylineColor;

    public void setMapType(String mapType){
        this.mapType = mapType;
    }
    public String getMapType(){
        return mapType;
    }

    public void setMarkerColor(String markerColor){
        this.markerColor = markerColor;
    }
    public String getMarkerColor(){
        return markerColor;
    }

    public void setPolylineColor(String polylineColor){
        this.polylineColor = polylineColor;
    }
    public String getPolylineColor(){
        return polylineColor;
    }

}
