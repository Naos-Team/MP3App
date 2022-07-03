package com.zxfdwka.interfaces;

import com.zxfdwka.item.ItemAlbums;
import com.zxfdwka.item.ItemApps;
import com.zxfdwka.item.ItemArtist;
import com.zxfdwka.item.ItemCountry;
import com.zxfdwka.item.ItemGenres;
import com.zxfdwka.item.ItemHomeBanner;
import com.zxfdwka.item.ItemServerPlayList;
import com.zxfdwka.item.ItemSong;

import java.util.ArrayList;

public interface HomeListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemHomeBanner> arrayListBanner, ArrayList<ItemAlbums> arrayListAlbums, ArrayList<ItemArtist> arrayListArtist, ArrayList<ItemServerPlayList> arrayListPlaylist, ArrayList<ItemSong> arrayListSongs, ArrayList<ItemCountry> arrayListCountry, ArrayList<ItemGenres> arrayListGenres, ArrayList<ItemApps> arrayListApps);
}
