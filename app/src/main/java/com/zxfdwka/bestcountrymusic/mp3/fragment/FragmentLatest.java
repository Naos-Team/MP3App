package com.zxfdwka.bestcountrymusic.mp3.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.zxfdwka.bestcountrymusic.mp3.activity.MainActivity;
import com.zxfdwka.bestcountrymusic.mp3.adapter.AdapterAllSongList;
import com.zxfdwka.bestcountrymusic.mp3.asyncTask.LoadSong;
import com.zxfdwka.bestcountrymusic.mp3.activity.PlayerService;
import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.ClickListenerPlayList;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.InterAdListener;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.SongListener;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemAlbums;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemMyPlayList;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemSong;
import com.zxfdwka.bestcountrymusic.mp3.utils.Constant;
import com.zxfdwka.bestcountrymusic.mp3.utils.EndlessRecyclerViewScrollListener;
import com.zxfdwka.bestcountrymusic.mp3.utils.GlobalBus;
import com.zxfdwka.bestcountrymusic.mp3.utils.Methods;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdsManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentLatest extends Fragment {

    private Methods methods;
    private RecyclerView rv;
    private AdapterAllSongList adapter;
    private ArrayList<ItemSong> arrayList, arrayListTemp;
    private CircularProgressBar progressBar;
    private FrameLayout frameLayout;
    private String addedFrom = "lat";

    private String errr_msg;
    private SearchView searchView;
    private int page = 1;
    private Boolean isOver = false, isScroll = false, isLoading = false, isNewAdded = true, isAllNew = false;
    private NativeAdsManager mNativeAdsManager;
    private ImageView img_Latest;
    private ConstraintLayout iv_latest;
    private TextView tv_Latest;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_latest, container, false);
        setHasOptionsMenu(true);

        methods = new Methods(getActivity(), new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                Intent intent = new Intent(getActivity(), PlayerService.class);
                intent.setAction(PlayerService.ACTION_PLAY);
                getActivity().startService(intent);
            }
        });

        arrayList = new ArrayList<>();
        arrayListTemp = new ArrayList<>();

        progressBar = rootView.findViewById(R.id.pb_latest);
        rv = rootView.findViewById(R.id.rv_latest);
        LinearLayoutManager llm_banner = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm_banner);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);
        rv.setNestedScrollingEnabled(false);

        frameLayout = rootView.findViewById(R.id.fl_empty);

        img_Latest = rootView.findViewById(R.id.img_Latest);
        img_Latest.setImageResource(R.drawable.latest_img);
        img_Latest.setColorFilter(getActivity().getResources().getColor(R.color.white));
        tv_Latest = rootView.findViewById(R.id.tv_Latest);
        tv_Latest.setText(getResources().getString(R.string.latest));

        iv_latest = rootView.findViewById(R.id.iv_latest);
        int[] colors_default = {getActivity().getResources().getColor(R.color.bg_items), getActivity().getResources().getColor(R.color.color_2)};

        ColorDrawable colorDrawable_bottom = new ColorDrawable(getActivity().getResources().getColor(R.color.color_2));
        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(colorDrawable_bottom);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("");

