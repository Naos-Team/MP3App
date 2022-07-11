package com.zxfdwka.bestcountrymusic.radio.interfaces;


import com.zxfdwka.bestcountrymusic.radio.item.ItemCity;

import java.util.ArrayList;

public interface CityListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemCity> arrayListCity);
}