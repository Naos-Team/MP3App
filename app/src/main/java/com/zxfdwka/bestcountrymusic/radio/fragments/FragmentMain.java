package com.zxfdwka.bestcountrymusic.radio.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.appbar.AppBarLayout;
import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.radio.utils.Methods;
import com.zxfdwka.bestcountrymusic.radio.utils.SharedPref;

public class FragmentMain extends Fragment {

    SharedPref sharedPref;
    public static AppBarLayout appBarLayout;
    SearchView searchView;
    Methods methods;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_radio, container, false);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            FragmentHome f1 = new FragmentHome();
        ft.add(R.id.frame_content_home, f1, "Radio");
        ft.addToBackStack(getString(R.string.radio));
        ft.commit();

        return rootView;
    }

}