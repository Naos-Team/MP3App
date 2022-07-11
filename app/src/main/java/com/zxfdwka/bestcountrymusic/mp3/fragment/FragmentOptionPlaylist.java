package com.zxfdwka.bestcountrymusic.mp3.fragment;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;
import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.mp3.activity.BaseActivity;
import com.zxfdwka.bestcountrymusic.mp3.adapter.AdapterMyPlaylist;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.OptionMusicListener;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.OptionPlayListListener;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemMyPlayList;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemSong;
import com.zxfdwka.bestcountrymusic.mp3.utils.Methods;

public class FragmentOptionPlaylist extends BottomSheetDialogFragment {
    private ConstraintLayout layout_option_playlist, btn_add_queue_option_playlist,
            btn_share_option_playlist;
    private TextView txt_title_option_playlist;
    private BottomSheetBehavior mBehavior;
    private ItemMyPlayList itemMyPlayList;
    private OptionPlayListListener listener;
    private ImageView imageView1, imageView2, imageView3, imageView4;
    private int posi = -1;

    public FragmentOptionPlaylist(ItemMyPlayList itemMyPlayList, OptionPlayListListener listener) {
        this.itemMyPlayList = itemMyPlayList;
        this.listener = listener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.fragment_option_playlist, null);
        layout_option_playlist = view.findViewById(R.id.layout_option_playlist);
        btn_add_queue_option_playlist = view.findViewById(R.id.btn_add_queue_option_playlist);
        btn_share_option_playlist = view.findViewById(R.id.btn_share_option_playlist);
        txt_title_option_playlist = view.findViewById(R.id.txt_title_option_playlist);

        imageView1 = view.findViewById(R.id.iv_myplaylist_option1);
        imageView2 = view.findViewById(R.id.iv_myplaylist_option2);
        imageView3 = view.findViewById(R.id.iv_myplaylist_option3);
        imageView4 = view.findViewById(R.id.iv_myplaylist_option4);

        setUp();
        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                getActivity().getResources().getDisplayMetrics().widthPixels,
                getActivity().getResources().getDisplayMetrics().heightPixels
        );
        layout_option_playlist.setLayoutParams(layoutParams);
        layout_option_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentOptionPlaylist.this.dismiss();
            }
        });
        return dialog;
    }

    private void setUp(){
        Picasso.get()
                .load(itemMyPlayList.getArrayListUrl().get(3))
                .placeholder(R.drawable.placeholder_song)
                .into(imageView1);
        Picasso.get()
                .load(itemMyPlayList.getArrayListUrl().get(2))
                .placeholder(R.drawable.placeholder_song)
                .into(imageView2);
        Picasso.get()
                .load(itemMyPlayList.getArrayListUrl().get(1))
                .placeholder(R.drawable.placeholder_song)
                .into(imageView3);
        Picasso.get()
                .load(itemMyPlayList.getArrayListUrl().get(0))
                .placeholder(R.drawable.placeholder_song)
                .into(imageView4);
        txt_title_option_playlist.setText(itemMyPlayList.getName());

        btn_add_queue_option_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRemove(itemMyPlayList);
                FragmentOptionPlaylist.this.dismiss();
            }
        });

        btn_share_option_playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onShare(itemMyPlayList);
                FragmentOptionPlaylist.this.dismiss();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}
