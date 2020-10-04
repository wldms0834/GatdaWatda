package com.example.thing.gatdawatda.plan;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class ItemMove_plan extends ItemTouchHelper.Callback{

    private final adapter1_plan madapter;

    public ItemMove_plan(adapter1_plan adapter) {
        madapter = adapter;
    }


    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        madapter.onRowMoved((adapter1_plan.myViewHolder)viewHolder,(adapter1_plan.myViewHolder)target, viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,int actionState) {

        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof adapter1_plan.myViewHolder) {
                adapter1_plan.myViewHolder myViewHolder =
                        (adapter1_plan.myViewHolder) viewHolder;
                madapter.onRowSelected(myViewHolder);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public void clearView(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof adapter1_plan.myViewHolder) {
            adapter1_plan.myViewHolder myViewHolder =
                    (adapter1_plan.myViewHolder) viewHolder;
            madapter.onRowClear(myViewHolder);
        }
    }


    public interface ItemTouchHelperContractinterface {
        void onRowMoved(adapter1_plan.myViewHolder viewHolder,adapter1_plan.myViewHolder target,int fromPosition, int toPosition);
        void onRowSelected(adapter1_plan.myViewHolder myViewHolder);
        void onRowClear(adapter1_plan.myViewHolder myViewHolder);
    }
}
