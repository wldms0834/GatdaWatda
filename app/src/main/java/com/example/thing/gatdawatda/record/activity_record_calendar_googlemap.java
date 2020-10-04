package com.example.thing.gatdawatda.record;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.thing.gatdawatda.R;
import com.example.thing.gatdawatda.plan.activity_my_plan;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.example.thing.gatdawatda.mypage.activity_mypage.user_id;

//구글 지도 검색 참고 사이트
//https://m.blog.naver.com/qbxlvnf11/221183308547
//https://developer88.tistory.com/129?category=220428

public class activity_record_calendar_googlemap extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Marker currentMarker = null;

    private GoogleMap mMap;
    private Geocoder geocoder;
    private ImageView button;
    private EditText editText;
    private Location location;

    Location mCurrentLocatiion;
    LatLng currentPosition;

    private Marker clickmareker = null;
    Intent intent;

    private boolean moveonce = true;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    View gmap_layout;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        geocoder = new Geocoder(this);
        editText = (EditText) findViewById(R.id.editText);
        button = (ImageView) findViewById(R.id.button);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)//1초
                .setFastestInterval(500);//0.5초


        gmap_layout = findViewById(R.id.gmap_layout);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        button.setOnClickListener(buttonClickListener);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            String str = editText.getText().toString();
            List<Address> addressList = null;
            try {
                // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
                addressList = geocoder.getFromLocationName(
                        str, // 주소
                        10); // 최대 검색 결과 개수
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addressList!=null) {
                if(addressList.size() !=0){

                    // 콤마를 기준으로 split
                    String[] splitStr = addressList.get(0).toString().split(",");
                    String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1, splitStr[0].length() - 2); // 주소

                    String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
                    String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도

                    // 좌표(위도, 경도) 생성
                    LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

                    // 마커 생성
                    MarkerOptions mOptions2 = new MarkerOptions();
                    mOptions2.title("search result");
                    mOptions2.snippet(address);
                    mOptions2.position(point);
                    // 마커 추가
                    mMap.addMarker(mOptions2);
                    // 해당 좌표로 화면 줌
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
                }else{
                    Toast.makeText(getApplicationContext(), "주소를 못찾겠습니다. 더 자세하게 입력해보세요", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "주소를 못찾겠습니다. 더 자세하게 입력해보세요", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;


        // 맵 터치 이벤트 구현 //
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if (clickmareker != null) clickmareker.remove();
                Toast.makeText(activity_record_calendar_googlemap.this, "마커를 클릭하세요~", Toast.LENGTH_LONG).show();
                MarkerOptions mOptions = new MarkerOptions();
                // 마커 타이틀
                mOptions.title("마커 좌표");
                Double latitude = point.latitude; // 위도
                Double longitude = point.longitude; // 경도
                // 마커의 스니펫(간단한 텍스트) 설정
                mOptions.snippet(latitude.toString() + ", " + longitude.toString());
                // LatLng: 위도 경도 쌍을 나타냄
                mOptions.position(new LatLng(latitude, longitude));
                // 마커(핀) 추가
                clickmareker = googleMap.addMarker(mOptions);
            }
        });


        //현위치 가져오기
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                Snackbar.make(gmap_layout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(activity_record_calendar_googlemap.this, REQUIRED_PERMISSIONS, 100);
                    }
                }).show();

            } else {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, 100);
            }
        }

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setOnMarkerClickListener(this);
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);
                mCurrentLocatiion = location;
                if (moveonce) {
                    moveonce = false;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 17));
                }
            }
        }
    };

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
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (currentMarker != null) currentMarker.remove();
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        markerOptions.draggable(true);


        currentMarker = mMap.addMarker(markerOptions);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(this);
        alert_confirm.setMessage("이곳으로 정하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Intent getintent = getIntent();
                                        Log.d("debug5.7","log : "+getintent.getStringExtra("fromwhere"));
                                        if(getintent.getStringExtra("fromwhere")!=null){
                                            if (getintent.getStringExtra("fromwhere").equals("record_spot")) {
                                                String spotID = getintent.getStringExtra("spotID");
                                                intent = new Intent(activity_record_calendar_googlemap.this, activity_record_spot.class);
                                                intent.putExtra("clickspot", spotID);
                                                intent.putExtra("markerlatitude", marker.getPosition().latitude);
                                                intent.putExtra("markerlongitude", marker.getPosition().longitude);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else if(getintent.getStringExtra("fromwhere").equals("my_plan")) {
                                                String planID = getintent.getStringExtra("planID");
                                                String spotID = getintent.getStringExtra("spotID");
                                                GeoPoint geo = new GeoPoint(marker.getPosition().latitude,marker.getPosition().longitude);
                                                firebaseFirestore.collection("Spot").document(spotID).update("location", geo);
                                                intent = new Intent(activity_record_calendar_googlemap.this, activity_my_plan.class);
                                                intent.putExtra("planID",planID);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                        else if (document.getString("istraveling") == "") {
                                            intent = new Intent(activity_record_calendar_googlemap.this, activity_record_calendar.class);
                                            intent.putExtra("markerlatitude", marker.getPosition().latitude);
                                            intent.putExtra("markerlongitude", marker.getPosition().longitude);
                                            intent.putExtra("spotID", marker.getSnippet());
                                           //Toast.makeText(activity_record_calendar_googlemap.this, marker.getSnippet(), Toast.LENGTH_LONG).show();
                                            startActivity(intent);
                                            finish();
                                        }
                                    } else {
                                        Log.d("aa", "No such document");
                                    }
                                } else {
                                    Log.d("aa", "get failed with ", task.getException());
                                }
                            }
                        });

                    }
                }).setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();

        return false;
    }
}