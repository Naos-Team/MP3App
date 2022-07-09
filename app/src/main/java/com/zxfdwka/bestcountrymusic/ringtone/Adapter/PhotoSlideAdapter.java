package com.zxfdwka.bestcountrymusic.ringtone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.ringtone.item.ItemPhotoSlide;

import java.util.List;

public class PhotoSlideAdapter extends PagerAdapter {
    private Context context;
    private List<ItemPhotoSlide> itemPhotoSlideList;


    public PhotoSlideAdapter(Context context, List<ItemPhotoSlide> itemPhotoSlideList) {
        this.context = context;
        this.itemPhotoSlideList = itemPhotoSlideList;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_photo_base_fav, container,false);
        ImageView imgPhoto = view.findViewById(R.id.img_photo);
        
        ItemPhotoSlide photoSlide = itemPhotoSlideList.get(position);
        if (photoSlide!=null){
            Glide.with(context).load(photoSlide.getId()).into(imgPhoto);
        }
        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        if (itemPhotoSlideList!=null){
            return itemPhotoSlideList.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
