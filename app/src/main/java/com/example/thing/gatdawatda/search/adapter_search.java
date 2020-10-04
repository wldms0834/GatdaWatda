package com.example.thing.gatdawatda.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.thing.gatdawatda.R;
import com.example.thing.gatdawatda.post.activity_post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class adapter_search extends RecyclerView.Adapter<adapter_search.myViewHolder> {
    Context context;
    List<item_search> recyclerlist;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    Intent intent;


    public static class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context context;
        ImageView ivCoursePic, ivCourseLike;
        TextView tvCourseName, tvCourseGuide, tvCourseDescription, tvCourseLike;
        View view;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            ivCoursePic = itemView.findViewById(R.id.courseItem_image);
            ivCourseLike = itemView.findViewById(R.id.course_btlike);
            tvCourseName = itemView.findViewById(R.id.courseItem_name);
            tvCourseGuide = itemView.findViewById(R.id.courseItem_guider);
            tvCourseDescription = itemView.findViewById(R.id.courseItem_explain);
            tvCourseLike = itemView.findViewById(R.id.course_tvlike);
            //view.setOnClickListener(this);
            //ivCourseLike.setOnClickListener(this);
            GradientDrawable drawable=
                    (GradientDrawable) itemView.getContext().getDrawable(R.drawable.imageview_rounding);

            ivCoursePic.setBackground(drawable);
            ivCoursePic.setClipToOutline(true);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.course_btlike) {
            } else {

            }
        }
    }

    public adapter_search(Context _context, List<item_search> _list) {
        context = _context;
        recyclerlist = _list;
    }


    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View holderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_listview, viewGroup, false);
        return new myViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(@NonNull final adapter_search.myViewHolder holder, final int pos) {
        GradientDrawable drawable = (GradientDrawable) context.getDrawable(R.drawable.imageview_rounding);
        holder.ivCoursePic.setBackground(drawable);
        firebaseFirestore.collection("Users").document(recyclerlist.get(pos).getGuideID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String Guider_user_id = task.getResult().getString("name");
                        holder.tvCourseGuide.setText(Guider_user_id + " 가이드님");
                    }
                }
            }
        });
        holder.tvCourseName.setText(recyclerlist.get(pos).getTripName());
        holder.tvCourseDescription.setText(recyclerlist.get(pos).getDecription());
        holder.tvCourseLike.setText(recyclerlist.get(pos).getLikenum().toString());
        Glide.with(context).load(recyclerlist.get(pos).getMainPic()).into(holder.ivCoursePic);
        Glide.with(context).load(R.drawable.ic_favorite_green_24dp).into(holder.ivCourseLike);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(context, activity_post.class);
                intent.putExtra("TripID", recyclerlist.get(pos).getTripID());
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });
    }


    @Override
    public int getItemCount() {
        return recyclerlist.size();
    }
}