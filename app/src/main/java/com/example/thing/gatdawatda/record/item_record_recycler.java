package com.example.thing.gatdawatda.record;

import android.view.View;
import android.view.ViewGroup;

//https://developer88.tistory.com/3
//https://developer88.tistory.com/102
//Student = item_record_recycler
public class item_record_recycler  {
    String ID;
    String image;
    String name;
    String location;
    double latitude;
    double longitude;

    public item_record_recycler(String _ID, String _image, String _name, String _loc,double _latitude, double _longitude){
        this.ID = _ID;
        this.image = _image;
        this.name = _name;
        this.location = _loc;
        this.latitude = _latitude;
        this.longitude = _longitude;
    }

    public item_record_recycler(item_record_recycler targetUser) {
        image = targetUser.getImage();
        name = targetUser.getname();
        location = targetUser.getlocation();
    }

    public String getImage(){
        return image;
    }
    public String getname(){
        return name;
    }
    public String getlocation(){
        return location;
    }
    public String getID(){return ID;}
    public double getLatitude(){return latitude;}
    public double getLongitude(){return longitude;}


}