package com.naosteam.countrymusic.ringtone.interfaces;

import com.naosteam.countrymusic.ringtone.item.ListltemCategory;

import java.util.ArrayList;
public interface CatListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ListltemCategory> arrayList);
}
