package com.naosteam.countrymusic.mp3.interfaces;

import com.naosteam.countrymusic.mp3.item.ItemMyPlayList;

public interface OptionPlayListListener {
    void onRemove(ItemMyPlayList itemMyPlayList);
    void onShare(ItemMyPlayList itemMyPlayList);
}
