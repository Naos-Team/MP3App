package com.naosteam.countrymusic.mp3.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.mp3.item.ItemServerPlayList;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterPlaylistHome extends RecyclerView.Adapter<AdapterPlaylistHome.MyViewHolder> {

    private ArrayList<ItemServerPlayList> arrayList;
    private Activity activity;

    class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView iv_song;
        TextView tv_title;
        ConstraintLayout layout_albums_home;

        MyViewHolder(View view) {
            super(view);
            iv_song = view.findViewById(R.id.iv_albums);
            tv_title = view.findViewById(R.id.tv_album_name);
            layout_albums_home = view.findViewById(R.id.layout_albums_home);
        }
    }

    public AdapterPlaylistHome(ArrayList<ItemServerPlayList> arrayList, Activity activity) {
        this.arrayList = arrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_albums_home, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.tv_title.setTypeface(holder.tv_title.getTypeface(), Typeface.BOLD);
        holder.tv_title.setText(arrayList.get(position).getName());
        Picasso.get()
                .load(arrayList.get(position).getImage())
                .placeholder(R.drawable.placeholder_song)
                .into(holder.iv_song);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                activity.getResources().getDisplayMetrics().heightPixels * 185 / 1000,
                activity.getResources().getDisplayMetrics().heightPixels * 185 / 1000);
        layoutParams.setMargins(activity.getResources().getDisplayMetrics().widthPixels*3/100, 5,
                2, 5);
        holder.layout_albums_home.setLayoutParams(layoutParams);
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