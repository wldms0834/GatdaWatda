package com.example.thing.gatdawatda.record;

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
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.example.thing.gatdawatda.record.activity_record.rv2adapter;

public class adapter1_record extends RecyclerView.Adapter<adapter1_record.myViewHolder> implements ItemMove_rv1.ItemTouchHelperContractinterface {
    Context context;
    List<item_record_recycler> recyclerlist;
    int[] ids = {R.drawable.ic_marker1, R.drawable.ic_marker2, R.drawable.ic_marker3, R.drawable.ic_marker4, R.drawable.ic_marker5, R.drawable.ic_marker6, R.drawable.ic_marker7, R.drawable.ic_marker8, R.drawable.ic_marker9, R.drawable.ic_marker10, R.drawable.ic_marker11,R.drawable.ic_marker12,R.drawable.ic_marker13,R.drawable.ic_marker14,R.drawable.ic_marker15,R.drawable.ic_marker16,R.drawable.ic_marker17,R.drawable.ic_marker18,R.drawable.ic_marker19,R.drawable.ic_marker20,};

    public static class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Context context;
        TextView tvRecycleSpotName, tvRecycleSpotLocation;
        ImageView ivSpotMain, ivdelete, ivRecyclernumber;
        View rowView;
        String spotname, spot_id;
        int spotposition;
        double latitude, longitude;

        public myViewHolder(View itemView) {
            super(itemView);
            rowView = itemView;
            ivSpotMain = itemView.findViewById(R.id.ivRecyclespotmain);
            tvRecycleSpotName = itemView.findViewById(R.id.tvRecyclespotname);
            tvRecycleSpotLocation = itemView.findViewById(R.id.tvRecyclerspotloc);
            ivRecyclernumber = itemView.findViewById(R.id.ivRecyclernumber);
            ivdelete = itemView.findViewById(R.id.ivRecycledelete);
            ivdelete.setOnClickListener(this);
            context = itemView.getContext();
            rowView.setOnClickListener(this);

            GradientDrawable drawable =
                    (GradientDrawable) context.getDrawable(R.drawable.imageview_rounding);
            ivSpotMain.setBackground(drawable);
            ivSpotMain.setClipToOutline(true);
        }

        @Override
        public void onClick(View v) {
            Log.d("aa", "myviewholder onclick실행완료");
            if (context instanceof activity_post) {
                ((activity_post) context).rvclick(spot_id);
            }
            if (context instanceof activity_record) {
                if (v.getId() == R.id.ivRecycledelete) {
                    ((activity_record) context).rvdelete(spotposition, spot_id);
                } else {
                    ((activity_record) context).rvclick(spot_id, latitude, longitude);
                }
            }

        }
    }


    public adapter1_record(Context _applicationContext, List<item_record_recycler> _recylcerlist) {
        this.context = _applicationContext;
        this.recyclerlist = _recylcerlist;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View holderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_record_recycler, viewGroup, false);
        return new myViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        //holder.ivSpotMain.setImageURI(Uri.parse(recyclerlist.get(position).getImage()));
        holder.ivRecyclernumber.setImageResource(ids[position]);
        holder.spot_id = recyclerlist.get(position).getID();
        holder.spotname = recyclerlist.get(position).getname();
        holder.spotposition = position;
        holder.tvRecycleSpotName.setText(holder.spotname);
        holder.latitude = recyclerlist.get(position).getLatitude();
        holder.longitude = recyclerlist.get(position).getLongitude();
        holder.tvRecycleSpotLocation.setText(getaddress(holder.latitude,holder.longitude));
        Picasso.get().load(recyclerlist.get(position).getImage()).into(holder.ivSpotMain);
        Log.d("aa", "myviewholder spotname" + holder.spotname);

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

    @Override
    public void onRowMoved(myViewHolder holder, myViewHolder target,int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(recyclerlist, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(recyclerlist, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        Log.d("aa", "row moved from " + fromPosition + "  to  " + toPosition);
        Log.d("aa", "context : " + context);
        if (context instanceof activity_record) {
            Log.d("aa", "change database");
            ((activity_record) context).changeDatabase(fromPosition, toPosition);
        }
        holder.ivRecyclernumber.setImageResource(ids[toPosition]);
        target.ivRecyclernumber.setImageResource(ids[fromPosition]);
    }

    @Override
    public void onRowSelected(myViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.GRAY);

    }

    @Override
    public void onRowClear(myViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);
        if (context instanceof activity_record) {
            ((activity_record) context).clearMap();
            ((activity_record) context).retrieveLocation();
        }
    }
}