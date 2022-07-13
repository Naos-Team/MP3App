package com.naosteam.countrymusic.mp3.interfaces;

import com.naosteam.countrymusic.mp3.item.ItemAlbums;
import com.naosteam.countrymusic.mp3.item.ItemArtist;
import com.naosteam.countrymusic.mp3.item.ItemSong;

import java.util.ArrayList;

public interface SearchListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemSong> arrayListSong, ArrayList<ItemArtist> arrayListArtist, ArrayList<ItemAlbums> arrayListAlbums);
}
