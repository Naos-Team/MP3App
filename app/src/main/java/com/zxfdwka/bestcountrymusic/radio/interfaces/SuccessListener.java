package com.zxfdwka.bestcountrymusic.radio.interfaces;

public interface SuccessListener {
    void onStart();
    void onEnd(String success, String registerSuccess, String message);
}