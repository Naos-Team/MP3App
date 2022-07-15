package com.naosteam.countrymusic.intro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.naosteam.countrymusic.HomeActivity;
import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.ringtone.Activity.MainActivity;
import com.naosteam.countrymusic.ringtone.SharedPref.SharedPref;

import java.util.ArrayList;

public class IntroActivity extends AppCompatActivity {

    ViewPager viewPager;
    IntroViewPagerAdapter adapter;
    TabLayout tabIndicator;
    RelativeLayout rl_bottom;
    TextView btn_skip;


//    String[] permissionsList = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        viewPager = findViewById(R.id.view_pager);
        tabIndicator = findViewById(R.id.tab_layout);
        rl_bottom = findViewById(R.id.rl_bottom);
        btn_skip = findViewById(R.id.btn_skip);


        ArrayList<IntroItem> arrayList = new ArrayList<>();
        arrayList.add(new IntroItem("Enjoy songs and radios", R.drawable.intro_1));
        arrayList.add(new IntroItem("Choose your own ringtone", R.drawable.intro_2));
        arrayList.add(new IntroItem("All is set. Try it yourself", R.drawable.intro_3));


        adapter = new IntroViewPagerAdapter(this, arrayList, () ->{
            new Handler().postDelayed(() -> {
                Intent mIntent = new Intent(IntroActivity.this, HomeActivity.class);
                startActivity(mIntent);
            },100);
        });

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position < 2){
                    tabIndicator.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    tabIndicator.setVisibility(View.VISIBLE);
                                }
                            });

                    btn_skip.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    btn_skip.setVisibility(View.VISIBLE);
                                }
                            });
                }else {
                    tabIndicator.animate()
                            .alpha(0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    tabIndicator.setVisibility(View.GONE);
                                }
                            });
                    btn_skip.animate()
                            .alpha(0f)
                            .setDuration(300)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    btn_skip.setVisibility(View.GONE);
                                }
                            });
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btn_skip.setOnClickListener((v) ->{
            viewPager.setCurrentItem(2, true);
        });

        tabIndicator.setupWithViewPager(viewPager);
    }
}