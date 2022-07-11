package com.zxfdwka.bestcountrymusic.radio.interfaces;
import com.zxfdwka.bestcountrymusic.radio.item.ItemRadio;

import java.util.ArrayList;

public interface RadioListListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemRadio> arrayListRadio);
}