package com.naosteam.countrymusic.mp3.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.naosteam.countrymusic.mp3.utils.Methods;
import com.naosteam.countrymusic.mp3.activity.MainActivity;
import com.naosteam.countrymusic.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class FragmentDashBoard extends Fragment {

    Methods methods;
    private FragmentManager fm;
    static AHBottomNavigation bottomNavigation;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        setHasOptionsMenu(true);

        methods = new Methods(getActivity());

        fm = getFragmentManager();

       bottomNavigation = (AHBottomNavigation) rootView.findViewById(R.id.space);

// Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem(getString(R.string.home), R.mipmap.ic_home_bottom, R.color.colorPrimary);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(getString(R.string.recent), R.mipmap.ic_recent, R.color.colorPrimary);
//        AHBottomNavigationItem item3 = new AHBottomNavigationItem(getString(R.string.music_library), R.mipmap.ic_music_library, R.color.colorPrimary);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(getString(R.string.categories), R.mipmap.ic_categories, R.color.colorPrimary);
        AHBottomNavigationItem item5 = new AHBottomNavigationItem(getString(R.string.latest), R.mipmap.ic_latest, R.color.colorPrimary);

// Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item4);
        bottomNavigation.addItem(item5);

        bottomNavigation.setColored(false);
//        bottomNavigation.setDefaultBackgroundColor(getActivity().getResources().getColor(R.color.grey));
        bottomNavigation.setAccentColor(getActivity().getResources().getColor(R.color.bottom_item_active_color));
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch (position){
                    case 0:
                        FragmentHome f1 = new FragmentHome();
                        loadFrag(f1, getString(R.string.home));
                        break;
                    case 1:
                        FragmentRecentSongs frecent = new FragmentRecentSongs();
                        loadFrag(frecent, getString(R.string.recently_played));
                        break;
                    case 2:
                        FragmentCategories fcat = new FragmentCategories();
                        loadFrag(fcat, getString(R.string.categories));
                        break;
                    case 3:
                        FragmentLatest flatest = new FragmentLatest();
                        loadFrag(flatest, getString(R.string.latest));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });


        FragmentHome f1 = new FragmentHome();
        loadFrag(f1, getString(R.string.home));

        return rootView;
    }

    public void loadFrag(Fragment f1, String name) {
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (name.equals(getString(R.string.search))) {
            ft.hide(fm.getFragments().get(fm.getBackStackEntryCount()));
            ft.add(R.id.fragment_dash, f1, name);
            ft.addToBackStack(name);
        } else {
            ft.replace(R.id.fragment_dash, f1, name);
        }
        ft.commitAllowingStateLoss();

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(name);
    }
}