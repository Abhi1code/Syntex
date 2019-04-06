package com.matrixfrats.syntex;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DELL on 26-01-2018.
 */

public class Sendingkey {

    Context context;
    private static final String DEFAULT = "N/A";
    private final String savingappkeyurl = "http://matrixfrats.com/gpslocation/savingappkey.php";
    private final String name;
    private final String downloadurl;

    public Sendingkey(Context c,final String name,final String downloadurl) {

        context = c;
        this.name = name;
        this.downloadurl = downloadurl;
        sendingkey();

    }

    private String gettingkey() {

        SharedPreferences sharedPreferences = context.getSharedPreferences("appid", Context.MODE_PRIVATE);
        return sharedPreferences.getString("appid", DEFAULT);

    }

    private void sendingkey() {

        SharedPreferences shared = context.getSharedPreferences("sendingappkeystatusid",Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = shared.edit();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, savingappkeyurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                       if(response.equals("connectionerror!!")){
                           editor.putInt("key",0);
                           editor.commit();
                       }
                        if(response.equals("success")){
                            editor.putInt("key",1);
                            editor.commit();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                     editor.putInt("key",0);
                     editor.commit();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name",name);
                params.put("id",gettingkey());
                params.put("downloadurl",downloadurl);
                return params;
            }
        };
        if(!gettingkey().equals("N/A")) {
            Mysingleton.getInstance(context.getApplicationContext()).addtorequestque(stringRequest);
        }
    }


}
