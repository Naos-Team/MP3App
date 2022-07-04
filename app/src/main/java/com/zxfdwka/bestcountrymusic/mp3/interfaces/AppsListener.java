package com.zxfdwka.bestcountrymusic.mp3.interfaces;

import com.zxfdwka.bestcountrymusic.mp3.item.ItemApps;

import java.util.ArrayList;

public interface AppsListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemApps> arrayList, int total_records);
}