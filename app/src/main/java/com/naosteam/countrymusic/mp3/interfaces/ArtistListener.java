package com.naosteam.countrymusic.mp3.interfaces;

import com.naosteam.countrymusic.mp3.item.ItemArtist;

import java.util.ArrayList;

public interface ArtistListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemArtist> arrayList, int totalRecords);
}