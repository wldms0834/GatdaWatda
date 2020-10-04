package com.example.thing.gatdawatda.search;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.thing.gatdawatda.FirestoreAdapter;
import com.example.thing.gatdawatda.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.security.AccessController.getContext;
//import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * RecyclerView adapter for a list of Course.
 */
public class search_listadapter extends FirestoreAdapter<search_listadapter.ViewHolder> {

    public interface OnTripSelectedListener {
        void OnTripSelected(DocumentSnapshot Course);
    }

    private OnTripSelectedListener mListener;


    public search_listadapter(Query query, OnTripSelectedListener listener) {
         super(query.whereEqualTo("isComplete",true));
       // query = query.whereEqualTo("isComplete",true);
            //super(query);
            mListener = listener;
        }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_search_listview, parent, false));

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
                holder.bind(getSnapshot(position), mListener);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        /*@BindView(R.id.courseItem_image)
        ImageView ivcourseItem_image;
        */
        ImageView ivcourseItem_image = itemView.findViewById(R.id.courseItem_image);

        @BindView(R.id.courseItem_guider)
        TextView tvcourseItem_guider;

        @BindView(R.id.courseItem_explain)
        TextView tvcourseItem_explain;

        @BindView(R.id.courseItem_name)
        TextView tvcourseItem_name;

        @BindView(R.id.tvLike)
        TextView tvcourseLikeNum;



        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnTripSelectedListener listener) {

            GradientDrawable drawable=
                    (GradientDrawable) itemView.getContext().getDrawable(R.drawable.imageview_rounding);

            ivcourseItem_image.setBackground(drawable);
            ivcourseItem_image.setClipToOutline(true);


            Course course = snapshot.toObject(Course.class);
            Resources resources = itemView.getResources();
            //RequestOptions placeholderRequest = new RequestOptions();
            //placeholderRequest.placeholder(R.drawable.default_image);

            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore.collection("Users").document(course.getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            String Guider_user_id = task.getResult().getString("name");
                            tvcourseItem_guider.setText(Guider_user_id + " 가이드님");

                        }
                    }
                }
            });
            String tempLike = String.valueOf(course.getLike());
            Log.d("rr", "실행완료1");
            tvcourseItem_name.setText(course.getName());
            Log.d("rr", "실행완료2");
            tvcourseItem_explain.setText(course.getDescription());
            Log.d("rr", "실행완료3");
            tvcourseLikeNum.setText(tempLike);
            Log.d("rr", "실행완료4");
            // Load image
            Glide.with(ivcourseItem_image.getContext())
                    //.setDefaultRequestOptions(placeholderRequest)
                    .load(course.getTripMainPic())
                    .into(ivcourseItem_image);
            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.OnTripSelected(snapshot);
                    }
                }
            });
        }
    }

}
