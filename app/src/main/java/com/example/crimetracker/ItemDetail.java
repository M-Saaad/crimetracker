package com.example.crimetracker;

public class ItemDetail {
    public String name , item, detail, locationName, locationLatLng, dateTime;

    public ItemDetail(){}

    public ItemDetail(String name , String item, String detail, String locationName, String locationLatLng, String dateTime){
        this.name = name;
        this.item = item;
        this.detail = detail;
        this.locationName = locationName;
        this.locationLatLng = locationLatLng;
        this.dateTime = dateTime;
    }
}