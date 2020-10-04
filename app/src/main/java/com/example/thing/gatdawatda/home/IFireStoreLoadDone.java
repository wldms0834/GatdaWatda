package com.example.thing.gatdawatda.home;

import com.example.thing.gatdawatda.search.Course;

import java.util.List;

public interface IFireStoreLoadDone {
    void onFireStoreLoadSuccess(List<Course> courses);
    void onFireStoreLoadFailed (String message);
}
