package com.zxfdwka.bestcountrymusic.mp3.interfaces;

public interface AboutListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message);
}