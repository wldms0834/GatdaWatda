package com.example.thing.gatdawatda.post;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.thing.gatdawatda.R;
import com.example.thing.gatdawatda.home.activity_home;
import com.example.thing.gatdawatda.mypage.activity_mypage;
import com.example.thing.gatdawatda.plan.activity_plan;
import com.example.thing.gatdawatda.record.activity_record;
import com.example.thing.gatdawatda.record.activity_record_calendar;
import com.example.thing.gatdawatda.record.adapter2_record;
import com.example.thing.gatdawatda.record.item_record_recycler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.thing.gatdawatda.mypage.activity_mypage.user_id;

//https://webnautes.tistory.com/647 구글지도
//https://mainia.tistory.com/1616 구글지도 polyline
public class activity_post extends AppCompatActivity implements OnMapReadyCallback, rv_refresh {
    Intent intent;
    GoogleMap mMap;

    private Toolbar my_toolbar;
    boolean needRequest = false;
    private Boolean isFirstLocationFirstLoad = true;

    boolean moveCamOnce = true;
    static int checkhowlong = 0;

    LatLng currentLatLng;
    private LatLng startLatLng = new LatLng(0, 0);        //polyline 시작점
    private LatLng endLatLng = new LatLng(0, 0);        //polyline 끝점
    Location mCurrentLocation;
    Location mPastLocation = new Location(LocationManager.GPS_PROVIDER);

    LocationManager lm;
    private Marker currentMarker = null;

    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.

    TextView CoureseName, CourseGuider, CourseDescription, CourseDate, CourseLikeNum;

    RecyclerView rv;
    RecyclerView rv2;
    List<item_record_recycler> recyclelist = new ArrayList<>();

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    ImageView CourseMainPic, userMainPic;


    //private long tempSpotNum;
    private long SpotNum;
    private long TripNum;
    private String TripID;
    private String GuideID;
    private String spot_id;
    FirebaseFirestore mFirestore;
    static adapter1_post rvadapter;
    static adapter2_record rv2adapter;
    ImageView btLike;
    long countlike;
    boolean like_clicked;
    ArrayList<String> triplist = new ArrayList<>();

    private boolean isMyPost=false;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        intent = getIntent();
        if(intent.getStringExtra("GuideID")!=null) {
            GuideID = intent.getStringExtra("GuideID");
            if(GuideID.equals(user_id))
            {
                isMyPost = true;
            }
        }
        TripID = intent.getStringExtra("TripID");
        mInit();
        setRecyclerView();


        firebaseFirestore.collection("Trip").document(TripID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String tripUserID = task.getResult().getString("user_id");
                        String name = task.getResult().getString("name");
                        String description = task.getResult().getString("description");
                        String tripMainPIC = task.getResult().getString("tripMainPic");
                        countlike = task.getResult().getLong("like");
                        Date startDate = task.getResult().getDate("startDate");
                        Date endDate = task.getResult().getDate("endDate");

                        CoureseName.setText(name);
                        CourseDescription.setText(description);
                        CourseLikeNum.setText(countlike + "");
                        CourseDate.setText((startDate.getYear() + 1900) + "-" + (startDate.getMonth() + 1) + "-" + startDate.getDate() + " ~ "
                                + (endDate.getYear() + 1900) + "-" + (endDate.getMonth() + 1) + "-" + endDate.getDate());

                        RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.aaa);

