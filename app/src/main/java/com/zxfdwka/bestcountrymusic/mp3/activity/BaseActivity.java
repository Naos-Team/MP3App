package com.zxfdwka.bestcountrymusic.mp3.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Target;
import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.mp3.asyncTask.GetRating;
import com.zxfdwka.bestcountrymusic.mp3.asyncTask.LoadFav;
import com.zxfdwka.bestcountrymusic.mp3.asyncTask.LoadRating;
import com.zxfdwka.bestcountrymusic.mp3.asyncTask.LoadSong;
import com.zxfdwka.bestcountrymusic.mp3.fragment.FragmentOptionMusic;
import com.zxfdwka.bestcountrymusic.mp3.fragment.LyricsFragment;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.OptionMusicListener;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.RatingListener;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.SongListener;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.SuccessListener;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemMyPlayList;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemSong;
import com.zxfdwka.bestcountrymusic.mp3.item.MessageEvent;
import com.zxfdwka.bestcountrymusic.mp3.utils.Constant;
import com.zxfdwka.bestcountrymusic.mp3.utils.DBHelper;
import com.zxfdwka.bestcountrymusic.mp3.utils.GlobalBus;
import com.zxfdwka.bestcountrymusic.mp3.utils.Methods;
import com.zxfdwka.bestcountrymusic.mp3.utils.PausableRotateAnimation;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    Methods methods;
    DBHelper dbHelper;
    DrawerLayout drawer;
    public ViewPager viewpager;
    ImagePagerAdapter adapter;
    SlidingUpPanelLayout mLayout;
    NavigationView navigationView;
    AudioManager am;
    Toolbar toolbar;
    Boolean isExpand = false, isRotateAnim = false;
    BottomSheetDialog dialog_desc;
    Dialog dialog_rate;
    RelativeLayout rl_min_header;
    LinearLayout ll_max_header, ll_topplayer, layout_music_player;
    RelativeLayout rl_music_loading;
    private Handler seekHandler = new Handler();
    PausableRotateAnimation rotateAnimation;
    String deviceId;

    RatingBar ratingBar;
    SeekBar seekBar_music, seekbar_min;
    View view_playlist, view_download, view_rate, view_round;
    TextView tv_min_title, tv_min_artist, tv_max_title, tv_max_artist, tv_music_title, tv_music_artist, tv_song_count,
            tv_current_time, tv_total_time;
    RoundedImageView iv_min_song, imageView_pager;
    ImageView iv_max_song, iv_music_bg, iv_min_previous, iv_min_play, iv_min_next, iv_max_fav, iv_max_option, iv_music_shuffle,
            iv_music_repeat, iv_music_previous, iv_music_next, iv_music_play, iv_music_add2playlist, iv_music_share,
            iv_music_download, iv_music_rate, iv_music_volume, imageView_heart, iv_lyrics, iv_list;

    LinearLayout ll_adView_base;
    Handler handler = new Handler();

    static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Constant.context = this;
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        methods = new Methods(this);
        dbHelper = new DBHelper(this);
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        ll_adView_base = findViewById(R.id.ll_adView_base);

        mLayout = findViewById(R.id.sliding_layout);
        toolbar = findViewById(R.id.toolbar_offline_music);
        setSupportActionBar(toolbar);
        methods.forceRTLIfSupported(getWindow());

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        adapter = new ImagePagerAdapter();
        layout_music_player = findViewById(R.id.layout_music_player);
        ll_topplayer = findViewById(R.id.ll_topplayer);
        navigationView = findViewById(R.id.nav_view);
        viewpager = findViewById(R.id.viewPager_song);
        viewpager.setOffscreenPageLimit(5);
        rl_min_header = findViewById(R.id.rl_min_header);
        ll_max_header = findViewById(R.id.ll_max_header);
        rl_music_loading = findViewById(R.id.rl_music_loading);
        ratingBar = findViewById(R.id.rb_music);
        seekBar_music = findViewById(R.id.seekbar_music);
        iv_list = findViewById(R.id.iv_list);
        seekbar_min = findViewById(R.id.seekbar_min);
        seekbar_min.setPadding(0, 0, 0, 0);

        int[] colors = {getResources().getColor(R.color.bg_items), Color.rgb( 119, 136,153)};

        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP, colors);
        gd.setCornerRadius(0f);
        layout_music_player.setBackground(gd);
        RelativeLayout rl = findViewById(R.id.rl);
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        iv_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, PlayListNowActivity.class);
                intent.putExtra("name", "Current Playlist");
                startActivity(intent);
            }
        });

        ll_topplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BaseActivity.this, PlayListNowActivity.class);
                intent.putExtra("name", "Current Playlist");
                startActivity(intent);
            }
        });

        iv_music_bg = findViewById(R.id.iv_music_bg);
        iv_music_play = findViewById(R.id.iv_music_play);
        iv_music_next = findViewById(R.id.iv_music_next);
        iv_music_previous = findViewById(R.id.iv_music_previous);
        iv_music_shuffle = findViewById(R.id.iv_music_shuffle);
        iv_music_repeat = findViewById(R.id.iv_music_repeat);
        iv_music_add2playlist = findViewById(R.id.iv_music_add2playlist);
        iv_music_share = findViewById(R.id.iv_music_share);
        iv_music_download = findViewById(R.id.iv_music_download);
        iv_music_rate = findViewById(R.id.iv_music_rate);
        iv_music_volume = findViewById(R.id.iv_music_volume);

        view_rate = findViewById(R.id.view_music_rate);
        view_download = findViewById(R.id.view_music_download);
        view_playlist = findViewById(R.id.view_music_playlist);
        view_round = findViewById(R.id.vBgLike);

        iv_min_song = findViewById(R.id.iv_min_song);
        iv_max_song = findViewById(R.id.iv_max_song);
        iv_min_previous = findViewById(R.id.iv_min_previous);
        iv_min_play = findViewById(R.id.iv_min_play);
        iv_min_next = findViewById(R.id.iv_min_next);
        iv_max_fav = findViewById(R.id.iv_max_fav);
        iv_max_option = findViewById(R.id.iv_max_option);
        imageView_heart = findViewById(R.id.ivLike);
        iv_lyrics = findViewById(R.id.iv_lyrics);

        tv_current_time = findViewById(R.id.tv_music_time);
        tv_total_time = findViewById(R.id.tv_music_total_time);
        tv_song_count = findViewById(R.id.tv_music_song_count);
        tv_music_title = findViewById(R.id.tv_music_title);
        tv_music_artist = findViewById(R.id.tv_music_artist);
        tv_min_title = findViewById(R.id.tv_min_title);
        tv_min_artist = findViewById(R.id.tv_min_artist);
        tv_max_title = findViewById(R.id.tv_max_title);
        tv_max_artist = findViewById(R.id.tv_max_artist);

        iv_max_option.setColorFilter(Color.WHITE);

        iv_max_fav.setOnClickListener(this);
        iv_max_option.setOnClickListener(this);

        iv_min_play.setOnClickListener(this);
        iv_min_next.setOnClickListener(this);
        iv_min_previous.setOnClickListener(this);

        iv_music_play.setOnClickListener(this);
        iv_music_next.setOnClickListener(this);
        iv_music_previous.setOnClickListener(this);
        iv_music_shuffle.setOnClickListener(this);
        iv_music_repeat.setOnClickListener(this);
        iv_music_add2playlist.setOnClickListener(this);
        iv_music_share.setOnClickListener(this);
        iv_music_download.setOnClickListener(this);
        iv_music_rate.setOnClickListener(this);
        iv_music_volume.setOnClickListener(this);
        iv_lyrics.setOnClickListener(this);

        if (methods.isDarkMode()) {
            iv_music_next.setColorFilter(Color.WHITE);
            iv_music_previous.setColorFilter(Color.WHITE);
            iv_music_add2playlist.setColorFilter(Color.WHITE);
            iv_music_share.setColorFilter(Color.WHITE);
            iv_music_download.setColorFilter(Color.WHITE);
            iv_music_rate.setColorFilter(Color.WHITE);
            iv_music_volume.setColorFilter(Color.WHITE);

            iv_min_next.setColorFilter(Color.WHITE);
            iv_min_play.setColorFilter(Color.WHITE);
            iv_min_previous.setColorFilter(Color.WHITE);
        }

        if (!Constant.isRepeat) {
            if (methods.isDarkMode()) {
                iv_music_repeat.setImageDrawable(getResources().getDrawable(R.drawable.ic_repeat));
            } else {
                iv_music_repeat.setImageDrawable(getResources().getDrawable(R.mipmap.ic_repeat));
            }
        } else {
            iv_music_repeat.setImageDrawable(getResources().getDrawable(R.mipmap.ic_repeat_hover));
        }

        if (!Constant.isSuffle) {
            iv_music_shuffle.setColorFilter(ContextCompat.getColor(BaseActivity.this, R.color.text_black));
        } else {
            iv_music_shuffle.setColorFilter(ContextCompat.getColor(BaseActivity.this, R.color.primary));
        }

