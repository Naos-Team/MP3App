package com.zxfdwka.bestcountrymusic.mp3.interfaces;

import com.zxfdwka.bestcountrymusic.mp3.item.ItemAlbums;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemArtist;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemSong;

import java.util.ArrayList;

public interface SearchListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemSong> arrayListSong, ArrayList<ItemArtist> arrayListArtist, ArrayList<ItemAlbums> arrayListAlbums);
}