                        Glide.with(activity_post.this).setDefaultRequestOptions(placeholderRequest).load(tripMainPIC).into(CourseMainPic);
                        CourseMainPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        //CourseMainPic.setScaleType(ImageView.ScaleType.FIT_XY);
                        firebaseFirestore.collection("Users").document(tripUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        String mainPic = task.getResult().getString("image");
                                        String Guider_user_id = task.getResult().getString("name");
                                        CourseGuider.setText("by " + Guider_user_id + " 가이드님");
                                        Glide.with(activity_post.this).load(mainPic).into(userMainPic);
                                    }
                                }
                            }
                        });
                        firebaseFirestore.collection("spot_like").document(tripUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        if (task.getResult().get("planlist") != null) {
                                            triplist = (ArrayList<String>) task.getResult().get("planlist");
                                            if (triplist.contains(TripID)) {
                                                btLike.setImageResource(R.drawable.ic_favorite_pink_24dp);
                                                like_clicked = true;
                                                Log.d("debug5.6", "task.get(planlist) exist -> like clicked");
                                            } else {
                                                btLike.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                                                like_clicked = false;
                                                Log.d("debug5.6", "task.get(planlist) exist -> like not clicked");
                                            }
                                        } else {
                                            Log.d("debug5.6", "task.get(planlist)==null");
                                            btLike.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                                            like_clicked = false;
                                        }

                                    }
                                }
                            }
                        });

                    } else {

                        //String error = task.getException().getMessage();
                        //Toast.makeText(activity_post.this, "trip(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();

                    }
                }
            }
        });

        my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);
        // ↓툴바에 홈버튼을 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // ↓툴바의 홈버튼의 이미지를 변경(기본 이미지는 뒤로가기 화살표)
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);
    }

    void setRecyclerView() {
        rv = findViewById(R.id.post_spot_recycler);
        rv2 = findViewById(R.id.post_rv2);
        rv.setHasFixedSize(true);
        rv2.setHasFixedSize(true);
        final Query mquery = firebaseFirestore.collection("Spot").orderBy("spotNum", Query.Direction.ASCENDING);
        mquery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // query.orderBy("time",Query.Direction.DESCENDING);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (TripID.equals(document.getString("trip_id"))) {
                            spot_id = document.getId();
                            String spotimage = document.getString("spotmainpic");
                            GeoPoint spotloc = document.getGeoPoint("location");
                            double latitude = spotloc.getLatitude();
                            double longitude = spotloc.getLongitude();
                            String spotlocation = spotloc.getLatitude() + "," + spotloc.getLongitude();
                            String name = document.getString("name");
                            recyclelist.add(new item_record_recycler(document.getId(), spotimage, name, spotlocation, latitude, longitude));
                        }
                    }
                    setAdapter();
                }
            }
        });

    }

    void setAdapter() {
        rvadapter = new adapter1_post(activity_post.this, recyclelist);
        rv.setLayoutManager(new LinearLayoutManager(activity_post.this, LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(rvadapter);

        rv2adapter = new adapter2_record(activity_post.this, recyclelist);
        rv2.setLayoutManager(new LinearLayoutManager(activity_post.this, LinearLayoutManager.HORIZONTAL, false));
        rv2.setAdapter(rv2adapter);
        setListViewHeightBasedOnItems(rv2, recyclelist.size());
    }

    public void mInit() {
        mLayout = findViewById(R.id.layout_main);
        CoureseName = findViewById(R.id.CoureseName);
        CourseGuider = findViewById(R.id.CourseGuider);
        CourseDescription = findViewById(R.id.CourseDescription);
        CourseDate = findViewById(R.id.CourseDate);
        CourseLikeNum = findViewById(R.id.CourseLikeNum);
        CourseMainPic = (ImageView) findViewById(R.id.CourseMainPic);
        btLike = (ImageView) findViewById(R.id.btLike);
        userMainPic = (ImageView) findViewById(R.id.userMainPic);

        btLike.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (like_clicked) {
                    firebaseFirestore.collection("Trip").document(TripID).update("like", --countlike).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                btLike.setImageResource(R.drawable.ic_favorite_border_24dp);
                                CourseLikeNum.setText(countlike + "");
                                like_clicked = false;
                                triplist.remove(TripID);
                                firebaseFirestore.collection("spot_like").document(user_id).update("planlist", triplist);
                            }
                        }
                    });
                } else {
                    firebaseFirestore.collection("Trip").document(TripID).update("like", ++countlike).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                btLike.setImageResource(R.drawable.ic_favorite_pink_24dp);
                                CourseLikeNum.setText(countlike + "");
                                like_clicked = true;
                                triplist.add(TripID);
                                ArrayList<String> resultlist = new ArrayList<>();
                                for(String s : triplist){
                                    if(!resultlist.contains(s)){
                                        resultlist.add(s);
                                    }
                                }
                                triplist = resultlist;
                                firebaseFirestore.collection("spot_like").document(user_id).update("planlist", triplist);
                            }
                        }
                    });
                }
            }
        });

        lm = (LocationManager) getSystemService((Context.LOCATION_SERVICE));
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        //네비게이션
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mNavigationListener);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem;
        if(isMyPost){
            menuItem = menu.getItem(3);
        }
        else{
            menuItem = menu.getItem(0);
        }
        menuItem.setChecked(true);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mNavigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    intent = new Intent(activity_post.this, activity_home.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.nav_plan:
                    intent = new Intent(activity_post.this, activity_plan.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.nav_record:
                    firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().getString("istraveling").equals("")) {
                                    Intent intent2 = new Intent(activity_post.this, activity_record_calendar.class);
                                    startActivity(intent2);
                                    finish();
                                } else {
                                    Intent intent2 = new Intent(activity_post.this, activity_record.class);
                                    startActivity(intent2);
                                    finish();
                                }
                            }
                        }
                    });
                    break;

                case R.id.nav_my_page:
                    intent = new Intent(activity_post.this, activity_mypage.class);
                    startActivity(intent);
                    break;
            }
            return true;
        }
    };


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setDefaultLocation();


        // mintime 1000이 1초    minDistance 1이 1미터
        int minTime = 1000, minDistance = 30;
        //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,minTime,minDistance,mLocationListener);
        //lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,minTime,minDistance,mLocationListener);

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
       /* mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (!marker.getSnippet().equals("currentposition")) {
                    intent = new Intent(activity_post.this, activity_post_spot.class);
                    intent.putExtra("spotID", spot_id);
                    Toast.makeText(activity_post.this, marker.getSnippet(), Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
                return false;
            }
        });*/

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
            }
        });


        retrieveLocation();

    }


    public String getCurrentAddress(LatLng latlng) {
        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_SHORT).show();
            return "잘못된 GPS 좌표";
        }
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0);
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void setDefaultLocation() {
        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";

        if (currentMarker != null) currentMarker.remove();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2001:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        needRequest = true;
                        return;
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void drawPath() {        //polyline을 그려주는 메소드
        LatLng zero = new LatLng(0, 0);        //polyline 시작점
        if (!startLatLng.equals(zero)) {
            PolylineOptions options = new PolylineOptions().add(startLatLng).add(endLatLng).width(7).color(ResourcesCompat.getColor(getResources(), R.color.pointColor, null)).geodesic(true);
            mMap.addPolyline(options);
        }
    }

    @Override
    public void retrieveLocation() {
        Log.d("aa", "retrieveLocation 시작");
        final Query query = firebaseFirestore.collection("Spot").orderBy("spotNum", Query.Direction.ASCENDING);//.whereEqualTo("user_id",user_id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("aa", " retrieveLocation 쿼리 task 성공");
                    int i = 0;
                    int[] ids = {R.drawable.ic_marker1, R.drawable.ic_marker2, R.drawable.ic_marker3, R.drawable.ic_marker4, R.drawable.ic_marker5, R.drawable.ic_marker6, R.drawable.ic_marker7, R.drawable.ic_marker8, R.drawable.ic_marker9, R.drawable.ic_marker10, R.drawable.ic_marker11,R.drawable.ic_marker12,R.drawable.ic_marker13,R.drawable.ic_marker14,R.drawable.ic_marker15,R.drawable.ic_marker16,R.drawable.ic_marker17,R.drawable.ic_marker18,R.drawable.ic_marker19,R.drawable.ic_marker20,};
                    // query.orderBy("time",Query.Direction.DESCENDING);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("aa", "trip_id in query : " + document.getString("trip_id"));
                        if (TripID.equals(document.getString("trip_id"))) {
                            GeoPoint geo = document.getGeoPoint("location");
                            Log.d("aa", "retrieveLocation document :" + document.getId());
                            double lat = geo.getLatitude();
                            double lng = geo.getLongitude();
                            LatLng latLng = new LatLng(lat, lng);
                            Bitmap markericon = getBitmap(ids[i]);
                            mMap.addMarker(new MarkerOptions().position(latLng).snippet(document.getId()).icon(BitmapDescriptorFactory.fromBitmap(markericon)));
                            i++;
                            endLatLng = new LatLng(lat, lng);
                            drawPath();
                            startLatLng = endLatLng;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        }
                    }
                }
            }
        });
    }


    @Override
    public void rvclick(String spot_id) {
        intent = new Intent(activity_post.this, activity_post_spot.class);
        intent.putExtra("spot_id", spot_id);
        //Toast.makeText(activity_post.this, spot_id, Toast.LENGTH_LONG).show();
        startActivity(intent);
    }

    @Override
    public void clearMap() {
        startLatLng = new LatLng(0, 0);
        endLatLng = new LatLng(0, 0);
        mMap.clear();
    }

    void setListViewHeightBasedOnItems(RecyclerView lv, int size) {
        if (lv == null) return;
        ViewGroup.LayoutParams params = lv.getLayoutParams();
        params.width = 377 * size;
        lv.setLayoutParams(params);
        lv.requestLayout();
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

    private Bitmap getBitmap(int drawableRes) {
        Drawable drawable = getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}

interface rv_refresh {
    void rvclick(String spot_id);

    void clearMap();

    void retrieveLocation();

}
