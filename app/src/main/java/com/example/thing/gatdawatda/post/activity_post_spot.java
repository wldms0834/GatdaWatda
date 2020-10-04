package com.example.thing.gatdawatda.post;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thing.gatdawatda.R;
import com.example.thing.gatdawatda.home.activity_home;
import com.example.thing.gatdawatda.mypage.activity_mypage;
import com.example.thing.gatdawatda.plan.activity_plan;
import com.example.thing.gatdawatda.record.activity_record;
import com.example.thing.gatdawatda.record.item_record_spot_list_pic_comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.thing.gatdawatda.mypage.activity_mypage.user_id;

public class activity_post_spot extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "activity_record_spot";
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    Intent intent;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_MAIN_PIC = 2;
    private File tempFile;
    private ArrayList<item_record_spot_list_pic_comment> mArrayList;
    private adapter_post_spot mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    TextView UserSpotAddress, UserSpotName, tvTripName, tvSpotLike;
    ImageView spotMainPic, ivSpotLike;
    Button btAddPic;
    Geocoder geocoder;

    public static Bitmap albumpicture;
    View mLayout;

    private ImageView setupImage;
    private Uri mainImageURI = null;
    private File MainPicFile;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Bitmap compressedImageFile;
    private Long tempSpotNum;
    private Long SpotNum;
    private Long TripNum;
    private String spot_id;
    private boolean isLiked;
    ArrayList<String> spotlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_spot);
        geocoder = new Geocoder(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        // ↓툴바에 홈버튼을 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // ↓툴바의 홈버튼의 이미지를 변경(기본 이미지는 뒤로가기 화살표)
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);

        intent = getIntent();
        spot_id = intent.getStringExtra("spot_id");
        //Toast.makeText(activity_post_spot.this, "post_spot" + spot_id, Toast.LENGTH_LONG).show();
        init();

        datafromfirebase();
        showlikenum();
        ivSpotLike.setOnClickListener(this);

    }


    private void init() {
        mLayout = findViewById(R.id.layout_post_spot);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_post_spot);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mArrayList = new ArrayList<>();
        mAdapter = new adapter_post_spot(mArrayList);
        mRecyclerView.setAdapter(mAdapter);
        UserSpotAddress = findViewById(R.id.UserSpotAddress);
        UserSpotName = findViewById(R.id.UserSpotName);
        spotMainPic = findViewById(R.id.spotMainPic);
        tvTripName = findViewById(R.id.tvTripName);
        tvSpotLike = findViewById(R.id.tv_post_spot_like);
        GradientDrawable drawable =
                (GradientDrawable) this.getDrawable(R.drawable.imageview_rounding);
        spotMainPic.setBackground(drawable);
        spotMainPic.setClipToOutline(true);

        ivSpotLike = findViewById(R.id.iv_post_spot_like);

        //네비게이션
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mNavigationListener);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        isLiked = false;
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

    }


    //바텀네비게이션
    BottomNavigationView.OnNavigationItemSelectedListener mNavigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    intent = new Intent(com.example.thing.gatdawatda.post.activity_post_spot.this, activity_home.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.nav_plan:
                    intent = new Intent(com.example.thing.gatdawatda.post.activity_post_spot.this, activity_plan.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.nav_record:
                    intent = new Intent(com.example.thing.gatdawatda.post.activity_post_spot.this, activity_record.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.nav_my_page:
                    intent = new Intent(com.example.thing.gatdawatda.post.activity_post_spot.this, activity_mypage.class);
                    startActivity(intent);
                    finish();
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Uri photoUri = data.getData();
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            assert photoUri != null;
            cursor = getContentResolver().query(photoUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            tempFile = new File(((Cursor) cursor).getString(column_index));
            //Toast.makeText(getApplicationContext(), "1 : " + tempFile, Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        MainPicFile = tempFile;
        albumpicture = BitmapFactory.decodeFile(tempFile.getAbsolutePath());

        if (requestCode == PICK_FROM_ALBUM) {
            item_record_spot_list_pic_comment newitem = new item_record_spot_list_pic_comment(albumpicture, "호주 여행중~");
            mArrayList.add(newitem);//RecyclerView 마지막에 삽입
            mAdapter.notifyDataSetChanged();//변경된 데이터를 화면에 반영
        } else if (requestCode == PICK_MAIN_PIC) {
            spotMainPic.setImageBitmap(albumpicture);
        }

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            if (tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e(TAG, tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }
        }
    }

    private boolean checkPermission() {
        int hasReadPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int hasWritePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasReadPermission == PackageManager.PERMISSION_GRANTED &&
                hasWritePermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_post_spot_like:
                if (isLiked) {
                    isLiked = false;
                    spotlist.remove(spot_id);
                    ivSpotLike.setImageResource(R.drawable.ic_favorite_border_24dp);
                } else {
                    isLiked = true;
                    spotlist.add(spot_id);
                    ivSpotLike.setImageResource(R.drawable.ic_favorite_pink_24dp);
                }
                firebaseFirestore.collection("spot_like").document(user_id).update("spotlist", spotlist);
                Log.d("aaaa", "onCLick break문");
                showlikenum();

                break;
        }
    }

    void datafromfirebase() {
        firebaseFirestore.collection("Spot").document(spot_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                GeoPoint spotloc = task.getResult().getGeoPoint("location");
                double latitude = spotloc.getLatitude();
                double longitude = spotloc.getLongitude();
                List<Address> list = null;
                try {
                    list = geocoder.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (list != null) {
                    if (list.size() == 0) {
                        UserSpotAddress.setText("주소없음 [" + latitude + ", " + longitude + "]");
                    } else {
                        UserSpotAddress.setText(list.get(0).getAddressLine(0));
                    }
                }

                String spotmainpicURI = document.getString("spotmainpic");
                Picasso.get().load(spotmainpicURI).into(spotMainPic);
                String TripID = document.getString("trip_id");
                String name = document.getString("name");
                UserSpotName.setText(name);

                firebaseFirestore.collection("Trip").document(TripID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                String tripName = task.getResult().getString("name");
                                tvTripName.setText(tripName);
                            }
                        }
                    }
                });

                ArrayList<String> piclist = (ArrayList<String>) document.get("spotPictures");
                ArrayList<String> commentlist = (ArrayList<String>) document.get("spotComments");

                if (piclist != null && commentlist != null) {
                    for (int i = 0; i < piclist.size(); i++) {
                        mArrayList.add(new item_record_spot_list_pic_comment(piclist.get(i), commentlist.get(i)));//RecyclerView 마지막에 삽입
                        mAdapter.notifyDataSetChanged();//변경된 데이터를 화면에 반영
                    }
                }

            }
        });
        Log.d("aaaa", "firebase에서 데이터찾기");
        firebaseFirestore.collection("spot_like").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        spotlist = (ArrayList<String>) document.get("spotlist");
                        for (String s : spotlist) {
                            if (s.equals(spot_id)) {
                                isLiked = true;
                                ivSpotLike.setImageResource(R.drawable.ic_favorite_pink_24dp);
                                break;
                            }
                        }
                    } else {
                        Log.d("aaaa", "document 없음");
                        Map<String, List<String>> newspot = new HashMap<>();
                        List<String> newspotlist = new ArrayList<>();
                        newspot.put("spotlist", newspotlist);
                        firebaseFirestore.collection("spot_like").document(user_id).set(newspot);
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

    void showlikenum() {
        final int[] num = {0};
        firebaseFirestore.collection("spot_like").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.exists()){
                            if(document.get("spotlist")!=null){
                                ArrayList<String> spotlist = (ArrayList<String>)document.get("spotlist");
                                if(spotlist.contains(spot_id)){
                                    num[0]++;
                                }
                            }
                        }
                    }
                    tvSpotLike.setText(String.valueOf(num[0]));

                }
            }
        });
    }
}


