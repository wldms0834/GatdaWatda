package com.example.thing.gatdawatda.plan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.thing.gatdawatda.R;
import com.example.thing.gatdawatda.mypage.activity_mypage;
import com.example.thing.gatdawatda.home.activity_home;
import com.example.thing.gatdawatda.record.activity_record;

public class activity_plan extends AppCompatActivity{
    BottomNavigationView bottomNav;
    Intent intent;
    View pager;
    ImageView btn_first;
    ImageView btn_second;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        //텝페이지 구현
        pager = findViewById(R.id.fragment_container);
        btn_first = findViewById(R.id.bt_first);
        btn_second = findViewById(R.id.bt_second);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment_plan_left.newInstance()).commit();

        /*
        FragmentManager fm =
        pager.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        pager.setCurrentItem(0);
*/
        btn_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btleft();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment_plan_left.newInstance()).commit();

            }
        });

        btn_second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btright();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment_plan_right.newInstance()).commit();

            }
        });


        //네비게이션
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mNavigationListener);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

    }
    public void btleft(){
        btn_first.setImageResource(R.drawable.ic_plan_y);
        btn_second.setImageResource(R.drawable.ic_myplan_n);
    }
    public void btright(){
        btn_first.setImageResource(R.drawable.ic_plan_n);
        btn_second.setImageResource(R.drawable.ic_myplan_y);
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mNavigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    intent = new Intent(activity_plan.this, activity_home.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.nav_plan:
                    intent = new Intent(activity_plan.this, activity_plan.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.nav_record:
                    intent = new Intent(activity_plan.this, activity_record.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.nav_my_page:
                    intent = new Intent(activity_plan.this, activity_mypage.class);
                    startActivity(intent);
                    finish();
                    break;
            }
            return true;
        }
    };

    /*
    //엑티비티 안보이면 종료
    @Override
    public void onPause() {
        super.onPause();

        // Remove the activity when its off the screen
        //finish();
    }*/
}