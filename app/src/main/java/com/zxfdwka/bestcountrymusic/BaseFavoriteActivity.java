package com.zxfdwka.bestcountrymusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.zxfdwka.bestcountrymusic.databinding.ActivityBaseCategoriesBinding;
import com.zxfdwka.bestcountrymusic.databinding.ActivityBaseFavoriteBinding;
import com.zxfdwka.bestcountrymusic.databinding.ActivityHomeBinding;
import com.zxfdwka.bestcountrymusic.mp3.activity.FavoriteActivity;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.InterScreenListener;
import com.zxfdwka.bestcountrymusic.mp3.utils.Methods;
import com.zxfdwka.bestcountrymusic.ringtone.Activity.FavouriteActivity;
import com.zxfdwka.bestcountrymusic.ringtone.Adapter.PhotoSlideAdapter;
import com.zxfdwka.bestcountrymusic.ringtone.item.ItemPhotoSlide;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class BaseFavoriteActivity extends AppCompatActivity {
    private ActivityBaseFavoriteBinding binding;
    private PhotoSlideAdapter adapter;
    private List<ItemPhotoSlide> mList;
    private Timer timer;
    private Methods methods;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBaseFavoriteBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mList = getListPhoto();
        adapter = new PhotoSlideAdapter(this, mList);

        methods = new Methods(this);

        binding.viewPager.setAdapter(adapter);
        binding.circleIndicator.setViewPager(binding.viewPager);
        adapter.registerDataSetObserver(binding.circleIndicator.getDataSetObserver());

        autoSlideImage();


        binding.lRingtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        startActivity(new Intent(BaseFavoriteActivity.this, FavouriteActivity.class));
                    }
                });
            }
        });

        binding.lMp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        startActivity(new Intent(BaseFavoriteActivity.this, FavoriteActivity.class));
                    }
                });
            }
        });

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methods.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        onBackPressed();
                    }
                });
            }
        });
    }


    private List<ItemPhotoSlide> getListPhoto(){
        List<ItemPhotoSlide> list = new ArrayList<>();
        list.add(new ItemPhotoSlide(R.drawable.home_fav));
        list.add(new ItemPhotoSlide(R.drawable.slide_fav1));
        list.add(new ItemPhotoSlide(R.drawable.slide_fav3));
        list.add(new ItemPhotoSlide(R.drawable.slide_fav2));
        list.add(new ItemPhotoSlide(R.drawable.slide_fav4));
        return list;
    }

    private void autoSlideImage(){
        if (mList == null || mList.isEmpty() || binding.viewPager == null){
            return;
        }

        if (timer == null){
            timer = new Timer();
        }

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        int currentItem = binding.viewPager.getCurrentItem();
                        int totalItem = mList.size()-1;
                        if (currentItem<totalItem){
                            currentItem++;
                            binding.viewPager.setCurrentItem(currentItem);
                        } else{
                            binding.viewPager.setCurrentItem(0);
                        }
                    }
                });
            }
        },500,3000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer!=null){
            timer.cancel();
            timer = null;
        }
    }
}