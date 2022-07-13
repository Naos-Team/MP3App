package com.naosteam.countrymusic.radio.interfaces;

import com.naosteam.countrymusic.radio.item.ItemLanguage;

import java.util.ArrayList;

public interface LanguageListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemLanguage> arrayListLanguage);
}