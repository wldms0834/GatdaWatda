package com.example.thing.gatdawatda.post;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thing.gatdawatda.R;
import com.example.thing.gatdawatda.post.activity_post;
import com.example.thing.gatdawatda.record.ItemMove_rv1;
import com.example.thing.gatdawatda.record.adapter1_record;
import com.example.thing.gatdawatda.record.item_record_recycler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class adapter1_post extends RecyclerView.Adapter<adapter1_post.myViewHolder> {
    Context context;
    List<item_record_recycler> recyclerlist;
    int[] ids = {R.drawable.ic_marker1, R.drawable.ic_marker2, R.drawable.ic_marker3, R.drawable.ic_marker4, R.drawable.ic_marker5, R.drawable.ic_marker6, R.drawable.ic_marker7, R.drawable.ic_marker8, R.drawable.ic_marker9, R.drawable.ic_marker10, R.drawable.ic_marker11,R.drawable.ic_marker12,R.drawable.ic_marker13,R.drawable.ic_marker14,R.drawable.ic_marker15,R.drawable.ic_marker16,R.drawable.ic_marker17,R.drawable.ic_marker18,R.drawable.ic_marker19,R.drawable.ic_marker20,};
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    String user_id;


    public static class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener    {
        Context context;
        TextView tvRecycleSpotName, tvRecycleSpotLocation;
        ImageView ivSpotMain, ivRecyclerLike, ivRecyclernumber;
        View rowView;
        String spotname,spot_id;
        int spotposition;
        double latitude,longitude;

        public myViewHolder(View itemView) {
            super(itemView);
            rowView = itemView;
            ivSpotMain = itemView.findViewById(R.id.ivRecyclespotmain);
            tvRecycleSpotName = itemView.findViewById(R.id.tvRecyclespotname);
            tvRecycleSpotLocation = itemView.findViewById(R.id.tvRecyclerspotloc);
            ivRecyclernumber = itemView.findViewById(R.id.ivRecyclernumber);
            ivRecyclerLike = itemView.findViewById(R.id.ivRecyclerLike);
            ivRecyclerLike.setOnClickListener(this);
            context = itemView.getContext();
            rowView.setOnClickListener(this);

            GradientDrawable drawable=
                    (GradientDrawable) context.getDrawable(R.drawable.imageview_rounding);
            ivSpotMain.setBackground(drawable);
            ivSpotMain.setClipToOutline(true);
        }

        @Override
        public void onClick(View v){
            Log.d("aa","myviewholder onclick실행완료");
            if(context instanceof activity_post||v.getId()!=R.id.ivRecyclerLike){
                ((activity_post)context).rvclick(spot_id);
            }
        }
    }


    public adapter1_post(Context _applicationContext, List<item_record_recycler> _recylcerlist) {
        this.context = _applicationContext;
        this.recyclerlist = _recylcerlist;
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View holderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_post_recycler,viewGroup,false);
        return new myViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, int position) {
        //holder.ivSpotMain.setImageURI(Uri.parse(recyclerlist.get(position).getImage()));


        holder.ivRecyclernumber.setImageResource(ids[position]);
        holder.spot_id = recyclerlist.get(position).getID();
        firebaseFirestore.collection("spot_like").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        ArrayList<String> spotlist = (ArrayList<String>)task.getResult().get("spotlist");
                        if(spotlist.contains(holder.spot_id)){
                            holder.ivRecyclerLike.setImageResource(R.drawable.ic_favorite_pink_24dp);
                        }
                    }
                }
            }
        });
        holder.spotname = recyclerlist.get(position).getname();
        holder.spotposition = position;
        holder.tvRecycleSpotName.setText(holder.spotname);
        holder.latitude = recyclerlist.get(position).getLatitude();
        holder.longitude = recyclerlist.get(position).getLongitude();
        holder.tvRecycleSpotLocation.setText(getaddress(holder.latitude,holder.longitude));
        Picasso.get().load(recyclerlist.get(position).getImage()).into(holder.ivSpotMain);
        holder.ivRecyclerLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("spot_like").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()){
                                ArrayList<String> spotlist = (ArrayList<String>)task.getResult().get("spotlist");
                                if(spotlist.contains(holder.spot_id)){
                                    holder.ivRecyclerLike.setImageResource(R.drawable.ic_favorite_border_24dp);
                                    spotlist.remove(holder.spot_id);
                                    firebaseFirestore.collection("spot_like").document(user_id).update("spotlist",spotlist);
                                }else{
                                    holder.ivRecyclerLike.setImageResource(R.drawable.ic_favorite_pink_24dp);
                                    spotlist.add(holder.spot_id);
                                    firebaseFirestore.collection("spot_like").document(user_id).update("spotlist",spotlist);
                                }
                            }
                        }
                    }
                });
            }
        });
        Log.d("aa","myviewholder spotname" + holder.spotname);

    }
    public String getaddress(Double lat,Double lon) {
        List<Address> list = null;
        Geocoder geocoder = new Geocoder(context);
        try {
            list = geocoder.getFromLocation(lat, lon, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list != null) {
            if (list.size() == 0) {
                return "주소의 이름을 못찾겠습니다";
            } else {
                return list.get(0).getAddressLine(0);
            }
        }else{
            return "주소의 이름을 못찾겠습니다";
        }
    }

    @Override
    public int getItemCount() {
        return recyclerlist.size();
    }


}