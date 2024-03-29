package com.naosteam.countrymusic.radio.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.mp3.utils.Constant;
import com.naosteam.countrymusic.mp3.utils.EndlessRecyclerViewScrollListener;
import com.naosteam.countrymusic.radio.activity.RadioBaseActivity;
import com.naosteam.countrymusic.radio.adapter.AdapterRadios;
import com.naosteam.countrymusic.radio.asyncTasks.LoadRadioList;
import com.naosteam.countrymusic.radio.interfaces.RadioListListener;
import com.naosteam.countrymusic.radio.item.ItemRadio;
import com.naosteam.countrymusic.radio.utils.Constants;
import com.naosteam.countrymusic.radio.utils.Methods;
import com.naosteam.countrymusic.radio.utils.SharedPref;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;


import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;


public class FragmentAllRadio extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<Object> arrayList;
    private AdapterRadios adapterRadios;
    private GridLayoutManager glm_banner;
    private CircularProgressBar progressBar;
    private SearchView searchView;
    private Methods methods;
    private TextView textView_empty;
    public static Button button_try;
    private LinearLayout ll_empty;
    private String errr_msg;
    SharedPref sharedPref;
    private int page = 1;
    private ConstraintLayout.LayoutParams radio_lp_grid, radio_lp_linear;
    private Boolean isLoaded = false, isVisible = false, isOver = false, isScroll = false, isLoading = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_radio_city_detail, container, false);

        methods = new Methods(getActivity());
        sharedPref = new SharedPref(getActivity());
        arrayList = new ArrayList<>();

        Constants.fragmentStatus = Constants.OTHER_HOME;

        progressBar = rootView.findViewById(R.id.progressBar_city_details);

        ll_empty = rootView.findViewById(R.id.ll_empty);
        textView_empty = rootView.findViewById(R.id.textView_empty_msg);
        button_try = rootView.findViewById(R.id.button_empty_try);
        ViewCompat.setBackgroundTintList(button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));

        glm_banner = new GridLayoutManager(getActivity(), 2);
        glm_banner.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (arrayList.get(position) instanceof AdView) {
                    return 2;
                } else {
                    return adapterRadios.isHeader(position) ? glm_banner.getSpanCount() : 1;
                }
            }
        });

        recyclerView = rootView.findViewById(R.id.recyclerView_city_detail);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(glm_banner);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(glm_banner) {
            @Override
            public void onLoadMore(int p, int totalItemsCount) {
                if (!isOver) {
                    if (!isLoading) {
                        isLoading = true;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isScroll = true;
                                loadAllRadio();
                            }
                        }, 0);
                    }
                } else {
                    adapterRadios.hideHeader();
                }
            }
        });

        button_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAllRadio();
            }
        });

        setItemResponsive();

        loadAllRadio();

        setHasOptionsMenu(true);
        return rootView;
    }

    private void setItemResponsive(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((RadioBaseActivity) getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        radio_lp_grid = new ConstraintLayout.LayoutParams((int) Math.floor(width/2), (int) Math.floor(height*0.25));
        int top = (int) Math.floor(width*0.04);
        int bottom = (int) Math.floor(width*0.04);
        radio_lp_grid.setMargins(0, top, 0, bottom);

        radio_lp_linear = new ConstraintLayout.LayoutParams((int) Math.floor(width*0.373), (int) Math.floor(height*0.25));
    }

    private void getBannerAds(){

        com.naosteam.countrymusic.mp3.utils.SharedPref mp3_pref = new com.naosteam.countrymusic.mp3.utils.SharedPref(getContext());
        if(mp3_pref.getIsPremium()){
            return;
        }

        for (int i = Constants.ITEM_PER_AD_GRID; i < arrayList.size(); i += Constants.ITEM_PER_AD_GRID+1){
            if(arrayList.get(i) instanceof ItemRadio){
                if(Constants.adBannerShow++ < Constants.BANNER_SHOW_LIMIT){
                    final AdView adView = new AdView(getContext());
                    adView.setAdSize(AdSize.SMART_BANNER);
                    adView.setAdUnitId(Constant.ad_banner_id_test);
                    adView.loadAd(new AdRequest.Builder().build());
                    arrayList.add(i, adView);
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search_radio, menu);


//        MenuItem item = menu.findItem(R.id.search);
//        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
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

            if (!searchView.isIconified()) {
                adapterRadios.getFilter().filter(s);
                adapterRadios.notifyDataSetChanged();
            }
            return true;
        }
    };

    private void loadAllRadio() {
        if (methods.isConnectingToInternet()) {
            LoadRadioList loadRadioList = new LoadRadioList(new RadioListListener() {
                @Override
                public void onStart() {
                    if (arrayList.size() == 0) {
                        ll_empty.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemRadio> arrayListRadio) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                if (arrayListRadio.size() == 0) {
                                    isOver = true;
                                    errr_msg = getString(R.string.no_radio_found);
                                    setEmpty();
                                } else {
                                    page = page + 1;
                                    arrayList.addAll(arrayListRadio);

                                    getBannerAds();

                                    setAdapter();
                                }
                            } else {
                                errr_msg = message;
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                                setEmpty();
                            }
                        } else {
                            errr_msg = getString(R.string.error_server);
                            setEmpty();
                        }
                        progressBar.setVisibility(View.GONE);
                        isLoading = false;
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_ALL_RADIO, page, "", "", "", "", "", "", "", "", "", "", "", "", null));
            loadRadioList.execute();
        } else {
            errr_msg = getString(R.string.internet_not_connected);
            setEmpty();
        }
    }

    private void setAdapter() {
        if (!isScroll) {
            adapterRadios = new AdapterRadios(getActivity(), arrayList, radio_lp_grid);
            recyclerView.setAdapter(adapterRadios);
            setEmpty();
        } else {
            adapterRadios.notifyDataSetChanged();
        }
    }

    private void setEmpty() {
        if (arrayList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText(errr_msg);
            recyclerView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Constants.fragmentStatus = Constants.AT_HOME;
    }
}