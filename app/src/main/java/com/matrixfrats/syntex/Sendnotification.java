package com.matrixfrats.syntex;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Sendnotification {

    private String whomtosend,title,message,clickaction;
    private Context context;
    private String sendurl = "http://matrixfrats.com/gpslocation/sendingnotification.php";

    public Sendnotification(String whomtosend, String title, String message, String clickaction, Context context){

        this.whomtosend = whomtosend;
        this.title = title;
        this.message = message;
        this.clickaction = clickaction;
        this.context = context;
        sendnotification();
    }

    public void sendnotification(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, sendurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("whomtosend",whomtosend);
                params.put("clickactivity",clickaction);
                params.put("message",message);
                params.put("title",title);
                return params;
            }
        };

        Mysingleton.getInstance(context).addtorequestque(stringRequest);
    }
}
