package com.zxfdwka.bestcountrymusic.radio.interfaces;

import com.zxfdwka.bestcountrymusic.radio.item.ItemOnDemandCat;
import com.zxfdwka.bestcountrymusic.radio.item.ItemRadio;

import java.util.ArrayList;

public interface HomeListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemRadio> arrayListFeatured, ArrayList<ItemRadio> arrayListMostViewed, ArrayList<ItemOnDemandCat> arrayListOnDemandCat);
}