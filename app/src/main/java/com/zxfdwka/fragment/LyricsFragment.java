package com.zxfdwka.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.item.ItemSong;
import com.zxfdwka.utils.Methods;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;

public class LyricsFragment extends BottomSheetDialogFragment {

    private Methods methods;
    private WebView webView;
    private ImageView iv_close;
    private TextView tv_song_name;
    private ItemSong itemSong;
    private BottomSheetBehavior mBehavior;
    private TextView tv_empty;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.fragment_lyrics_bottom, null);

        if (getArguments() != null) {
            itemSong = (ItemSong) getArguments().getSerializable("item");
        }

        methods = new Methods(getActivity());

        webView = view.findViewById(R.id.webview_lyrics);
        tv_empty = view.findViewById(R.id.tv_lyrics_empty);
        iv_close = view.findViewById(R.id.iv_close);
        tv_song_name = view.findViewById(R.id.tv_lyrics_song_name);

        iv_close.setColorFilter(Color.WHITE);

        tv_song_name.setText(itemSong.getTitle());

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LyricsFragment.this.dismiss();
            }
        });

        if(!itemSong.getLyrics().equals("")) {
            String mimeType = "text/html;charset=UTF-8";
            String encoding = "utf-8";
            String text = "";
            if (methods.isDarkMode()) {
                text = "<html><head>"
                        + "<style> body{color:#fff !important;text-align:left}"
                        + "</style></head>"
                        + "<body>"
                        + itemSong.getLyrics()
                        + "</body></html>";
            } else {
                text = "<html><head>"
                        + "<style> body{color:#000 !important;text-align:left}"
                        + "</style></head>"
                        + "<body>"
                        + itemSong.getLyrics()
                        + "</body></html>";
            }

            webView.setVisibility(View.VISIBLE);
            tv_empty.setVisibility(View.GONE);


            webView.setBackgroundColor(Color.TRANSPARENT);
            webView.loadDataWithBaseURL("blarg://ignored", text, mimeType, encoding, "");
        } else {
            webView.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
        }

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}