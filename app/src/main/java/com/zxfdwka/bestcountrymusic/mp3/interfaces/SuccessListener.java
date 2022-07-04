package com.zxfdwka.bestcountrymusic.mp3.interfaces;

public interface SuccessListener {
    void onStart();
    void onEnd(String success, String registerSuccess, String message);
}