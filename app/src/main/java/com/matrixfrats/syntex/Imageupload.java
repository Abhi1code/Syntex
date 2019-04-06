package com.matrixfrats.syntex;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

/**
 * Created by DELL on 19-03-2018.
 */

public class Imageupload extends AppCompatActivity {

    private CardView userimagecardview;
    private ImageView imageicon;
    private CircleImageView selectimage;
    private TextView justtextview,uploaderrormessage;
    private Button uploadphoto,skip;
    private static final String DEFAULT = "N/A";
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int REQUESTCODE = 121;
    private ProgressDialog progressdialog;
    private Uri uri;
    private File actualImage,compressedImage;
    private FirebaseAuth auth;

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference().child("userphotos").child(getsharedprefname()+".webp");
        auth = FirebaseAuth.getInstance();
        //userimagecardview.setCardBackgroundColor(getRandomColor());
        progressdialog = new ProgressDialog(this);
        progressdialog.setTitle("Uploading Image");
        progressdialog.setCancelable(false);
        progressdialog.setCanceledOnTouchOutside(false);
        progressdialog.setMessage("Please wait...");
        uploaderrormessage.setVisibility(View.INVISIBLE);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imageupload);
        setTitle("Choose Image");
        SharedPreferences gettingusername = getSharedPreferences("savingusername", Context.MODE_PRIVATE);
        if(!gettingusername.getString("downloadurl","imagenotuploaded").equals("imagenotuploaded")){
            startActivity(new Intent(this,Decidingpoint.class));
            finish();
        }
        findingview();
    }

    private void findingview() {
        userimagecardview = (CardView)findViewById(R.id.userimagecardview);
        imageicon = (ImageView)findViewById(R.id.imageicon);
        selectimage = (CircleImageView)findViewById(R.id.selectimage);
        justtextview = (TextView)findViewById(R.id.justtextview);
        uploadphoto = (Button)findViewById(R.id.uploadphoto);
        skip = (Button)findViewById(R.id.skip);
        uploaderrormessage = (TextView)findViewById(R.id.uploaderrormessage);

        userimagecardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUESTCODE);
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Imageupload.this,Decidingpoint.class));
                finish();
            }
        });

        uploadphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploaderrormessage.setVisibility(View.INVISIBLE);
                uploadphoto();
            }
        });

    }

    private String getsharedprefname(){
        SharedPreferences gettingusername = getSharedPreferences("savingusername", Context.MODE_PRIVATE);
        return gettingusername.getString("name",DEFAULT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == REQUESTCODE){
            if(data == null){
                Toast.makeText(this, "Failed to open picture", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                uri = data.getData();
                actualImage = FileUtil.from(this, data.getData());
                customCompressImage();
                if(compressedImage == null){
                    return;
                }
                selectimage.setImageBitmap(BitmapFactory.decodeFile(compressedImage.getAbsolutePath()));
                SharedPreferences pref = getSharedPreferences("path", Context.MODE_PRIVATE);
                SharedPreferences.Editor prefeditor = pref.edit();
                prefeditor.putString("path",compressedImage.getPath());
                prefeditor.commit();

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private void customCompressImage() {
        if (actualImage == null) {
            return;
        } else {
            try {
                compressedImage = new Compressor(this)
                        .setMaxWidth(100)
                        .setMaxHeight(100)
                        .setQuality(40)
                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                        .setDestinationDirectoryPath(getFilesDir().getAbsolutePath())
                        .compressToFile(actualImage);

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

        }
    }

    private boolean checkinternetconnection(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }else {
            return false;
        }
    }

    private void uploadphoto(){
        progressdialog.show();
        SharedPreferences shared = getSharedPreferences("path",Context.MODE_PRIVATE);
        SharedPreferences gettingusername = getSharedPreferences("savingusername", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = gettingusername.edit();

        if(shared.getString("path","error").equals("error") || uri == null){
            Toast.makeText(this, "Please select image", Toast.LENGTH_SHORT).show();
            progressdialog.cancel();
            return;
        }
        if(!checkinternetconnection()){
            progressdialog.cancel();
            uploaderrormessage.setVisibility(View.VISIBLE);
            return;
        }
        storageReference.putFile(Uri.fromFile(new File(shared.getString("path",DEFAULT)))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Uri downloadurl = taskSnapshot.getDownloadUrl();
                databaseReference.child("users").child(auth.getCurrentUser().getUid()).child("image").setValue(downloadurl.toString());
                //databaseReference.child(getsharedprefname()).child("image").setValue(downloadurl.toString());
                editor.putString("downloadurl",downloadurl.toString());
                editor.commit();
                progressdialog.cancel();
                startActivity(new Intent(Imageupload.this,Decidingpoint.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressdialog.cancel();
                uploaderrormessage.setVisibility(View.VISIBLE);
                return;
            }
        });
    }

}
