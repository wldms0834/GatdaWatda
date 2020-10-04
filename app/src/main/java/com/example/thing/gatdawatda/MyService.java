package com.example.thing.gatdawatda;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.thing.gatdawatda.mypage.activity_mypage.user_id;

public class MyService extends Service {

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    Location mCurrentLocation;
    Location mPastLocation;
    int checkhowlong =0;
    Long SpotNum;
    String TripID;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @SuppressLint({"MissingPermission", "RestrictedApi"})
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        Log.d("debug5.7", "서비스 실행");
        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000 * 60 * 10)//1시간
                .setFastestInterval(1000 * 60 * 10);//1시간
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        getDataFromFirebase();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스가 종료될 때 실행
        Log.d("debug5.7", "서비스의 onDestroy");
    }

    void test1(final double mlatitude, final double mlongitude) {
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
            Map<String, Object> spotnum = new HashMap<>();
            gps.put("spotNum", SpotNum);
        }
        mPastLocation = mCurrentLocation;
    }

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
                test1(mLatitude, mLongitude);
            }
        }
    };
    void getDataFromFirebase(){
        FirebaseFirestore.getInstance().collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    TripID= task.getResult().getString("istraveling");
                    SpotNum=task.getResult().getLong("spotNum");
                }
            }
        });
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

}
