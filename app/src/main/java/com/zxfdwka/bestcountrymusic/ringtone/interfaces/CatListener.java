package com.zxfdwka.bestcountrymusic.ringtone.interfaces;

import com.zxfdwka.bestcountrymusic.ringtone.item.ListltemCategory;

import java.util.ArrayList;
public interface CatListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ListltemCategory> arrayList);
}
