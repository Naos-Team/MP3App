package com.zxfdwka.bestcountrymusic.radio.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.radio.activity.RadioBaseActivity;
import com.zxfdwka.bestcountrymusic.radio.adapter.AdapterCityDetails;
import com.zxfdwka.bestcountrymusic.radio.asyncTasks.LoadRadioList;
import com.zxfdwka.bestcountrymusic.radio.interfaces.RadioListListener;
import com.zxfdwka.bestcountrymusic.radio.item.ItemRadio;
import com.zxfdwka.bestcountrymusic.radio.utils.Constants;
import com.zxfdwka.bestcountrymusic.radio.utils.Methods;
import com.zxfdwka.bestcountrymusic.radio.utils.SharedPref;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentLanguageDetails extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<ItemRadio> arrayList;
    private AdapterCityDetails adapterLangDetails;
    private CircularProgressBar progressBar;
    private SearchView searchView;
    private Methods methods;
    private TextView textView_empty;
    public static AppCompatButton button_try;
    private LinearLayout ll_empty;
    private String errr_msg;
    SharedPref sharedPref;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_radio_city_detail, container, false);

        Constants.fragmentStatus = Constants.OTHER_HOME;

        methods = new Methods(getActivity());
        sharedPref = new SharedPref(getActivity());

        ll_empty = rootView.findViewById(R.id.ll_empty);
        textView_empty = rootView.findViewById(R.id.textView_empty_msg);
        button_try = rootView.findViewById(R.id.button_empty_try);
        ViewCompat.setBackgroundTintList(button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));
        progressBar = rootView.findViewById(R.id.progressBar_city_details);

        arrayList = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recyclerView_city_detail);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        loadRadioByLang();

        button_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRadioByLang();
            }
        });

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search_radio, menu);

        MenuItem item = menu.findItem(R.id.search);
        //MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

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
                adapterLangDetails.getFilter().filter(s);
                adapterLangDetails.notifyDataSetChanged();
            }
            return true;
        }
    };

    private void loadRadioByLang() {
        if (methods.isConnectingToInternet()) {
            LoadRadioList loadRadioList = new LoadRadioList(new RadioListListener() {
                @Override
                public void onStart() {
                    arrayList.clear();
                    ll_empty.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemRadio> arrayListRadio) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                arrayList.addAll(arrayListRadio);
                                errr_msg = getString(R.string.items_not_found);
                                setAdapter();
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
                    }
                }
            }, methods.getAPIRequest(Constants.METHOD_RADIO_BY_LANGUAGE, 0, "", "", "", "", Constants.itemLanguage.getId(), "", "", "", "", "", "", "", null));
            loadRadioList.execute();
        } else {
            errr_msg = getString(R.string.internet_not_connected);
            setEmpty();
        }
    }

    private void setAdapter() {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((RadioBaseActivity) getActivity()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        ConstraintLayout.LayoutParams radio_lp_grid = new ConstraintLayout.LayoutParams((int) Math.floor(width/2), (int) Math.floor(height*0.25));
        int top = (int) Math.floor(width*0.04);
        int bottom = (int) Math.floor(width*0.04);
        radio_lp_grid.setMargins(0, top, 0, bottom);

        adapterLangDetails = new AdapterCityDetails(getActivity(), arrayList, radio_lp_grid);
        recyclerView.setAdapter(adapterLangDetails);
        setEmpty();
    }

    private void setEmpty() {
        progressBar.setVisibility(View.GONE);
        if (arrayList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText(errr_msg);
            recyclerView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }
}
