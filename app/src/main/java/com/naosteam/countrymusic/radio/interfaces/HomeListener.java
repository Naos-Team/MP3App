package com.naosteam.countrymusic.radio.interfaces;

import com.naosteam.countrymusic.radio.item.ItemOnDemandCat;
import com.naosteam.countrymusic.radio.item.ItemRadio;

import java.util.ArrayList;

public interface HomeListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemRadio> arrayListFeatured, ArrayList<ItemRadio> arrayListMostViewed, ArrayList<ItemOnDemandCat> arrayListOnDemandCat);
}