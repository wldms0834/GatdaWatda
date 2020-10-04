package com.example.thing.gatdawatda.record;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.applikeysolutions.cosmocalendar.settings.appearance.ConnectedDayIconPosition;
import com.applikeysolutions.cosmocalendar.settings.lists.connected_days.ConnectedDays;
import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.example.thing.gatdawatda.R;
import com.example.thing.gatdawatda.home.activity_home;
import com.example.thing.gatdawatda.mypage.activity_mypage;
import com.example.thing.gatdawatda.plan.activity_plan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.applikeysolutions.cosmocalendar.utils.SelectionType.RANGE;
import static com.example.thing.gatdawatda.mypage.activity_mypage.user_id;


public class activity_record_calendar extends AppCompatActivity {

    Intent intent;
    BottomNavigationView bottomNav;

    String START_DATE, END_DATE, CUR_DATE;
    long isStartDateOK, isEndDateOK;
    long tripPeriod;
    private String TripID;
    String COUNTRY = "나라 정보 없음";

    Geocoder geocoder;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_calendar);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        TextView tvSpotName_calendar = (TextView) findViewById(R.id.tvSpotName_calendar);
        View btEditLocation = findViewById(R.id.btEditLocation);
        Button btStartRecord = (Button) findViewById(R.id.btStartRecord);

        final CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        final MaterialCalendarView materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView2);

        final TextView tv_range_start_date = (TextView) findViewById(R.id.tv_range_start_date);
        final TextView tv_range_end_date = (TextView) findViewById(R.id.tv_range_end_date);


        geocoder = new Geocoder(this);

        Calendar calendar = Calendar.getInstance();
        Set<Long> days = new TreeSet<>();
        days.add(calendar.getTimeInMillis());
        int textColor = Color.parseColor("#ff0000");
        int selectedTextColor = Color.parseColor("#ff4000");
        int disabledTextColor = Color.parseColor("#ff8000");
        ConnectedDays connectedDays = new ConnectedDays(days, textColor, selectedTextColor, disabledTextColor);

        calendarView.addConnectedDays(connectedDays);
        calendarView.setConnectedDayIconPosition(ConnectedDayIconPosition.TOP);

        calendarView.setSelectionType(RANGE);
        calendarView.setCalendarOrientation(0); //0:HORIZONTAL 1:VERTICAL

        intent = getIntent();

        final double mlatitude = intent.getDoubleExtra("markerlatitude", 0.00);
        final double mlongitude = intent.getDoubleExtra("markerlongitude", 0.00);
        String spotID = intent.getStringExtra("spotID");

        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(mlatitude, mlongitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list != null) {
            if (list.size() == 0) {
                tvSpotName_calendar.setText("");
            } else {
                tvSpotName_calendar.setText(list.get(0).getAddressLine(0));
                String[] splitAddress = list.get(0).getAddressLine(0).split("\\s");
                COUNTRY = splitAddress[0];
            }
        }

        btEditLocation.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(activity_record_calendar.this, activity_record_calendar_googlemap.class);
                startActivity(intent);
                finish();
            }
        });

        btStartRecord.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Date startDate;
                final Date endDate;
                final Date curDate;
                //날짜계산코드
                if (calendarView.getSelectionType() == SelectionType.RANGE) {
                    List<Calendar> selectedDays = calendarView.getSelectedDates();
                    startDate = selectedDays.get(0).getTime();
                    final int size = selectedDays.size();
                    endDate = selectedDays.get(size - 1).getTime();

                    long now = System.currentTimeMillis();
                    curDate = new Date(now);

                    isStartDateOK = curDate.getTime() - startDate.getTime(); //오늘날짜 - 출발날짜
                    isStartDateOK = isStartDateOK / (24 * 60 * 60 * 1000);

                    isEndDateOK = endDate.getTime() - curDate.getTime(); //도착날짜 - 오늘날짜
                    isEndDateOK = isEndDateOK / (24 * 60 * 60 * 1000);

                    tripPeriod = endDate.getTime() - startDate.getTime(); //도착날짜 - 출발날짜
                    tripPeriod = tripPeriod / (24 * 60 * 60 * 1000) + 1; //일수가 나옴
                    //날짜계산끝

                    tv_range_start_date.setText(startDate.getYear() + 1900 + "-" + (startDate.getMonth() + 1) + "-" + startDate.getDate());
                    tv_range_end_date.setText(endDate.getYear() + 1900 + "-" + (endDate.getMonth() + 1) + "-" + endDate.getDate());

                    if (isStartDateOK >= 0 && isEndDateOK >= 0) {

                        //Trip필드초기화
                        Map<String, Object> tripInfo = new HashMap<>();
                        tripInfo.put("description", "");
                        tripInfo.put("endDate", endDate);
                        tripInfo.put("isComplete", false);
                        tripInfo.put("like", 0);
                        tripInfo.put("name", "");
                        tripInfo.put("startDate", startDate);
                        tripInfo.put("tripCountry", "");
                        tripInfo.put("tripMainPic", "https://firebasestorage.googleapis.com/v0/b/gatawata-8c561.appspot.com/o/spot_mainImages%2FLSsiElaEEIUK2JqkjKJjG8qjdA82(20).jpg?alt=media&token=3c6ece15-8c89-4c6c-a157-33bf9c6d98b2");
                        tripInfo.put("uploadTime", new Date());
                        tripInfo.put("user_id", user_id);
                        firebaseFirestore.collection("Trip").document().set(tripInfo);

                        final Query query = firebaseFirestore.collection("Trip").whereEqualTo("user_id", user_id);
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if (!document.getBoolean("isComplete")) {
                                            TripID = document.getId();
                                            //Users 업데이트
                                            Map<String, Object> updateUsers = new HashMap<>();
                                            updateUsers.put("istraveling", TripID);
                                            updateUsers.put("spotNum", 0);
                                            firebaseFirestore.collection("Users").document(user_id).update(updateUsers);

                                            //Spot의 필드 초기화
                                            Map<String, Object> spotInfo = new HashMap<>();
                                            spotInfo.put("isSpot", false);//업로드하면 true로 바꾸기
                                            GeoPoint latLng = new GeoPoint(mlatitude, mlongitude);
                                            spotInfo.put("location", latLng);
                                            //아래는 디폴트이미지 만들어 넣어야함.
                                            spotInfo.put("spotmainpic", "https://firebasestorage.googleapis.com/v0/b/gatawata-8c561.appspot.com/o/spot_mainImages%2FLSsiElaEEIUK2JqkjKJjG8qjdA82(17).jpg?alt=media&token=361ac833-3bab-4861-ba11-eeff53dbf87a");
                                            spotInfo.put("spotNum", 0);
                                            spotInfo.put("trip_id", TripID);
                                            spotInfo.put("user_id", user_id);
                                            spotInfo.put("country", getcountry(latLng));
                                            //Spot중 spotPictures필드 배열로 만들기
                                            List<String> pictures = new ArrayList<>();
                                            //아래는 디폴트이미지 만들어 넣어야함.
                                            pictures.add("https://firebasestorage.googleapis.com/v0/b/gatawata-8c561.appspot.com/o/spot_mainImages%2FLSsiElaEEIUK2JqkjKJjG8qjdA82(17).jpg?alt=media&token=361ac833-3bab-4861-ba11-eeff53dbf87a");
                                            spotInfo.put("spotPictures", pictures);
                                            firebaseFirestore.collection("Spot").document().set(spotInfo);

                                            //Trip중 TripCountry필드 배열로 만들기
                                            List<String> country = new ArrayList<>();
                                            country.add(getcountry(latLng));
                                            firebaseFirestore.collection("Trip").document(TripID).update("tripCountry", country);

                                            /*
                                            intent = new Intent(activity_record_calendar.this,MyService.class);
                                            startService(intent);
                                            */
                                            intent = new Intent(activity_record_calendar.this, activity_record.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            }
                        });
                    } else {
                        Snackbar.make(view, "날짜를 잘못 선택하셨습니다. 다시 선택해주세요.", Snackbar.LENGTH_LONG).show();
                        //Toast.makeText(activity_record_calendar.this, "날짜를 잘못 선택하셨습니다. 다시 선택해주세요.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        //네비게이션
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Intent intent0 = new Intent(activity_record_calendar.this, activity_home.class);
                        startActivity(intent0);
                        finish();
                        break;

                    case R.id.nav_plan:
                        Intent intent1 = new Intent(activity_record_calendar.this, activity_plan.class);
                        startActivity(intent1);
                        finish();
                        break;

                    case R.id.nav_record:
                        break;

                    case R.id.nav_my_page:
                        Intent intent3 = new Intent(activity_record_calendar.this, activity_mypage.class);
                        startActivity(intent3);
                        finish();
                        break;
                }
                return false;
            }
        });
        //네비게이션 끝
    }

    public String getcountry(GeoPoint latlng) {
        String country = "";
        Geocoder geo = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geo.getFromLocation(latlng.getLatitude(), latlng.getLongitude(), 1);
            if (addresses.isEmpty()) {
                country = "대한민국";
            } else {
                country = addresses.get(0).getCountryName();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return country;
    }
}