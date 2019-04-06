package com.matrixfrats.syntex;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DELL on 03-02-2018.
 */

public class Pairinglistadaptor extends RecyclerView.Adapter<Pairinglistadaptor.Viewholder> {

    private ArrayList<Trialgettersetter> marraylist;
    private Context c;
    public onItemclicklistener mlistener;

    public interface onItemclicklistener{
        void  tracelocation(int position,String name);
        void  deletepairing(int position,String name);
    }

    public void setOnitemclicklistener(onItemclicklistener listener){
        mlistener = listener;
    }

    public Pairinglistadaptor(ArrayList<Trialgettersetter> arrayList, Context c) {
        marraylist = arrayList;
        this.c = c;
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pairinglistcardview,parent,false);
        Viewholder viewholder = new Viewholder(v,mlistener,marraylist);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {
         Trialgettersetter searchgettersetter = marraylist.get(position);
         holder.changecolorcardviewpairing.setCardBackgroundColor(getRandomColor());
         holder.firstlettertextviewpairing.setText(searchgettersetter.getMsearchcode().toUpperCase());
         holder.username.setText(searchgettersetter.getMsearchname().toUpperCase());
         if(!searchgettersetter.getMsearchimage().equals("default") && searchgettersetter.getMsearchimage() != null && !TextUtils.isEmpty(searchgettersetter.getMsearchimage())){
            Picasso.with(c).load(Uri.parse(searchgettersetter.getMsearchimage())).fit().centerCrop().into(holder.userimage);
          }
    }

    @Override
    public int getItemCount() {
        return marraylist.size();
    }

    private int getRandomColor() {
        Random rand = new Random();
        String color[] = {"#ff0000","#000080","#191970","#FF4500","#4169E1","#8B4513","#FA8072","#2E8B57","#FFFF00","#0000ff",
                "#006400","#800000","#6B8E23","#800080","#C71585","#B22222","#D2691E","#2F4F4F","#008B8B","#FF1493"};

        return Color.parseColor(color[rand.nextInt(20)]);
    }

    public static class Viewholder extends RecyclerView.ViewHolder{

        public CircleImageView userimage;
        public TextView username,firstlettertextviewpairing;
        public Button seelocation,deleterequest;
        public CardView changecolorcardviewpairing;

        public Viewholder(View itemView, final onItemclicklistener listener, final ArrayList<Trialgettersetter> arrayList) {
            super(itemView);
            userimage = (CircleImageView)itemView.findViewById(R.id.userimage);
            username = (TextView)itemView.findViewById(R.id.pairinglistname);
            firstlettertextviewpairing = (TextView)itemView.findViewById(R.id.firstlettertextviewpairing);
            changecolorcardviewpairing = (CardView)itemView.findViewById(R.id.changecolorcardviewpairing);
            seelocation = (Button)itemView.findViewById(R.id.seelocation);
            deleterequest = (Button)itemView.findViewById(R.id.deletepairing);

            seelocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            Trialgettersetter searchgettersetter = arrayList.get(position);
                            String name = searchgettersetter.getMsearchname();
                            listener.tracelocation(position,name);
                        }
                    }
                }
            });
            deleterequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            Trialgettersetter searchgettersetter = arrayList.get(position);
                            String name = searchgettersetter.getMsearchname();
                            listener.deletepairing(position,name);
                        }
                    }
                }
            });

        }
    }
}
