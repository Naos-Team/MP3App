package com.naosteam.countrymusic.mp3.interfaces;

public interface RatingListener {
    void onStart();
    void onEnd(String success, String isRateSuccess, String message, int rating);
}