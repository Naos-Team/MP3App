package com.naosteam.countrymusic.mp3.interfaces;

import com.naosteam.countrymusic.mp3.item.ItemSong;

import java.util.ArrayList;

public interface SongListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemSong> arrayList, int total_records);
}