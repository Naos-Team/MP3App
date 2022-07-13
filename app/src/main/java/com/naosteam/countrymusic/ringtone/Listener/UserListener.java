package com.naosteam.countrymusic.ringtone.Listener;

import com.naosteam.countrymusic.ringtone.item.ListltemUser;

import java.util.ArrayList;


public interface UserListener {
    void onStart();
    void onEnd(String success, ArrayList<ListltemUser> arrayListCat);
}
