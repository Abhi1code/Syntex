package com.matrixfrats.syntex;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DELL on 28-01-2018.
 */

public class Friendsearched extends AppCompatActivity implements View.OnClickListener{

    private static final String DEFAULT = "N/A";
    private CircleImageView searchedimage;
    private TextView searchedname,status,cardfinaltextview;
    private Button clicktopair,refresh;
    private ProgressDialog progressdialog;
    private final String matchnameurl = "http://matrixfrats.com/gpslocation/requeststatus.php";
    private final String sendrequesturl = "http://matrixfrats.com/gpslocation/sendrequest.php";

    @Override
    protected void onStart() {
        super.onStart();
        final String nametopair = getIntent().getStringExtra("searchedname");

        if(nametopair!=null) {
            searchedname.setText(nametopair.toUpperCase());
            cardfinaltextview.setText(""+nametopair.toUpperCase().charAt(0));
        }
        showimage();
        progressdialog = new ProgressDialog(this);
        progressdialog.setTitle("Getting status");
        progressdialog.setCanceledOnTouchOutside(false);
        progressdialog.setMessage("Please wait...");
        progressdialog.show();
        getstatus(nametopair);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friendsearched);
        setTitle("Status");
        getviews();

    }


    private void getviews(){
        searchedimage = (CircleImageView) findViewById(R.id.searchedimage);
        searchedname = (TextView)findViewById(R.id.searchedtext);
        clicktopair = (Button)findViewById(R.id.buttontopair);
        status = (TextView)findViewById(R.id.status);
        cardfinaltextview = (TextView)findViewById(R.id.cardfinaltextview);
        refresh = (Button)findViewById(R.id.refresh);
        clicktopair.setOnClickListener(this);
        refresh.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttontopair :
                     progressdialog.show();
                     maintainbuttonclicks();
                     sendrequest(getIntent().getStringExtra("searchedname"));
                break;
            case R.id.refresh :
                     progressdialog.show();
                     Mysingleton.getInstance(this.getApplicationContext()).cancelPendingRequests("getstatus");
                     getstatus(getIntent().getStringExtra("searchedname"));
                break;
        }
    }

    private String getsharedprefname(){
        SharedPreferences gettingusername = getSharedPreferences("savingusername", Context.MODE_PRIVATE);
        return gettingusername.getString("name",DEFAULT);
    }

    private void getstatus(final String nametopair){
        if(!TextUtils.isEmpty(nametopair)){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, matchnameurl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                             if(response.equals("Notpaired")){
                                 settext("Not Paired");
                                 clicktopair.setEnabled(true);
                                 clicktopair.setClickable(true);
                                 progressdialog.cancel();
                             }else {
                                 if(response.equals("0")){
                                     settext("Request Pending");
                                     maintainbuttonclicks();
                                     progressdialog.cancel();
                                 } else {
                                     if(response.equals("1")){
                                         settext("Paired");
                                         maintainbuttonclicks();
                                         progressdialog.cancel();
                                     }else {
                                         if(response.equals("ConnectionProblem")){
                                             settext("Connection Error");
                                             maintainbuttonclicks();
                                             progressdialog.cancel();
                                         }else {
                                             maintainbuttonclicks();
                                             settext("Connection Error");
                                             progressdialog.cancel();
                                         }
                                     }
                                 }
                             }
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    settext("Connection Error");
                    maintainbuttonclicks();
                    progressdialog.cancel();
                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("personwhosended", getsharedprefname());
                    params.put("persontowhomsended", nametopair);
                    return params;
                }
            };
            Mysingleton.getInstance(this.getApplicationContext()).addtorequestque(stringRequest,"getstatus");
        }else {
            settext("Connection Error");
            maintainbuttonclicks();
            progressdialog.cancel();
        }

    }

    private void sendrequest(final String nametopair){
        if(!TextUtils.isEmpty(nametopair)){
            StringRequest stringRequest = new StringRequest(Request.Method.POST, sendrequesturl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                           if(response.equals("success")){
                               settext("Request Sended");
                               alertdialog("Request Sended");
                               maintainbuttonclicks();

                               new Sendnotification(nametopair,"Knock Knock","Pairing request from "+getsharedprefname(),
                                       "com.matrixfrats.requestsendedtoyou",Friendsearched.this.getApplicationContext());

                           }else {
                                   alertdialog("Connection Problem...");
                                   maintainbuttonclicks();
                                   settext("Try again later....");
                           }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    alertdialog("Connection Problem...");
                    maintainbuttonclicks();
                    settext("Connection Problem");
                }
            }
            ){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("personwhosended", getsharedprefname());
                    params.put("persontowhomsended", nametopair);
                    params.put("personwhosendedimage",getuserimagelink());
                    params.put("persontowhomsendedimage",getlocatorimagelink());
                    return params;
                }
            };
            Mysingleton.getInstance(this.getApplicationContext()).addtorequestque(stringRequest,"sendrequest");
        }else {
            settext("Connection Error");
            maintainbuttonclicks();
            progressdialog.cancel();
        }
    }

    private void settext(String s){
        //status.setTextColor(Color.argb(5,152,210,150));
        status.setText(s);
    }

    private void maintainbuttonclicks(){
        clicktopair.setEnabled(false);
        clicktopair.setClickable(false);
    }

    private void alertdialog(String s){
        progressdialog.cancel();
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(Friendsearched.this);
        alertdialog.setMessage(s).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.cancel();
                        dialogInterface.dismiss();
                    }

                });

        AlertDialog showalertdialog = alertdialog.create();
        showalertdialog.setTitle("Pairing Request");
        showalertdialog.show();

    }

    private void showimage(){
        final String downloadurl = getIntent().getStringExtra("downloadurl");
        if(downloadurl != null){
            Picasso.with(this).load(Uri.parse(downloadurl)).fit().centerCrop().into(searchedimage);
        }
    }

    private String getuserimagelink(){
        SharedPreferences downloadurl = getSharedPreferences("savingusername", Context.MODE_PRIVATE);
        return downloadurl.getString("downloadurl","default");
    }

    private String getlocatorimagelink(){
        final String downloadurl = getIntent().getStringExtra("downloadurl");
        if(downloadurl != null){
            return downloadurl;
        }else{
            return "default";
        }
    }
}
