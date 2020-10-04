package com.example.thing.gatdawatda.record;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thing.gatdawatda.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class adapter_record_spot extends RecyclerView.Adapter<adapter_record_spot.CustomViewHolder> {
    private ArrayList<item_record_spot_list_pic_comment> mList;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView ivSpotPic;
        protected TextView comment;

        public CustomViewHolder(View view) {
            super(view);
            this.ivSpotPic = (ImageView) view.findViewById(R.id.ivSpotPic);
            this.comment = (TextView) view.findViewById(R.id.tvComment);
        }
    }

    public adapter_record_spot(ArrayList<item_record_spot_list_pic_comment> list) {
        this.mList = list;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recordspot_pic_comment, viewGroup, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, final int position) {
        item_record_spot_list_pic_comment c = mList.get(position);
        if (c.getIvSpotPic() != null) {
            if (mList.get(position).getIvSpotPic() != null) {
                viewholder.ivSpotPic.setImageBitmap(mList.get(position).getIvSpotPic());
            }
        } else {
            Picasso.get().load(mList.get(position).getpicURI()).into(viewholder.ivSpotPic);
        }

        String com = mList.get(position).getComment();
        if (!com.equals("  ")) {
            viewholder.comment.setText(com);
        }
        viewholder.comment.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        viewholder.comment.setGravity(Gravity.CENTER);
        viewholder.comment.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mList.get(position).setComment(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //viewholder.comment.setText(mList.get(position).getIvSpotPic()+"");
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

}
