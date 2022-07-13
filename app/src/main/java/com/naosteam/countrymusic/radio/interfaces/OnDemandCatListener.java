package com.naosteam.countrymusic.radio.interfaces;


import com.naosteam.countrymusic.radio.item.ItemOnDemandCat;

import java.util.ArrayList;

public interface OnDemandCatListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemOnDemandCat> arrayListOnDemandCat);
}