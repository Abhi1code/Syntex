package com.matrixfrats.syntex;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shuhart.stepview.StepView;
import com.squareup.picasso.Picasso;


import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by DELL on 04-02-2018.
 */

public class Mapfragment extends AppCompatActivity implements OnMapReadyCallback, PopupMenu.OnMenuItemClickListener {

    private static final String NA = "N/A";
    private LinearLayout maplayout;
    private RelativeLayout errorlayout;
    private TextView cardtextview1, cardtextview2, cardtextview3, cardtextview4, usernameincard, locatornameincard;
    private String locatorname;
    private String locatorimage;
    private GoogleMap mgooglemap;
    private DatabaseReference databaseReference;
    private Handler handler;
    private Marker locatormarker;
    private Marker usermarker;
    private BroadcastReceiver latlongreceiver = null;
    private ValueEventListener latlongvalueeventlistener;
    private Button typeofmap;
    private CardView card1, card2, card3, card4, usercardbase, locatorcardbase, userimagecardview, locatorimagecardview;
    private StepView stepview;
    private CircleImageView locatorimageincard, userimageincard;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        if (googleplayservices()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setContentView(R.layout.mapfragment);

            getviews();
            initiatemaps();

        } else {
            setContentView(R.layout.googlemapnotpresent);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent().getStringExtra("name") != null) {
            locatorname = getIntent().getStringExtra("name").trim();
            locatorimage = getIntent().getStringExtra("imageurl").trim();
        } else {
            finish();
        }
        setimages();
        handler = new Handler();
        typeofmap.setVisibility(View.GONE);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        setyouraccessinlocatorid();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.child(locatorname).child("accessed by").child(getsharedprefname()).removeValue();
        databaseReference.child(getsharedprefname()).child("accessed by").child(getsharedprefname()).removeValue();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (latlongreceiver != null) {
            unregisterReceiver(latlongreceiver);
        }
        if (latlongvalueeventlistener != null) {
            databaseReference.child(locatorname).child("latlon").removeEventListener(latlongvalueeventlistener);
        }
        startActivity(new Intent(this, Pairinglist.class));
    }

    public boolean googleplayservices() {

        GoogleApiAvailability gaa = GoogleApiAvailability.getInstance();
        int available = gaa.isGooglePlayServicesAvailable(this);
        if (available == ConnectionResult.SUCCESS) {
            return true;
        } else if (gaa.isUserResolvableError(available)) {
            Dialog dialog = gaa.getErrorDialog(this, available, 0);
            dialog.show();

        } else {
            Toast.makeText(this, "Fail To Get Google Maps", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void initiatemaps() {
        MapFragment mf = (MapFragment) getFragmentManager().findFragmentById(R.id.mapfragment);
        mf.getMapAsync(this);
    }

    private void getviews() {
        maplayout = (LinearLayout) findViewById(R.id.maplayout);
        errorlayout = (RelativeLayout) findViewById(R.id.errorlayout);
        card1 = (CardView) findViewById(R.id.card1);
        card2 = (CardView) findViewById(R.id.card2);
        card3 = (CardView) findViewById(R.id.card3);
        card4 = (CardView) findViewById(R.id.card4);
        usercardbase = (CardView) findViewById(R.id.usercardbase);
        locatorcardbase = (CardView) findViewById(R.id.locatorcardbase);
        userimagecardview = (CardView) findViewById(R.id.userimagecardview);
        locatorimagecardview = (CardView) findViewById(R.id.locatorimagecardview);
        cardtextview1 = (TextView) findViewById(R.id.cardtextview1);
        cardtextview2 = (TextView) findViewById(R.id.cardtextview2);
        cardtextview3 = (TextView) findViewById(R.id.cardtextview3);
        cardtextview4 = (TextView) findViewById(R.id.cardtextview4);
        usernameincard = (TextView) findViewById(R.id.usernameincard);
        locatornameincard = (TextView) findViewById(R.id.locatornameincard);
        userimageincard = (CircleImageView) findViewById(R.id.userimageincard);
        locatorimageincard = (CircleImageView) findViewById(R.id.locatorimageincard);
        stepview = (StepView) findViewById(R.id.stepview);
        typeofmap = (Button) findViewById(R.id.typeofmap);
        typeofmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupmenu = new PopupMenu(Mapfragment.this, view);
                popupmenu.setOnMenuItemClickListener(Mapfragment.this);
                popupmenu.inflate(R.menu.menuformap);
                popupmenu.show();
            }
        });
        usercardbase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(usermarker != null) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng
                            (new LatLng(usermarker.getPosition().latitude, usermarker.getPosition().longitude));
                    mgooglemap.animateCamera(cameraUpdate);
                }
            }
        });
        locatorcardbase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locatormarker != null) {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng
                            (new LatLng(locatormarker.getPosition().latitude, locatormarker.getPosition().longitude));
                    mgooglemap.animateCamera(cameraUpdate);
                }
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.normal:
                mgooglemap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid:
                mgooglemap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite:
                mgooglemap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain:
                mgooglemap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return false;
        }
    }

    private String getsharedprefname() {
        SharedPreferences gettingusername = getSharedPreferences("savingusername", Context.MODE_PRIVATE);
        return gettingusername.getString("name", NA);
    }

    private void setyouraccessinlocatorid() {
        if (!checkinternetconnection()) {
            changingtext("Poor Internet\nConnection", 1, true);
        }
        databaseReference.child(locatorname).child("accessed by").child(getsharedprefname())
                .setValue(1, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        latlonglistener();
                    }
                });
    }

    private boolean checkinternetconnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    private void latlonglistener() {
        final Runnable runforlocator = new Runnable() {
            @Override
            public void run() {
                handler.removeCallbacks(this);
                errorcontroller(1);
            }
        };

        latlongvalueeventlistener = databaseReference.child(locatorname).child("latlon").addValueEventListener(new ValueEventListener() {

            int counter = 0;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                handler.removeCallbacks(runforlocator);
                if (counter == 0) {
                    changingtext("Trying to access\n" + locatorname + "\n" + " Location", 2, false);
                    handler.postDelayed(runforlocator, 45000);
                } else {
                    String latlongstring = dataSnapshot.getValue(String.class);
                    String[] latandlong = latlongstring.split(",");
                    LatLng locatorlatlongobject = new LatLng(Double.parseDouble(latandlong[0]), Double.parseDouble(latandlong[1]));
                    locatormarker(locatorlatlongobject);
                    if (locatormarker != null && usermarker != null) {
                        visibilityofmap();
                    }
                    handler.postDelayed(runforlocator, 120000);
                    if (counter == 1 && usermarker == null) {
                        changingtext("Trying to access\n your Location", 3, false);
                        checkinglocationpermission();
                    }
                }
                counter++;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                handler.removeCallbacks(runforlocator);
                errorcontroller(2);
            }
        });
    }

    private void locatormarker(LatLng locatorlatlongobject) {
        if (locatormarker != null) {
            locatormarker.remove();
        }
        MarkerOptions locatormarkeroptions = new MarkerOptions()
                .title(locatorname + " Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .position(locatorlatlongobject);
        if (mgooglemap != null) {
            locatormarker = mgooglemap.addMarker(locatormarkeroptions);
            locatormarker.showInfoWindow();
        }
    }

    private void usermarker(LatLng userlatlongobject) {
        if (usermarker != null) {
            usermarker.remove();
        }
        MarkerOptions usermarkeroptions = new MarkerOptions()
                .title("Your Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .position(userlatlongobject);
        if (mgooglemap != null) {
            usermarker = mgooglemap.addMarker(usermarkeroptions);
            usermarker.showInfoWindow();
        }
    }

    private void checkinglocationpermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setyouraccessinuserid();

        } else {
            Intent permissionintent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            permissionintent.setData(uri);
            startActivity(permissionintent);
            Toast.makeText(this, "Please provide location permission", Toast.LENGTH_LONG).show();
        }
    }

    private void setyouraccessinuserid() {

        databaseReference.child(getsharedprefname()).child("accessed by").child(getsharedprefname())
                .setValue(1, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                        startService(new Intent(Mapfragment.this, Uploadlatlongservice.class));
                        receivers();
                    }
                });
    }


    private void visibilityofmap() {
        maplayout.setVisibility(View.VISIBLE);
        errorlayout.setVisibility(View.GONE);
        typeofmap.setVisibility(View.VISIBLE);
        usercardbase.setVisibility(View.VISIBLE);
        locatorcardbase.setVisibility(View.VISIBLE);
    }

    private void visibilityoferror() {
        errorlayout.setVisibility(View.VISIBLE);
        maplayout.setVisibility(View.GONE);
        typeofmap.setVisibility(View.GONE);
        usercardbase.setVisibility(View.INVISIBLE);
        locatorcardbase.setVisibility(View.INVISIBLE);
    }

    private void receivers() {
        final Runnable runforuser = new Runnable() {
            @Override
            public void run() {
                handler.removeCallbacks(this);
                changingtext("Unable to update \nyour Location", 3, true);
                //errorcontroller(3);
            }
        };
        //latlong receiver
        if (latlongreceiver == null) {
            latlongreceiver = new BroadcastReceiver() {
                int counter = 0;

                @Override
                public void onReceive(Context context, Intent intent) {
                    handler.removeCallbacks(runforuser);
                    if (intent.getExtras().getString("cordinates") != null) {
                        String latlong = intent.getExtras().getString("cordinates");
                        String[] latandlong = latlong.split(",");
                        LatLng latlongobject = new LatLng(Double.parseDouble(latandlong[0]), Double.parseDouble(latandlong[1]));
                        usermarker(latlongobject);
                        if (counter == 0) {
                            finalshow();
                            counter++;
                        }
                    }
                    handler.postDelayed(runforuser, 45000);
                }
            };
        }
        registerReceiver(latlongreceiver, new IntentFilter("location update"));
        handler.postDelayed(runforuser, 30000);

    }

    private void finalshow() {
        changingtext("Plotting Locations", 4, false);
        if (locatormarker != null && usermarker != null) {
            visibilityofmap();
            zoom();
        } else {
            changingtext("Something goes\nwrong", 4, true);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mgooglemap = googleMap;
        mgooglemap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mgooglemap.getUiSettings().setZoomControlsEnabled(true);
        mgooglemap.getUiSettings().setZoomGesturesEnabled(true);
        mgooglemap.getUiSettings().setMapToolbarEnabled(false);
        mgooglemap.setIndoorEnabled(true);
        mgooglemap.setBuildingsEnabled(true);

    }

    private void zoom() {
        Double latoflocator = locatormarker.getPosition().latitude;
        Double longoflocator = locatormarker.getPosition().longitude;
        Double latofuser = usermarker.getPosition().latitude;
        Double longofuser = usermarker.getPosition().longitude;
        float finalresult[] = new float[10];
        Location.distanceBetween(latoflocator, longoflocator, latofuser, longofuser, finalresult);
        LatLng latlng = new LatLng((latoflocator + latofuser) / 2, (longoflocator + longofuser) / 2);
        //Toast.makeText(this, "" + finalresult[0] / 1000, Toast.LENGTH_SHORT).show();
        Integer zoomlevel = 2;

        if (finalresult[0] / 1000 > 3000) {
            zoomlevel = 2;
        } else if (finalresult[0] / 1000 < 3000 && finalresult[0] / 1000 > 500) {
            zoomlevel = 4;
        } else if (finalresult[0] / 1000 < 500 && finalresult[0] / 1000 > 50) {
            zoomlevel = 7;
        } else if (finalresult[0] / 1000 < 50 && finalresult[0] / 1000 > 2) {
            zoomlevel = 10;
        } else if (finalresult[0] / 1000 < 2 && finalresult[0] / 1000 > 0.5) {
            zoomlevel = 14;
        } else if (finalresult[0] / 1000 < 0.5) {
            zoomlevel = 17;
        }

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, zoomlevel);
        mgooglemap.animateCamera(cameraUpdate);
    }

    private void errorcontroller(int i) {

        if (i == 1 || i == 2) {
            changingtext("Not getting Location\nUpdates from " + locatorname + "\nPlease ensure " +
                    "that " + locatorname + "\nis using this app\n and had active Internet\n Connection", 2, true);
            if (locatormarker != null) {
                locatormarker.remove();
            }
        }
        visibilityoferror();
    }


    private void changingtext(String message, Integer cardnumber, Boolean error) {
        stepview.done(false);
        stepview.go(cardnumber - 1, true);
        switch (cardnumber) {
            case 1:
                cardtextview1.setText(message);
                if (error) {
                    cardtextview1.setTextColor(Color.RED);
                } else {
                    cardtextview1.setTextColor(Color.BLACK);
                }
                card1.setVisibility(View.VISIBLE);
                card2.setVisibility(View.INVISIBLE);
                card3.setVisibility(View.INVISIBLE);
                card4.setVisibility(View.INVISIBLE);
                break;
            case 2:
                cardtextview2.setText(message);
                if (error) {
                    cardtextview2.setTextColor(Color.RED);
                } else {
                    cardtextview2.setTextColor(Color.BLACK);
                }
                card1.setVisibility(View.INVISIBLE);
                card2.setVisibility(View.VISIBLE);
                card3.setVisibility(View.INVISIBLE);
                card4.setVisibility(View.INVISIBLE);
                break;
            case 3:
                cardtextview3.setText(message);
                if (error) {
                    cardtextview3.setTextColor(Color.RED);
                } else {
                    cardtextview3.setTextColor(Color.BLACK);
                }
                card1.setVisibility(View.INVISIBLE);
                card2.setVisibility(View.INVISIBLE);
                card3.setVisibility(View.VISIBLE);
                card4.setVisibility(View.INVISIBLE);
                break;
            case 4:
                cardtextview4.setText(message);
                if (error) {
                    cardtextview4.setTextColor(Color.RED);
                } else {
                    cardtextview4.setTextColor(Color.BLACK);
                }
                card1.setVisibility(View.INVISIBLE);
                card2.setVisibility(View.INVISIBLE);
                card3.setVisibility(View.INVISIBLE);
                card4.setVisibility(View.VISIBLE);
                break;
            default:
                cardtextview2.setText(message);
                if (error) {
                    cardtextview2.setTextColor(Color.RED);
                } else {
                    cardtextview2.setTextColor(Color.BLACK);
                }
                card1.setVisibility(View.INVISIBLE);
                card2.setVisibility(View.VISIBLE);
                card3.setVisibility(View.INVISIBLE);
                card4.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void setimages() {
        userimagecardview.setCardBackgroundColor(getRandomColor());
        locatorimagecardview.setCardBackgroundColor(getRandomColor());
        usernameincard.setText("" + getsharedprefname().toUpperCase().charAt(0));
        locatornameincard.setText("" + locatorname.toUpperCase().charAt(0));
        SharedPreferences pref = getSharedPreferences("path", Context.MODE_PRIVATE);
        SharedPreferences gettingusername = getSharedPreferences("savingusername", Context.MODE_PRIVATE);
        if (!pref.getString("path", "error").equals("error") && !gettingusername.getString("downloadurl","imagenotuploaded").equals("imagenotuploaded")) {
            userimageincard.setImageBitmap(BitmapFactory.decodeFile(pref.getString("path", "error")));
        }
        if (!locatorimage.equals("default") && locatorimage != null && !TextUtils.isEmpty(locatorimage)) {
            Picasso.with(this).load(Uri.parse(locatorimage)).fit().centerCrop().into(locatorimageincard);
        }
    }

    private int getRandomColor() {
        Random rand = new Random();
        String color[] = {"#ff0000", "#000080", "#191970", "#FF4500", "#4169E1", "#8B4513", "#FA8072", "#2E8B57", "#FFFF00", "#0000ff",
                "#006400", "#800000", "#6B8E23", "#800080", "#C71585", "#B22222", "#D2691E", "#2F4F4F", "#008B8B", "#FF1493"};

        return Color.parseColor(color[rand.nextInt(20)]);
    }
}
