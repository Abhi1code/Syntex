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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DELL on 01-02-2018.
 */

public class Requestreceivedadaptor extends RecyclerView.Adapter<Requestreceivedadaptor.viewholder>{

    private ArrayList<Trialgettersetter> arraylist;
    Context c;
    public onItemclicklistener mlistener;

    public interface onItemclicklistener{
        void  addrequest(int position,String name);
        void  deleterequest(int position,String name);
    }

    public void setOnitemclicklistener(onItemclicklistener listener){
        mlistener = listener;
    }

    public Requestreceivedadaptor(ArrayList<Trialgettersetter> list,Context c){
        arraylist = list;
        this.c = c;
    }

    @Override
    public viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.requestreceivedcardview,parent,false);
        viewholder viewHolder = new viewholder(v,mlistener,arraylist);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(viewholder holder, final int position) {
        Trialgettersetter searchgettersetter = arraylist.get(position);

        holder.username.setText(searchgettersetter.getMsearchname().toUpperCase());
        holder.changecolorcardview.setCardBackgroundColor(getRandomColor());
        holder.firstlettertextview.setText(searchgettersetter.getMsearchcode());
        if(!searchgettersetter.getMsearchimage().equals("default") && searchgettersetter.getMsearchimage() != null && !TextUtils.isEmpty(searchgettersetter.getMsearchimage())){
            Picasso.with(c).load(Uri.parse(searchgettersetter.getMsearchimage())).fit().centerCrop().into(holder.userimage);
        }
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }

    private int getRandomColor() {
        Random rand = new Random();
        String color[] = {"#ff0000","#000080","#191970","#FF4500","#4169E1","#8B4513","#FA8072","#2E8B57","#FFFF00","#0000ff",
                "#006400","#800000","#6B8E23","#800080","#C71585","#B22222","#D2691E","#2F4F4F","#008B8B","#FF1493"};

        return Color.parseColor(color[rand.nextInt(20)]);
    }

    public static class viewholder extends RecyclerView.ViewHolder{

        public CircleImageView userimage;
        public TextView username,firstlettertextview;
        public RelativeLayout insidecardview;
        public Button addrequest,deleterequest;
        public CardView changecolorcardview;

        public viewholder(View itemView, final onItemclicklistener listener, final ArrayList<Trialgettersetter> arrayList) {
            super(itemView);
            userimage = (CircleImageView) itemView.findViewById(R.id.requestreceiveduserimage);
            username = (TextView)itemView.findViewById(R.id.requestreceivedusername);
            changecolorcardview = (CardView)itemView.findViewById(R.id.changecolorcardview);
            firstlettertextview = (TextView)itemView.findViewById(R.id.firstlettertextview);
            insidecardview = (RelativeLayout)itemView.findViewById(R.id.requestreceivedcardlayout);
            addrequest = (Button)itemView.findViewById(R.id.addfriend);
            deleterequest = (Button)itemView.findViewById(R.id.deleterequest);

            addrequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   if(listener != null){
                       int position = getAdapterPosition();
                       if(position != RecyclerView.NO_POSITION) {
                           Trialgettersetter searchgettersetter = arrayList.get(position);
                           String name = searchgettersetter.getMsearchname();
                           listener.addrequest(position,name);
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
                            listener.deleterequest(position,name);
                        }
                    }
                }
            });
        }
    }
}
