package com.example.thing.gatdawatda.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thing.gatdawatda.R;
import com.example.thing.gatdawatda.post.activity_post;
import com.example.thing.gatdawatda.search.Course;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.annotations.NonNull;

import static com.example.thing.gatdawatda.home.activity_home.Trip_click_ID;

public class ViewPagerAdapter extends PagerAdapter {

    Context context;
    List<Course> courseList;
    LayoutInflater inflater;

    String GuideID;

    public ViewPagerAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return courseList.size();
    }

    @Override
    public  boolean isViewFromObject (@NonNull View view, @NonNull Object o) {
        return view ==o;
    }

    @Override
    public void destroyItem (@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager)container).removeView((View)object);
    }

    @NonNull
    @Override
    public Object instantiateItem (@NonNull final ViewGroup container, final int position) {
        //Inflate view
        View view = inflater.inflate(R.layout.fragment_home_viewpager,container,false);

        //View
        ImageView ivmainPic = (ImageView)view.findViewById(R.id.ivmainPic);
        TextView tvPostName = (TextView)view.findViewById(R.id.tvPostName);
        final TextView tvGuiderName = (TextView)view.findViewById(R.id.tvGuiderName);
        TextView tvPostDescription = (TextView)view.findViewById(R.id.tvPostDescription);
        TextView tvLike = (TextView)view.findViewById(R.id.tvCourseLike) ;
        View ivGradation = (View)view.findViewById(R.id.ivGradation);


        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Users").document(courseList.get(position).getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String Guider_user_id = task.getResult().getString("name");
                        GuideID = task.getResult().getId();
                        tvGuiderName.setText("by "+Guider_user_id + " 님");
                    }
                }
            }
        });
        final String TripId=firebaseFirestore.collection("Trip").document().getId();

        String tempLike = String.valueOf(courseList.get(position).getLike());
        Picasso.get().load(courseList.get(position).getTripMainPic()).into(ivmainPic);
        GradientDrawable drawable=
                (GradientDrawable) view.getContext().getDrawable(R.drawable.imageview_rounding);

        ivmainPic.setBackground(drawable);
        ivmainPic.setClipToOutline(true);
        ivmainPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
        tvPostName.setText(courseList.get(position).getName());
        tvPostDescription.setText(courseList.get(position).getDescription());
        tvLike.setText(tempLike);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, activity_post.class);
                intent.putExtra("TripID", Trip_click_ID.get(position));
                intent.putExtra("GuideID", GuideID);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        container.addView(view);

        return view;
    }
}