package com.zxfdwka.bestcountrymusic.mp3.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.ClickListenerPlayList;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemSong;
import com.zxfdwka.bestcountrymusic.mp3.utils.Methods;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class AdapterSelectableSongList extends RecyclerView.Adapter<AdapterSelectableSongList.MyViewHolder> {

    private ArrayList<ItemSong> arrayList;
    private ArrayList<String> arrayListSelectedIDs = new ArrayList<>();
    private int selectedCounter = 0;
    private ClickListenerPlayList recyclerClickListener;
    private Methods methods;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_song, textView_catname;
        ImageView imageView;
        CheckBox checkBox;
        RelativeLayout rl;

        MyViewHolder(View view) {
            super(view);
            rl = view.findViewById(R.id.ll_select);
            textView_song = view.findViewById(R.id.tv_songlist_name);
            textView_catname = view.findViewById(R.id.tv_songlist_cat);
            checkBox = view.findViewById(R.id.cb_select);
            imageView = view.findViewById(R.id.iv_songlist);
        }
    }

    public AdapterSelectableSongList(Context context, ArrayList<ItemSong> arrayList, ClickListenerPlayList recyclerClickListener) {
        this.arrayList = arrayList;
        this.recyclerClickListener = recyclerClickListener;
        methods = new Methods(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_selectable_songs, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        holder.textView_song.setText(arrayList.get(position).getTitle());
        Picasso.get()
                .load(Uri.parse(arrayList.get(position).getImageSmall()))
                .placeholder(R.drawable.placeholder_song)
                .into(holder.imageView);

        holder.textView_catname.setText(arrayList.get(position).getArtist());

        holder.checkBox.setChecked(arrayListSelectedIDs.contains(arrayList.get(holder.getAdapterPosition()).getId()));

        holder.rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(arrayListSelectedIDs.contains(arrayList.get(holder.getAdapterPosition()).getId())) {
                    selectedCounter = selectedCounter - 1;
                    arrayListSelectedIDs.remove(arrayList.get(holder.getAdapterPosition()).getId());
                } else {
                    selectedCounter = selectedCounter + 1;
                    arrayListSelectedIDs.add(arrayList.get(holder.getAdapterPosition()).getId());
                }
                notifyItemChanged(holder.getAdapterPosition());
                recyclerClickListener.onClick(0);
            }
        });

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrayListSelectedIDs.contains(arrayList.get(holder.getAdapterPosition()).getId())) {
                    selectedCounter = selectedCounter - 1;
                    arrayListSelectedIDs.remove(arrayList.get(holder.getAdapterPosition()).getId());
                } else {
                    selectedCounter = selectedCounter + 1;
                    arrayListSelectedIDs.add(arrayList.get(holder.getAdapterPosition()).getId());
                }
                recyclerClickListener.onClick(0);
            }
        });
    }

    public void toggleSelectAll(Boolean isSelect) {
        if(isSelect) {
            selectedCounter = arrayList.size();
            for (int i = 0; i < arrayList.size(); i++) {
                arrayListSelectedIDs.add(arrayList.get(i).getId());
            }
        } else {
            arrayListSelectedIDs.clear();
            selectedCounter = 0;
        }

        notifyDataSetChanged();
    }

    public int getSelectedCounts() {
        return selectedCounter;
    }

    public ArrayList<ItemSong> getSelectedIDs() {
        ArrayList<ItemSong> arrayListSeleted = new ArrayList<>();
        for (int i = 0; i < arrayListSelectedIDs.size(); i++) {
            for (int j = 0; j < arrayList.size(); j++) {
                if(arrayListSelectedIDs.get(i).equals(arrayList.get(j).getId())) {
                    arrayListSeleted.add(arrayList.get(j));
                }
            }
        }

        return arrayListSeleted;
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public ItemSong getItem(int pos) {
        return arrayList.get(pos);
    }
}