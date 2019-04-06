package com.matrixfrats.syntex;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by DELL on 25-03-2018.
 */

public class Dialog extends AppCompatDialogFragment {

    private String name;
    private TextView message;
    private CheckBox checkBox;
    private Dialogclicklistener dialogclicklistener;

    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        name = getArguments().getString("name");

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.dialog,null);

        alertDialog.setView(v)
                .setTitle("Alert")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                 if(checkBox.isChecked()){
                     dialogclicklistener.startactivity(true,name);
                 }else {
                     dialogclicklistener.startactivity(false,name);
                 }
            }
        });
        message = (TextView)v.findViewById(R.id.dialogmessage);
        checkBox = (CheckBox)v.findViewById(R.id.checkBox);
        message.setText("Make sure that "+name+" is using this app and have " +
                "active internet connection or otherwise location of "+name+" would not be accessed.");

        return alertDialog.create();
    }

    public interface Dialogclicklistener{
        void startactivity(Boolean checkedstatus,final String name);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            dialogclicklistener = (Dialogclicklistener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString()+" must implement Dialogclicklistener");
        }
    }
}
