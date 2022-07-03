package com.zxfdwka.interfaces;

import com.zxfdwka.item.ItemAlbums;
import com.zxfdwka.item.ItemArtist;
import com.zxfdwka.item.ItemSong;

import java.util.ArrayList;

public interface SearchListener {
    void onStart();
    void onEnd(String success, ArrayList<ItemSong> arrayListSong, ArrayList<ItemArtist> arrayListArtist, ArrayList<ItemAlbums> arrayListAlbums);
}
