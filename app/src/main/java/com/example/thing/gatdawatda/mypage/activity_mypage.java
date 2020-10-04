package com.example.thing.gatdawatda.mypage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.thing.gatdawatda.R;
import com.example.thing.gatdawatda.home.activity_home;
import com.example.thing.gatdawatda.activity_login;
import com.example.thing.gatdawatda.plan.activity_plan;
import com.example.thing.gatdawatda.plan.fragment_plan_right;
import com.example.thing.gatdawatda.post.activity_post;
import com.example.thing.gatdawatda.record.activity_record;
import com.example.thing.gatdawatda.record.activity_record_calendar;
import com.example.thing.gatdawatda.search.activity_search;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class activity_mypage extends AppCompatActivity {
    BottomNavigationView bottomNav;
    Intent intent;
    private Toolbar toolbar_mypage;
    private static final String TAG = activity_mypage.class.getName();

    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private Uri mainImageURI = null;

    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    public static  String guideId =null;
    public static String user_id;

    Bitmap bitmap;

    private TextView tvName;
    private ImageView ivSelfie;
    private TextView tvPostingNum;
    private  TextView tvLikeNum;
    private View btLike, btPosting,btLogout,btSetting;

    private boolean myPage=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        firebaseAuth = FirebaseAuth.getInstance();
        if (user_id == null) {
            user_id = firebaseAuth.getCurrentUser().getUid();
        }
        intent = getIntent();
        if (intent.getStringExtra("guideID") != null) {
            String id = intent.getStringExtra("guideID");
            if (!id.equals(user_id)) {
                myPage = false;
                user_id = id;
            }
        } else {
            user_id = firebaseAuth.getCurrentUser().getUid();
        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        ///툴바
        toolbar_mypage = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar_mypage);
        getSupportActionBar().setDisplayShowTitleEnabled(false);




        tvName = findViewById(R.id.tvName);
        ivSelfie = findViewById(R.id.ivSelfie);
        tvPostingNum = findViewById(R.id.tvPostingNum);
        tvLikeNum = findViewById(R.id.tvLikeNum);

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");

                        mainImageURI = Uri.parse(image);
                        tvName.setText(name);

                        //RequestOptions placeholderRequest = new RequestOptions();
                        //placeholderRequest.placeholder(R.drawable.default_image);

                        Glide.with(activity_mypage.this).load(image).into(ivSelfie);
                    }
                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(activity_mypage.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                }
            }
        });

        //GridView
        firebaseFirestore.collection("Trip").whereEqualTo("user_id", user_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    final List<String> tripID = new ArrayList<String>();
                    long likeSum = 0;
                    List<String> tripMainPicList = new ArrayList<String>();
                    for(QueryDocumentSnapshot document : task.getResult()){
                        if(document.getBoolean("isComplete")){
                            tripMainPicList.add(document.getString("tripMainPic"));
                            tripID.add(document.getId());
                            likeSum += document.getLong("like");
                        }
                    }
                    MyGridViewAdapter adapter = new MyGridViewAdapter(getApplicationContext(), R.layout.item_mypage_gridview, tripMainPicList);
                    GridView gv = (GridView) findViewById(R.id.gv);
                    gv.setAdapter(adapter);

                    Log.d("cc", "tripID : " + tripID);
                    tvPostingNum.setText(tripID.size()+"");
                    tvLikeNum.setText((int)likeSum+"");

                    gv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            intent = new Intent(activity_mypage.this, activity_post.class);
                            intent.putExtra("TripID", tripID.get(position));
                            intent.putExtra("GuideID", user_id);
                            startActivity(intent);
                        }
                    });
                }
            }
        });

        //네비게이션
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mNavigationListener);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem;
        if (myPage) {
            menuItem = menu.getItem(3);
        } else {
            menuItem = menu.getItem(0);
        }
        menuItem.setChecked(true);



    }



    private BottomNavigationView.OnNavigationItemSelectedListener mNavigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    intent = new Intent(activity_mypage.this, activity_home.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.nav_plan:
                    intent = new Intent(activity_mypage.this, activity_plan.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.nav_record:
                    firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().getString("istraveling").equals("")) {
                                    Intent intent2 = new Intent(activity_mypage.this, activity_record_calendar.class);
                                    startActivity(intent2);
                                    finish();
                                } else {
                                    Intent intent2 = new Intent(activity_mypage.this, activity_record.class);
                                    startActivity(intent2);
                                    finish();
                                }
                            }
                        }
                    });
                    break;

                case R.id.nav_my_page:
                    intent = new Intent(activity_mypage.this, activity_mypage.class);
                    startActivity(intent);
                    finish();
                    break;
            }
            return true;
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (myPage == true) {
            getMenuInflater().inflate(R.menu.mypage_toolbar, menu);
            return true;
        }
        else {

        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d("debug5.8",""+item.getItemId());
        switch (item.getItemId()) {
            case R.id.action_logout_btn:
                logOut();
                return true;

            case R.id.action_settings_btn:
                Intent settingsIntent = new Intent(activity_mypage.this, activity_setup.class);
                startActivity(settingsIntent);
                return true;

            case android.R.id.home:
                finish();
                return true;

            default:
                return false;


        }
    }



    private void logOut() {
        firebaseAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(activity_mypage.this, activity_login.class);
        startActivity(loginIntent);
        finish();
    }

    class MyGridViewAdapter extends BaseAdapter{
        Context context;
        int layout;
        List<String> tripMainPicList;
        LayoutInflater inf;

        public MyGridViewAdapter(Context context, int layout, List<String> tripMainPicList){
            this.context = context;
            this.layout = layout;
            this.tripMainPicList = tripMainPicList;
            inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return tripMainPicList.size();
        }

        @Override
        public Object getItem(int position) {
            return tripMainPicList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = inf.inflate(layout, null);
            }
            ImageView iv = (ImageView)convertView.findViewById(R.id.ivTripMainPic);

            Glide.with(activity_mypage.this).load(tripMainPicList.get(position)).into(iv);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return convertView;
        }
    }
    /*

    //엑티비티 안보이면 종료
    @Override
    public void onPause() {
        super.onPause();

        // Remove the activity when its off the screen
        finish();
    }*/
}