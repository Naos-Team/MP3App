package com.naosteam.countrymusic.radio.interfaces;


import com.naosteam.countrymusic.radio.item.ItemCity;

import java.util.ArrayList;

public interface CityListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemCity> arrayListCity);
}