package com.matrixfrats.syntex;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by DELL on 26-01-2018.
 */

public class Searchfriend extends AppCompatActivity implements View.OnClickListener{

    private RecyclerView searchrecycleview;
    private RecyclerView.Adapter searchadaptor;
    private RecyclerView.LayoutManager manager;
    private ArrayList<Trialgettersetter> arrayList;
    private EditText searchname;
    private Button searchbutton;
    private DatabaseReference databasereference;
    private static final String DEFAULT = "N/A";
    private ProgressDialog progressdialog;

    @Override
    protected void onStart() {
        super.onStart();
        databasereference = FirebaseDatabase.getInstance().getReference();
        progressdialog = new ProgressDialog(this);
        progressdialog.setTitle("Searching User");

        progressdialog.setCanceledOnTouchOutside(false);
        progressdialog.setMessage("Please wait...");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchfriend);
        setTitle("Search");
        fillarraylist();
        findview();
        setrecycleview();
        searchbutton.setOnClickListener(this);
    }

    private void findview(){
        searchrecycleview = (RecyclerView)findViewById(R.id.searchrecycleview);
        searchname = (EditText) findViewById(R.id.searchbar);
        searchbutton = (Button)findViewById(R.id.searchbutton);
    }

    private void setrecycleview(){
        searchrecycleview.setHasFixedSize(true);
        searchadaptor = new Searchadaptor(arrayList,this);
        manager = new LinearLayoutManager(this);
        searchrecycleview.setLayoutManager(manager);
        searchrecycleview.setAdapter(searchadaptor);

    }

    private void fillarraylist(){
        arrayList = new ArrayList<>();


    }

    @Override
    public void onClick(View view) {
        final String searchedname = searchname.getText().toString().trim();

        if(!TextUtils.isEmpty(searchedname)) {
            closesoftkeyboard();
            if(checkineternetconnection()) {
                progressdialog.show();
                if (arrayList.size() > 0) {
                    arrayList.clear();
                    searchadaptor.notifyDataSetChanged();
                }
                searchdatabase(searchedname);
            }else {
                alertdialog("No Internet Connection");
            }
        }
    }

    private void searchdatabase(final String searchedname) {
        databasereference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int counter = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    //String uid = snapshot.getKey();
                    String name = snapshot.child("name").getValue(String.class);
                    String imageurl = snapshot.child("image").getValue(String.class);

                  if(name.trim().toLowerCase().contains(searchedname.toLowerCase().trim())){
                      if(!name.trim().equals(getsharedprefname())) {
                          arrayList.add(new Trialgettersetter(imageurl, name,""+name.toUpperCase().charAt(0)));
                          searchadaptor.notifyDataSetChanged();
                          counter++;
                      }
                  }

                    if(counter == 6) {
                        break;
                    }
                }
                progressdialog.cancel();
                if (arrayList.size() == 0) {

                    alertdialog("No user found...");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressdialog.cancel();
                alertdialog("Connection error...");
            }
        });
    }

    private void closesoftkeyboard(){
         View v = this.getCurrentFocus();
         if(v != null){
             InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
             imm.hideSoftInputFromWindow(v.getWindowToken(),0);
         }
    }

    private String getsharedprefname(){
        SharedPreferences gettingusername = getSharedPreferences("savingusername", Context.MODE_PRIVATE);
        return gettingusername.getString("name",DEFAULT);
    }

    private void alertdialog(String s){
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(Searchfriend.this);
        alertdialog.setMessage(s).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        dialogInterface.dismiss();
                    }

        });

        AlertDialog showalertdialog = alertdialog.create();
        showalertdialog.setTitle("Searching status");
        showalertdialog.show();

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
