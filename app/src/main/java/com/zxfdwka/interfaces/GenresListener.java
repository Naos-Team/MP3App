package com.zxfdwka.interfaces;

import com.zxfdwka.item.ItemGenres;

import java.util.ArrayList;

public interface GenresListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemGenres> arrayList, int total_records);
}