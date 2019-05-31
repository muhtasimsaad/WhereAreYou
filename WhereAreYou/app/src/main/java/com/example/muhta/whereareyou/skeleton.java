package com.example.muhta.whereareyou;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class skeleton {
    ArrayList<marker> markers;
    String username;
    int shareLocations=0;
    int uploadFrequency;
    int notifications=0;
    ArrayList<Friend> frndList = new ArrayList<Friend>();
    int image=-1;

}
class Friend {
    String name;
    String UID;
    String email;
    LatLng latlon;
    int image;
    long pictime;
    long time;
    String notification;
    boolean notificationEnabled=false;
    String settings;
    Friend(String uid){
        this.UID=uid;
    }
}

class marker{
    LatLng latlon;
    String name;
    int image;

}
