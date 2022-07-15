package com.naosteam.countrymusic.mp3.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdsManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.material.appbar.AppBarLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.naosteam.countrymusic.MethodsAll;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.mp3.adapter.AdapterAllSongList;
import com.naosteam.countrymusic.mp3.interfaces.ClickListenerPlayList;
import com.naosteam.countrymusic.mp3.interfaces.InterAdListener;
import com.naosteam.countrymusic.mp3.item.ItemAlbums;
import com.naosteam.countrymusic.mp3.item.ItemMyPlayList;
import com.naosteam.countrymusic.mp3.item.ItemSong;
import com.naosteam.countrymusic.mp3.utils.Constant;
import com.naosteam.countrymusic.mp3.utils.GlobalBus;
import com.naosteam.countrymusic.mp3.utils.Methods;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SongByMyPlaylistActivity extends BaseActivity {

    AppBarLayout appBarLayout;
    Toolbar toolbar_playlist;
    Methods methods;
    RecyclerView rv;
    ItemMyPlayList itemMyPlayList;
    AdapterAllSongList adapter;
    ArrayList<ItemSong> arrayList, arrayListTemp;
    CircularProgressBar progressBar;
    FrameLayout frameLayout;
    ImageView iv_playlist2;
    ConstraintLayout iv_playlist;
    TextView tv_no_song, txt_title_SongByPlaylist;
    String addedFrom = "myplay";
    CollapsingToolbarLayout collapsing_play;

    SearchView searchView;
    private NativeAdsManager mNativeAdsManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout contentFrameLayout = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_song_by_playlist, contentFrameLayout);

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        MethodsAll.getInstance().setContext(SongByMyPlaylistActivity.this);
        MethodsAll.getInstance().setContext_upgrade(SongByMyPlaylistActivity.this);

        itemMyPlayList = (ItemMyPlayList) getIntent().getSerializableExtra("item");
        addedFrom = addedFrom + itemMyPlayList.getName();

        methods = new Methods(this, new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                Intent intent = new Intent(SongByMyPlaylistActivity.this, PlayerService.class);
                intent.setAction(PlayerService.ACTION_PLAY);
                startService(intent);
            }
        });
        collapsing_play = findViewById(R.id.collapsing_play);
        methods.forceRTLIfSupported(getWindow());
        methods.showSMARTBannerAd(ll_adView_base);

        toolbar.setVisibility(View.GONE);

        appBarLayout = findViewById(R.id.mainappbar);
        toolbar_playlist = findViewById(R.id.toolbar_playlist);
        setSupportActionBar(toolbar_playlist);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        arrayList = new ArrayList<>();
        arrayListTemp = new ArrayList<>();

        frameLayout = findViewById(R.id.fl_empty);
        progressBar = findViewById(R.id.pb_song_by_playlist);
        progressBar.setVisibility(View.GONE);
        rv = findViewById(R.id.rv_song_by_playlist);
        LinearLayoutManager llm_banner = new LinearLayoutManager(this);
        rv.setLayoutManager(llm_banner);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);

        addNewDataToArrayList(dbHelper.loadDataPlaylist(itemMyPlayList.getId(), true));

        iv_playlist = findViewById(R.id.iv_collapse_playlist);
        iv_playlist2 = findViewById(R.id.iv_collapse_playlist2);
        tv_no_song = findViewById(R.id.tv_playlist_no_song);

        txt_title_SongByPlaylist = findViewById(R.id.txt_title_SongByPlaylist);
        collapsing_play.setTitle("");

        Picasso.get()
                .load(itemMyPlayList.getArrayListUrl().get(3))
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        CheckColor checkColor = new CheckColor(bitmap);
                        checkColor.execute();
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        int[] colors = {getResources().getColor(R.color.bg_items), Color.rgb( 119, 136,153)};

                        GradientDrawable gd = new GradientDrawable(
                                GradientDrawable.Orientation.BOTTOM_TOP, colors);
                        gd.setCornerRadius(0f);
                        appBarLayout.setBackground(gd);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

        Picasso.get()
                .load(itemMyPlayList.getArrayListUrl().get(3))
                .into(iv_playlist2);

        txt_title_SongByPlaylist.setText(itemMyPlayList.getName());
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true);
        appBarLayout = findViewById(R.id.mainappbar);
        int height_img = typedValue.TYPE_DIMENSION + getResources().getDisplayMetrics().widthPixels * 1 / 2
                + 20 + txt_title_SongByPlaylist.getHeight() + tv_no_song.getHeight() + 5;
        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDisplayMetrics().heightPixels * 46 / 100);
        appBarLayout.setLayoutParams(layoutParams);

        AppBarLayout appBarLayout = findViewById(R.id.mainappbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                txt_title_SongByPlaylist.setAlpha(1 - Math.abs((float) verticalOffset / appBarLayout.getTotalScrollRange()));
                tv_no_song.setAlpha(1 - Math.abs((float) verticalOffset / appBarLayout.getTotalScrollRange()));
                iv_playlist.setAlpha(1 - Math.abs((float) verticalOffset / appBarLayout.getTotalScrollRange()));
                iv_playlist2.setAlpha(1 - Math.abs((float) verticalOffset / appBarLayout.getTotalScrollRange()));
                if(Math.abs((float) verticalOffset / appBarLayout.getTotalScrollRange()) > 0.5f) {
                    collapsing_play.setTitle(itemMyPlayList.getName());

                    int[] colors = {getResources().getColor(R.color.colorPrimary), Color.rgb( 119, 136,153)};

                    GradientDrawable gd = new GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP, colors);
                    gd.setCornerRadius(0f);
                    appBarLayout.setBackground(gd);
                } else {
                    collapsing_play.setTitle("");
                    int[] colors = {getResources().getColor(R.color.bg_items), Color.rgb( 119, 136,153)};

                    GradientDrawable gd = new GradientDrawable(
                            GradientDrawable.Orientation.BOTTOM_TOP, colors);
                    gd.setCornerRadius(0f);
                    appBarLayout.setBackground(gd);
                }
            }
        });

        setAdapter();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.hideHeader();
            }
        }, 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appBarLayout.setExpanded(false);
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if (adapter != null) {
                if (!searchView.isIconified()) {
                    adapter.getFilter().filter(s);
                    adapter.notifyDataSetChanged();
                }
            }
            return true;
        }
    };

    private void loadNativeAds() {
        if (Constant.isNativeAd) {
            if (Constant.natveAdType.equals("admob")) {
                AdLoader.Builder builder = new AdLoader.Builder(SongByMyPlaylistActivity.this, Constant.ad_native_id);
                AdLoader adLoader = builder.forUnifiedNativeAd(
                        new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                            @Override
                            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                                // A native ad loaded successfully, check if the ad loader has finished loading
                                // and if so, insert the ads into the list.

                                adapter.addAds(unifiedNativeAd);

                            }
                        }).withAdListener(
                        new AdListener() {
                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                            }
                        }).build();

                // Load the Native Express ad.
                adLoader.loadAds(new AdRequest.Builder().build(), 5);
            } else {
                mNativeAdsManager = new NativeAdsManager(SongByMyPlaylistActivity.this, Constant.ad_native_id, 5);
                mNativeAdsManager.setListener(new NativeAdsManager.Listener() {
                    @Override
                    public void onAdsLoaded() {
                        adapter.setFBNativeAdManager(mNativeAdsManager);
                    }

                    @Override
                    public void onAdError(AdError adError) {

                    }
                });
                mNativeAdsManager.loadAds();
            }
        }
    }

    private void addNewDataToArrayList(ArrayList<ItemSong> arrayListLatest) {
        arrayListTemp.addAll(arrayListLatest);
        for (int i = 0; i < arrayListLatest.size(); i++) {
            arrayList.add(arrayListLatest.get(i));

            if (Constant.isNativeAd) {
                int abc = arrayList.lastIndexOf(null);
                if (((arrayList.size() - (abc + 1)) % Constant.adNativeDisplay == 0) && (arrayListLatest.size() - 1 != i || arrayListTemp.size() != 50)) {
                    arrayList.add(null);
                }
            }
        }
    }

    private void setAdapter() {
        adapter = new AdapterAllSongList(SongByMyPlaylistActivity.this, arrayList, new ClickListenerPlayList() {
            @Override
            public void onClick(int position) {
                int real_pos = adapter.getRealPos(position, arrayListTemp);

                Constant.isOnline = true;
                if(!Constant.addedFrom.equals(addedFrom)) {
                    Constant.arrayList_play.clear();
                    Constant.arrayList_play.addAll(arrayListTemp);
                    Constant.addedFrom = addedFrom;
                    Constant.isNewAdded = true;
                }
                Constant.playPos = real_pos;

                methods.showInterAd(real_pos, "");
            }

            @Override
            public void onItemZero() {
                setEmpty();
            }
        }, "playlist");
        adapter.setIs_PlayList(true);
        loadNativeAds();

        rv.setAdapter(adapter);
        setEmpty();
    }

    public void setEmpty() {
        tv_no_song.setText(arrayList.size() + " " + getString(R.string.songs));
        if (arrayList.size() > 0) {
            rv.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
        } else {
            rv.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);

            frameLayout.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View myView = inflater.inflate(R.layout.layout_err_nodata, null);

            myView.findViewById(R.id.btn_empty_try).setVisibility(View.GONE);
            frameLayout.addView(myView);
        }
    }

    private class CheckColor extends AsyncTask<Void, Void, Drawable> {
        private Bitmap bitmap;

        public CheckColor(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Drawable doInBackground(Void... voids) {

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            int pixel = 0;
            int[] value = {0, 0, 0, 0};
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    // get current index in 2D-matrix
                    int index = y * width + x;
                    pixel = pixels[index];
                    if (pixel != Color.BLACK) {
                        value[0] += Color.alpha(pixel);
                        value[1] += Color.red(pixel);
                        value[2] += Color.green(pixel);
                        value[3] += Color.blue(pixel);
                    } else {
                        value[0] += Color.alpha(Color.WHITE);
                        value[1] += Color.red(Color.WHITE);
                        value[2] += Color.green(Color.WHITE);
                        value[3] += Color.blue(Color.WHITE);
                    }
                }
            }
            for(int i = 0; i < 4; i++){
                value[i] = (int)value[i]/(width*height);
            }
            if(value[0] < 10)
                value[0] = 10;
            int[] colors = {getResources().getColor(R.color.bg_items), Color.argb(value[0], value[1], value[2], value[3])};

//create a new gradient color
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.BOTTOM_TOP, colors);
            gd.setCornerRadius(0f);
            return ((Drawable) gd);
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);
            iv_playlist.setBackground(drawable);
        }
    }

    @Override
    public void onBackPressed() {
        if (mLayout.getPanelState().equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if (dialog_desc != null && dialog_desc.isShowing()) {
            dialog_desc.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEquilizerChange(ItemAlbums itemAlbums) {
        adapter.notifyDataSetChanged();
        GlobalBus.getBus().removeStickyEvent(itemAlbums);
    }

    @Override
    protected void onResume() {
        MethodsAll.getInstance().setContext(SongByMyPlaylistActivity.this);
        MethodsAll.getInstance().setContext_upgrade(SongByMyPlaylistActivity.this);
        super.onResume();
    }
}