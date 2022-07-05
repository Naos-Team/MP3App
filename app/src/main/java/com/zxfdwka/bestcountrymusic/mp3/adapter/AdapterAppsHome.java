package com.zxfdwka.bestcountrymusic.mp3.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxfdwka.bestcountrymusic.mp3.activity.R;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemApps;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterAppsHome extends RecyclerView.Adapter<AdapterAppsHome.MyViewHolder> {

    private ArrayList<ItemApps> arrayList;
    private Activity activity;

    class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView iv_song;
        TextView tv_title, tv_recent_cat;
        ImageView imageView3;
        ConstraintLayout layout_recent, cs_layout_recent;

        MyViewHolder(View view) {
            super(view);
            iv_song = view.findViewById(R.id.iv_recent);
            tv_title = view.findViewById(R.id.tv_recent_song);
            layout_recent = view.findViewById(R.id.layout_recent);
            imageView3 = view.findViewById(R.id.iv_recent_more);
            tv_recent_cat = view.findViewById(R.id.tv_recent_cat);
            cs_layout_recent = view.findViewById(R.id.cs_layout_recent);
        }
    }

    public AdapterAppsHome(ArrayList<ItemApps> arrayList, Activity activity) {
        this.arrayList = arrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_recent, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_title.setTypeface(holder.tv_title.getTypeface(), Typeface.BOLD);
        holder.tv_title.setText(arrayList.get(position).getName());
        Picasso.get()
                .load(arrayList.get(position).getImage())
                .resize(300,300)
                .placeholder(R.drawable.placeholder_song)
                .into(holder.iv_song);
        holder.imageView3.setVisibility(View.GONE);
        holder.tv_recent_cat.setVisibility(View.GONE);
        holder.tv_title.setGravity(Gravity.CENTER_VERTICAL);
        ConstraintSet cs = new ConstraintSet();
        cs.clone(holder.layout_recent);
        cs.setVerticalWeight(holder.cs_layout_recent.getId(),1.5f);
        cs.applyTo(holder.layout_recent);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                activity.getResources().getDisplayMetrics().heightPixels * 185 / 1000,
                activity.getResources().getDisplayMetrics().heightPixels * 230 / 1000);
        layoutParams.setMargins(activity.getResources().getDisplayMetrics().widthPixels*3/100, 5,
                2, 5);
        holder.layout_recent.setLayoutParams(layoutParams);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}