package com.example.thing.gatdawatda.home;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Guide POJO.
 */
@IgnoreExtraProperties
public class Guide {
    private String name;
    private String user_id;
    private String image;

    public Guide() {}

    public Guide(String user_id,String name, String image) {
        this.user_id = user_id;
        this.name = name;
        this.image = image;

    }
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {this.image = image;}

}

