package com.naosteam.countrymusic.mp3.interfaces;

import com.naosteam.countrymusic.mp3.item.ItemAlbums;
import com.naosteam.countrymusic.mp3.item.ItemApps;
import com.naosteam.countrymusic.mp3.item.ItemArtist;
import com.naosteam.countrymusic.mp3.item.ItemCountry;
import com.naosteam.countrymusic.mp3.item.ItemGenres;
import com.naosteam.countrymusic.mp3.item.ItemHomeBanner;
import com.naosteam.countrymusic.mp3.item.ItemServerPlayList;
import com.naosteam.countrymusic.mp3.item.ItemSong;

import java.util.ArrayList;

public interface HomeListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemHomeBanner> arrayListBanner, ArrayList<ItemAlbums> arrayListAlbums, ArrayList<ItemArtist> arrayListArtist, ArrayList<ItemServerPlayList> arrayListPlaylist, ArrayList<ItemSong> arrayListSongs, ArrayList<ItemCountry> arrayListCountry, ArrayList<ItemGenres> arrayListGenres, ArrayList<ItemApps> arrayListApps);
}
