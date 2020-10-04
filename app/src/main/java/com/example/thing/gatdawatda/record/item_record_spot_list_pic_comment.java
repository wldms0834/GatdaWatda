package com.example.thing.gatdawatda.record;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class item_record_spot_list_pic_comment {
    private Bitmap ivSpotPic;
    private String picURI;
    private String comment;

    public Bitmap getIvSpotPic() {
        return ivSpotPic;
    }

    public void setIvSpotPic(Bitmap ivSpotPic) {
        this.ivSpotPic = ivSpotPic;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public item_record_spot_list_pic_comment(Bitmap ivSpotPic, String comment) {
        this.ivSpotPic = ivSpotPic;
        this.comment = comment;
    }
    public item_record_spot_list_pic_comment(String picURI, String comment) {
        this.picURI = picURI;
        this.comment = comment;
    }
    public String getpicURI(){return picURI;}
}