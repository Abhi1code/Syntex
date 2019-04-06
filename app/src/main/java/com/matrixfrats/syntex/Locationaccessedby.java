package com.matrixfrats.syntex;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by DELL on 14-03-2018.
 */

public class Locationaccessedby extends AppCompatActivity {

    private RecyclerView locationaccessedby;
    private RelativeLayout refreshinlocationaccessed;
    private Button refreshbuttoninlocationaccessed;
    private TextView errormessageinlocationaccessed;
    private RecyclerView.Adapter adapter;
    private ArrayList<Trialgettersetter> arrayList;
    private RecyclerView.LayoutManager manager;
    private DatabaseReference databaseReference;
    private ProgressDialog progressdialog;
    private static final String DEFAULT = "N/A";

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        refreshinlocationaccessed.setVisibility(View.GONE);
        progressdialog = new ProgressDialog(this);
        progressdialog.setCanceledOnTouchOutside(false);
        progressdialog.setMessage("please wait...");
        progressdialog.show();
        showlocationaccessedby();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locationaccessedby);
        setTitle("Traced by");
        fillarraylist();
        findview();
        setrecycleview();
    }

    private void findview(){
        locationaccessedby = (RecyclerView)findViewById(R.id.locationaccessedby);
        errormessageinlocationaccessed = (TextView)findViewById(R.id.errormessageinlocationaccessed);
        refreshbuttoninlocationaccessed = (Button)findViewById(R.id.refreshbuttoninlocationaccessed);
        refreshinlocationaccessed = (RelativeLayout)findViewById(R.id.refreshinlocationaccessed);
        refreshbuttoninlocationaccessed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressdialog.show();
                showlocationaccessedby();
            }
        });
    }

    private void setrecycleview(){
        locationaccessedby.setHasFixedSize(true);
        adapter = new Locationaccessedbyadaptor(arrayList,this);
        manager = new LinearLayoutManager(this);
        locationaccessedby.setLayoutManager(manager);
        locationaccessedby.setAdapter(adapter);

    }

    private void fillarraylist(){
        arrayList = new ArrayList<>();

    }

    private void showlocationaccessedby(){
        if (arrayList.size() != 0) {
            arrayList.clear();
        }
        if(checkineternetconnection()) {
            databaseReference.child(getsharedprefname()).child("accessed by").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Integer value = snapshot.getValue(Integer.class);
                        if (value.equals(1) && !snapshot.getKey().equals(getsharedprefname())) {

                            arrayList.add(new Trialgettersetter("",snapshot.getKey(),""+snapshot.getKey().toUpperCase().charAt(0)));

                        }
                    }

                    if (arrayList.size() == 0) {
                        refreshinlocationaccessed.setVisibility(View.VISIBLE);
                        refreshbuttoninlocationaccessed.setVisibility(View.GONE);
                        errormessageinlocationaccessed.setText("No One is Watching Your Location");

                    } else {
                        refreshinlocationaccessed.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }
                    progressdialog.cancel();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    refreshinlocationaccessed.setVisibility(View.VISIBLE);
                    refreshbuttoninlocationaccessed.setVisibility(View.VISIBLE);
                    errormessageinlocationaccessed.setText("Connection error..");
                    progressdialog.cancel();
                }
            });
        }else {
            refreshinlocationaccessed.setVisibility(View.VISIBLE);
            refreshbuttoninlocationaccessed.setVisibility(View.VISIBLE);
            errormessageinlocationaccessed.setText("Connection error..");
            progressdialog.cancel();
        }

    }

    private String getsharedprefname() {
        SharedPreferences gettingusername = getSharedPreferences("savingusername", Context.MODE_PRIVATE);
        return gettingusername.getString("name", DEFAULT);
    }

    private boolean checkineternetconnection(){

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo nf = cm.getActiveNetworkInfo();
        if (nf != null && nf.isConnected()) {
            return true;
        }else{
            return false;
        }
    }

}
