package com.example.thing.gatdawatda.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thing.gatdawatda.R;
import com.example.thing.gatdawatda.activity_loading;
import com.example.thing.gatdawatda.mypage.activity_mypage;
import com.example.thing.gatdawatda.plan.activity_plan;
import com.example.thing.gatdawatda.post.activity_post;
import com.example.thing.gatdawatda.record.activity_record;
import com.example.thing.gatdawatda.record.activity_record_calendar;
import com.example.thing.gatdawatda.record.activity_record_spot;
import com.example.thing.gatdawatda.search.Course;
import com.example.thing.gatdawatda.search.activity_search;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.example.thing.gatdawatda.mypage.activity_mypage.user_id;

public class activity_home extends AppCompatActivity implements GuideAdapter.OnGuideSelectedListener, IFireStoreLoadDone {

    private static final String TAG = "MainActivity";
    private RecyclerView mGuideRecycler;
    private List<Guide> guide_List;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private GuideAdapter mAdapter;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    static List <String> Trip_click_ID = new ArrayList<String>();


    //viewpager 코드
    ViewPagerAdapter viewpageradapter;
    IFireStoreLoadDone iFireStoreLoadDone;
    CollectionReference courses;
    ViewPager viewPager;


    BottomNavigationView bottomNav;
    Intent intent;
    private Toolbar my_toolbar;
    ImageView btAsia,btEurope,btAfrica,btNAmerica,btSAmerica,btOceania;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mFirestore = FirebaseFirestore.getInstance();

        //viewpager init
        viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager, true);
        iFireStoreLoadDone = this;
        courses = mFirestore.collection("Trip");
        getData();





        //인기가이드를 위한 코드
        mGuideRecycler = (RecyclerView) findViewById(R.id.recyclerGuide);
        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();
        // Get ${LIMIT} restaurants
        mQuery = mFirestore.collection("Users")
                .orderBy("like", Query.Direction.DESCENDING)
                .limit(11);

        // RecyclerView
        mAdapter = new GuideAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mGuideRecycler.setVisibility(View.GONE);
                } else {
                    mGuideRecycler.setVisibility(View.VISIBLE);
                }
            }
            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(findViewById(android.R.id.content),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };
        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(activity_home.this, LinearLayoutManager.HORIZONTAL, false);
        mGuideRecycler.setLayoutManager(horizontalLayoutManagaer);
        mGuideRecycler.setAdapter(mAdapter);
        mGuideRecycler.hasFixedSize();


       // Intent intent2 = new Intent(this, activity_loading.class);
        //startActivity(intent2);

        my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getActionBar().setIcon(R.drawable.logo);


        findViewById(R.id.btAsia).setOnClickListener(mClickListener);
        findViewById(R.id.btEurope).setOnClickListener(mClickListener);
        findViewById(R.id.btAfrica).setOnClickListener(mClickListener);
        findViewById(R.id.btNAmerica).setOnClickListener(mClickListener);
        findViewById(R.id.btSAmerica).setOnClickListener(mClickListener);
        findViewById(R.id.btOceania).setOnClickListener(mClickListener);


        //네비게이션부분임
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mNavigationListener);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

    }

    private void getData() {
        mQuery =courses.orderBy("like", Query.Direction.DESCENDING).whereEqualTo("isComplete",true).limit(11);
        mQuery.get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iFireStoreLoadDone.onFireStoreLoadFailed(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            List<Course> courses = new ArrayList<>();
                            Trip_click_ID = new ArrayList<>();
                            for(QueryDocumentSnapshot courseSnapShot: task.getResult())
                            {

                                Course course = courseSnapShot.toObject(Course.class);
                                Trip_click_ID.add(courseSnapShot.getId());
                                courses.add(course);
                            }
                            iFireStoreLoadDone.onFireStoreLoadSuccess(courses);
                        }
                    }
                });
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

    private BottomNavigationView.OnNavigationItemSelectedListener mNavigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    intent = new Intent(activity_home.this, activity_home.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.nav_plan:
                    intent = new Intent(activity_home.this, activity_plan.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.nav_record:
                    String user_id;
                    firebaseAuth = FirebaseAuth.getInstance();
                    user_id = firebaseAuth.getCurrentUser().getUid();
                    firebaseFirestore = FirebaseFirestore.getInstance();
                    firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().getString("istraveling").equals("")) {
                                    Intent intent2 = new Intent(activity_home.this, activity_record_calendar.class);
                                    startActivity(intent2);
                                    finish();
                                } else {
                                    Intent intent2 = new Intent(activity_home.this, activity_record.class);
                                    startActivity(intent2);
                                    finish();
                                }
                            }
                        }
                    });
                    break;

                case R.id.nav_my_page:
                    intent = new Intent(activity_home.this, activity_mypage.class);
                    startActivity(intent);
                    finish();
                    break;
            }
            return true;
        }
    };

    //홈 툴바
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_button: { // 오른쪽 상단 버튼 눌렀을 때
                Intent intent = new Intent(this, activity_search.class);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    ImageView.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            intent = new Intent(getApplicationContext(), activity_search.class);

            switch (v.getId()) {
                case R.id.btAsia:
                    intent.putExtra("name","아시아");
                    startActivity(intent);
                    break;

                case R.id.btEurope:
                    intent.putExtra("name","유럽");
                    startActivity(intent);
                    break;

                case R.id.btAfrica:
                    intent.putExtra("name","아프리카");
                    startActivity(intent);
                    break;

                case R.id.btNAmerica:
                    intent.putExtra("name","북아메리카");
                    startActivity(intent);
                    break;

                case R.id.btSAmerica:
                    intent.putExtra("name","남아메리카");
                    startActivity(intent);
                    break;

                case R.id.btOceania:
                    intent.putExtra("name","오세아니아");
                    startActivity(intent);
                    break;

            }
        }
    };

    @Override
    public void OnGuideSelected(DocumentSnapshot Guide) {
        intent = new Intent(this, activity_mypage.class);
        //intent.putExtra(activity_mypage.guideId, Guide.getId());
        intent.putExtra("guideID",Guide.getId());
        //Toast.makeText(this, "id"+Guide.getId(), Toast.LENGTH_LONG).show();
        startActivity(intent);
    }

    @Override
    public void onFireStoreLoadSuccess(List<Course> courses) {
        viewpageradapter = new ViewPagerAdapter(this,courses);
        viewPager.setAdapter(viewpageradapter);
    }

    @Override
    public void onFireStoreLoadFailed(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    class myFragmentAdapter extends FragmentPagerAdapter {

        // ViewPager에 들어갈 Fragment들을 담을 리스트
        private ArrayList<Fragment> fragments = new ArrayList<>();

        // 필수 생성자
        myFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        // List에 Fragment를 담을 함수
        void addItem(Fragment fragment) {
            fragments.add(fragment);
        }
    }





    //엑티비티 안보이면 종료
  /* @Override    public void onPause() {
        super.onPause();

        // Remove the activity when its off the screen
        finish();
    }
*/
}


