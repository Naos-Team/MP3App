package com.zxfdwka.bestcountrymusic.ringtone.Receiver;

public interface NemosoftsListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message);
}