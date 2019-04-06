package com.matrixfrats.syntex;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by DELL on 26-01-2018.
 */

public class Appid extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String id_receive = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences savingappid = getSharedPreferences("appid", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = savingappid.edit();
        editor.putString("appid",id_receive);
        editor.commit();

    }
}
