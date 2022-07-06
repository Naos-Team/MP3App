package com.zxfdwka.bestcountrymusic.mp3.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zxfdwka.bestcountrymusic.mp3.adapter.AdapterGenres;
import com.zxfdwka.bestcountrymusic.mp3.asyncTask.LoadGenres;
import com.zxfdwka.bestcountrymusic.mp3.activity.ArtistByGenreActivity;
import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.GenresListener;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.InterAdListener;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemGenres;
import com.zxfdwka.bestcountrymusic.mp3.utils.Constant;
import com.zxfdwka.bestcountrymusic.mp3.utils.EndlessRecyclerViewScrollListener;
import com.zxfdwka.bestcountrymusic.mp3.utils.Methods;
import com.zxfdwka.bestcountrymusic.mp3.utils.RecyclerItemClickListener;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdsManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentGenres extends Fragment {

    private Methods methods;
    private RecyclerView rv;
    private AdapterGenres adapterGenres;
    private ArrayList<ItemGenres> arrayList, arrayListTemp;
    private CircularProgressBar progressBar;
    private FrameLayout frameLayout;
    private GridLayoutManager glm_banner;
    private Boolean isLoading = false;

    private String errr_msg;
    private SearchView searchView;
    private int page = 1;
    private Boolean isOver = false, isScroll = false;
    private NativeAdsManager mNativeAdsManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_categories, container, false);

        methods = new Methods(getActivity(), new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                try {
                    Intent intent = new Intent(getActivity(), ArtistByGenreActivity.class);
                    intent.putExtra("item", adapterGenres.getItem(position));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        arrayList = new ArrayList<>();
        arrayListTemp = new ArrayList<>();

        progressBar = rootView.findViewById(R.id.pb_cat);
        frameLayout = rootView.findViewById(R.id.fl_empty);

        rv = rootView.findViewById(R.id.rv_cat);
        glm_banner = new GridLayoutManager(getActivity(), 3);
        glm_banner.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
//                return adapterCat.isHeader(position) ? glm_banner.getSpanCount() : 1;
                return (adapterGenres.getItemViewType(position) >= 1000 || adapterGenres.isHeader(position)) ? glm_banner.getSpanCount() : 1;
            }
        });

        rv.setLayoutManager(glm_banner);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);

        rv.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInterAd(position, "");
            }
        }));

        rv.addOnScrollListener(new EndlessRecyclerViewScrollListener(glm_banner) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if (!isOver) {
                    if (!isLoading) {
                        isLoading = true;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isScroll = true;
                                loadGenres();
                            }
                        }, 0);
                    }
                } else {
                    adapterGenres.hideHeader();
                }
            }
        });

        loadGenres();

        setHasOptionsMenu(true);
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
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if (adapterGenres != null) {
                if (!searchView.isIconified()) {
                    adapterGenres.getFilter().filter(s);
                    adapterGenres.notifyDataSetChanged();
                }
            }
            return true;
        }
    };

    private void loadGenres() {
        if (methods.isNetworkAvailable()) {
            LoadGenres loadGenres = new LoadGenres(new GenresListener() {
                @Override
                public void onStart() {
                    if (arrayList.size() == 0) {
                        frameLayout.setVisibility(View.GONE);
                        rv.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemGenres> arrayListGenres, int total_records) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                if (arrayListGenres.size() == 0) {
                                    try {
                                        adapterGenres.hideHeader();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    isOver = true;
                                    errr_msg = getString(R.string.err_no_genres_found);
                                    setEmpty();
                                } else {
                                    addNewDataToArrayList(arrayListGenres, total_records);

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
            }, methods.getAPIRequest(Constant.METHOD_GENRES, page, "", "", "", "", "", "", "", "","","","","","","","", null));
            loadGenres.execute(String.valueOf(page));
        } else {
            errr_msg = getString(R.string.err_internet_not_conn);
            setEmpty();
        }
    }



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

                                adapterGenres.addAds(unifiedNativeAd);

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
                        adapterGenres.setFBNativeAdManager(mNativeAdsManager);
                    }

                    @Override
                    public void onAdError(AdError adError) {

                    }
                });
                mNativeAdsManager.loadAds();
            }
        }
    }

    private void addNewDataToArrayList(ArrayList<ItemGenres> arrayListGenres, int total_records) {
        arrayListTemp.addAll(arrayListGenres);
        for (int i = 0; i < arrayListGenres.size(); i++) {
            arrayList.add(arrayListGenres.get(i));

            if (Constant.isNativeAd) {
                int abc = arrayList.lastIndexOf(null);
                if (((arrayList.size() - (abc + 1)) % Constant.adNativeDisplay == 0) && (arrayListGenres.size() - 1 != i || arrayListTemp.size() != total_records)) {
                    arrayList.add(null);
                }
            }
        }
    }

    private void setAdapter() {
        if (!isScroll) {
            loadNativeAds();

            adapterGenres = new AdapterGenres(getActivity(), arrayList);
            rv.setAdapter(adapterGenres);
            setEmpty();
        } else {
            adapterGenres.notifyDataSetChanged();
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
            if (errr_msg.equals(getString(R.string.err_no_genres_found))) {
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
                    loadGenres();
                }
            });


            frameLayout.addView(myView);
        }
    }

    @Override
    public void onDestroy() {
        if(adapterGenres != null) {
            adapterGenres.destroyNativeAds();
        }
        super.onDestroy();
    }
}