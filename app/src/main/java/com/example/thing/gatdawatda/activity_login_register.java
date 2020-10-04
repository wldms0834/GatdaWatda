package com.example.thing.gatdawatda;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.thing.gatdawatda.home.activity_home;
import com.example.thing.gatdawatda.mypage.activity_setup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class activity_login_register extends AppCompatActivity {


    private EditText reg_email_field;
    private EditText reg_pass_field;
    private EditText reg_confirm_pass_field;
    private Button reg_btn;
    private Button reg_login_btn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        reg_email_field = findViewById(R.id.etEmail);
        reg_pass_field = findViewById(R.id.etPw);
        reg_confirm_pass_field = findViewById(R.id.etPwCheck);
        reg_btn = findViewById(R.id.btRegister);
        reg_login_btn = findViewById(R.id.btLogin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_register);
        setSupportActionBar(toolbar);
        // ↓툴바에 홈버튼을 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // ↓툴바의 홈버튼의 이미지를 변경(기본 이미지는 뒤로가기 화살표)
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);


        reg_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent setupIntent = new Intent(activity_login_register.this, activity_login.class);
                startActivity(setupIntent);
                finish();

            }
        });

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = reg_email_field.getText().toString();
                String pass = reg_pass_field.getText().toString();
                String confirm_pass = reg_confirm_pass_field.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) & !TextUtils.isEmpty(confirm_pass)){

                    if(pass.equals(confirm_pass)){

                        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful()){

                                    storeFirestore(mAuth.getCurrentUser().getUid());
                                    Intent setupIntent = new Intent(activity_login_register.this, activity_setup.class);
                                    startActivity(setupIntent);
                                    finish();

                                } else {

                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(activity_login_register.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();

                                }

                            }
                        });

                    } else {

                        Toast.makeText(activity_login_register.this, "Confirm Password and Password Field doesn't match.", Toast.LENGTH_LONG).show();

                    }
                }


            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){

            sendToMain();

        }

    }

    private void sendToMain() {

        Intent mainIntent = new Intent(activity_login_register.this, activity_home.class);
        startActivity(mainIntent);
        finish();

    }

    private void storeFirestore(final String user_id) {
        Map<String, Object> user = new HashMap<>();

        user.put("like", 0);
        user.put("image", "");
        user.put("name","");
        user.put("istraveling", "");
        user.put("spotNum",0);
        user.put("tripNum",0);
        firebaseFirestore.collection("Users").document(user_id)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
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

}