package com.example.thing.gatdawatda.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.thing.gatdawatda.R;
import com.example.thing.gatdawatda.home.activity_home;
import com.example.thing.gatdawatda.post.activity_post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class activity_search extends AppCompatActivity implements search_listadapter.OnTripSelectedListener, SwipeRefreshLayout.OnRefreshListener {
    private ListView m_oListView = null;
    Intent intent;
    public String name;

    private RecyclerView mSearchRecycler;
    private View mEmptyView;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private search_listadapter mAdapter;
    adapter_search rvadapter;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private Spinner spinner_sort;
    private Button btCancel, btSort;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    final ArrayList<item_search> triplist = new ArrayList<>();
    private int filter_popular = 0;
    boolean isnameArray;
    ArrayList<String> nameArray = new ArrayList<>();
    String[] filter_name = {"uploadTime", "like"};
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ImageView btBack = (ImageView) findViewById(R.id.btBack);
        final TextView tvSearch = (TextView) findViewById(R.id.tvSearch); /*TextView선언*/
        Button btCancel = (Button) findViewById(R.id.btCancel);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        //검색 위한 코드
        mSearchRecycler = (RecyclerView) findViewById(R.id.recycler_search);
        mEmptyView = (View) findViewById(R.id.view_empty);
        spinner_sort = findViewById(R.id.spinner_sort);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this,
                R.array.sort_by,
                R.layout.color_spinner_layout
        );
        spinner_sort.setAdapter(adapter);
        FirebaseFirestore.setLoggingEnabled(true);
        mFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        intent = getIntent();
        if (intent.getStringExtra("name") != null) {
            name = intent.getStringExtra("name");
            Log.d("debug5.7", "intent name : " + name);
            mSearchRecycler.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            tvSearch.setText(name);
        } else {
            mSearchRecycler.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }
        setcontinent();
        //getTriplist();

        //스피너 선택
        spinner_sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    filter_popular = 0;
                    getTriplist();
                } else {
                    filter_popular = 1;
                    getTriplist();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btBack.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(activity_search.this, activity_home.class);
                startActivity(intent);
                finish();
            }
        });

        btCancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvSearch.setText(null);
                mSearchRecycler.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            }
        });

        tvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                name = tvSearch.getText().toString();
                setcontinent();
                getTriplist();
            }
        });

        rvadapter = new adapter_search(getBaseContext(), triplist);
        LinearLayoutManager LayoutManagaer = new LinearLayoutManager(activity_search.this, LinearLayoutManager.VERTICAL, false);
        mSearchRecycler.setLayoutManager(LayoutManagaer);
        mSearchRecycler.setAdapter(rvadapter);
        mSearchRecycler.hasFixedSize();
    }

    void getTriplist() {
        Log.d("debug5.7", "getTriplist");
        triplist.clear();
        if (name != null) {
            if (name.equals("")) {
                mSearchRecycler.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mSearchRecycler.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
                mFirestore.collection("Trip").orderBy(filter_name[filter_popular], Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            triplist.clear();
                            Log.d("debug5.7", "onComplete called");
                            ArrayList<String> check = new ArrayList<>();
                            for (final QueryDocumentSnapshot qdocument : task.getResult()) {
                                if (qdocument.getBoolean("isComplete")) {
                                    Log.d("debug5.7", "oncomplete_  document id : " + qdocument.getId());
                                    ArrayList<String> tripCountry = (ArrayList<String>) qdocument.get("tripCountry");
                                    String tname = qdocument.getString("name");
                                    String tdescription = qdocument.getString("description");
                                    //final String tid = qdocument.getId();
                                    if (isnameArray) {
                                        for (String s : nameArray) {
                                            if (tripCountry.contains(s) || tname.contains(s) || tdescription.contains(s)) {
                                                if (!check.contains(qdocument.getId())) {
                                                    check.add(qdocument.getId());
                                                    triplist.add(new item_search(qdocument.getId(), qdocument.getString("tripMainPic"), qdocument.getString("name"),
                                                            qdocument.getString("user_id"), qdocument.getString("description"), qdocument.getLong("like")));
                                                    Log.d("debug5.7", "triplist add : " + tname);
                                                }
                                            }
                                        }
                                    } else {
                                        if (tripCountry.contains(name) || tname.contains(name) || tdescription.contains(name)) {
                                            if (!check.contains(qdocument.getId())) {
                                                check.add(qdocument.getId());
                                                triplist.add(new item_search(qdocument.getId(), qdocument.getString("tripMainPic"), qdocument.getString("name"),
                                                        qdocument.getString("user_id"), qdocument.getString("description"), qdocument.getLong("like")));
                                            }
                                        }
                                    }
                                }
                            }
                            if (triplist.isEmpty()) {
                                mSearchRecycler.setVisibility(View.GONE);
                                mEmptyView.setVisibility(View.VISIBLE);
                            }
                            Log.d("finaldebug","print "+check);
                            rvadapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        } else {
            mSearchRecycler.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }


    @Override
    public void OnTripSelected(DocumentSnapshot Course) {
        intent = new Intent(this, activity_post.class);
        intent.putExtra("TripID", Course.getId());
        startActivity(intent);

    }

    void setcontinent() {
        if (name != null) {
            if (name.equals("아시아")) {
                isnameArray = true;
                nameArray.clear();
                nameArray.add("대한민국");
                nameArray.add("대만");
                nameArray.add("베트남");
                nameArray.add("중국");
                nameArray.add("홍콩");
                nameArray.add("필리핀");
                nameArray.add("러시아");
                nameArray.add("일본");
            } else if (name.equals("유럽")) {
                isnameArray = true;
                nameArray.clear();
                nameArray.add("바티칸");
                nameArray.add("그리스");
                nameArray.add("프랑스");
                nameArray.add("독일");
                nameArray.add("영국");
                nameArray.add("포르투칼");

            } else if (name.equals("아프리카")) {
                isnameArray = true;
                nameArray.clear();

            } else if (name.equals("북아메리카")) {
                isnameArray = true;
                nameArray.clear();
                nameArray.add("미국");
                nameArray.add("멕시코");
                nameArray.add("캐나다");
            } else if (name.equals("남아메리카")) {
                isnameArray = true;
                nameArray.clear();
                nameArray.add("브라질");
                nameArray.add("아르헨티나");


            } else if (name.equals("오세아니아")) {
                nameArray.add("호주");
                nameArray.add("오스트레일리아");
                nameArray.add("뉴질랜드");
                isnameArray = true;
            } else {
                isnameArray = false;
                nameArray.clear();
            }
        }

    }

    @Override
    public void onRefresh() {
        mSearchRecycler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //onStart();
                getTriplist();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 100);
    }
}