package com.zxfdwka.bestcountrymusic.mp3.interfaces;

import com.zxfdwka.bestcountrymusic.mp3.item.ItemMyPlayList;

public interface OptionPlayListListener {
    void onRemove(ItemMyPlayList itemMyPlayList);
    void onShare(ItemMyPlayList itemMyPlayList);
}
