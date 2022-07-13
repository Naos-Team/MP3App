package com.naosteam.countrymusic.ringtone.Login;

public interface SuccessListener {
    void onStart();
    void onEnd(String success, String registerSuccess, String message);
}