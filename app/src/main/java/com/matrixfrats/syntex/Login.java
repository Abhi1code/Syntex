package com.matrixfrats.syntex;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Login extends AppCompatActivity implements TextWatcher {

    private EditText nameinput;
    private TextView errormessage;
    private Button login;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener listener;
    private DatabaseReference databasereference;
    private ProgressDialog progressdialog;

    @Override
    protected void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(listener);
        databasereference = FirebaseDatabase.getInstance().getReference();
        progressdialog = new ProgressDialog(this);
        progressdialog.setTitle("Logging In");
        progressdialog.setCancelable(false);
        progressdialog.setCanceledOnTouchOutside(false);
        progressdialog.setMessage("Please wait...");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setTitle("Login");
        findingview();
        errormessage.setVisibility(View.INVISIBLE);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressdialog.show();
                closesoftkeyboard();
                register();

            }
        });


        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(Login.this, Imageupload.class));
                    finish();
                }
            }
        };
    }


    private void findingview() {

        nameinput = (EditText) findViewById(R.id.nameinput);
        errormessage = (TextView) findViewById(R.id.errormessage);
        login = (Button) findViewById(R.id.login);
        nameinput.addTextChangedListener(this);

    }

    private void closesoftkeyboard(){
        View v = this.getCurrentFocus();
        if(v != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(),0);
        }
    }

    private void register() {
        final String inputname = nameinput.getText().toString().trim();

        if (!TextUtils.isEmpty(inputname) && inputname.length() >= 6) {
            if(checkinternetconnection()) {

                auth.createUserWithEmailAndPassword(inputname + "@abc.com", inputname).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            auth.signInWithEmailAndPassword(inputname + "@abc.com", inputname);
                            savingusername(inputname);
                            DatabaseReference reference = databasereference.child("users");
                            DatabaseReference namereference = databasereference.child(inputname);
                            DatabaseReference accessreference = namereference.child("accessed by");
                            namereference.child("latlon").setValue("1,1");
                            accessreference.child(inputname).setValue(0);
                            DatabaseReference userdb = reference.child(auth.getCurrentUser().getUid());
                            userdb.child("name").setValue(inputname, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                   // progressdialog.cancel();
                                }
                            });
                        } else {
                            progressdialog.cancel();
                            errormessage("something went wrong ..");
                        }
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressdialog.cancel();
                                errormessage("username already exist..");
                            }
                        });
            }else {
                progressdialog.cancel();
                errormessage("Connection error !!");
            }
        } else {
            progressdialog.cancel();
            errormessage("More than 6 characters required");
        }

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (!TextUtils.isEmpty(editable)) {
            errormessage.setVisibility(View.INVISIBLE);
        } else {
            if (nameinput.length() <= 6) {
                errormessage("More than 6 characters required");
            } else {
                errormessage.setVisibility(View.INVISIBLE);
            }

        }
        if(nameinput.length() == 11){
            errormessage("Maximum limit of 11 characters");
            closesoftkeyboard();
        }
    }


    private void errormessage(String message) {
        errormessage.setText(message);
        errormessage.setVisibility(View.VISIBLE);
    }

    private void savingusername(final String name) {
        SharedPreferences pref = getSharedPreferences("sendingappkeystatusid", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefeditor = pref.edit();
        prefeditor.putInt("key",0);
        prefeditor.commit();
        SharedPreferences savingusername = getSharedPreferences("savingusername", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = savingusername.edit();
        editor.putString("name", name);
        editor.commit();
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

}