package com.example.thing.gatdawatda.record;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thing.gatdawatda.R;
import com.example.thing.gatdawatda.home.activity_home;
import com.example.thing.gatdawatda.mypage.activity_mypage;
import com.example.thing.gatdawatda.plan.activity_plan;
import com.example.thing.gatdawatda.post.activity_post;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;

import id.zelory.compressor.Compressor;

import static com.example.thing.gatdawatda.mypage.activity_mypage.user_id;

//activity_record
/*
* activity_record_spot
* activity_record_calendar_googlemap
*
*
*
* */

public class activity_record extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, rv_refresh {
    Intent intent;
    GoogleMap mMap;

    private Toolbar my_toolbar;

    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    static final int PICK_TRIP_MAIN = 1;
    boolean needRequest = false;
    private Boolean isFirstLocationFirstLoad = true;

    boolean moveCamOnce = true;
    static int checkhowlong = 0;

    File tripmainpic;
    LatLng currentLatLng;
    Date startDate;
    private LatLng startLatLng = new LatLng(0, 0);        //polyline 시작점
    private LatLng endLatLng = new LatLng(0, 0);        //polyline 끝점
    Location mCurrentLocation;
    Location mPastLocation = new Location(LocationManager.GPS_PROVIDER);

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    LocationManager lm;
    private Marker currentMarker = null;

    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.

    TextView tvAttraction, tvWeather, tvDday, tvDateMessage;
    ImageView ivTripmain;
    Button bt_addspot;
    RecyclerView rv;
    RecyclerView rv2;
    List<item_record_recycler> recyclelist = new ArrayList<>();

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    //private long tempSpotNum;
    private long SpotNum;
    private long TripNum;
    private String TripID;
    FirebaseFirestore mFirestore;
    static adapter1_record rvadapter;
    static adapter2_record rv2adapter;

    Bitmap tripmainbitmap;
    LayoutInflater sendinf;
    View send_layout;
    boolean mapready = false;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDataFromFirebase();
        setContentView(R.layout.activity_record);

        mInit();

       // tvDday.setText();

        my_toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(my_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000 )//1초
                .setFastestInterval(1000);//1초