//        ImageView iv_white_blur = findViewById(R.id.iv_music_white_blur);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (50 * methods.getScreenHeight() / 100));
//        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        iv_white_blur.setLayoutParams(params);

        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset == 0.0f) {
                    isExpand = false;

                    rl_min_header.setVisibility(View.VISIBLE);
                    ll_max_header.setVisibility(View.INVISIBLE);
                } else if (slideOffset > 0.0f && slideOffset < 1.0f) {
                    rl_min_header.setVisibility(View.VISIBLE);
                    ll_max_header.setVisibility(View.VISIBLE);

                    if (isExpand) {
                        rl_min_header.setAlpha(1.0f - slideOffset);
                        ll_max_header.setAlpha(0.0f + slideOffset);
                    } else {
                        rl_min_header.setAlpha(1.0f - slideOffset);
                        ll_max_header.setAlpha(slideOffset);
                    }
                } else {
                    isExpand = true;

                    rl_min_header.setVisibility(View.INVISIBLE);
                    ll_max_header.setVisibility(View.VISIBLE);
                    changeFav(Constant.arrayList_play.get(Constant.playPos).getIsFavourite());
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    try {
                        if (viewpager.getAdapter() == null || Constant.isNewAdded || !Constant.addedFrom.equals(adapter.getIsLoadedFrom())) {
                            viewpager.setAdapter(adapter);
//                            Constant.isNewAdded = false;
                        }
                        viewpager.setCurrentItem(Constant.playPos);
                    } catch (Exception e) {
                        adapter.notifyDataSetChanged();
                        viewpager.setCurrentItem(Constant.playPos);
                    }
                }
            }
        });

        seekBar_music.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                try {
                    Intent intent = new Intent(BaseActivity.this, PlayerService.class);
                    intent.setAction(PlayerService.ACTION_SEEKTO);
                    intent.putExtra("seekto", methods.getSeekFromPercentage(progress, PlayerService.getInstance().getDuration()));
                    startService(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//        newRotateAnim();
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Constant.isScrolled = true;
            }

            @Override
            public void onPageSelected(int position) {
                changeTextPager(Constant.arrayList_play.get(position));

                View view = viewpager.findViewWithTag("myview" + position);
                changeFav(Constant.arrayList_play.get(position).getIsFavourite());
                if (view != null) {
                    ImageView iv = view.findViewById(R.id.iv_vp_play);
                    if (Constant.playPos == position) {
                        iv.setVisibility(View.GONE);
                    } else {
                        iv.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tv_current_time.setText("00:00");


        if (Constant.pushSID.equals("0")) {
            if (Constant.arrayList_play.size() == 0) {
                Constant.arrayList_play.addAll(dbHelper.loadDataRecent(true, Constant.recentLimit));
                if (Constant.arrayList_play.size() > 0) {
                    GlobalBus.getBus().postSticky(Constant.arrayList_play.get(Constant.playPos));
                }
            }
        } else {
            new LoadSong(new SongListener() {
                @Override
                public void onStart() {
                    Constant.pushSID = "0";
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemSong> arrayList, int total_records) {
                    if (success.equals("1") && !verifyStatus.equals("-1") && arrayList.size() > 0) {
                        Constant.isOnline = true;
                        Constant.arrayList_play.clear();
                        Constant.arrayList_play.addAll(arrayList);
                        Constant.playPos = 0;

                        Intent intent = new Intent(BaseActivity.this, PlayerService.class);
                        intent.setAction(PlayerService.ACTION_PLAY);
                        startService(intent);
                    } else if (verifyStatus.equals("-1")) {
                        methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_SINGLE_SONG, 0, "", Constant.pushSID, "", "", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "", null)).execute();
        }


        handler.postDelayed(runnable, 180000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_search_home:
//                searchView.openSearch();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_min_play:
                playPause();
                break;
            case R.id.iv_music_play:
                playPause();
                break;
            case R.id.iv_min_next:
                next();
                break;
            case R.id.iv_music_next:
                next();
                break;
            case R.id.iv_min_previous:
                previous();
                break;
            case R.id.iv_music_previous:
                previous();
                break;
            case R.id.iv_music_shuffle:
                setShuffle();
                break;
            case R.id.iv_music_repeat:
                setRepeat();
                break;
            case R.id.iv_max_option:
                FragmentOptionMusic fragmentOptionMusic = new FragmentOptionMusic(Constant.arrayList_play.get(viewpager.getCurrentItem()), new OptionMusicListener() {
                    @Override
                    public void onDescription(ItemSong itemSong) {
                        showBottomSheetDialog(itemSong);
                    }

                    @Override
                    public void onLike(ItemSong itemSong, View view) {
                        if (Constant.isLogged) {
                            if (Constant.arrayList_play.size() > 0) {
                                if (Constant.isOnline) {
                                    methods.animateHeartButton(view);
                                    view.setSelected(!view.isSelected());
                                    view.setSelected(view.isSelected());
                                }
                            } else {
                                Toast.makeText(BaseActivity.this, getResources().getString(R.string.err_no_songs_selected), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            methods.clickLogin();
                        }
                    }

                    @Override
                    public void onAddToPlaylist(ItemSong itemSong) {
                        methods.openPlaylists(itemSong, true);
                    }

                    @Override
                    public void onAddToQueue(ItemSong itemSong) {
                        Constant.arrayList_play.add(itemSong);
                        GlobalBus.getBus().postSticky(new ItemMyPlayList("", "", null));
                        Toast.makeText(BaseActivity.this, getString(R.string.add_to_queue), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSearchYTB(ItemSong itemSong) {
                        try {
                            Intent intent = new Intent(Intent.ACTION_SEARCH);
                            intent.setPackage("com.google.android.youtube");
                            intent.putExtra("query", itemSong.getTitle());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(BaseActivity.this, getString(R.string.youtube_not_installed), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onShare(ItemSong itemSong) {
                        shareSong();
                    }

                    @Override
                    public void onRate(ItemSong itemSong) {
                        openRateDialog();
                    }

                    @Override
                    public void onEndLike() {
                    }
                }, methods, BaseActivity.this, false);

                fragmentOptionMusic.show(getSupportFragmentManager(), fragmentOptionMusic.getTag());
                break;
            case R.id.iv_max_fav:
                if (Constant.isLogged) {
                    if (Constant.arrayList_play.size() > 0) {
                        if (Constant.isOnline) {
                            methods.animateHeartButton(view);
//                        methods.animatePhotoLike(view_round, imageView_heart);
                            view.setSelected(!view.isSelected());
                            findViewById(R.id.ivLike).setSelected(view.isSelected());
                            fav();
                        }
                    } else {
                        Toast.makeText(BaseActivity.this, getResources().getString(R.string.err_no_songs_selected), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    methods.clickLogin();
                }
                break;
            case R.id.iv_music_share:
                shareSong();
                break;
            case R.id.iv_music_add2playlist:
                if (Constant.arrayList_play.size() > 0) {
                    methods.openPlaylists(Constant.arrayList_play.get(viewpager.getCurrentItem()), Constant.isOnline);
                } else {
                    Toast.makeText(BaseActivity.this, getResources().getString(R.string.err_no_songs_selected), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_music_download:
                if (checkPerDownload()) {
                    if (Constant.arrayList_play.size() > 0) {
                        methods.download(Constant.arrayList_play.get(viewpager.getCurrentItem()));
                    } else {
                        Toast.makeText(BaseActivity.this, getString(R.string.err_no_songs_selected), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    checkPerDownload();
                }
                break;
            case R.id.iv_music_rate:
                if (Constant.arrayList_play.size() > 0) {
                    openRateDialog();
                }
                break;
            case R.id.iv_music_volume:
                changeVolume();
                break;
            case R.id.iv_lyrics:
                LyricsFragment lyricsFragment = new LyricsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", Constant.arrayList_play.get(viewpager.getCurrentItem()));
                lyricsFragment.setArguments(bundle);

                lyricsFragment.show(getSupportFragmentManager(), lyricsFragment.getTag());
                break;
        }
    }

    public void showBottomSheetDialog(ItemSong itemSong) {
        View view = getLayoutInflater().inflate(R.layout.layout_desc, null);

        dialog_desc = new BottomSheetDialog(this);
        dialog_desc.setContentView(view);
        dialog_desc.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        dialog_desc.show();

        AppCompatButton button = dialog_desc.findViewById(R.id.button_detail_close);
        TextView textView = dialog_desc.findViewById(R.id.tv_desc_title);
        textView.setText(itemSong.getTitle());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_desc.dismiss();
            }
        });

        WebView webview_song_desc = dialog_desc.findViewById(R.id.webView_bottom);
        String mimeType = "text/html;charset=UTF-8";
        String encoding = "utf-8";
        String text = "<html><head>"
                + "<style> body{color: #000 !important;text-align:left}"
                + "</style></head>"
                + "<body>"
                + itemSong.getDescription()
                + "</body></html>";

//        webview_song_desc.loadData(text, mimeType, encoding);
        webview_song_desc.loadDataWithBaseURL("blarg://ignored", text, mimeType, encoding, "");
    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            seekUpdation();
        }
    };

    public void seekUpdation() {
        try {
            seekbar_min.setProgress(methods.getProgressPercentage(PlayerService.exoPlayer.getCurrentPosition(), PlayerService.getInstance().getDuration()));
            seekBar_music.setProgress(methods.getProgressPercentage(PlayerService.exoPlayer.getCurrentPosition(), PlayerService.getInstance().getDuration()));
            tv_current_time.setText(methods.milliSecondsToTimer(PlayerService.exoPlayer.getCurrentPosition(), PlayerService.getInstance().getDuration()));
            tv_total_time.setText(methods.milliSecondsToTimer(PlayerService.exoPlayer.getDuration(), PlayerService.getInstance().getDuration()));
            seekBar_music.setSecondaryProgress(PlayerService.exoPlayer.getBufferedPercentage());
            if (PlayerService.exoPlayer.getPlayWhenReady() && Constant.isAppOpen) {
                seekHandler.removeCallbacks(run);
                seekHandler.postDelayed(run, 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playPause() {
        methods.showRateDialog();
        if (Constant.arrayList_play.size() > 0) {
            Intent intent = new Intent(BaseActivity.this, PlayerService.class);
            if (Constant.isPlayed) {
                intent.setAction(PlayerService.ACTION_TOGGLE);
                startService(intent);
            } else {
                if (!Constant.isOnline || methods.isNetworkAvailable()) {
                    intent.setAction(PlayerService.ACTION_PLAY);
                    startService(intent);
                } else {
                    Toast.makeText(BaseActivity.this, getResources().getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(BaseActivity.this, getResources().getString(R.string.err_no_songs_selected), Toast.LENGTH_SHORT).show();
        }
    }

    public void next() {
        methods.showRateDialog();
        if (Constant.arrayList_play.size() > 0) {
            if (!Constant.isOnline || methods.isNetworkAvailable()) {
                isRotateAnim = false;
                Intent intent = new Intent(BaseActivity.this, PlayerService.class);
                intent.setAction(PlayerService.ACTION_NEXT);
                startService(intent);
            } else {
                Toast.makeText(BaseActivity.this, getResources().getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(BaseActivity.this, getResources().getString(R.string.err_no_songs_selected), Toast.LENGTH_SHORT).show();
        }
    }

    public void previous() {
        methods.showRateDialog();
        if (Constant.arrayList_play.size() > 0) {
            if (!Constant.isOnline || methods.isNetworkAvailable()) {
                isRotateAnim = false;
                Intent intent = new Intent(BaseActivity.this, PlayerService.class);
                intent.setAction(PlayerService.ACTION_PREVIOUS);
                startService(intent);
            } else {
                Toast.makeText(BaseActivity.this, getResources().getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(BaseActivity.this, getResources().getString(R.string.err_no_songs_selected), Toast.LENGTH_SHORT).show();
        }
    }

    public void setRepeat() {
        methods.showRateDialog();
        if (Constant.isRepeat) {
            Constant.isRepeat = false;
            iv_music_repeat.setImageDrawable(getResources().getDrawable(R.mipmap.ic_repeat));
        } else {
            Constant.isRepeat = true;
            iv_music_repeat.setImageDrawable(getResources().getDrawable(R.mipmap.ic_repeat_hover));
        }
    }

    public void setShuffle() {
        methods.showRateDialog();
        if (Constant.isSuffle) {
            Constant.isSuffle = false;
            iv_music_shuffle.setColorFilter(ContextCompat.getColor(BaseActivity.this, R.color.text_black));
        } else {
            Constant.isSuffle = true;
            iv_music_shuffle.setColorFilter(ContextCompat.getColor(BaseActivity.this, R.color.primary));
        }
    }

    private void shareSong() {
        if (Constant.arrayList_play.size() > 0) {

            if (Constant.isOnline || Constant.isDownloaded) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_song));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getString(R.string.listening) + " - " + Constant.arrayList_play.get(viewpager.getCurrentItem()).getTitle() + "\n\nvia " + getResources().getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + getPackageName());
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_song)));
            } else {
                if (checkPer()) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("audio/mp3");
                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse(Constant.arrayList_play.get(viewpager.getCurrentItem()).getUrl()));
                    share.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getString(R.string.listening) + " - " + Constant.arrayList_play.get(viewpager.getCurrentItem()).getTitle() + "\n\nvia " + getResources().getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + getPackageName());
                    startActivity(Intent.createChooser(share, getResources().getString(R.string.share_song)));
                }
            }
        } else {
            Toast.makeText(BaseActivity.this, getResources().getString(R.string.err_no_songs_selected), Toast.LENGTH_SHORT).show();
        }
    }

    public void fav() {
        loadFav(Constant.arrayList_play.get(viewpager.getCurrentItem()).getId(), viewpager.getCurrentItem());
    }

    private void loadFav(String qid, final int posi) {
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
                        changeFav(Constant.arrayList_play.get(posi).getIsFavourite());
                        Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_FAV, 0, "", qid, "", "song", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "", null));
            loadFav.execute();
        } else {
            Toast.makeText(this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    public void changeFav(Boolean isFav) {
        if (isFav) {
            iv_max_fav.setImageDrawable(getResources().getDrawable(R.mipmap.ic_fav_hover));
        } else {
            iv_max_fav.setImageDrawable(getResources().getDrawable(R.mipmap.ic_fav));
        }
        adapter.notifyDataSetChanged();
    }

    private void openRateDialog() {
        dialog_rate = new Dialog(BaseActivity.this);
        dialog_rate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_rate.setContentView(R.layout.layout_review);

        final ImageView iv_close = dialog_rate.findViewById(R.id.iv_rate_close);
        final TextView textView = dialog_rate.findViewById(R.id.tv_rate);
        final RatingBar ratingBar = dialog_rate.findViewById(R.id.rb_add);
        final Button button = dialog_rate.findViewById(R.id.button_submit_rating);
        final Button button_later = dialog_rate.findViewById(R.id.button_later_rating);

        ratingBar.setStepSize(Float.parseFloat("1"));

        if (Constant.arrayList_play.get(viewpager.getCurrentItem()).getUserRating().equals("") || Constant.arrayList_play.get(viewpager.getCurrentItem()).getUserRating().equals("0")) {
            new GetRating(new RatingListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String isRateSuccess, String message, int rating) {
                    if (rating > 0) {
                        ratingBar.setRating(rating);
                        textView.setText(getString(R.string.thanks_for_rating));
                    } else {
                        ratingBar.setRating(1);
                    }
                    Constant.arrayList_play.get(viewpager.getCurrentItem()).setUserRating(String.valueOf(rating));

                }
            }, methods.getAPIRequest(Constant.METHOD_SINGLE_SONG, 0, "", Constant.arrayList_play.get(viewpager.getCurrentItem()).getId(), "", "", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "", null)).execute();
        } else {
            if (Integer.parseInt(Constant.arrayList_play.get(viewpager.getCurrentItem()).getUserRating()) != 0 && !Constant.arrayList_play.get(viewpager.getCurrentItem()).getUserRating().equals("")) {
                textView.setText(getString(R.string.thanks_for_rating));
                ratingBar.setRating(Integer.parseInt(Constant.arrayList_play.get(viewpager.getCurrentItem()).getUserRating()));
            } else {
                ratingBar.setRating(1);
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Constant.isLogged) {
                    if (ratingBar.getRating() != 0) {
                        if (methods.isNetworkAvailable()) {
                            loadRatingApi(String.valueOf((int) ratingBar.getRating()));
                        } else {
                            Toast.makeText(BaseActivity.this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(BaseActivity.this, getString(R.string.select_rating), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    methods.clickLogin();
                }
            }
        });

        button_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_rate.dismiss();
            }
        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_rate.dismiss();
            }
        });

        dialog_rate.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog_rate.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog_rate.show();
        Window window = dialog_rate.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    private void loadRatingApi(final String rate) {

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(BaseActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.loading));

        LoadRating loadRating = new LoadRating(new RatingListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onEnd(String success, String isRateSuccess, String message, int rating) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (success.equals("1")) {
                    if (isRateSuccess.equals("1")) {
                        Constant.arrayList_play.get(viewpager.getCurrentItem()).setAverageRating(String.valueOf(rating));
                        Constant.arrayList_play.get(viewpager.getCurrentItem()).setTotalRate(String.valueOf(Integer.parseInt(Constant.arrayList_play.get(viewpager.getCurrentItem()).getTotalRate() + 1)));
                        Constant.arrayList_play.get(viewpager.getCurrentItem()).setUserRating(String.valueOf(rate));
                        ratingBar.setRating(rating);
                    }
                    Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BaseActivity.this, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
                dialog_rate.dismiss();
            }
        }, methods.getAPIRequest(Constant.METHOD_RATINGS, 0, "", Constant.arrayList_play.get(viewpager.getCurrentItem()).getId(), "", "", "", "", "", "", rate, "", "", "", "", Constant.itemUser.getId(), "", null));

        loadRating.execute();
    }

    private void changeVolume() {

        final RelativePopupWindow popupWindow = new RelativePopupWindow(this);

        // inflate your layout or dynamically add view
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.layout_dailog_volume, null);
        ImageView imageView1 = view.findViewById(R.id.iv1);
        ImageView imageView2 = view.findViewById(R.id.iv2);
        imageView1.setColorFilter(Color.BLACK);
        imageView2.setColorFilter(Color.BLACK);

        VerticalSeekBar seekBar = view.findViewById(R.id.seekbar_volume);
        seekBar.getThumb().setColorFilter(ContextCompat.getColor(BaseActivity.this, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        seekBar.getProgressDrawable().setColorFilter(ContextCompat.getColor(BaseActivity.this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        final AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        seekBar.setMax(am.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        int volume_level = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBar.setProgress(volume_level);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        popupWindow.setContentView(view);
        popupWindow.showOnAnchor(iv_music_volume, RelativePopupWindow.VerticalPosition.ABOVE, RelativePopupWindow.HorizontalPosition.CENTER);
    }

//    private void download() {
//        if (Constant.arrayList_play.size() > 0) {
//
//            File root = new File(getExternalCacheDir().getAbsolutePath() + "/temp");
//            if (!root.exists()) {
//                root.mkdirs();
//            }
//
//            Random random = new Random();
//            String a= String.valueOf(System.currentTimeMillis());
//            String name = String.valueOf(random.nextInt((999999-100000) + 100000)) + a.substring(a.length()-6,a.length()-1);
//
////            File file = new File(root, Constant.arrayList_play.get(viewpager.getCurrentItem()).getTitle() + ".mp3");
//            File file = new File(root, name + ".mp3");
//
////            if (!file.exists()) {
//            if (!dbHelper.checkDownload(Constant.arrayList_play.get(viewpager.getCurrentItem()).getId())) {
//
//                String url = Constant.arrayList_play.get(viewpager.getCurrentItem()).getUrl();
//
//                if (!DownloadService.getInstance().isDownloading()) {
//                    Intent serviceIntent = new Intent(BaseActivity.this, DownloadService.class);
//                    serviceIntent.setAction(DownloadService.ACTION_START);
//                    serviceIntent.putExtra("downloadUrl", url);
//                    serviceIntent.putExtra("file_path", root.toString());
//                    serviceIntent.putExtra("file_name", file.getName());
//                    serviceIntent.putExtra("item", Constant.arrayList_play.get(viewpager.getCurrentItem()));
//                    startService(serviceIntent);
//                } else {
//                    Intent serviceIntent = new Intent(BaseActivity.this, DownloadService.class);
//                    serviceIntent.setAction(DownloadService.ACTION_ADD);
//                    serviceIntent.putExtra("downloadUrl", url);
//                    serviceIntent.putExtra("file_path", root.toString());
//                    serviceIntent.putExtra("file_name", file.getName());
//                    serviceIntent.putExtra("item", Constant.arrayList_play.get(viewpager.getCurrentItem()));
//                    startService(serviceIntent);
//                }
//
//                new AsyncTask<String, String, String>() {
//                    @Override
//                    protected String doInBackground(String... strings) {
//
//                        String json = JsonUtils.okhttpGET(Constant.URL_DOWNLOAD_COUNT + Constant.arrayList_play.get(viewpager.getCurrentItem()).getId());
//                        return null;
//                    }
//                }.execute();
//            } else {
//                Toast.makeText(BaseActivity.this, getResources().getString(R.string.already_download), Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(BaseActivity.this, getResources().getString(R.string.err_no_songs_selected), Toast.LENGTH_SHORT).show();
//        }
//    }

    public void newRotateAnim() {
        if (rotateAnimation != null) {
            rotateAnimation.cancel();
        }
        rotateAnimation = new PausableRotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(Constant.rotateSpeed);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setInterpolator(new LinearInterpolator());
    }

    public void changeImageAnimation(Boolean isPlay) {
        try {
            if (!isPlay) {
                rotateAnimation.pause();
            } else {
                if (!isRotateAnim) {
                    isRotateAnim = true;
                    if (imageView_pager != null) {
                        imageView_pager.setAnimation(null);
                    }
                    View view_pager = viewpager.findViewWithTag("myview" + Constant.playPos);
//                    newRotateAnim();
                    imageView_pager = view_pager.findViewById(R.id.image);
                    imageView_pager.startAnimation(rotateAnimation);
                } else {
                    rotateAnimation.resume();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeTextPager(ItemSong itemSong) {
        ratingBar.setRating(Integer.parseInt(itemSong.getAverageRating()));

        tv_music_artist.setText(itemSong.getArtist());
        tv_music_title.setText(itemSong.getTitle());
        tv_song_count.setText((viewpager.getCurrentItem() + 1) + "/" + Constant.arrayList_play.size());
    }



    public void changeText(final ItemSong itemSong, final String page) {

        Picasso.get().load(Constant.arrayList_play.get(Constant.playPos).getImageBig()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap2, Picasso.LoadedFrom from) {
                ((BaseActivity) Constant.context).change_bg_layout(bitmap2);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
        tv_min_title.setText(itemSong.getTitle());
        tv_min_artist.setText(itemSong.getArtist());

//        tv_max_title.setText(itemSong.getTitle());
//        tv_max_artist.setText(Constant.addedFrom);
        ratingBar.setRating(Integer.parseInt(itemSong.getAverageRating()));
        tv_music_title.setText(itemSong.getTitle());
        tv_music_artist.setText(itemSong.getArtist());
        String temp = "th";
        if(Constant.playPos == 0)
            temp = "st";
        if(Constant.playPos == 1)
            temp = "nd";
        if(Constant.playPos == 2)
            temp = "rd";
        temp = (Constant.playPos + 1) + temp;
        if(Constant.playPos == Constant.arrayList_play.size() - 1)
            temp = "last";
        tv_max_artist.setText("The " + temp + " track in the playlist");
        tv_total_time.setText(itemSong.getDuration());
        changeFav(itemSong.getIsFavourite());

        if (Constant.isOnline) {

            Picasso.get()
                    .load(itemSong.getImageSmall())
                    .placeholder(R.drawable.placeholder_song)
                    .into(iv_min_song);

//            Picasso.get()
//                    .load(itemSong.getImageSmall())
//                    .placeholder(R.drawable.placeholder_song)
//                    .into(iv_max_song);

//            if (ratingBar.getVisibility() == View.GONE) {
//                ratingBar.setVisibility(View.VISIBLE);
//                iv_max_fav.setVisibility(View.VISIBLE);
//
//                iv_music_rate.setVisibility(View.VISIBLE);
//                view_rate.setVisibility(View.VISIBLE);
//                iv_music_add2playlist.setVisibility(View.VISIBLE);
//            }

            if (Constant.isSongDownload) {
                iv_music_download.setVisibility(View.VISIBLE);
                view_download.setVisibility(View.VISIBLE);
            } else {
                iv_music_download.setVisibility(View.GONE);
                view_download.setVisibility(View.GONE);
            }
        } else {
            if (Constant.isDownloaded) {
                iv_music_add2playlist.setVisibility(View.GONE);
            } else {
                iv_music_add2playlist.setVisibility(View.VISIBLE);
            }
            Picasso.get()
                    .load(Uri.parse(itemSong.getImageSmall()))
                    .placeholder(R.drawable.placeholder_song)
                    .into(iv_min_song);

            Picasso.get()
                    .load(Uri.parse(itemSong.getImageSmall()))
                    .placeholder(R.drawable.placeholder_song)
                    .into(iv_max_song);

//            if (ratingBar.getVisibility() == View.VISIBLE) {
//                ratingBar.setVisibility(View.GONE);
//                iv_max_fav.setVisibility(View.GONE);
//
//                iv_music_rate.setVisibility(View.GONE);
//                view_rate.setVisibility(View.GONE);
//
//                iv_music_download.setVisibility(View.GONE);
//                view_download.setVisibility(View.GONE);
//            }
        }

        if (viewpager.getAdapter() == null || Constant.isNewAdded || !Constant.addedFrom.equals(adapter.getIsLoadedFrom())) {
            viewpager.setAdapter(adapter);
            Constant.isNewAdded = false;
        }
        try {
            viewpager.setCurrentItem(Constant.playPos);
        } catch (Exception e) {
            adapter.notifyDataSetChanged();
            viewpager.setCurrentItem(Constant.playPos);
        }
    }

    public void hide_Music_layout(){
        mLayout.setEnabled(false);
    }

    public void show_Music_layout(){
        mLayout.setEnabled(true);
    }

    public void changePlayPauseIcon(Boolean isPlay) {
        if (!isPlay) {
            iv_music_play.setImageDrawable(getResources().getDrawable(R.drawable.play));
            iv_min_play.setImageDrawable(getResources().getDrawable(R.mipmap.ic_play_grey));
        } else {
            iv_music_play.setImageDrawable(getResources().getDrawable(R.drawable.pause));
            iv_min_play.setImageDrawable(getResources().getDrawable(R.mipmap.ic_pause_grey));
        }
        seekUpdation();
    }

    public void isBuffering(Boolean isBuffer) {
        if (isBuffer) {
            rl_music_loading.setVisibility(View.VISIBLE);
            iv_music_play.setVisibility(View.INVISIBLE);
        } else {
            rl_music_loading.setVisibility(View.INVISIBLE);
            iv_music_play.setVisibility(View.VISIBLE);
            changePlayPauseIcon(!isBuffer);
//            seekUpdation();
        }
        iv_music_next.setEnabled(!isBuffer);
        iv_music_previous.setEnabled(!isBuffer);
        iv_min_next.setEnabled(!isBuffer);
        iv_min_previous.setEnabled(!isBuffer);
        iv_music_download.setEnabled(!isBuffer);
        iv_min_play.setEnabled(!isBuffer);
        seekBar_music.setEnabled(!isBuffer);
    }

    public void change_bg_layout(Bitmap bitmap){
        CheckColor checkColor = new CheckColor(bitmap);
        checkColor.execute();
    }

    private class CheckColor extends AsyncTask<Void, Void, Drawable>{
        private Bitmap bitmap;

        public CheckColor(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Drawable doInBackground(Void... voids) {

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            int pixel = 0;
            int[] value = {0, 0, 0, 0};
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    // get current index in 2D-matrix
                    int index = y * width + x;
                    pixel = pixels[index];
                    if (pixel != Color.BLACK) {
                        value[0] += Color.alpha(pixel);
                        value[1] += Color.red(pixel);
                        value[2] += Color.green(pixel);
                        value[3] += Color.blue(pixel);
                    } else {
                        value[0] += Color.alpha(Color.WHITE);
                        value[1] += Color.red(Color.WHITE);
                        value[2] += Color.green(Color.WHITE);
                        value[3] += Color.blue(Color.WHITE);
                    }
                }
            }
            for(int i = 0; i < 4; i++){
                value[i] = (int)value[i]/(width*height);
            }
            if(value[0] < 10)
                value[0] = 10;
            int[] colors = {getResources().getColor(R.color.bg_items) , Color.argb(value[0], value[1], value[2], value[3])};

//create a new gradient color
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.BOTTOM_TOP, colors);
            gd.setCornerRadius(0f);
            return ((Drawable) gd);
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            super.onPostExecute(drawable);
            layout_music_player.setBackground(drawable);
        }
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;
        private String loadedPage = "";

        private ImagePagerAdapter() {
            inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            return Constant.arrayList_play.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view.equals(object);
        }

        String getIsLoadedFrom() {
            return loadedPage;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            View imageLayout = inflater.inflate(R.layout.layout_viewpager, container, false);
            assert imageLayout != null;
            RoundedImageView imageView = imageLayout.findViewById(R.id.image);
            final ImageView imageView_play = imageLayout.findViewById(R.id.iv_vp_play);
            final ProgressBar spinner = imageLayout.findViewById(R.id.loading);
            final ImageView fav = imageLayout.findViewById(R.id.iv_max_fav);
            loadedPage = Constant.addedFrom;

            if (Constant.playPos == position) {
                imageView_play.setVisibility(View.GONE);
            }

            if (Constant.isOnline) {
                Picasso.get()
                        .load(Constant.arrayList_play.get(position).getImageBig())
                        .resize(300, 300)
                        .placeholder(R.drawable.cd)
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                spinner.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                spinner.setVisibility(View.GONE);
                            }
                        });
            } else {
                Picasso.get()
                        .load(Uri.parse(Constant.arrayList_play.get(position).getImageSmall()))
                        .placeholder(R.drawable.placeholder_song)
                        .into(imageView);
                spinner.setVisibility(View.GONE);
            }

            imageView_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constant.playPos = viewpager.getCurrentItem();
                    isRotateAnim = false;
                    if (!Constant.isOnline || methods.isNetworkAvailable()) {
                        Intent intent = new Intent(BaseActivity.this, PlayerService.class);
                        intent.setAction(PlayerService.ACTION_PLAY);
                        startService(intent);
                        imageView_play.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(BaseActivity.this, getResources().getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if (position == 0) {
                isRotateAnim = false;
                imageView_pager = imageView;
            }

            imageLayout.setTag("myview" + position);
            container.addView(imageLayout, 0);
            return imageLayout;

        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

//    private void openOptionPopUp(ItemSong itemSong) {
//        PopupMenu popup = new PopupMenu(BaseActivity.this, iv_max_option);
//        popup.getMenuInflater().inflate(R.menu.popup_base_option, popup.getMenu());
//
////        if (Constant.isLoginOn) {
////            popup.getMenu().findItem(R.id.popup_base_report).setVisible(true);
////        } else {
////            popup.getMenu().findItem(R.id.popup_base_report).setVisible(false);
////        }
//
//        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
////                    case R.id.popup_base_report:
////                        Intent intent = new Intent(BaseActivity.this, ReportActivity.class);
////                        startActivity(intent);
////                        break;
//                    case R.id.popup_base_desc:
//                        showBottomSheetDialog(itemSong);
//                        break;
//                }
//                return true;
//            }
//        });
//        popup.show();
//    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onSongChange(ItemSong itemSong) {
        changeText(itemSong, "home");
        Constant.context = BaseActivity.this;
        changeImageAnimation(PlayerService.getInstance().getIsPlayling());
//        GlobalBus.getBus().removeStickyEvent(itemSong);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onBufferChange(MessageEvent messageEvent) {

        if (messageEvent.message.equals("buffer")) {
            isBuffering(messageEvent.flag);
        } else {
            changePlayPauseIcon(messageEvent.flag);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onViewPagerChanged(ItemMyPlayList itemMyPlayList) {
        adapter.notifyDataSetChanged();
        tv_song_count.setText(Constant.playPos + 1 + "/" + Constant.arrayList_play.size());
        GlobalBus.getBus().removeStickyEvent(itemMyPlayList);
    }

    @Override
    public void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
    }

    @Override
    public void onStop() {
        GlobalBus.getBus().unregister(this);
        super.onStop();
    }

    public Boolean checkPerDownload() {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
//            if ((ContextCompat.checkSelfPermission(BaseActivity.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
//                requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
//                return false;
//            } else {
//                return true;
//            }
            return true;
        } else {
            if ((ContextCompat.checkSelfPermission(BaseActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    return false;
                }
            }
            return true;
        }
    }

    public Boolean checkPer() {
        if ((ContextCompat.checkSelfPermission(BaseActivity.this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{READ_PHONE_STATE}, 1);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean canUseExternalStorage = false;

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canUseExternalStorage = true;
                }

                if (!canUseExternalStorage) {
                    Toast.makeText(BaseActivity.this, getResources().getString(R.string.err_cannot_use_features), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        seekHandler.removeCallbacks(run);
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    protected void onResume() {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable,180000);
        super.onResume();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.e("aaa","load ad");
//            ll_adView_base.removeAllViews();
            methods.showSMARTBannerAd(ll_adView_base);
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable,180000);
        }
    };
}