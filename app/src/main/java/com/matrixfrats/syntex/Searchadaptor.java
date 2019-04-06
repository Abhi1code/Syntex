package com.matrixfrats.syntex;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DELL on 27-01-2018.
 */

public class Searchadaptor extends RecyclerView.Adapter<Searchadaptor.Searchviewholder>{

    private ArrayList<Trialgettersetter> marraylist;
    Context c;

    public static class Searchviewholder extends RecyclerView.ViewHolder {

        public CircleImageView msearchimage;
        public TextView msearchname,cardviewtext;
        public RelativeLayout searchcardlayout;
        public CardView colourchangecardview;

        public Searchviewholder(View itemView) {
            super(itemView);
            msearchimage = (CircleImageView) itemView.findViewById(R.id.searchimage);
            msearchname = (TextView) itemView.findViewById(R.id.searchname);
            searchcardlayout = (RelativeLayout)itemView.findViewById(R.id.cardlayout);
            cardviewtext = (TextView)itemView.findViewById(R.id.cardviewtext);
            colourchangecardview = (CardView)itemView.findViewById(R.id.colourchangecardview);
        }
    }

    public Searchadaptor(ArrayList<Trialgettersetter> arraylist, Context c){
        marraylist = arraylist;
        this.c = c;
    }

    @Override
    public Searchviewholder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchcardview,parent,false);
        Searchviewholder searchviewholder = new Searchviewholder(v);
        return searchviewholder;
    }

    @Override
    public void onBindViewHolder(final Searchviewholder holder, final int position) {
        final Trialgettersetter searchgettersetter = marraylist.get(position);

        if (searchgettersetter.getMsearchimage() != null) {
            Picasso.with(c).load(Uri.parse(searchgettersetter.getMsearchimage())).fit().centerCrop().into(holder.msearchimage);
        }

        holder.colourchangecardview.setCardBackgroundColor(getRandomColor());
        holder.msearchname.setText(searchgettersetter.getMsearchname().toUpperCase());
        holder.cardviewtext.setText(searchgettersetter.getMsearchcode());
        holder.searchcardlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, Friendsearched.class);
                intent.putExtra("searchedname", searchgettersetter.getMsearchname());
                if(searchgettersetter.getMsearchimage() != null){
                    intent.putExtra("downloadurl",searchgettersetter.getMsearchimage());
                }
                c.startActivity(intent);
            }
        });
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


}
