package com.matrixfrats.syntex;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by DELL on 25-01-2018.
 */

public class Mysingleton {

    private static Mysingleton mysingleton;
    private static Context context;
    private RequestQueue requestQueue;


    private Mysingleton(Context c) {
        context = c;
        requestQueue = requestQueue();
    }

    public static synchronized Mysingleton getInstance(Context context) {

        if (mysingleton == null) {
            mysingleton = new Mysingleton(context);
        }
        return mysingleton;
    }

    public RequestQueue requestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());

        }
        return requestQueue;
    }

    public <T> void addtorequestque(Request<T> request) {
        requestQueue.add(request);
    }

    public <T> void addtorequestque(Request<T> request,String tag){
        request.setTag(TextUtils.isEmpty(tag) ? tag : "tag");
        requestQueue.add(request);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
}
