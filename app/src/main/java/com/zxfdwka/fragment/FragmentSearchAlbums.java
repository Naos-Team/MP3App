package com.zxfdwka.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zxfdwka.adapter.AdapterAlbums;
import com.zxfdwka.asyncTask.LoadAlbums;
import com.zxfdwka.interfaces.AlbumsListener;
import com.zxfdwka.interfaces.InterAdListener;
import com.zxfdwka.item.ItemAlbums;
import com.zxfdwka.bestcountrymusic.MainActivity;
import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.SongByCatActivity;
import com.zxfdwka.utils.Constant;
import com.zxfdwka.utils.EndlessRecyclerViewScrollListener;
import com.zxfdwka.utils.Methods;
import com.zxfdwka.utils.RecyclerItemClickListener;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdsManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentSearchAlbums extends Fragment {

    private Methods methods;
    private RecyclerView rv;
    private AdapterAlbums adapter;
    private ArrayList<ItemAlbums> arrayList_albums, arrayListTemp;
    private CircularProgressBar progressBar;
    private FrameLayout frameLayout;

    private String errr_msg;
    private int page = 1;
    private Boolean isOver = false, isScroll = false, isLoading = false;
    private NativeAdsManager mNativeAdsManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_albums, container, false);

        methods = new Methods(getActivity(), new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                Intent intent = new Intent(getActivity(), SongByCatActivity.class);
                intent.putExtra("type", getString(R.string.albums));
                intent.putExtra("id", adapter.getItem(position).getId());
                intent.putExtra("name", adapter.getItem(position).getName());
                startActivity(intent);
            }
        });
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.search_albums));

        arrayList_albums = new ArrayList<>();
        arrayListTemp = new ArrayList<>();

        frameLayout = rootView.findViewById(R.id.fl_empty);
        progressBar = rootView.findViewById(R.id.pb_albums);
        rv = rootView.findViewById(R.id.rv_albums);
        GridLayoutManager glm_banner = new GridLayoutManager(getActivity(),2);
        glm_banner.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return (adapter.getItemViewType(position) >= 1000 || adapter.isHeader(position)) ? glm_banner.getSpanCount() : 1;
            }
        });
        rv.setLayoutManager(glm_banner);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);

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
                                loadAlbums();
                            }
                        }, 0);
                    }
                }
            }
        });

        rv.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInterAd(position, "");
            }
        }));

        loadAlbums();

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
    }

    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            if (methods.isNetworkAvailable()) {
                page = 1;
                isScroll = false;
                Constant.search_item = s.replace(" ", "%20");
                arrayList_albums.clear();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                loadAlbums();
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };

    private void loadAlbums() {
        try {
            if (methods.isNetworkAvailable()) {
                LoadAlbums loadAlbums = new LoadAlbums(new AlbumsListener() {
                    @Override
                    public void onStart() {
                        if (arrayList_albums.size() == 0) {
                            frameLayout.setVisibility(View.GONE);
                            rv.setVisibility(View.GONE);
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemAlbums> arrayListAlbums, int total_records) {
                        if (getActivity() != null) {
                            if (success.equals("1")) {
                                if (!verifyStatus.equals("-1")) {
                                    if (arrayListAlbums.size() == 0) {
                                        isOver = true;
                                        errr_msg = getString(R.string.err_no_albums_found);
                                        try {
                                            adapter.hideHeader();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        setEmpty();
                                    } else {
                                        addNewDataToArrayList(arrayListAlbums, total_records);

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
                }, methods.getAPIRequest(Constant.METHOD_SEARCH, page, "", "", Constant.search_item, "album", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "", null));
                loadAlbums.execute();

            } else {
                errr_msg = getString(R.string.err_internet_not_conn);
                setEmpty();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private void addNewDataToArrayList(ArrayList<ItemAlbums> arrayListAlbums, int total_records) {
        arrayListTemp.addAll(arrayListAlbums);
        for (int i = 0; i < arrayListAlbums.size(); i++) {
            arrayList_albums.add(arrayListAlbums.get(i));

            if (Constant.isNativeAd) {
                int abc = arrayList_albums.lastIndexOf(null);
                if (((arrayList_albums.size() - (abc + 1)) % Constant.adNativeDisplay == 0) && (arrayListAlbums.size() - 1 != i || arrayListTemp.size() != total_records)) {
                    arrayList_albums.add(null);
                }
            }
        }
    }

    private void setAdapter() {
        if (!isScroll) {
            loadNativeAds();

            adapter = new AdapterAlbums(getActivity(), arrayList_albums, true);
            rv.setAdapter(adapter);
            setEmpty();
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    public void setEmpty() {
        if (arrayList_albums.size() > 0) {
            rv.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.GONE);
        } else {
            rv.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);

            frameLayout.removeAllViews();
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View myView = null;
            if (errr_msg.equals(getString(R.string.err_no_albums_found))) {
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
                    loadAlbums();
                }
            });
            frameLayout.addView(myView);
        }
    }

    @Override
    public void onDestroy() {
        if(adapter != null) {
            adapter.destroyNativeAds();
        }
        super.onDestroy();
    }
}