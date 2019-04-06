package com.matrixfrats.syntex;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DELL on 03-02-2018.
 */

public class Pairinglist extends AppCompatActivity implements Dialog.Dialogclicklistener{

    private static final String DEFAULT = "N/A";
    private final String showingpairinglist = "http://matrixfrats.com/gpslocation/pairinglist.php";
    private final String deletingpairing = "http://matrixfrats.com/gpslocation/deletepairing.php";
    private RecyclerView recyclerView;
    private Pairinglistadaptor adapter;
    private RecyclerView.LayoutManager manager;
    private ArrayList<Trialgettersetter> arrayList;
    private ProgressDialog progressdialog;
    private RelativeLayout refreshinrelativelayout;
    private Button refreshbutton;
    private TextView errormessage;
    private String imageurl = "";

    @Override
    protected void onStart() {
        super.onStart();
        if (arrayList.size() != 0) {
            arrayList.clear();
        }
        refreshinrelativelayout.setVisibility(View.GONE);
        progressdialog = new ProgressDialog(this);
        progressdialog.setCanceledOnTouchOutside(false);
        progressdialog.setMessage("please wait...");
        progressdialog.show();
        showpairedlist();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pairinglist);
        setTitle("Paired list");
        findview();
        fillarraylist();
        setrecycleview();
    }

    private void findview() {
        recyclerView = (RecyclerView) findViewById(R.id.pairedlistrecyclerview);
        refreshinrelativelayout = (RelativeLayout) findViewById(R.id.refreshinrelativelayout);
        refreshbutton = (Button) findViewById(R.id.refreshbuttoninpairedlist);
        errormessage = (TextView) findViewById(R.id.errormessageinpaired);
        refreshbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showpairedlist();
                progressdialog.show();
            }
        });
    }

    private void setrecycleview() {
        recyclerView.setHasFixedSize(true);
        adapter = new Pairinglistadaptor(arrayList, this);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.setOnitemclicklistener(new Pairinglistadaptor.onItemclicklistener() {
            @Override
            public void tracelocation(int position, String name) {
                Trialgettersetter trialgettersetter = arrayList.get(position);
                imageurl = trialgettersetter.getMsearchimage();
                alertdialog(deletingpairing, "Do you want to trace location of " + name + ".", name, position, "");
            }

            @Override
            public void deletepairing(int position, String name) {
                alertdialog(deletingpairing, "Are you sure you want to delete pairing from " + name + ".", name, position, "delete");
            }
        });
    }

    private void fillarraylist() {
        arrayList = new ArrayList<>();

    }


    private void showpairedlist() {
        if (arrayList.size() != 0) {
            arrayList.clear();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, showingpairinglist, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if (jsonArray.length() != 0) {
                        visiblerecyclerview();
                        int count = 0;
                        while (count < jsonArray.length()) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(count);
                                arrayList.add(new Trialgettersetter(jsonObject.getString("personwithpairingimage"),
                                        jsonObject.getString("personwithpairing"),""+jsonObject.getString("personwithpairing").charAt(0)));
                                adapter.notifyDataSetChanged();
                                count++;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        visiblerefresh();
                        refreshbutton.setVisibility(View.GONE);
                        errormessage.setText("No Paired User");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    visiblerefresh();
                    buttonvisibility("Connection Problem");
                }

                progressdialog.cancel();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                visiblerefresh();
                buttonvisibility("Connection Problem");
                progressdialog.cancel();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("username", getsharedprefname());
                return map;
            }
        };
        Mysingleton.getInstance(this.getApplicationContext()).addtorequestque(stringRequest, "pairinglist");
    }

    private void deletepairing(String url, final String name, final int position) {

        StringRequest deletepairing = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("success")) {
                    progressdialog.cancel();
                    arrayList.remove(position);
                    adapter.notifyDataSetChanged();
                    if (arrayList.size() == 0) {
                        visiblerefresh();
                        refreshbutton.setVisibility(View.GONE);
                        errormessage.setText("No Paired User");
                    }

                    new Sendnotification(name,"Knock Knock",""+getsharedprefname()+" deletes your pairing",
                            "com.matrixfrats.pairinglist",Pairinglist.this.getApplicationContext());

                    erroralertdialog("Successful...", "success","nothing");



                } else {
                    progressdialog.cancel();
                    erroralertdialog("Connection Problem..", "error","nothing");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressdialog.cancel();
                erroralertdialog("Connection Problem", "error","nothing");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("username", getsharedprefname());
                map.put("requestname", name);
                return map;
            }
        };
        Mysingleton.getInstance(this.getApplicationContext()).addtorequestque(deletepairing, "deletepairing");
    }


    private String getsharedprefname() {
        SharedPreferences gettingusername = getSharedPreferences("savingusername", Context.MODE_PRIVATE);
        return gettingusername.getString("name", DEFAULT);
    }

    private void alertdialog(final String url, String s, final String name, final int position, final String code) {

        AlertDialog.Builder alertdialog = new AlertDialog.Builder(Pairinglist.this);
        alertdialog.setMessage(s).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (code.equals("delete")) {
                            deletepairing(url, name, position);
                            progressdialog.show();
                        }
                        if (code.equals("")) {
                            tooltips(name);
                        }

                        dialogInterface.cancel();
                        dialogInterface.dismiss();
                    }

                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                dialogInterface.dismiss();
            }
        });

        AlertDialog showalertdialog = alertdialog.create();
        showalertdialog.setTitle("Alert");
        showalertdialog.show();

    }

    private void erroralertdialog(String s, final String code,final String name) {

        AlertDialog.Builder alertdialog = new AlertDialog.Builder(Pairinglist.this);
        alertdialog.setMessage(s).setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.cancel();
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog showalertdialog = alertdialog.create();
        showalertdialog.setTitle("Message");
        showalertdialog.show();

    }

    private void visiblerecyclerview() {
        refreshinrelativelayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

    }

    private void visiblerefresh() {
        recyclerView.setVisibility(View.GONE);
        refreshinrelativelayout.setVisibility(View.VISIBLE);
    }

    private void buttonvisibility(String s) {
        refreshbutton.setVisibility(View.VISIBLE);
        errormessage.setText(s);
    }

    private void tooltips(final String name){

        new Sendnotification(name,"Knock Knock",""+getsharedprefname()+" is watching your location",
                "com.matrixfrats.locationaccessedby",Pairinglist.this.getApplicationContext());

        SharedPreferences checkstatus = getSharedPreferences("checkstatus",MODE_PRIVATE);
        if(checkstatus.getBoolean("value",false)){
            Intent intent = new Intent(Pairinglist.this,Mapfragment.class);
            intent.putExtra("name",name.trim());
            intent.putExtra("imageurl",imageurl);
            startActivity(intent);
            finish();
        }else {
            Dialog dialog = new Dialog();
            Bundle bundle = new Bundle();
            bundle.putString("name",name);
            dialog.setArguments(bundle);
            dialog.setCancelable(false);
            dialog.show(getSupportFragmentManager(),"dialog");

        }
    }

    @Override
    public void startactivity(Boolean checkedstatus,final String name) {
        if(checkedstatus){
            SharedPreferences checkstatus = getSharedPreferences("checkstatus",MODE_PRIVATE);
            SharedPreferences.Editor editor = checkstatus.edit();
            editor.putBoolean("value",true);
            editor.commit();
        }
        Intent intent = new Intent(Pairinglist.this,Mapfragment.class);
        intent.putExtra("name",name.trim());
        intent.putExtra("imageurl",imageurl);
        startActivity(intent);
        finish();
    }


}
