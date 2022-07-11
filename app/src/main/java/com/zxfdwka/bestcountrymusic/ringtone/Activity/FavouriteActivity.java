package com.zxfdwka.bestcountrymusic.ringtone.Activity;


import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.InterScreenListener;
import com.zxfdwka.bestcountrymusic.ringtone.Adapter.SongAdapter;
import com.zxfdwka.bestcountrymusic.ringtone.DBHelper.DBHelper;
import com.zxfdwka.bestcountrymusic.ringtone.Listener.ClickListenerRecorder;
import com.zxfdwka.bestcountrymusic.ringtone.Listener.InterAdListener;
import com.zxfdwka.bestcountrymusic.ringtone.Method.Methods;
import com.zxfdwka.bestcountrymusic.ringtone.SharedPref.Setting;
import com.zxfdwka.bestcountrymusic.ringtone.item.ItemRingtone;

import java.util.ArrayList;


public class FavouriteActivity extends AppCompatActivity {
    Methods methods;
    private RecyclerView rv;
    private SongAdapter adapter;
    private ArrayList<ItemRingtone> arrayList;
    private ProgressBar progressBar;
    private com.zxfdwka.bestcountrymusic.mp3.utils.Methods methods1;
    LinearLayout adView;

    Toolbar toolbar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (Setting.Dark_Mode ) {
//            setTheme(R.style.AppTheme);
//        } else {
//            setTheme(R.style.AppTheme);
//        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories_ringtone);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        methods1 = new com.zxfdwka.bestcountrymusic.mp3.utils.Methods(this);
        adView = findViewById(R.id.adView);
        methods1.showSMARTBannerAd(adView);

        toolbar2 = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar2);
        setTitle(getResources().getString(R.string.favourite));


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods1.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        finish();
                    }
                });

            }
        });


        DBHelper dbHelper = new DBHelper(this);
        arrayList = new ArrayList<>();
        arrayList.addAll(dbHelper.loadFavData());

        progressBar = findViewById(R.id.load_video);
        progressBar.setVisibility(View.GONE);

        rv = findViewById(R.id.recycler);
        LinearLayoutManager llm_banner = new LinearLayoutManager(this);
        rv.setLayoutManager(llm_banner);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);

        methods = new Methods(this, new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                adapter.notifyDataSetChanged();
            }
        });

        adapter = new SongAdapter(FavouriteActivity.this, arrayList, new ClickListenerRecorder() {
            @Override
            public void onClick(int position) {
                methods.showInter(position, "");
                Setting.arrayList_play_rc.clear();
                Setting.arrayList_play_rc.addAll(arrayList);
                Setting.playPos_rc = position;
            }

        }, "");
        rv.setAdapter(adapter);

        LinearLayout adView = findViewById(R.id.adView);
        methods.showBannerAd(adView);
    }

    @Override
    protected void onStart() {
        try {
            Setting.exoPlayer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStart();
    }


    @Override
    public void onDestroy() {
        try {
            Setting.exoPlayer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        try {
            Setting.exoPlayer.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }






}
