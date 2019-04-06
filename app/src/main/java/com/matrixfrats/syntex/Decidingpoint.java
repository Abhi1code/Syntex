package com.matrixfrats.syntex;

import android.*;
import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.security.Permission;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DELL on 26-01-2018.
 */

public class Decidingpoint extends AppCompatActivity implements View.OnClickListener {

    private static final String DEFAULT = "N/A";
    private static final int DEF = 1;
    private CardView searchbutton, paireddevices, accessedby, requestsendedtoyou,card,cardimage;
    private TextView name,cardtextview;
    private CircleImageView carduserimage;
    private ObjectAnimator animator;

    @Override
    protected void onStart() {
        super.onStart();
        name.setText(getusername().toUpperCase());
        cardtextview.setText(""+getusername().toUpperCase().charAt(0));
        SharedPreferences pref = getSharedPreferences("path", Context.MODE_PRIVATE);
        SharedPreferences gettingusername = getSharedPreferences("savingusername", Context.MODE_PRIVATE);
        if(!pref.getString("path","error").equals("error") && !gettingusername.getString("downloadurl","imagenotuploaded").equals("imagenotuploaded")){
            carduserimage.setImageBitmap(BitmapFactory.decodeFile(pref.getString("path","error")));
        }
        checkpermission();

    }

    @Override
    public void onCreate(Bundle savedInstanceStatee) {
        super.onCreate(savedInstanceStatee);
        setContentView(R.layout.decidingpoint);
        setTitle("Profile");
        findview();
        animation();
    }

    private void animation(){
        //starting animation
        Animation anime = AnimationUtils.loadAnimation(this, R.anim.bottom);
        card.setAnimation(anime);
        //animation in image
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        cardimage.setCameraDistance(scale);
        animator = ObjectAnimator.ofFloat(cardimage, "rotationY", 0, 720);
        animator.setDuration(600);
    }

    private void findview() {

        searchbutton = (CardView) findViewById(R.id.searchbutton);
        paireddevices = (CardView) findViewById(R.id.paireddevices);
        accessedby = (CardView) findViewById(R.id.accessedby);
        requestsendedtoyou = (CardView) findViewById(R.id.requestsendedtoyou);
        card = (CardView)findViewById(R.id.card);
        cardimage = (CardView)findViewById(R.id.cardimage);
        name = (TextView) findViewById(R.id.name);
        cardtextview = (TextView)findViewById(R.id.cardtextview);
        carduserimage = (CircleImageView)findViewById(R.id.carduserimage);
        searchbutton.setOnClickListener(this);
        paireddevices.setOnClickListener(this);
        accessedby.setOnClickListener(this);
        requestsendedtoyou.setOnClickListener(this);
        cardimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animator.start();
            }
        });
    }

    @Override
    public void onClick(View view) {

            uploadappkey("default");

        switch (view.getId()) {
            case R.id.searchbutton:
                startActivity(new Intent(this, Searchfriend.class));
                break;
            case R.id.paireddevices:
                startActivity(new Intent(this, Pairinglist.class));
                break;
            case R.id.accessedby:
                startActivity(new Intent(this,Locationaccessedby.class));
                break;
            case R.id.requestsendedtoyou:
                startActivity(new Intent(this, Requestsendedtoyou.class));
                break;
        }
    }

    private String getusername() {
        SharedPreferences gettingusername = getSharedPreferences("savingusername", Context.MODE_PRIVATE);
        return gettingusername.getString("name", DEFAULT);
    }

    private void uploadappkey(String downloadurl) {


        SharedPreferences pref = getSharedPreferences("sendingappkeystatusid", Context.MODE_PRIVATE);
        switch (pref.getInt("key", DEF)) {
            case 0:
                Sendingkey sendingkey = new Sendingkey(this, getusername(),downloadurl);
                break;
            case 1:

                break;
        }
    }

    private void checkpermission(){
        if(Build.VERSION.SDK_INT >= 23 &&
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},001);

        }else {
            startService(new Intent(this, Uploadlatlongservice.class));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 001) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startService(new Intent(this,Uploadlatlongservice.class));
            } else {
                alertdialog();
            }
        }
        if(requestCode == 002) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startService(new Intent(this,Uploadlatlongservice.class));
            }
        }
    }

    private void alertdialog() {

        AlertDialog.Builder alertdialog = new AlertDialog.Builder(this);
        alertdialog.setMessage("This app is based on location services so " +
                "please provide us permission to use your phone location").setCancelable(false)
                .setPositiveButton("ALLOW", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        dialogInterface.dismiss();
                        if(Build.VERSION.SDK_INT >= 23){
                            requestPermissions(
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},002);

                        }
                    }

                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                dialogInterface.dismiss();
            }
        });

        AlertDialog showalertdialog = alertdialog.create();
        showalertdialog.setTitle("Permission Request");
        showalertdialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuforfeedback,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.feedback:
                   startActivity(new Intent(this,Sendfeedback.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
