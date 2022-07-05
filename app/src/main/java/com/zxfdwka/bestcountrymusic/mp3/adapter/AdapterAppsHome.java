package com.zxfdwka.bestcountrymusic.mp3.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemApps;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterAppsHome extends RecyclerView.Adapter<AdapterAppsHome.MyViewHolder> {

    private ArrayList<ItemApps> arrayList;

    class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView iv_song;
        TextView tv_title;

        MyViewHolder(View view) {
            super(view);
            iv_song = view.findViewById(R.id.iv_albums);
            tv_title = view.findViewById(R.id.tv_album_name);
        }
    }

    public AdapterAppsHome(ArrayList<ItemApps> arrayList) {
        this.arrayList = arrayList;
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
                .resize(300,300)
                .placeholder(R.drawable.placeholder_song)
                .into(holder.iv_song);
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