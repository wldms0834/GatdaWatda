package com.example.thing.gatdawatda.record;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.thing.gatdawatda.R;

import java.util.Collections;
import java.util.List;
import static com.example.thing.gatdawatda.record.activity_record.rvadapter;
import static com.example.thing.gatdawatda.mypage.activity_mypage.user_id;

public class adapter2_record extends RecyclerView.Adapter<adapter2_record.myViewHolder2> implements  ItemMove_rv2.ItemTouchHelperContractinterface {
    Context context;
    List<item_record_recycler> recyclerlist;

    class myViewHolder2 extends RecyclerView.ViewHolder{
        TextView tv;
        ImageView iv;
        View rowView;
        public myViewHolder2(View itemView) {
            super(itemView);
            rowView = itemView;
            iv = itemView.findViewById(R.id.rv2_iv);
            tv = itemView.findViewById(R.id.rv2_tv);
        }
    }

    public adapter2_record(Context _applicationContext, List<item_record_recycler> _recylcerlist) {
        this.context = _applicationContext;
        this.recyclerlist = _recylcerlist;
        Log.d("aa","adapter_rv2 생성자 호출");

    }
    @NonNull
    @Override
    public myViewHolder2 onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View holderView2 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_record_rv2,viewGroup,false);
        adapter2_record.myViewHolder2 myViewHolder2 = new adapter2_record.myViewHolder2(holderView2);
        return myViewHolder2;
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder2 holder2, int position) {
        holder2.iv.setImageResource(R.drawable.ic_simplemap);
        holder2.iv.setScaleType(ImageView.ScaleType.FIT_XY);
        holder2.tv.setText(recyclerlist.get(position).getname());
    }

    @Override
    public int getItemCount() {
        return recyclerlist.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
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
        rvadapter.notifyItemMoved(fromPosition,toPosition);
        if(context instanceof activity_record){
            ((activity_record)context).changeDatabase(fromPosition,toPosition);
        }
    }

    @Override
    public void onRowSelected(myViewHolder2 myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.GRAY);

    }

    @Override
    public void onRowClear(adapter2_record.myViewHolder2 myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);
        if (context instanceof activity_record) {
            ((activity_record)context).clearMap();
            ((activity_record)context).retrieveLocation();
        }
    }
}