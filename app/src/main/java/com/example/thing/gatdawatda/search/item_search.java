package com.example.thing.gatdawatda.search;


public class item_search {
    private String TripID;
    private String MainPic;
    private String TripName;
    private String GuideID;
    private String decription;
    private Long likenum;
    private boolean isLiked;


    public item_search(String _TripID, String _MainPic, String _TripName, String _GuideID, String _description, Long _likenum) {
        TripID = _TripID;
        MainPic = _MainPic;
        TripName = _TripName;
        GuideID = _GuideID;
        decription = _description;
        likenum = _likenum;
    }

    public String getTripID() {
        return TripID;
    }

    public String getMainPic() {
        return MainPic;
    }

    public String getTripName() {
        return TripName;
    }

    public String getGuideID() {
        return GuideID;
    }

    public String getDecription() {
        return decription;
    }

    public Long getLikenum() {
        return likenum;
    }
}