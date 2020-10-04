package com.example.thing.gatdawatda.plan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.thing.gatdawatda.R;
import com.example.thing.gatdawatda.home.activity_home;
import com.example.thing.gatdawatda.mypage.activity_mypage;
import com.example.thing.gatdawatda.record.activity_record;
import com.example.thing.gatdawatda.record.activity_record_calendar;
import com.example.thing.gatdawatda.record.activity_record_calendar_googlemap;
import com.example.thing.gatdawatda.record.adapter2_record;
import com.example.thing.gatdawatda.record.item_record_recycler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.thing.gatdawatda.mypage.activity_mypage.user_id;

public class activity_my_plan extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, rv_refresh {

    //파이어베이스
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;


    //Recyclerview
    RecyclerView rv;
    List<item_record_recycler> recyclelist = new ArrayList<>();
    static adapter1_plan rvadapter;
    static adapter2_record rv2adapter;

    //구글맵
    GoogleMap mMap;
    private LatLng startLatLng = new LatLng(0, 0);        //polyline 시작점
    private LatLng endLatLng = new LatLng(0, 0);        //polyline 끝점

    Intent intent;

    private String planID;
    private String planName;
    ArrayList<String> planSpotIDList = new ArrayList<>();

    Map<String, GeoPoint> id_location = new HashMap<>();
    TextView tvTest;
    Button bt_addspot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getIntent();
        planID = intent.getStringExtra("planID");
        //datafromfirebase();
        setContentView(R.layout.activity_my_plan);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        // ↓툴바에 홈버튼을 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // ↓툴바의 홈버튼의 이미지를 변경(기본 이미지는 뒤로가기 화살표)
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);

        mInit();

    }

    void datafromfirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        firebaseFirestore.collection("Plan").document(planID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        planName = document.getString("name");
                        planSpotIDList = (ArrayList<String>) document.get("plan_spot");
                        Log.d("cccc", "datafromfirebase_planSpotIDList.size() : " + planSpotIDList.size());
                        setRecyclerView();
                        tvTest.setText(planName);

                    }
                }
            }
        });
    }


    void setRecyclerView() {
        rv = findViewById(R.id.record_spot_recycler);
        rv.setHasFixedSize(true);
        Log.d("cccc", "setRecyclerView_planSpotIDList.size() : " + planSpotIDList.size());
        final int[] aaa = {0};
        for (int i = 0; i < planSpotIDList.size(); i++) {
            Log.d("cccc", "planID : " + planSpotIDList.get(i));
            firebaseFirestore.collection("Spot").document(planSpotIDList.get(i)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String spotimage = document.getString("spotmainpic");
                            GeoPoint spotloc = document.getGeoPoint("location");
                            id_location.put(document.getId(), spotloc);
                            double latitude = spotloc.getLatitude();
                            double longitude = spotloc.getLongitude();
                            String spotlocation = spotloc.getLatitude() + "," + spotloc.getLongitude();
                            String name = document.getString("name");
                            Log.d("cccc", "task exists : ID = " + document.getId());
                            aaa[0]++;
                            Log.d("cccc", "for문 숫자" + aaa[0]);
                            recyclelist.add(new item_record_recycler(document.getId(), spotimage, name, spotlocation, latitude, longitude));
                            //              Log.d("cccc", " planSpotIDList의 사이즈"+ planSpotIDList.size());
                            //             Log.d("cccc", " finalI 사이즈"+ finalI);
                            if (aaa[0] == planSpotIDList.size()) {
                                List<item_record_recycler> templist = new ArrayList<>();
                                Log.d("cccc", "for문 2개 시작합니다...");
                                for (String s : planSpotIDList) {
                                    Log.d("cccc", "planSpotlist의 아이디" + s);
                                    for (int j = 0; j < recyclelist.size(); j++) {
                                        Log.d("cccc", "recyclelist의 아이디" + recyclelist.get(j).getID());
                                        if (s.equals(recyclelist.get(j).getID())) {
                                            templist.add(recyclelist.get(j));
                                            Log.d("cccc", "2중for문의 if문" + templist.get(templist.size() - 1).getID());
                                        }
                                    }
                                }
                                recyclelist = templist;
                                retrieveLocation();
                                setAdapter();
                            }
                        }
                    }
                }
            });
        }
    }

    void setAdapter() {
        rvadapter = new adapter1_plan(activity_my_plan.this, recyclelist);
        rv.setLayoutManager(new LinearLayoutManager(activity_my_plan.this, LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(rvadapter);
        ItemTouchHelper.Callback callback = new ItemMove_plan(rvadapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);

    }

    public void mInit() {
        tvTest = (TextView) findViewById(R.id.tvTest);
        bt_addspot = findViewById(R.id.bt_add_spot);

        //스팟추가버튼클릭리스너 아직 구현안함.
        bt_addspot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> newspot = new HashMap<>();
                newspot.put("isSpot", false);//업로드하면 true로 바꾸기
                GeoPoint latLng = new GeoPoint(0, 0);
                newspot.put("location", latLng);
                //아래는 디폴트이미지 만들어 넣어야함.
                newspot.put("spotmainpic", "https://firebasestorage.googleapis.com/v0/b/gatawata-8c561.appspot.com/o/201506181360657663_1.jpg?alt=media&token=502f8f6f-5377-4925-b893-77eca28c64ee");
                newspot.put("spotNum", 51);
                newspot.put("trip_id", planID);
                newspot.put("user_id", user_id);
                newspot.put("name", "");
                newspot.put("country", "대한민국");
                String spotID = firebaseFirestore.collection("Spot").document().getId();
                firebaseFirestore.collection("Spot").document(spotID).set(newspot);
                planSpotIDList.add(spotID);
                firebaseFirestore.collection("Plan").document(planID).update("plan_spot", planSpotIDList);
                intent = new Intent(activity_my_plan.this, activity_record_calendar_googlemap.class);
                intent.putExtra("fromwhere", "my_plan");
                intent.putExtra("spotID", spotID);
                intent.putExtra("planID", planID);
                startActivity(intent);
                finish();
            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map_plan);
        mapFragment.getMapAsync(this);

        //네비게이션
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mNavigationListener);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        datafromfirebase();
    }

    private void drawPath() {        //polyline을 그려주는 메소드
        LatLng zero = new LatLng(0, 0);        //polyline 시작점
        if (!startLatLng.equals(zero)) {
            PolylineOptions options = new PolylineOptions().add(startLatLng).add(endLatLng).width(7).color(ResourcesCompat.getColor(getResources(), R.color.pointColor, null)).geodesic(true);
            mMap.addPolyline(options);
        }
    }

    public void retrieveLocation() {
        mMap.clear();
        final int[] ids = {R.drawable.ic_marker1, R.drawable.ic_marker2, R.drawable.ic_marker3, R.drawable.ic_marker4, R.drawable.ic_marker5, R.drawable.ic_marker6, R.drawable.ic_marker7, R.drawable.ic_marker8, R.drawable.ic_marker9, R.drawable.ic_marker10, R.drawable.ic_marker11,R.drawable.ic_marker12,R.drawable.ic_marker13,R.drawable.ic_marker14,R.drawable.ic_marker15,R.drawable.ic_marker16,R.drawable.ic_marker17,R.drawable.ic_marker18,R.drawable.ic_marker19,R.drawable.ic_marker20,};
        for (int i = 0; i < planSpotIDList.size(); i++) {
            final int finalI = i;
            GeoPoint geo = id_location.get(planSpotIDList.get(i));
            double lat = geo.getLatitude();
            double lng = geo.getLongitude();
            LatLng latLng = new LatLng(lat, lng);
            Bitmap markericon = getBitmap(ids[finalI]);
            mMap.addMarker(new MarkerOptions().position(latLng).snippet(planSpotIDList.get(i)).icon(BitmapDescriptorFactory.fromBitmap(markericon)));
            endLatLng = new LatLng(lat, lng);
            drawPath();
            startLatLng = endLatLng;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng, 16));
        }
    }

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @Override
    public void rvclick(String docname, double latitude, double longitude) {
        Log.d("cccc", "plan ID : " + planSpotIDList);
        for (int i = 0; i < recyclelist.size(); i++) {
            Log.d("cccc", "recycler" + recyclelist.get(i).getID());
        }
        //intent = new Intent(activity_my_plan.this, activity_record_spot.class);
        //intent.putExtra("markerlatitude", latitude);
        //intent.putExtra("markerlongitude", longitude);
        //startActivity(intent);
    }

    @Override
    public void rvdelete(int spotposition, String spot_id) {
        recyclelist.remove(spotposition);
        rv.getAdapter().notifyDataSetChanged();
        planSpotIDList.remove(spot_id);
        retrieveLocation();
        firebaseFirestore.collection("Plan").document(planID).update("plan_spot", planSpotIDList);
    }

    @Override
    public void clearMap() {
        startLatLng = new LatLng(0, 0);
        endLatLng = new LatLng(0, 0);
        mMap.clear();
    }

    //changeDatabase코드 전반적 수정 필요

    public void changeDatabase(final int datafrom, final int datato) {
        Collections.swap(planSpotIDList, datafrom, datato);
        firebaseFirestore.collection("Plan").document(planID).update("plan_spot", planSpotIDList);
        clearMap();
        retrieveLocation();
    }

    void setListViewHeightBasedOnItems(RecyclerView lv, int size) {
        if (lv == null) return;
        ViewGroup.LayoutParams params = lv.getLayoutParams();
        params.width = 400 * size;
        lv.setLayoutParams(params);
        lv.requestLayout();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mNavigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    intent = new Intent(activity_my_plan.this, activity_home.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.nav_plan:
                    intent = new Intent(activity_my_plan.this, activity_plan.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.nav_record:
                    firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().getString("istraveling").equals("")) {
                                    Intent intent2 = new Intent(activity_my_plan.this, activity_record_calendar.class);
                                    startActivity(intent2);
                                    finish();
                                } else {
                                    Intent intent2 = new Intent(activity_my_plan.this, activity_record.class);
                                    startActivity(intent2);
                                    finish();
                                }
                            }
                        }
                    });
                    break;

                case R.id.nav_my_page:
                    intent = new Intent(activity_my_plan.this, activity_mypage.class);
                    startActivity(intent);
                    finish();
                    break;
            }
            return true;
        }
    };


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
}


interface rv_refresh {
    void rvclick(String docname, double latitude, double longitude);

    void rvdelete(int spotposition, String spot_id);

    void clearMap();

    void retrieveLocation();

    void changeDatabase(int from, int to);
}