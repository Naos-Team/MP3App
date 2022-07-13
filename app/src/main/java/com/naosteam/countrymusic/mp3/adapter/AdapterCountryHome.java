package com.naosteam.countrymusic.mp3.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.mp3.item.ItemCountry;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterCountryHome extends RecyclerView.Adapter<AdapterCountryHome.MyViewHolder> {

    private ArrayList<ItemCountry> arrayList;
    private Activity activity;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RoundedImageView imageView_artist;
        ConstraintLayout layout_home_artist;

        MyViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.tv_home_artist);
            imageView_artist = view.findViewById(R.id.iv_home_artist);
            layout_home_artist = view.findViewById(R.id.layout_home_artist);
        }
    }

    public AdapterCountryHome(ArrayList<ItemCountry> arrayList, Activity activity) {
        this.arrayList = arrayList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_home_artist, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.textView.setText(arrayList.get(position).getName());
        Picasso.get()
                .load(arrayList.get(position).getImage())
                .placeholder(R.drawable.placeholder_artist)
                .into(holder.imageView_artist);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                activity.getResources().getDisplayMetrics().heightPixels * 185 / 1000,
                activity.getResources().getDisplayMetrics().heightPixels * 185 /85*100 / 1000);
        layoutParams.setMargins(activity.getResources().getDisplayMetrics().widthPixels*3/100, 5,
                2, 5);
        holder.layout_home_artist.setLayoutParams(layoutParams);
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