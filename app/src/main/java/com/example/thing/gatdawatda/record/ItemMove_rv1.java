package com.example.thing.gatdawatda.record;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

public class ItemMove_rv1 extends ItemTouchHelper.Callback {

    private final adapter1_record mAdapter1;

    public ItemMove_rv1(@Nullable adapter1_record adapter) {
        mAdapter1 = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }


    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        mAdapter1.onRowMoved((adapter1_record.myViewHolder)viewHolder,(adapter1_record.myViewHolder)target, viewHolder.getAdapterPosition(), target.getAdapterPosition());

        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,int actionState) {

        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof adapter1_record.myViewHolder) {
                adapter1_record.myViewHolder myViewHolder =
                        (adapter1_record.myViewHolder) viewHolder;
                mAdapter1.onRowSelected(myViewHolder);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof adapter1_record.myViewHolder) {
            adapter1_record.myViewHolder myViewHolder =
                    (adapter1_record.myViewHolder) viewHolder;
            mAdapter1.onRowClear(myViewHolder);
        }
    }

    public interface ItemTouchHelperContractinterface {
        void onRowMoved(adapter1_record.myViewHolder myViewHolder,adapter1_record.myViewHolder target,int fromPosition, int toPosition);
        void onRowSelected(adapter1_record.myViewHolder myViewHolder);
        void onRowClear(adapter1_record.myViewHolder myViewHolder);
    }

}
