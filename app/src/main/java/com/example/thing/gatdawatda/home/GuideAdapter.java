package com.example.thing.gatdawatda.home;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.thing.gatdawatda.FirestoreAdapter;
import com.example.thing.gatdawatda.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
//import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * RecyclerView adapter for a list of Guides.
 */
public class GuideAdapter extends FirestoreAdapter<GuideAdapter.ViewHolder> {

    public interface OnGuideSelectedListener {
        void OnGuideSelected(DocumentSnapshot Guide);
    }

    private OnGuideSelectedListener mListener;

    public GuideAdapter(Query query, OnGuideSelectedListener listener) {
        super(query);
        mListener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_guide, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.profile_icon)
        ImageView imageView;

        @BindView(R.id.ranking)
        ImageView rankImageView;

        @BindView(R.id.profile_name)
        TextView nameView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnGuideSelectedListener listener) {

            Guide guide = snapshot.toObject(Guide.class);
            Resources resources = itemView.getResources();

            if (getAdapterPosition()== 0) {
                Log.d("AA", String.valueOf(getAdapterPosition()));
                        Glide.with(rankImageView.getContext())
                        //.setDefaultRequestOptions(placeholderRequest)
                        .load(R.drawable.ic_one)
                        .into(rankImageView);
            }
            else if (getAdapterPosition()==1) {
                Glide.with(rankImageView.getContext())
                        //.setDefaultRequestOptions(placeholderRequest)
                        .load(R.drawable.ic_two)
                        .into(rankImageView);
            }

            else if (getAdapterPosition()==2) {
                Glide.with(rankImageView.getContext())
                        //.setDefaultRequestOptions(placeholderRequest)
                        .load(R.drawable.ic_three)
                        .into(rankImageView);
            }

            else if (getAdapterPosition()==3) {
                Glide.with(rankImageView.getContext())
                        //.setDefaultRequestOptions(placeholderRequest)
                        .load(R.drawable.ic_four)
                        .into(rankImageView);
            }

            else if (getAdapterPosition()==4) {
                Glide.with(rankImageView.getContext())
                        //.setDefaultRequestOptions(placeholderRequest)
                        .load(R.drawable.ic_five)
                        .into(rankImageView);
            }

            //RequestOptions placeholderRequest = new RequestOptions();
            //placeholderRequest.placeholder(R.drawable.default_image);

            // Load image
            Glide.with(imageView.getContext())
                    //.setDefaultRequestOptions(placeholderRequest)
                    .load(guide.getImage())
                    .into(imageView);


            nameView.setText(guide.getName() + " 님");
            ;

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.OnGuideSelected(snapshot);
                    }
                }
            });
        }

    }
}
