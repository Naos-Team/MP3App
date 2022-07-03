package com.zxfdwka.interfaces;

import com.zxfdwka.item.ItemAlbums;

import java.util.ArrayList;

public interface AlbumsListener {
    void onStart();

    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemAlbums> arrayList, int total_records);
}