package com.matrixfrats.syntex;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DELL on 01-02-2018.
 */

public class Requestsendedtoyou extends AppCompatActivity implements View.OnClickListener {

    private static final String DEFAULT = "N/A";
    private final String showingrequest = "http://matrixfrats.com/gpslocation/showingrequest.php";
    private final String acceptrequest = "http://matrixfrats.com/gpslocation/acceptrequest.php";
    private final String deleterequest = "http://matrixfrats.com/gpslocation/deleterequest.php";
    public ArrayList<Trialgettersetter> arrayList;
    private RecyclerView recyclerView;
    private RelativeLayout refreshbuttonandtextview;
    private Button refreshbuttoninrequestreceived;
    private TextView errormessageinrequestreceived;
    private Requestreceivedadaptor adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressDialog progressdialog;

    @Override
    protected void onStart() {
        super.onStart();
        refreshbuttonandtextview.setVisibility(View.GONE);
        progressdialog = new ProgressDialog(this);
        progressdialog.setCanceledOnTouchOutside(false);
        progressdialog.setMessage("please wait...");
        progressdialog.show();
        showinglist();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requestsendedtoyou);
        setTitle("Pending Request");
        findview();
        fillarraylist();
        setrecycleview();
    }

    private void findview() {
        recyclerView = (RecyclerView) findViewById(R.id.recycleviewinrequestsended);
        refreshbuttonandtextview = (RelativeLayout) findViewById(R.id.refreshbuttonandtextview);
        refreshbuttoninrequestreceived = (Button) findViewById(R.id.refreshbuttoninrequestreceived);
        errormessageinrequestreceived = (TextView) findViewById(R.id.errormessageinrequestreceived);
        refreshbuttoninrequestreceived.setOnClickListener(this);
    }

    private void setrecycleview() {
        recyclerView.setHasFixedSize(true);
        adapter = new Requestreceivedadaptor(arrayList, this);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnitemclicklistener(new Requestreceivedadaptor.onItemclicklistener() {
            @Override
            public void addrequest(int position, String name) {
                 alertdialog(acceptrequest,"By accepting Request your location can be traced by "+name+" . Do you still want to accept Request.",name,position);
            }

            @Override
            public void deleterequest(int position, String name) {
                 alertdialog(deleterequest,"Are you sure you want to  delete request from "+name+".",name,position);
            }
        });
    }

    private void fillarraylist() {
        arrayList = new ArrayList<>();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.refreshbuttoninrequestreceived:
                showinglist();
                progressdialog.show();
                break;
        }
    }

    private void showinglist() {
        if (arrayList.size() != 0) {
            arrayList.clear();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, showingrequest, new Response.Listener<String>() {
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
                                arrayList.add(new Trialgettersetter(jsonObject.getString("personwhosendedimage"), jsonObject.getString("personwhosendedrequest"),
                                        ""+jsonObject.getString("personwhosendedrequest").toUpperCase().charAt(0)));
                                adapter.notifyDataSetChanged();
                                count++;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        visiblerefresh();
                        refreshbuttoninrequestreceived.setVisibility(View.GONE);
                        errormessageinrequestreceived.setText("No Request Found");
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
        Mysingleton.getInstance(this.getApplicationContext()).addtorequestque(stringRequest, "receivedrequest");
    }


    private String getsharedprefname() {
        SharedPreferences gettingusername = getSharedPreferences("savingusername", Context.MODE_PRIVATE);
        return gettingusername.getString("name", DEFAULT);
    }


    private void acceptanddelete(final String s, final String name, final int position) {
        StringRequest acceptorreject = new StringRequest(Request.Method.POST,s, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                 if(response.equals("success")){
                     progressdialog.cancel();
                     arrayList.remove(position);
                     adapter.notifyDataSetChanged();
                     if(arrayList.size() == 0){
                         visiblerefresh();
                         refreshbuttoninrequestreceived.setVisibility(View.GONE);
                         errormessageinrequestreceived.setText("No Request Found");
                     }
                     sendnotification(s,name);
                     erroralertdialog("Successful...");

                 }else {
                     progressdialog.cancel();
                     erroralertdialog("Connection Problem..");
                 }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressdialog.cancel();
                erroralertdialog("Connection Problem");
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("username", getsharedprefname());
                map.put("requestname",name);
                return map;
            }
        };
        Mysingleton.getInstance(this.getApplicationContext()).addtorequestque(acceptorreject, "acceptorreject");
    }


    private void sendnotification(String url ,String nametopair){

        if (url.equals(acceptrequest)){

            new Sendnotification(nametopair,"Knock Knock",""+getsharedprefname()+" accepted your pairing request",
                    "com.matrixfrats.pairinglist",Requestsendedtoyou.this.getApplicationContext());

        }else if (url.equals(deleterequest)){

            new Sendnotification(nametopair,"Knock Knock",""+getsharedprefname()+" deletes your pairing request",
                    "com.matrixfrats.nothingtoopen",Requestsendedtoyou.this.getApplicationContext());

        }else {
            return;
        }

    }

    private void alertdialog(final String url,String s, final String name, final int position) {

        AlertDialog.Builder alertdialog = new AlertDialog.Builder(Requestsendedtoyou.this);
        alertdialog.setMessage(s).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        acceptanddelete(url,name,position);
                        progressdialog.show();
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
        showalertdialog.setTitle("Attention");
        showalertdialog.show();

    }

    private void erroralertdialog(String s) {

        AlertDialog.Builder alertdialog = new AlertDialog.Builder(Requestsendedtoyou.this);
        alertdialog.setMessage(s).setCancelable(false)
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
        refreshbuttonandtextview.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

    }

    private void visiblerefresh() {
        recyclerView.setVisibility(View.GONE);
        refreshbuttonandtextview.setVisibility(View.VISIBLE);
    }

    private void buttonvisibility(String s) {
        refreshbuttoninrequestreceived.setVisibility(View.VISIBLE);
        errormessageinrequestreceived.setText(s);
    }
}
