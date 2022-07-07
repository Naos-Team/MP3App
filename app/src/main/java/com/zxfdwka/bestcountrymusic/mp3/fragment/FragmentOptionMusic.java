package com.zxfdwka.bestcountrymusic.mp3.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.zxfdwka.bestcountrymusic.mp3.activity.R;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemSong;
import com.zxfdwka.bestcountrymusic.mp3.utils.Methods;

public class FragmentOptionMusic extends BottomSheetDialogFragment {
    private ConstraintLayout layout_option_music;
    private BottomSheetBehavior mBehavior;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.fragment_option_music, null);
        layout_option_music = view.findViewById(R.id.layout_option_music);

//        if (getArguments() != null) {
//            itemSong = (ItemSong) getArguments().getSerializable("item");
//        }
//        tv_empty = view.findViewById(R.id.tv_lyrics_empty);
//        iv_close = view.findViewById(R.id.iv_close);
//        tv_song_name = view.findViewById(R.id.tv_lyrics_song_name);
//
//        iv_close.setColorFilter(Color.WHITE);
//
//        tv_song_name.setText(itemSong.getTitle());
//
//        iv_close.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentOptionMusic.this.dismiss();
//            }
//        });

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                getActivity().getResources().getDisplayMetrics().widthPixels,
                getActivity().getResources().getDisplayMetrics().heightPixels
        );
        layout_option_music.setLayoutParams(layoutParams);
        layout_option_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentOptionMusic.this.dismiss();
            }
        });
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}
