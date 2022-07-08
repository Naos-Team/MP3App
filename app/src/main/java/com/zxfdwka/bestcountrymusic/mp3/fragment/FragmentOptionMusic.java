package com.zxfdwka.bestcountrymusic.mp3.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;
import com.zxfdwka.bestcountrymusic.mp3.activity.BaseActivity;
import com.zxfdwka.bestcountrymusic.mp3.activity.R;
import com.zxfdwka.bestcountrymusic.mp3.asyncTask.LoadFav;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.OptionMusicListener;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.SuccessListener;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemSong;
import com.zxfdwka.bestcountrymusic.mp3.utils.Constant;
import com.zxfdwka.bestcountrymusic.mp3.utils.Methods;

public class FragmentOptionMusic extends BottomSheetDialogFragment {
    private ConstraintLayout layout_option_music, btn_like_option_music,
            btn_add_playlist_option_music, btn_add_queue_option_music,
            btn_search_YTB_option_music, btn_description_option_music,
            btn_share_option_music, btn_rate_option_music;
    private ImageView img_option_music, img_fav_option_music;
    private TextView txt_title_option_music, txt_art_option_music, txt_fav_option_music;
    private BottomSheetBehavior mBehavior;
    private ItemSong itemSong;
    private OptionMusicListener listener;
    private Methods methods;
    private BaseActivity baseActivity;

    public FragmentOptionMusic(ItemSong itemSong, OptionMusicListener listener, Methods methods, BaseActivity activity) {
        this.itemSong = itemSong;
        this.listener = listener;
        this.methods = methods;
        this.baseActivity = activity;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        baseActivity.changeFav(itemSong.getIsFavourite());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.fragment_option_music, null);
        layout_option_music = view.findViewById(R.id.layout_option_music);
        btn_like_option_music = view.findViewById(R.id.btn_like_option_music);
        btn_add_playlist_option_music = view.findViewById(R.id.btn_add_playlist_option_music);
        btn_add_queue_option_music = view.findViewById(R.id.btn_add_queue_option_music);
        btn_search_YTB_option_music = view.findViewById(R.id.btn_search_YTB_option_music);
        btn_share_option_music = view.findViewById(R.id.btn_share_option_music);
        btn_rate_option_music = view.findViewById(R.id.btn_rate_option_music);
        btn_description_option_music = view.findViewById(R.id.btn_description_option_music);
        img_option_music = view.findViewById(R.id.img_option_music);
        txt_title_option_music = view.findViewById(R.id.txt_title_option_music);
        txt_art_option_music = view.findViewById(R.id.txt_art_option_music);
        img_fav_option_music = view.findViewById(R.id.img_fav_option_music);
        txt_fav_option_music = view.findViewById(R.id.txt_fav_option_music);

        setUp();
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

    private void changeFav(){
        if (itemSong.getIsFavourite()) {
            img_fav_option_music.setImageDrawable(getResources().getDrawable(R.mipmap.ic_fav_hover));
            txt_fav_option_music.setText("Favorited");
        } else {
            img_fav_option_music.setImageDrawable(getResources().getDrawable(R.mipmap.ic_fav));
            txt_fav_option_music.setText("Favorite");
        }
    }

    private void setUp(){
        Picasso.get().load(itemSong.getImageBig()).into(img_option_music);
        txt_title_option_music.setText(itemSong.getTitle());
        txt_art_option_music.setText(itemSong.getArtist());

        changeFav();

        btn_description_option_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDescription(itemSong);
            }
        });

        btn_like_option_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLike(itemSong, img_fav_option_music);
                int posi = Constant.arrayList_play.indexOf(itemSong);
                if (methods.isNetworkAvailable()) {
                    LoadFav loadFav = new LoadFav(new SuccessListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onEnd(String success, String favSuccess, String message) {
                            if (success.equals("1")) {
                                if (favSuccess.equals("1")) {
                                    Constant.arrayList_play.get(posi).setIsFavourite(true);
                                } else if (favSuccess.equals("-2")) {
                                    methods.getInvalidUserDialog(message);
                                } else {
                                    Constant.arrayList_play.get(posi).setIsFavourite(false);
                                }
                                itemSong = Constant.arrayList_play.get(posi);
                                changeFav();
                                baseActivity.changeFav(itemSong.getIsFavourite());
                                if(getActivity() != null)
                                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, methods.getAPIRequest(Constant.METHOD_FAV, 0, "", itemSong.getId(), "", "song", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "", null));
                    loadFav.execute();
                } else {
                    if(getActivity() != null)
                        Toast.makeText(getActivity(), getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_add_playlist_option_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddToPlaylist(itemSong);
            }
        });

        btn_add_queue_option_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAddToQueue(itemSong);
            }
        });

        btn_search_YTB_option_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSearchYTB(itemSong);
            }
        });

        btn_share_option_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onShare(itemSong);
            }
        });

        btn_rate_option_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRate(itemSong);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}
