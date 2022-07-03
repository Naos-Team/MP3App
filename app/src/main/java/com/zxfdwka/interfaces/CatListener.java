package com.zxfdwka.interfaces;

import com.zxfdwka.item.ItemCat;

import java.util.ArrayList;

public interface CatListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemCat> arrayList, int total_records);
}
