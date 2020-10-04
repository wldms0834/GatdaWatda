package com.example.thing.gatdawatda.record;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

public class ItemMove_rv2 extends ItemTouchHelper.Callback {

    private final adapter2_record mAdapter2;

    public ItemMove_rv2(@Nullable adapter2_record adapter) {
        Log.d("aa", "ItemMoveCallback 생성자");
        mAdapter2 = adapter;

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
        int dragFlags = ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        mAdapter2.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());

        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
                                  int actionState) {

        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof adapter2_record.myViewHolder2) {
                adapter2_record.myViewHolder2 myViewHolder =
                        (adapter2_record.myViewHolder2) viewHolder;
                mAdapter2.onRowSelected(myViewHolder);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof adapter2_record.myViewHolder2) {
            adapter2_record.myViewHolder2 myViewHolder =
                    (adapter2_record.myViewHolder2) viewHolder;
            mAdapter2.onRowClear(myViewHolder);
        }
    }

    public interface ItemTouchHelperContractinterface {
        void onRowMoved(int fromPosition, int toPosition);
        void onRowSelected(adapter2_record.myViewHolder2 myViewHolder);
        void onRowClear(adapter2_record.myViewHolder2 myViewHolder);
    }

}
