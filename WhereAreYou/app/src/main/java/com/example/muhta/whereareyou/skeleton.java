package com.example.muhta.whereareyou;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class skeleton {
    Marker[] markers;
    String username;
    int shareLocations=-1;
    int downloadFrequency;
    int uploadFrequency;
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
    String settings;
    Friend(String uid){
        this.UID=uid;
    }
}

class Marker{
    LatLng latlon;
    String name;
    int image;
}
