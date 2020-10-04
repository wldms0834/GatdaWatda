package com.example.thing.gatdawatda.plan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.example.thing.gatdawatda.R;
import com.example.thing.gatdawatda.post.activity_post_spot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.thing.gatdawatda.mypage.activity_mypage.user_id;

public class fragment_plan_left extends Fragment {
    public static fragment_plan_left newInstance() {
        return new fragment_plan_left();
    }

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    String childid;

    final ArrayList<String> countrylist = new ArrayList<>();
    ArrayList<String> spotlist = new ArrayList<>();
    Map<String, ArrayList<String>> temp = new HashMap<>();
    Map<String, String> id_name = new HashMap<>();
    Map<String, String> mainPic_id = new HashMap<>();
    ExpandableListView elv;
    View bt_like2plan;
    ArrayList<String> planlist = new ArrayList<>();
    ArrayList<String> myplanlist = new ArrayList<>();
    ArrayList<String> myplanlistname = new ArrayList<>();

    public fragment_plan_left() {
        // required
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_plan_sub1, container, false);
        elv = view.findViewById(R.id.list);
        bt_like2plan = view.findViewById(R.id.bt_like_2_plan);
        datafromfirebase();
        bt_like2plan.setOnClickListener(new View.OnClickListener() {

            //팝업창이 뜨고
            /*
            팝업이뜨고
            이름이랑 완료버튼/취소버튼

            이름입력후 오나료누르면 데베로
            Plan에 doc은 임의로 name 에 이름
            planspot에 spotid list채로넣어주기
            user_id넘기기


            record activity에서
            트립생성버튼눌렀을때 Spot에 isSpot이 false인것들 다 삭제
             */

            @Override
            public void onClick(View v) {
                if (planlist.isEmpty()) {
                    Toast.makeText(getContext(), "스팟을 길게 눌러 선택한 뒤에 버튼을 눌러주세요. ", Toast.LENGTH_LONG).show();
                } else {
                    final EditText edittext = new EditText(getContext());

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyCustomDialogStyle);
                    // LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(LAYOUT_INFLATER); View layout = inflater.inflate( R.layout.cutom_dialog, (ViewGroup)findViewById( R.id.layout_root)); TextView text = (TextView)layout.findViewById(R.id.text); text.setText("Hello, this is a cutom dialog"); ImageView image = (ImageView)layout.findViewById( R.id.image); image.setImageResource( R.drawable.android); builder = new AlertDialog.Builder(mContext); builder.setView(layout); alertDialog = builder.create();
                    builder.setTitle("내 코스 만들기");
                    builder.setIcon(R.drawable.ic_add_box_24dp);
                    //builder.setMessage("나만의 코스의 이름을 적어주세요");
                    final List SelectedItems = new ArrayList();
                    int defaultItem = -1;
                    SelectedItems.add(defaultItem);
                    if (myplanlistname.size() == myplanlist.size()) {
                        myplanlistname.add("새롭게 코스 생성하기");
                    }
                    edittext.setHint("새로운 코스 이름을 적어주세요.");
                    edittext.setTextSize(15);
                    final CharSequence[] items = myplanlistname.toArray(new String[myplanlistname.size()]);
                    Log.d("aaaa", "check data : ");

                    builder.setSingleChoiceItems(items, defaultItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SelectedItems.clear();
                            SelectedItems.add(which);
                        }
                    });
                    builder.setView(edittext);
                    builder.setPositiveButton("확인",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    final String name = edittext.getText().toString();
                                    final Map<String, Object> myplan = new HashMap<>();
                                    myplan.put("user_id", user_id);
                                    myplan.put("name", name);
                                    final ArrayList<String> planidlist = new ArrayList<>();
                                    for (String s : id_name.keySet()) {
                                        if (planlist.contains(s)) {
                                            planidlist.add(id_name.get(s));
                                            spotlist.remove(id_name.get(s));
                                            Log.d("aaaa", "id_name,keyset : " + s);
                                        }
                                    }
                                    myplan.put("plan_spot", planidlist);
                                    final int item = (int) SelectedItems.get(0);
                                    if (item == myplanlist.size()) {
                                        firebaseFirestore.collection("Plan").document().set(myplan);
                                        //firebaseFirestore.collection("spot_like").document(user_id).update("spotlist",spotlist);
                                        Toast.makeText(getContext(), "나만의 코스 :" + name + " 생성 완료", Toast.LENGTH_LONG).show();
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment_plan_right.newInstance()).commit();
                                        ((activity_plan) getActivity()).btright();
                                        Log.d("aaaa", "결과 spotlist : " + spotlist);
                                    } else {
                                        Log.d("aaaa", "결과 item : " + item);

                                        firebaseFirestore.collection("Plan").document(myplanlist.get(item)).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot thisdocument = task.getResult();
                                                    ArrayList<String> plan_spot = (ArrayList<String>) thisdocument.get("plan_spot");
                                                    for (String s : planidlist) {
                                                        plan_spot.add(s);
                                                    }
                                                    firebaseFirestore.collection("Plan").document(thisdocument.getId()).update("plan_spot", plan_spot);
                                                    Toast.makeText(getContext(), "나만의 코스 :" + myplanlistname.get(item) + "에 추가 완료", Toast.LENGTH_LONG).show();
                                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment_plan_right.newInstance()).commit();
                                                    ((activity_plan) getActivity()).btright();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                    builder.setNegativeButton("취소",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.getWindow().setBackgroundDrawableResource(R.drawable.rounding_corner_border);
                    alert.show();


                    Log.d("aaaa", "plan list " + planlist);
                }
            }
        });
        elv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), activity_post_spot.class);
                intent.putExtra("spot_id", childid);
                startActivity(intent);
            }
        });
        elv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int itemType = ExpandableListView.getPackedPositionType(id);
                boolean retVal = true;
                if (itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    int childPosition = ExpandableListView.getPackedPositionChild(id);
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    String selectedchild = elv.getExpandableListAdapter().getChild(groupPosition, childPosition).toString();
                    View color = view.findViewById(R.id.child_name);
                    if (planlist.contains(selectedchild)) {
                        planlist.remove(selectedchild);
                        color.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.mainColor, null));
                        Log.d("aaaa", "deleted : " + selectedchild);
                    } else {
                        planlist.add(selectedchild);
                        color.setBackgroundColor(Color.LTGRAY);
                        Log.d("aaaa", "added : " + selectedchild);
                    }
                    //do your per-item callback here
                    return true; //true if we consumed the click, false if not
                } else if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    //Log.d("aaaa", "PACKED_POSITION_TYPE_GROUP : " + groupPosition);
                    //do your per-group callback here

                    return true;
                } else {
                    return false;
                }
            }
        });


        return view;
    }

    void datafromfirebase() {
        firebaseFirestore.collection("spot_like").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        spotlist = (ArrayList<String>) document.get("spotlist");
                        //Log.d("aaaa", "spotlist : " + spotlist);
                        //Log.d("aaaa", "spotlist for문 실행  ");

                        for (final String s : spotlist) {
                            firebaseFirestore.collection("Spot").document(s).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        String country = task.getResult().getString("country");
                                        String name = task.getResult().getString("name");
                                        String spot_main_pic = task.getResult().getString("spotmainpic");
                                        ArrayList<String> idtemp = new ArrayList<>();

                                        if (!countrylist.contains(country)) {
                                            countrylist.add(country);
                                            idtemp.add(name);
                                            id_name.put(name, s);
                                            mainPic_id.put(s, spot_main_pic);
                                            temp.put(country, idtemp);
                                            //childtemp.add(idtemp);
                                        } else {
                                            idtemp = temp.get(country);
                                            idtemp.add(name);
                                            id_name.put(name, s);
                                            mainPic_id.put(s, spot_main_pic);
                                            temp.put(country, idtemp);
                                            //childtemp.add(idtemp);
                                        }
                                        elv.setAdapter(new SavedTabsListAdapter(getContext()));

                                    }
                                }
                            });
                        }
                    }
                }
            }
        });

        Query mquery = firebaseFirestore.collection("Plan").whereEqualTo("user_id", user_id);
        mquery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        myplanlist.add(document.getId());
                        myplanlistname.add(document.getString("name"));
                    }
                }
            }
        });
    }

    public class SavedTabsListAdapter extends BaseExpandableListAdapter {
        private ArrayList<String> groups = countrylist;
        private ArrayList<ArrayList<String>> children = getchildtemp();
        private Context ctx;


        SavedTabsListAdapter(Context ctx) {
            this.ctx = ctx;
        }

        ArrayList<ArrayList<String>> getchildtemp() {
            ArrayList<ArrayList<String>> childtemp = new ArrayList<>();
            for (String s : countrylist) {
                childtemp.add(temp.get(s));
            }
            //Log.d("aaaa", "최종 childtemp" + childtemp);
            return childtemp;
        }

        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return children.get(i).size();
        }

        @Override
        public Object getGroup(int i) {
            return groups.get(i);
        }

        @Override
        public Object getChild(int i, int i1) {
            return children.get(i).get(i1);
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(fragment_plan_left.this.getActivity());
            textView.setText(getGroup(i).toString());
            textView.setTextSize(15);
            textView.setPadding(100, 0, 0, 0);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setHeight(150);
            return textView;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) this.ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.item_plan_child, null);
            }
            SwipeLayout swipeLayout = (SwipeLayout) view.findViewById(R.id.swipe_child);
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Left, view.findViewById(R.id.bottom_wrapper));

            TextView tv_swipe_name = view.findViewById(R.id.tv_swipe_name);
            Button bt_delete = view.findViewById(R.id.bt_delete);
            ImageView tv_swipe_pic = view.findViewById(R.id.tv_swipe_pic);
            View child_name = view.findViewById(R.id.child_name);

            String childname = getChild(i, i1).toString();
            Log.d("aaaa", "childname :" + childname);
            childid = id_name.get(childname);
            String childpic = mainPic_id.get(childid);
            View colorlayout = view.findViewById(R.id.child_name);
            Log.d("aaaa", "child Id before button : " + childid);
            bt_delete.setTag(childid);
            bt_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String childid = v.getTag().toString();
                    spotlist.remove(childid);
                    Log.d("aaaa", "child ID : " + childid);
                    Map<String, Object> newspotlike = new HashMap<>();
                    newspotlike.put("spotlist", spotlist);
                    firebaseFirestore.collection("spot_like").document(user_id).update(newspotlike);
                    Toast.makeText(getContext(), "스팟 삭제 완료", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment_plan_left.newInstance()).commit();

                }
            });

            if (planlist.contains(childname)) {
                colorlayout.setBackgroundColor(Color.LTGRAY);
            }

            GradientDrawable drawable =
                    (GradientDrawable) view.getContext().getDrawable(R.drawable.imageview_rounding);

            tv_swipe_pic.setBackground(drawable);
            tv_swipe_pic.setClipToOutline(true);
            tv_swipe_pic.setScaleType(ImageView.ScaleType.CENTER_CROP);

            tv_swipe_name.setText(childname);
            Glide.with(fragment_plan_left.this).load(childpic).into(tv_swipe_pic);

            /*child_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), activity_post_spot.class);
                    intent.putExtra("spot_id", childid);
                    startActivity(intent);
                }
            });*/

            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }


    }
}