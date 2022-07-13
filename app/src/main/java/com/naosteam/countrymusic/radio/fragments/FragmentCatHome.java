package com.naosteam.countrymusic.radio.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.radio.activity.RadioBaseActivity;
import com.naosteam.countrymusic.radio.adapter.AdapterRadioList;
import com.naosteam.countrymusic.radio.asyncTasks.LoadHome;
import com.naosteam.countrymusic.radio.asyncTasks.LoadRadioList;
import com.naosteam.countrymusic.radio.interfaces.HomeListener;
import com.naosteam.countrymusic.radio.interfaces.RadioListListener;
import com.naosteam.countrymusic.radio.item.ItemOnDemandCat;
import com.naosteam.countrymusic.radio.item.ItemRadio;
import com.naosteam.countrymusic.radio.utils.Constants;
import com.naosteam.countrymusic.radio.utils.Methods;
import com.naosteam.countrymusic.radio.utils.SharedPref;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentCatHome extends Fragment {

    private ArrayList<Object> arrayList;
    private AdapterRadioList adapterRadioList;
    private RecyclerView recyclerView;
    private LinearLayout ll_empty;
    private SearchView searchView;
    private Methods methods;
    private CircularProgressBar progressBar;
    private TextView textView_empty;
    public static AppCompatButton button_try;
    private SharedPref sharedPref;
    private String type;
    private ConstraintLayout.LayoutParams radio_lp_grid, radio_lp_linear;


    private View rootView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_radio_cat, container, false);

        Bundle bundle = getArguments();
        if(bundle != null){
            type = bundle.getString("type");
        }

        Constants.fragmentStatus = Constants.OTHER_HOME;

        sharedPref = new SharedPref(getActivity());
        methods = new Methods(getActivity());
        progressBar = rootView.findViewById(R.id.progressBar_cat);
        button_try = rootView.findViewById(R.id.button_empty_try);
        ll_empty = rootView.findViewById(R.id.ll_empty);
        ViewCompat.setBackgroundTintList(button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));

        setItemResponsive();

        setHasOptionsMenu(true);

        arrayList = new ArrayList<>();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(arrayList.get(position) instanceof AdView){
                    return 2;
                }else{
                    return 1;
                }
            }
        });
        recyclerView = rootView.findViewById(R.id.recyclerView_cat);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);

        switch (type){
            case "featured":
                loadFeature("featured");

                button_try.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadFeature("featured");
                    }
                });
                break;
            case "trending":
                loadFeature("trending");

                button_try.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadFeature("trending");
                    }
                });
                break;
            case "latest":
                loadLatest();

                button_try.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadLatest();
                    }
                });
                break;
        }
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
        for (int i = Constants.ITEM_PER_AD_GRID; i < arrayList.size(); i += Constants.ITEM_PER_AD_GRID+1){
            if(Constants.adBannerShow++ < Constants.BANNER_SHOW_LIMIT){
                final AdView adView = new AdView(getContext());
                adView.setAdSize(AdSize.SMART_BANNER);
                adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
                adView.loadAd(new AdRequest.Builder().build());
                arrayList.add(i, adView);
            }
        }
    }

    private void loadLatest() {
        if (methods.isConnectingToInternet()) {
            LoadRadioList loadRadioList = new LoadRadioList(new RadioListListener() {
                @Override
                public void onStart() {
                    if (getActivity() != null) {
                        arrayList.clear();
                        ll_empty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemRadio> arrayListRadio) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                arrayList.addAll(arrayListRadio);
                                getBannerAds();
                                if (arrayList.size() > 0) {
                                    adapterRadioList = new AdapterRadioList(getActivity(), arrayList, radio_lp_grid, true);
                                    recyclerView.setAdapter(adapterRadioList);
                                    if (Constants.arrayList_radio.size() == 0) {
                                        for (Object o : arrayList){
                                            if(o instanceof ItemRadio){
                                                Constants.arrayList_radio.add((ItemRadio) o);
                                            }
                                        }
                                        ((RadioBaseActivity) getActivity()).changeText(Constants.arrayList_radio.get(0));
                                    }
                                }
                            } else {
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                        setEmpty();
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_LATEST, 0, "", "", "", "", "", "", "", "", "", "", "", "", null));
            loadRadioList.execute();
        } else {
            methods.showToast(getString(R.string.internet_not_connected));
        }
    }

    private void loadFeature(final String type) {
        LoadHome loadHome = new LoadHome(new HomeListener() {
            @Override
            public void onStart() {
                if (getActivity() != null) {
                    arrayList.clear();
                    ll_empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onEnd(String success, ArrayList<ItemRadio> arrayList_featured, ArrayList<ItemRadio> arrayList_mostviewed, ArrayList<ItemOnDemandCat> arrayList_ondemand_cat) {
                if (getActivity() != null) {
                    if (success.equals("1")) {
                        switch (type){
                            case "featured":
                                arrayList.addAll(arrayList_featured);
                                break;
                            case "trending":
                                arrayList.addAll(arrayList_mostviewed);
                                break;
                        }
                        getBannerAds();
                        if (arrayList.size() > 0) {
                            adapterRadioList = new AdapterRadioList(getActivity(), arrayList, radio_lp_grid, true);
                            recyclerView.setAdapter(adapterRadioList);
                        }
                    } else {
                        methods.showToast(getString(R.string.error_server));
                    }
                    progressBar.setVisibility(View.GONE);
                    setEmpty();
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_HOME, 0, "", "", "", "", "", "", "", "", "", "", "", "", null));
        loadHome.execute();
    }

    private void setEmpty() {
        if (arrayList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText("No Item Found");
            recyclerView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search_radio, menu);
        //MenuItem item = menu.findItem(R.id.search);
        //MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
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
            if (arrayList.size() > 0) {
                if (!searchView.isIconified()) {
                    adapterRadioList.getFilter().filter(s);
                    adapterRadioList.notifyDataSetChanged();
                }
            }
            return true;
        }
    };
}
