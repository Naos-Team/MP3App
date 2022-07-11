package com.zxfdwka.bestcountrymusic.radio.interfaces;


import com.zxfdwka.bestcountrymusic.radio.item.ItemOnDemandCat;

import java.util.ArrayList;

public interface OnDemandCatListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemOnDemandCat> arrayListOnDemandCat);
}