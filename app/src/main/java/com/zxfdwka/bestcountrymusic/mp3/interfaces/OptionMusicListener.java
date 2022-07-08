package com.zxfdwka.bestcountrymusic.mp3.interfaces;

import android.view.View;

import com.zxfdwka.bestcountrymusic.mp3.item.ItemSong;

public interface OptionMusicListener {
    void onDescription(ItemSong itemSong);
    void onLike(ItemSong itemSong, View view);
    void onAddToPlaylist(ItemSong itemSong);
    void onAddToQueue(ItemSong itemSong);
    void onSearchYTB(ItemSong itemSong);
    void onShare(ItemSong itemSong);
    void onRate(ItemSong itemSong);
    void onEndLike();
}
