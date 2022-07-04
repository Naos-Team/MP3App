package com.zxfdwka.bestcountrymusic.mp3.interfaces;

import com.zxfdwka.bestcountrymusic.mp3.item.ItemSong;

import java.util.ArrayList;

public interface SongListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemSong> arrayList, int total_records);
}