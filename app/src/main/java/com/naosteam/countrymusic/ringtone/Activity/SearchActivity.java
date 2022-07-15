package com.naosteam.countrymusic.ringtone.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naosteam.countrymusic.MethodsAll;
import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.mp3.interfaces.InterScreenListener;
import com.naosteam.countrymusic.ringtone.Adapter.SongAdapter;
import com.naosteam.countrymusic.ringtone.EndlessRecyclerViewScroll.EndlessRecyclerViewScrollListener;
import com.naosteam.countrymusic.ringtone.Listener.ClickListenerRecorder;
import com.naosteam.countrymusic.ringtone.Listener.InterAdListener;
import com.naosteam.countrymusic.ringtone.Listener.RingtoneListener;
import com.naosteam.countrymusic.ringtone.Load.LoadSongs;
import com.naosteam.countrymusic.ringtone.Method.Methods;
import com.naosteam.countrymusic.ringtone.SharedPref.Setting;
import com.naosteam.countrymusic.ringtone.item.ItemRingtone;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    public static SongAdapter adapter;
    ArrayList<ItemRingtone> arrayList;
    ProgressBar progressBar;
    Boolean isOver = false, isScroll = false;
    int page = 0;
    GridLayoutManager grid;
    LoadSongs load;
    LinearLayout ll_ad;
    EditText searchView;
    ImageView search;
    Methods methods;
    Toolbar toolbar2;

    com.naosteam.countrymusic.mp3.utils.Methods methods1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Setting.Dark_Mode ) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ringtone);

        MethodsAll.getInstance().setContext(SearchActivity.this);
        MethodsAll.getInstance().setContext_upgrade(SearchActivity.this);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        toolbar2 = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar2);
        setTitle( getResources().getString(R.string.search));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods1.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        onBackPressed();

                    }
                });
            }
        });

        arrayList = new ArrayList<>();
        progressBar = findViewById(R.id.load_video);

        methods1 = new com.naosteam.countrymusic.mp3.utils.Methods(this);
        ll_ad = findViewById(R.id.ll_ad);
        methods1.showSMARTBannerAd(ll_ad);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        grid = new GridLayoutManager(SearchActivity.this, 1);
        grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isHeader(position) ? grid.getSpanCount() : 1;
            }
        });
        recyclerView.setLayoutManager(grid);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(grid) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if(!isOver) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isScroll = true;
                            getData();
                        }
                    }, 0);
                } else {
                    adapter.hideHeader();
                }
            }
        });


        searchView = findViewById(R.id.search_view);
        search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();

            }
        });

        methods = new Methods(SearchActivity.this, new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                //methods.showInter(position, "");
                Setting.arrayList_play_rc.clear();
                Setting.arrayList_play_rc.addAll(arrayList);
                Setting.playPos_rc = position;

                adapter.notifyDataSetChanged();
            }
        });

    }


    private void getData() {
        load = new LoadSongs(new RingtoneListener() {
            @Override
            public void onStart() {
                if(arrayList.size() == 0) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onEnd(String success, ArrayList<ItemRingtone> arrayListWall) {
                if(arrayListWall.size() == 0) {
                    isOver = true;
                    try {
                        adapter.hideHeader();
                    }catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                } else {
                    page = page + 1;
                    arrayList.addAll(arrayListWall);
                    progressBar.setVisibility(View.INVISIBLE);

                    Setad();
                }
            }
        },methods.getAPIRequest(Setting.METHOD_SEARCH, page, "", "", searchView.getText().toString().toUpperCase(), "", "", "", "", "","","","","","","","", null));
        load.execute();
    }

    private void Setad() {

        if(!isScroll) {
            adapter = new SongAdapter(SearchActivity.this, arrayList, new ClickListenerRecorder() {
                @Override
                public void onClick(int position) {
                    //methods.showInter(position, "");
                    Setting.arrayList_play_rc.clear();
                    Setting.arrayList_play_rc.addAll(arrayList);
                    Setting.playPos_rc = position;

                    adapter.notifyDataSetChanged();
                }

            }, "");
            recyclerView.setAdapter(adapter);
        }
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

    @Override
    protected void onResume() {
        MethodsAll.getInstance().setContext(SearchActivity.this);
        MethodsAll.getInstance().setContext_upgrade(SearchActivity.this);
        super.onResume();
    }
}

