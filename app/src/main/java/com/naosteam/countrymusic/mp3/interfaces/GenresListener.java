package com.naosteam.countrymusic.mp3.interfaces;

import com.naosteam.countrymusic.mp3.item.ItemGenres;

import java.util.ArrayList;

public interface GenresListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemGenres> arrayList, int total_records);
}