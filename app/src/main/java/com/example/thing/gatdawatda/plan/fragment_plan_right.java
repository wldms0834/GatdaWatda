package com.example.thing.gatdawatda.plan;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thing.gatdawatda.R;
import com.example.thing.gatdawatda.post.activity_post;
import com.example.thing.gatdawatda.record.activity_record;
import com.example.thing.gatdawatda.record.item_record_recycler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.thing.gatdawatda.mypage.activity_mypage.user_id;

public class fragment_plan_right extends Fragment {
    public static fragment_plan_right newInstance() {
        return new fragment_plan_right();
    }

    //파이어베이스
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    RecyclerView recycler_plan_right;
    RecyclerView.Adapter mAdapter;
    ArrayList<Pair<String, String>> planList = new ArrayList<Pair<String, String>>();

    public fragment_plan_right() {
        // required
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan_sub2, container, false);
        recycler_plan_right = view.findViewById(R.id.recycler_plan_right);

        datafromfirebase();

        //리스트뷰 터치 리스너
        recycler_plan_right.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP) {
                    //position구하기
                    View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                    int position = recyclerView.getChildAdapterPosition(child);

                    if (position != -1) {
                        Intent intent = new Intent(getActivity(), activity_my_plan.class);
                        intent.putExtra("planID", planList.get(position).first);
                        startActivity(intent);
                    }
                }
                return false;

            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {
            }
        });

        return view;
    }


    void datafromfirebase() {
        Query query = firebaseFirestore.collection("Plan").whereEqualTo("user_id", user_id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        planList.add(new Pair<String, String>(document.getId(), document.getString("name")));

                        mAdapter = new MyAdapter(planList);
                        recycler_plan_right.setAdapter(mAdapter);
                        recycler_plan_right.setLayoutManager(new LinearLayoutManager(getActivity()));

                    }
                }
            }
        });
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private ArrayList<Pair<String, String>> arrayList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            protected TextView tvPlanName;

            public MyViewHolder(View itemView) {
                super(itemView);
                this.tvPlanName = (TextView) itemView.findViewById(R.id.tvPlanName);
            }
        }

        public MyAdapter(ArrayList<Pair<String, String>> _arrayList) {
            this.arrayList = _arrayList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View holderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_myplan_listview, viewGroup, false);
            MyViewHolder viewHolder = new MyViewHolder(holderView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.tvPlanName.setText(arrayList.get(position).second);
        }

        @Override
        public int getItemCount() {
            return (arrayList.size());
        }
    }
}

