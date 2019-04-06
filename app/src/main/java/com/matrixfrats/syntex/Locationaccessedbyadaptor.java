package com.matrixfrats.syntex;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DELL on 14-03-2018.
 */

public class Locationaccessedbyadaptor extends RecyclerView.Adapter<Locationaccessedbyadaptor.Locationaccessedbyviewholder> {

    ArrayList<Trialgettersetter> arrayList;
    Context context;

    public Locationaccessedbyadaptor(ArrayList<Trialgettersetter> arrayList,Context context){

        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public Locationaccessedbyviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchcardview,parent,false);
        Locationaccessedbyviewholder locationaccessedbyviewholder = new Locationaccessedbyviewholder(view);
        return locationaccessedbyviewholder;
    }

    @Override
    public void onBindViewHolder(Locationaccessedbyviewholder holder, int position) {

        Trialgettersetter searchgettersetter = arrayList.get(position);

        holder.locatorname.setText(searchgettersetter.getMsearchname().toUpperCase());
        holder.colourchangecardview.setCardBackgroundColor(getRandomColor());
        holder.cardviewtext.setText(searchgettersetter.getMsearchcode());

        if(searchgettersetter.getMsearchimage() != null && !TextUtils.isEmpty(searchgettersetter.getMsearchimage()) && !searchgettersetter.getMsearchimage().equals("default")){
            Picasso.with(context).load(searchgettersetter.getMsearchimage()).fit().centerCrop().into(holder.circularimage);
        }

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "If you want to block him ,then remove him from paired list", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class Locationaccessedbyviewholder extends RecyclerView.ViewHolder {

        RelativeLayout cardview;
        CircleImageView circularimage;
        TextView locatorname,cardviewtext;
        CardView colourchangecardview;

        public Locationaccessedbyviewholder(View itemView) {
            super(itemView);
            cardview = (RelativeLayout)itemView.findViewById(R.id.cardlayout);
            circularimage = (CircleImageView)itemView.findViewById(R.id.searchimage);
            locatorname = (TextView)itemView.findViewById(R.id.searchname);
            cardviewtext = (TextView)itemView.findViewById(R.id.cardviewtext);
            colourchangecardview = (CardView)itemView.findViewById(R.id.colourchangecardview);
        }
    }

    private int getRandomColor() {
        Random rand = new Random();
        String color[] = {"#ff0000","#000080","#191970","#FF4500","#4169E1","#8B4513","#FA8072","#2E8B57","#FFFF00","#0000ff",
                "#006400","#800000","#6B8E23","#800080","#C71585","#B22222","#D2691E","#2F4F4F","#008B8B","#FF1493"};

        return Color.parseColor(color[rand.nextInt(20)]);
    }
}