//create a new gradient color
        GradientDrawable gd_default = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP, colors_default);
        gd_default.setCornerRadius(0f);
        iv_latest.setBackground(gd_default);

        TypedValue typedValue = new TypedValue();
        getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, typedValue, true);
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDisplayMetrics().heightPixels * 25 / 100 - typedValue.TYPE_DIMENSION);
        iv_latest.setLayoutParams(layoutParams);

        rv.addOnScrollListener(new EndlessRecyclerViewScrollListener(llm_banner) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if (!isOver) {
                    if (!isLoading) {
                        isLoading = true;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isScroll = true;
                                loadLatestSongs();
                            }
                        }, 0);
                    }
                }
            }
        });

        loadLatestSongs();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
        super.onCreateOptionsMenu(menu, inflater);
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
                AdLoader.Builder builder = new AdLoader.Builder(getActivity(), Constant.ad_native_id);
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
                mNativeAdsManager = new NativeAdsManager(getActivity(), Constant.ad_native_id, 5);
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

    private void loadLatestSongs() {
        if (methods.isNetworkAvailable()) {
            LoadSong loadSong = new LoadSong(new SongListener() {
                @Override
                public void onStart() {
                    if (arrayList.size() == 0) {
                        arrayList.clear();
                        frameLayout.setVisibility(View.GONE);
                        rv.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemSong> arrayListLatest, int total_records) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                if (arrayListLatest.size() == 0) {
                                    isOver = true;
                                    errr_msg = getString(R.string.err_no_songs_found);
                                    setEmpty();
                                } else {
                                    if (Constant.addedFrom.equals(addedFrom)) {
                                        if (isNewAdded) {
                                            for (int i = 0; i < arrayListLatest.size(); i++) {
                                                if (!methods.isContains(arrayListLatest.get(i).getId())) {
                                                    Constant.arrayList_play.add(i + (arrayList.size()), arrayListLatest.get(i));
                                                    Constant.playPos = Constant.playPos + 1;
                                                } else {
                                                    isNewAdded = false;
                                                    break;
                                                }
                                            }
                                            addNewDataToArrayList(arrayListLatest, total_records);
                                            try {
                                                GlobalBus.getBus().postSticky(new ItemMyPlayList("", "", null));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            addNewDataToArrayList(arrayListLatest, total_records);
                                            if (Constant.arrayList_play.size() <= arrayList.size()) {
//                                        Constant.arrayList_play.clear();
//                                                Constant.arrayList_play.addAll(arrayListLatest);

                                                if (!isAllNew) {
                                                    for (int i = 0; i < arrayListLatest.size(); i++) {
                                                        if (!methods.isContains(arrayListLatest.get(i).getId())) {
                                                            Constant.arrayList_play.add(arrayListLatest.get(i));
                                                            isAllNew = true;
                                                        }
                                                    }
                                                } else {
                                                    Constant.arrayList_play.addAll(arrayListLatest);
                                                }

                                                try {
                                                    GlobalBus.getBus().postSticky(new ItemMyPlayList("", "", null));
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    } else {
                                        isNewAdded = false;
                                        addNewDataToArrayList(arrayListLatest, total_records);
                                    }

                                    page = page + 1;
                                    setAdapter();
                                }
                            } else {
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            }
                        } else {
                            errr_msg = getString(R.string.err_server);
                            setEmpty();
                        }
                        progressBar.setVisibility(View.GONE);
                        isLoading = false;
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_LATEST, page, "", "", "", "", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "", null));
            loadSong.execute();
        } else {
            errr_msg = getString(R.string.err_internet_not_conn);
            setEmpty();
        }
    }

    private void addNewDataToArrayList(ArrayList<ItemSong> arrayListLatest, int total_records) {
        arrayListTemp.addAll(arrayListLatest);
        for (int i = 0; i < arrayListLatest.size(); i++) {
            arrayList.add(arrayListLatest.get(i));

            if (Constant.isNativeAd) {
                int abc = arrayList.lastIndexOf(null);
                if (((arrayList.size() - (abc + 1)) % Constant.adNativeDisplay == 0) && (arrayListLatest.size() - 1 != i || arrayListTemp.size() != total_records)) {
                    arrayList.add(null);
                }
            }
        }
    }

    private void setAdapter() {
        if (!isScroll) {
            adapter = new AdapterAllSongList(getActivity(), arrayList, new ClickListenerPlayList() {
                @Override
                public void onClick(int position) {
                    int real_pos = adapter.getRealPos(position, arrayListTemp);

                    Constant.isOnline = true;
                    if (!Constant.addedFrom.equals(addedFrom)) {
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

                }
            }, "online");
            loadNativeAds();
            rv.setAdapter(adapter);
            setEmpty();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    public void setEmpty() {
        if (arrayList.size() > 0) {
            rv.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
        } else {
            rv.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);

            frameLayout.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View myView = null;
            if (errr_msg.equals(getString(R.string.err_no_songs_found))) {
                myView = inflater.inflate(R.layout.layout_err_nodata, null);
            } else if (errr_msg.equals(getString(R.string.err_internet_not_conn))) {
                myView = inflater.inflate(R.layout.layout_err_internet, null);
            } else if (errr_msg.equals(getString(R.string.err_server))) {
                myView = inflater.inflate(R.layout.layout_err_server, null);
            }

            TextView textView = myView.findViewById(R.id.tv_empty_msg);
            textView.setText(errr_msg);

            myView.findViewById(R.id.btn_empty_try).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadLatestSongs();
                }
            });

            frameLayout.addView(myView);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEquilizerChange(ItemAlbums itemAlbums) {
        try {
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        GlobalBus.getBus().removeStickyEvent(itemAlbums);
    }

    @Override
    public void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
    }

    @Override
    public void onStop() {
        GlobalBus.getBus().unregister(this);
        ColorDrawable colorDrawable = new ColorDrawable(getActivity().getResources().getColor(R.color.colorPrimary));
        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(colorDrawable);
        super.onStop();
    }

    @Override
    public void onResume() {
        ColorDrawable colorDrawable = new ColorDrawable(getActivity().getResources().getColor(R.color.color_2));
        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(colorDrawable);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (adapter != null) {
            adapter.destroyNativeAds();
        }
        super.onDestroy();
    }
}