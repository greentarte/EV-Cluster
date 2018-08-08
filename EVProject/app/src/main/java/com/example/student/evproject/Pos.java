package com.example.student.evproject;

public class Pos {

    public String formattedAddress;
    public double lat;
    public double lng;

    Pos(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }

    Pos(String formattedAddress, double lat, double lng) {
        this.formattedAddress = formattedAddress;
        this.lat = lat;
        this.lng = lng;
    }
}
