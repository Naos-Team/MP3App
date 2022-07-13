package com.naosteam.countrymusic.ringtone.Listener;

import com.naosteam.countrymusic.ringtone.item.ItemRingtone;

import java.util.ArrayList;

public interface RingtoneListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemRingtone> arrayListCat);
}
