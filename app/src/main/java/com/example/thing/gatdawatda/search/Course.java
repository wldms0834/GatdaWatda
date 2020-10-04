package com.example.thing.gatdawatda.search;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Course {
    private String name;
    private String user_id;
    private String description;
    private String tripMainPic;
    private long like;

    public Course() {}

    public Course(String name, String user_id, String description, String tripMainPic, long like) {
        this.name = name;
        this.user_id = user_id;
        this.description = description;
        this.tripMainPic = tripMainPic;
        this.like = like;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getTripMainPic() {
        return tripMainPic;
    }
    public void setTripMainPic(String tripMainPic) {
        this.tripMainPic = tripMainPic;
    }
    public long getLike() {
        return like;
    }
    public void setLike(long like) {
        this.like = like;
    }
}




