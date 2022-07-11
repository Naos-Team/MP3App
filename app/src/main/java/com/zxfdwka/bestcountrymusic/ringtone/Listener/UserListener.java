package com.zxfdwka.bestcountrymusic.ringtone.Listener;

import com.zxfdwka.bestcountrymusic.ringtone.item.ListltemUser;

import java.util.ArrayList;


public interface UserListener {
    void onStart();
    void onEnd(String success, ArrayList<ListltemUser> arrayListCat);
}
