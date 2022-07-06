package com.zxfdwka.bestcountrymusic.ringtone.Listener;

import com.zxfdwka.bestcountrymusic.ringtone.item.ItemRingtone;

import java.util.ArrayList;

public interface RingtoneListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemRingtone> arrayListCat);
}
