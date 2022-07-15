package com.naosteam.countrymusic.intro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.naosteam.countrymusic.HomeActivity;
import com.naosteam.countrymusic.R;

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
        arrayList.add(new IntroItem("Enjoy musics and radios", R.drawable.intro_1));
        arrayList.add(new IntroItem("Choose your own ringtone", R.drawable.intro_2));
        arrayList.add(new IntroItem("Let's start", R.drawable.intro_3));


        adapter = new IntroViewPagerAdapter(this, arrayList, () ->{
            new Handler().postDelayed(() -> {
                Intent mIntent = new Intent(IntroActivity.this, HomeActivity.class);
                startActivity(mIntent);
            },100);
        });
    }
}