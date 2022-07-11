package com.zxfdwka.bestcountrymusic.radio.interfaces;

import com.zxfdwka.bestcountrymusic.radio.item.ItemLanguage;

import java.util.ArrayList;

public interface LanguageListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemLanguage> arrayListLanguage);
}