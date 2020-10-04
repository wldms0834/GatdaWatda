package com.example.thing.gatdawatda.record;

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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thing.gatdawatda.R;
import com.example.thing.gatdawatda.home.activity_home;
import com.example.thing.gatdawatda.mypage.activity_mypage;
import com.example.thing.gatdawatda.plan.activity_plan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.zelory.compressor.Compressor;

import static com.example.thing.gatdawatda.mypage.activity_mypage.user_id;

public class activity_record_spot extends AppCompatActivity {

    Intent intent;

    String[] REQUIRED_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_MAIN_PIC = 2;
    private File tempFile;
    private ArrayList<item_record_spot_list_pic_comment> mArrayList;
    private adapter_record_spot mAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    TextView tvSpotAddress;
    EditText etSpotName;
    ImageView spotMainPic;
    Button btSaveSpot, btAddPic;

    public static Bitmap albumpicture;
    View mLayout;

    private ImageView setupImage;
    private Uri mainImageURI = null;
    private File MainPicFile;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Bitmap compressedImageFile;
    private String spotID;
    private String spotmainpicURI;
    private String TripID;
    private String name;
    private long SpotNum;
    private boolean mainimagechanged = false;
    Geocoder geocoder;
    double mlatitude, mlongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_spot);
        geocoder = new Geocoder(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        // ↓툴바에 홈버튼을 활성화
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // ↓툴바의 홈버튼의 이미지를 변경(기본 이미지는 뒤로가기 화살표)
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        init();

        btAddPic = findViewById(R.id.btAddPic);

        tvSpotAddress.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(activity_record_spot.this, activity_record_calendar_googlemap.class);
                intent.putExtra("fromwhere","record_spot");
                intent.putExtra("spotID", spotID);
                startActivity(intent);
                finish();
            }
        });

        //스팟메인사진고르기
        spotMainPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    goToAlbum(PICK_MAIN_PIC);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity_record_spot.this, REQUIRED_PERMISSIONS[0])) {
                        Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                                Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(activity_record_spot.this, REQUIRED_PERMISSIONS, 100);
                                goToAlbum(PICK_MAIN_PIC);
                            }
                        }).show();
                    } else {
                        ActivityCompat.requestPermissions(activity_record_spot.this, REQUIRED_PERMISSIONS, 100);
                    }
                }
            }
        });

        //스팟에 사진 다이나믹하게 추가하기
        btAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    goToAlbum(PICK_FROM_ALBUM);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity_record_spot.this, REQUIRED_PERMISSIONS[0])) {
                        Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                                Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(activity_record_spot.this, REQUIRED_PERMISSIONS, 100);
                                goToAlbum(PICK_FROM_ALBUM);
                            }
                        }).show();
                    } else {
                        ActivityCompat.requestPermissions(activity_record_spot.this, REQUIRED_PERMISSIONS, 100);
                    }
                }
            }
        });
    }

    private void init() {
        mLayout = findViewById(R.id.layout_record_spot);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_pic_and_com_list);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mArrayList = new ArrayList<>();
        btSaveSpot = findViewById(R.id.btSaveSpot);
        btSaveSpot.setOnClickListener(mOnButtonClickListener);
        mAdapter = new adapter_record_spot(mArrayList);
        mRecyclerView.setAdapter(mAdapter);
        tvSpotAddress = findViewById(R.id.tvSpotAddress);
        etSpotName = findViewById(R.id.etSpotName);
        spotMainPic = findViewById(R.id.spotMainPic);

        GradientDrawable drawable =
                (GradientDrawable) this.getDrawable(R.drawable.imageview_rounding);
        spotMainPic.setBackground(drawable);
        spotMainPic.setClipToOutline(true);


        intent = getIntent();
        //spotID = intent.getStringExtra("spotID");
        spotID = intent.getStringExtra("clickspot");
        //Toast.makeText(this, "click spot : " + spotID, Toast.LENGTH_LONG).show();
        mlatitude = intent.getDoubleExtra("markerlatitude", 0.00);
        mlongitude = intent.getDoubleExtra("markerlongitude", 0.00);

        firebaseFirestore.collection("Spot").document(spotID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot document = task.getResult();
                spotmainpicURI = document.getString("spotmainpic");
                Picasso.get().load(spotmainpicURI).into(spotMainPic);
                TripID = document.getString("trip_id");
                name = document.getString("name");
                etSpotName.setText(name);

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
        //

        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(mlatitude, mlongitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list != null) {
            if (list.size() == 0) {
                tvSpotAddress.setText("주소없음 [" + mlatitude + ", " + mlongitude + "]");
            } else {
                tvSpotAddress.setText(list.get(0).getAddressLine(0));
            }
        }

        //네비게이션
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mNavigationListener);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

    }

    //스팟 저장 버튼
    View.OnClickListener mOnButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(activity_record_spot.this, "스팟 저장중입니다. 잠시만 기다려주세요 ", Toast.LENGTH_SHORT).show();

            final String spotName = etSpotName.getText().toString();
            String spotAddress = tvSpotAddress.getText().toString();

            if (mainimagechanged) {
                File newImageFile = new File(MainPicFile.getAbsolutePath());
                try {
                    compressedImageFile = new Compressor(activity_record_spot.this)
                            .setMaxHeight(125)
                            .setMaxWidth(125)
                            .setQuality(50)
                            .compressToBitmap(newImageFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] thumbData = baos.toByteArray();

                final Uri[] download_uri = new Uri[1];
                UploadTask image_path = storageReference.child("spot_mainImages").child(spotID + ".jpg").putBytes(thumbData);
                image_path.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            download_uri[0] = task.getResult().getDownloadUrl();
                            //Log.d("spotlog", "download url : " + download_uri[0]);

                            final Map<String, Object> spotInfo = new HashMap<>();
                            GeoPoint latLng = new GeoPoint(mlatitude, mlongitude);
                            spotInfo.put("location", latLng);
                            spotInfo.put("name", spotName);
                            spotInfo.put("spotmainpic", download_uri[0].toString());
                            final List<String> pictures = new ArrayList<>();
                            final List<String> comments = new ArrayList<>();
                            Log.d("spotlog", "mArrayList size : " + mArrayList.size());
                            int count = 1;
                            boolean end = false;
                            if (mArrayList.size() == 0) {
                                end = true;
                            }
                            for (int i = 0; i < mArrayList.size(); i++) {
                                end = false;
                                if (i == mArrayList.size() - 1) {
                                    end = true;
                                }
                                if (mArrayList.get(i).getIvSpotPic() != null) {

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    final String tempcom = mArrayList.get(i).getComment();
                                    mArrayList.get(i).getIvSpotPic().compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] thumbData = baos.toByteArray();
                                    UploadTask image_path = storageReference.child("spot_pics").child(spotID).child(new Date().getTime() + ".jpg").putBytes(thumbData);
                                    final boolean finalEnd = end;
                                    image_path.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            pictures.add(task.getResult().getDownloadUrl().toString());
                                            comments.add(tempcom);
                                            if (finalEnd) {
                                                spotInfo.put("spotPictures", pictures);
                                                spotInfo.put("spotComments", comments);
                                                Log.d("spotlog", "pictures : " + pictures);
                                                Log.d("spotlog", "comments : " + comments);
                                                firebaseFirestore.collection("Spot").document(spotID).update(spotInfo);

                                                finish();
                                            }
                                        }
                                    });
                                } else {
                                    pictures.add(mArrayList.get(i).getpicURI());
                                    comments.add(mArrayList.get(i).getComment());
                                    count++;
                                    if (end) {
                                        firebaseFirestore.collection("Spot").document(spotID).update(spotInfo);

                                        finish();
                                    }
                                }
                            }
                            if (count == mArrayList.size() || end ) {
                                firebaseFirestore.collection("Spot").document(spotID).update(spotInfo);

                                finish();
                            }
                        } else {
                            Toast.makeText(activity_record_spot.this, "(IMAGE Error) : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Log.d("spotlog", "메인사진변경x else문 ");

                final Map<String, Object> spotInfo = new HashMap<>();
                GeoPoint latLng = new GeoPoint(mlatitude, mlongitude);
                spotInfo.put("location", latLng);
                spotInfo.put("name", spotName);
                final List<String> pictures = new ArrayList<>();
                final List<String> comments = new ArrayList<>();
                Log.d("spotlog", "mArrayList size : " + mArrayList.size());

                int count = 1;
                boolean end = false;

                if(mArrayList.size() ==0){
                    end = true;
                }
                for (int i = 0; i < mArrayList.size(); i++) {
                    Log.d("spotlog", "for문 실행확인 : " + i);
                    if (i == mArrayList.size() - 1) {
                        end = true;
                    }
                    if (mArrayList.get(i).getIvSpotPic() != null) {
                        Log.d("spotlog", "for문 내 if문 : ");
                        final boolean finalEnd = end;

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        final String tempcom = mArrayList.get(i).getComment();
                        mArrayList.get(i).getIvSpotPic().compress(Bitmap.CompressFormat.JPEG, 100, baos);

                        byte[] thumbData = baos.toByteArray();
                        UploadTask image_path = storageReference.child("spot_pics").child(spotID).child(new Date().getTime() + i + ".jpg").putBytes(thumbData);
                        image_path.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                Log.d("spotlog", "이미지 저장 완료");
                                pictures.add(task.getResult().getDownloadUrl().toString());
                                comments.add(tempcom);
                                if (finalEnd) {
                                    spotInfo.put("spotPictures", pictures);
                                    spotInfo.put("spotComments", comments);
                                    Log.d("spotlog", "pictures : " + pictures);
                                    Log.d("spotlog", "comments : " + comments);
                                    firebaseFirestore.collection("Spot").document(spotID).update(spotInfo);
                                    finish();
                                }
                            }
                        });
                    } else {
                        pictures.add(mArrayList.get(i).getpicURI());
                        comments.add(mArrayList.get(i).getComment());
                        count++;
                        if (end) {
                            firebaseFirestore.collection("Spot").document(spotID).update(spotInfo);

                            finish();
                        }
                    }
                    if (count == mArrayList.size() || end) {
                        firebaseFirestore.collection("Spot").document(spotID).update(spotInfo);

                        finish();
                    }

                }
                if(end){
                    firebaseFirestore.collection("Spot").document(spotID).update(spotInfo);

                    finish();
                }

            }
        }
    };

    private void goToAlbum(final int i) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, i);
    }

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
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (requestCode == PICK_MAIN_PIC) {
            MainPicFile = tempFile;
            mainimagechanged = true;
        }
        albumpicture = BitmapFactory.decodeFile(tempFile.getAbsolutePath());

        if (requestCode == PICK_FROM_ALBUM) {
            item_record_spot_list_pic_comment newitem = new item_record_spot_list_pic_comment(albumpicture, "  ");
            mArrayList.add(newitem);//RecyclerView 마지막에 삽입
            mAdapter.notifyDataSetChanged();//변경된 데이터를 화면에 반영
        } else if (requestCode == PICK_MAIN_PIC) {
            spotMainPic.setImageBitmap(albumpicture);
        }

        //Log.d("spotlog", "MainPicFile : " + MainPicFile + " tempFile : " + tempFile + "albumpicture : " + albumpicture);
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            if (tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
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

    //바텀네비게이션
    BottomNavigationView.OnNavigationItemSelectedListener mNavigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    intent = new Intent(activity_record_spot.this, activity_home.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.nav_plan:
                    intent = new Intent(activity_record_spot.this, activity_plan.class);
                    startActivity(intent);
                    finish();
                    break;

                case R.id.nav_record:
                    intent = new Intent(activity_record_spot.this, activity_record.class);
                    finish();
                    break;

                case R.id.nav_my_page:
                    intent = new Intent(activity_record_spot.this, activity_mypage.class);
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