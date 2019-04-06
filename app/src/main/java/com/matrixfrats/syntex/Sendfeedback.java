package com.matrixfrats.syntex;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by DELL on 30-03-2018.
 */

public class Sendfeedback extends AppCompatActivity implements View.OnClickListener {

    private static final String DEFAULT = "N/A";
    private EditText feedbackbox;
    private Button sendfeedback;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendfeedback);
        setTitle("Feedback");
        feedbackbox = (EditText) findViewById(R.id.feedbackbox);
        sendfeedback = (Button) findViewById(R.id.sendfeedback);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        sendfeedback.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String feedback = feedbackbox.getText().toString().trim();
        if (TextUtils.isEmpty(feedback)) {
            return;
        }
        closesoftkeyboard();
        databaseReference.child("feedback").child(getusername()).setValue(feedback);
        Toast.makeText(this, "Sending feedback..", Toast.LENGTH_SHORT).show();
        finish();
    }


    private String getusername() {
        SharedPreferences gettingusername = getSharedPreferences("savingusername", Context.MODE_PRIVATE);
        return gettingusername.getString("name", DEFAULT);
    }

    private void closesoftkeyboard() {
        View v = this.getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }


}