        //set_sendLayout();
        //.setInterval(1000)//1초
        //.setFastestInterval(500);//0.5초
    }

    void set_sendLayout() {

    }

    void setRecyclerView() {
        rv = findViewById(R.id.record_spot_recycler);
        rv2 = findViewById(R.id.record_rv2);
        rv.setHasFixedSize(true);
        rv2.setHasFixedSize(true);
        mFirestore = FirebaseFirestore.getInstance();
        recyclelist.clear();
        final Query mquery = firebaseFirestore.collection("Spot").orderBy("spotNum", Query.Direction.ASCENDING);
        mquery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // query.orderBy("time",Query.Direction.DESCENDING);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getString("user_id").equals(user_id) && document.getString("trip_id").equals(TripID)) {
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
        rvadapter = new adapter1_record(activity_record.this, recyclelist);
        rv.setLayoutManager(new LinearLayoutManager(activity_record.this, LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(rvadapter);
        ItemTouchHelper.Callback callback = new ItemMove_rv1(rvadapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rv);

        rv2adapter = new adapter2_record(activity_record.this, recyclelist);
        rv2.setLayoutManager(new LinearLayoutManager(activity_record.this, LinearLayoutManager.HORIZONTAL, false));
        rv2.setAdapter(rv2adapter);
        ItemTouchHelper.Callback callback2 = new ItemMove_rv2(rv2adapter);
        ItemTouchHelper touchHelper2 = new ItemTouchHelper(callback2);
        touchHelper2.attachToRecyclerView(rv2);
        setListViewHeightBasedOnItems(rv2, recyclelist.size());
    }

    public void mInit() {
        mLayout = findViewById(R.id.layout_main);
        tvAttraction = findViewById(R.id.tvAttraction);
        bt_addspot = findViewById(R.id.bt_add_spot);
        tvDateMessage = findViewById(R.id.tvDateMessage);
        tvDday = findViewById(R.id.tvDday);



        //스팟추가버튼 누르면 데이터베이스에 새로운 스팟 바로 생성 //취소했을땐 어쩌지?
        bt_addspot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> spotInfo = new HashMap<>();
                spotInfo.put("isSpot", false);//업로드하면 true로 바꾸기
                GeoPoint latLng = new GeoPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getAltitude());
                spotInfo.put("location", latLng);
                //아래는 디폴트이미지 만들어 넣어야함.
                spotInfo.put("spotmainpic", "https://firebasestorage.googleapis.com/v0/b/gatawata-8c561.appspot.com/o/spot_mainImages%2FLSsiElaEEIUK2JqkjKJjG8qjdA82(17).jpg?alt=media&token=361ac833-3bab-4861-ba11-eeff53dbf87a");
                spotInfo.put("spotNum", ++SpotNum);
                spotInfo.put("trip_id", TripID);
                spotInfo.put("name", "이름을 설정해주세요.");
                spotInfo.put("country", getcountry(latLng));

                spotInfo.put("user_id", user_id);
                //Spot중 spotPictures필드 배열로 만들기
                List<String> pictures = new ArrayList<>();
                //아래는 선택한 pictures를 반복문으로 넣어줘야함.
                pictures.add("https://firebasestorage.googleapis.com/v0/b/gatawata-8c561.appspot.com/o/spot_mainImages%2FLSsiElaEEIUK2JqkjKJjG8qjdA82(17).jpg?alt=media&token=361ac833-3bab-4861-ba11-eeff53dbf87a");
                spotInfo.put("spotPictures", pictures);
                String spotID = firebaseFirestore.collection("Spot").document().getId();
                Log.d("aaaa","버튼클릭 : 스팟아이디"+spotID);
                firebaseFirestore.collection("Spot").document(spotID).set(spotInfo);
                firebaseFirestore.collection("Users").document(user_id).update("spotNum",SpotNum);

                intent = new Intent(activity_record.this, activity_record_spot.class);
                intent.putExtra("clickspot",spotID);
                startActivity(intent);
            }
        });
        intent = getIntent();

        lm = (LocationManager) getSystemService((Context.LOCATION_SERVICE));
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //네비게이션
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mNavigationListener);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
    }

    //현 위치 가져오는 코드
    LocationCallback locationCallback = new LocationCallback() {
        double mRound(double a) {
            return Math.round(a * 10000) / 10000.0;
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                Location location = locationList.get(locationList.size() - 1);
                mCurrentLocation = location;
                double mLatitude = mRound(mCurrentLocation.getLatitude());
                double mLongitude = mRound(mCurrentLocation.getLongitude());
                currentLatLng = new LatLng(mLatitude, mLongitude);

                if (currentMarker != null) currentMarker.remove();
                MarkerOptions markerOptions = new MarkerOptions();
                String markerTitle = getCurrentAddress(currentLatLng);
                markerOptions.position(currentLatLng);
                markerOptions.title(markerTitle);
                markerOptions.snippet("currentposition");
                markerOptions.zIndex(10);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.default_image));
                currentMarker = mMap.addMarker(markerOptions);
                if (moveCamOnce) {
                    //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14));

                    moveCamOnce = false;
                }
                //makespot(mLatitude, mLongitude);
            }
        }
    };

    private void startLocationUpdates() {
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);
            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            if (checkPermission())
                mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        setDefaultLocation();

        if (checkPermission()) {
            startLocationUpdates();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(activity_record.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            } else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                /*
                if (!marker.getSnippet().equals("currentposition")) {
                    intent = new Intent(activity_record.this, activity_record_spot.class);
                    intent.putExtra("markerlatitude", marker.getPosition().latitude);
                    intent.putExtra("markerlongitude", marker.getPosition().longitude);
                    intent.putExtra("spotID", marker.getSnippet());
                    Toast.makeText(activity_record.this, marker.getSnippet(), Toast.LENGTH_LONG).show();
                    startActivity(intent);
                }
                */
                //미구현 . 삭제하기
                return false;
            }
        });

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
        mapready = true;
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
    protected void onStart() {
        super.onStart();
        if (checkPermission()) {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            if (mMap != null)
                mMap.setMyLocationEnabled(true);
        }
    }

    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;
            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }
            if (check_result) {
                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                } else {
                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }
            }
        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity_record.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, 2001);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
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
            case PICK_TRIP_MAIN:
                if (resultCode == RESULT_OK) {
                    Uri photoUri = data.getData();
                    Cursor cursor = null;
                    File tempFile;
                    try {
                        String[] proj = {MediaStore.Images.Media.DATA};
                        assert photoUri != null;
                        cursor = getContentResolver().query(photoUri, proj, null, null, null);
                        assert cursor != null;
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        tempFile = new File(((Cursor) cursor).getString(column_index));
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                    tripmainpic = tempFile;
                    tripmainbitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                    Toast.makeText(getApplicationContext(), "filepath : " + tripmainbitmap, Toast.LENGTH_LONG).show();
                    ivTripmain.setImageBitmap(tripmainbitmap);
                    break;
                }
            case RESULT_CANCELED:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.record_toolbar, menu);
        return true;
    }

    //send버튼
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send_button: {
                Log.d("aa", "send버튼 클릭");

                sendinf = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
                send_layout = sendinf.inflate(R.layout.activity_record_send, (ViewGroup) findViewById(R.id.record_send_layout));
                ivTripmain = send_layout.findViewById(R.id.iv_send_pic);
                final EditText et_name = send_layout.findViewById(R.id.tv_send_name);
                final EditText et_tag = send_layout.findViewById(R.id.tv_send_tag);
                AlertDialog.Builder aDialog = new AlertDialog.Builder(this);
                aDialog.setView(send_layout);

                ivTripmain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(intent, PICK_TRIP_MAIN);
                    }
                });
                aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Map<String, Object> tripupload = new HashMap<>();
                        tripupload.put("name", et_name.getText().toString());
                        tripupload.put("description", et_tag.getText().toString());
                        tripupload.put("isComplete", true);
                        tripupload.put("uploadTime", new Date());
                        if (tripmainpic == null) {
                            Toast.makeText(activity_record.this, "사진을 선택해주세요", Toast.LENGTH_SHORT).show();
                        } else {
                            File newImageFile = new File(tripmainpic.getAbsolutePath());
                            Bitmap compressedImageFile = null;

                            try {
                                compressedImageFile = new Compressor(activity_record.this)
                                        .setMaxHeight(500)
                                        .setMaxWidth(500)
                                        .setQuality(50)
                                        .compressToBitmap(newImageFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] thumbData = baos.toByteArray();
                            UploadTask image_path = storageReference.child("trip_mainImages").child(TripID + "jpg").putBytes(thumbData);
                            image_path.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    final Uri download_uri;
                                    download_uri = task.getResult().getDownloadUrl();
                                    tripupload.put("tripMainPic", download_uri.toString());

                                    firebaseFirestore.collection("Trip").document(TripID).update(tripupload);
                                    Toast.makeText(activity_record.this, "Trip Saved Successfully", Toast.LENGTH_SHORT).show();

                                    //User의 isTraveling초기화
                                    Map<String, Object> User = new HashMap<>();
                                    User.put("istraveling", "");
                                    TripNum++;
                                    User.put("tripNum", TripNum);
                                    firebaseFirestore.collection("Users").document(user_id).update(User);

                                    intent = new Intent(activity_record.this, activity_post.class);
                                    intent.putExtra("TripID", TripID);
                                    startActivity(intent);
                                    finish();

                                }
                            });
                        }
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (tripmainpic != null) {
                            tripmainpic = null;
                        }
                    }
                }).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //엑티비티 안보이면 종료
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mapready){
            clearMap();
            retrieveLocation();
            setRecyclerView();
        }
    }

    private void drawPath() {        //polyline을 그려주는 메소드
            LatLng zero = new LatLng(0, 0);        //polyline 시작점
            if (!startLatLng.equals(zero)) {
            PolylineOptions options = new PolylineOptions().add(startLatLng).add(endLatLng).width(7).color(ResourcesCompat.getColor(getResources(),R.color.pointColor,null)).geodesic(true);
            mMap.addPolyline(options);
        }
    }

    //스팟생성
    void makespot(final double mlatitude, final double mlongitude) {
        //locationCallback에서 호출되는 스팟에 얼마나 있는지 확인
        float distance = mPastLocation.distanceTo(mCurrentLocation);
        if (distance < 0.5) {
            checkhowlong += 1;
        }
        if (checkhowlong == 100) {
            checkhowlong = 0;
            ////firebase에 스팟 위치 저장
            Map<String, Object> gps = new HashMap<>();
            gps.put("isSpot", false);
            GeoPoint latLng = new GeoPoint(mlatitude, mlongitude);
            gps.put("location", latLng);
            gps.put("spotmainpic", "https://firebasestorage.googleapis.com/v0/b/gatawata-8c561.appspot.com/o/spot_mainImages%2FLSsiElaEEIUK2JqkjKJjG8qjdA82(21).jpg?alt=media&token=dc8a3cde-a145-4333-bb8a-6350d0c5775e");
            gps.put("spotNum", ++SpotNum);
            gps.put("trip_id", TripID);
            gps.put("user_id", user_id);
            gps.put("name", "스팟의 이름을 설정하세요");
            gps.put("country", getcountry(latLng));
            //firebaseFirestore.collection("Spot").document().set(gps);
            Map<String, Object> spotnum = new HashMap<>();
            gps.put("spotNum", SpotNum);
            //firebaseFirestore.collection("Users").document(user_id).set(spotnum);

        }
        mPastLocation = mCurrentLocation;
        tvWeather.setText("distance" + distance + " howlong = " + checkhowlong);
        // tvAttraction.setText("mlatitude " + mlatitude + "  mlongitude " + mlongitude);
    }

    public String getcountry(GeoPoint latlng) {
        String country = "";
        Geocoder geo = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geo.getFromLocation(latlng.getLatitude(), latlng.getLongitude(), 1);
            if(addresses.isEmpty()) {
                country = "대한민국";
            }else {
                country = addresses.get(0).getCountryName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return country;
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
                    // query.orderBy("time",Query.Direction.DESCENDING);
                    int i =0;
                    int[] ids = {R.drawable.ic_marker1, R.drawable.ic_marker2, R.drawable.ic_marker3, R.drawable.ic_marker4, R.drawable.ic_marker5, R.drawable.ic_marker6, R.drawable.ic_marker7, R.drawable.ic_marker8, R.drawable.ic_marker9, R.drawable.ic_marker10, R.drawable.ic_marker11,R.drawable.ic_marker12,R.drawable.ic_marker13,R.drawable.ic_marker14,R.drawable.ic_marker15,R.drawable.ic_marker16,R.drawable.ic_marker17,R.drawable.ic_marker18,R.drawable.ic_marker19,R.drawable.ic_marker20,};
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Log.d("aa", "trip_id in query : " + document.getString("trip_id"));
                        if (document.getString("user_id").equals(user_id) && document.getString("trip_id").equals(TripID)) {
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
                        }
                    }
                }
            }
        });
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
        intent = new Intent(activity_record.this, activity_record_spot.class);
        intent.putExtra("clickspot", docname);
        intent.putExtra("markerlatitude", latitude);
        intent.putExtra("markerlongitude", longitude);
        startActivity(intent);
    }

    @Override
    public void clearMap() {
        startLatLng = new LatLng(0, 0);
        endLatLng = new LatLng(0, 0);
        mMap.clear();
    }

    private void getDataFromFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        Log.d("aa", "user_id : " + user_id);
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        TripID = document.getString("istraveling");
                        Log.d("aa", "first TripID : " + TripID);
                        SpotNum = document.getLong("spotNum");
                        TripNum = document.getLong("tripNum");
                        Date startDate = document.getDate("startDate");
                        Map<String, Object> data = document.getData();
                        Log.d("aa", "DocumentSnapshot data: " + data);
                        if (TripID.equals("")) {
                            startActivity(new Intent(activity_record.this, activity_record_calendar.class));
                            finish();
                        }
                        setRecyclerView();

                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        // 시간을 나타냇 포맷을 정한다 ( yyyy/MM/dd 같은 형태로 변형 가능 )
                        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy년 MM월 dd일");
                        // 오늘날짜
                        String formatDate = sdfNow.format(date);
                        //String start = sdfNow.format(startDate);
                        tvDateMessage.setText(formatDate);
                        //tvDday.setText(start);
                    }
                    else
                        Log.d("aa", "No such document");
                    }
                 else {
                    Log.d("aa", "get failed with ", task.getException());
                }
            }
        });
        Log.d("aa", "getDatabase() - final TripID : " + TripID);

    }

    @Override
    public void changeDatabase(final int datafrom, final int datato) {
        Log.d("aa", "changeDatabase called");
        Query mquery = firebaseFirestore.collection("Spot").whereEqualTo("trip_id", TripID);
        mquery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    final String[] documentto = new String[1];
                    final String[] documentfrom = new String[1];
                    Log.d("aa", "onComplete : from-to" + datafrom + "=" + datato);
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getLong("spotNum") == datafrom)
                            documentfrom[0] = document.getId();
                        if (document.getLong("spotNum") == datato && !document.getId().equals(documentfrom[0]))
                            documentto[0] = document.getId();
                    }
                    Map<String, Object> objectfrom = new HashMap<>();
                    objectfrom.put("spotNum", datato);
                    firebaseFirestore.collection("Spot").document(documentfrom[0]).update(objectfrom);

                    Map<String, Object> objectto = new HashMap<>();
                    objectto.put("spotNum", datafrom);
                    firebaseFirestore.collection("Spot").document(documentto[0]).update(objectto);
                }
            }

            ;
        });
    }

    @Override
    public void rvdelete(final int spotlocation, String spotID) {
/*
        firebaseFirestore.collection("Spot").document(spotID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                recyclelist.remove(spotlocation);
                rvadapter.notifyDataSetChanged();
                rv2adapter.notifyDataSetChanged();
                setListViewHeightBasedOnItems(rv2,recyclelist.size());
                clearMap();
                retrieveLocation();
            }
        });
        */
    }
    void setListViewHeightBasedOnItems(RecyclerView lv, int size) {
        if (lv == null) return;
        ViewGroup.LayoutParams params = lv.getLayoutParams();
        params.width = 377 * size;
        lv.setLayoutParams(params);
        lv.requestLayout();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mNavigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    intent = new Intent(activity_record.this, activity_home.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.nav_plan:
                    intent = new Intent(activity_record.this, activity_plan.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.nav_record:
                    intent = new Intent(activity_record.this, activity_record.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.nav_my_page:
                    intent = new Intent(activity_record.this, activity_mypage.class);
                    startActivity(intent);
                    finish();
                    break;
            }
            return true;
        }
    };

}

interface rv_refresh {
    void rvclick(String docname, double latitude, double longitude);
    void clearMap();
    void retrieveLocation();
    void rvdelete(int spotposition, String spotID);
    void changeDatabase(int from, int to);
}