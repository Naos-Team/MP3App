package com.zxfdwka.bestcountrymusic.radio.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.labo.kaji.relativepopupwindow.RelativePopupWindow;
import com.makeramen.roundedimageview.RoundedImageView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.AdConsentListener;
import com.zxfdwka.bestcountrymusic.radio.utils.Methods;
import com.zxfdwka.bestcountrymusic.radio.adapter.AdapterSuggest;
import com.zxfdwka.bestcountrymusic.radio.asyncTasks.LoadRadioViewed;
import com.zxfdwka.bestcountrymusic.radio.asyncTasks.LoadReport;
import com.zxfdwka.bestcountrymusic.radio.fragments.FragmentCity;
import com.zxfdwka.bestcountrymusic.radio.fragments.FragmentFavourite;
import com.zxfdwka.bestcountrymusic.radio.fragments.FragmentFeaturedRadio;
import com.zxfdwka.bestcountrymusic.radio.fragments.FragmentHome;
import com.zxfdwka.bestcountrymusic.radio.fragments.FragmentLanguage;
import com.zxfdwka.bestcountrymusic.radio.fragments.FragmentLanguageDetails;
import com.zxfdwka.bestcountrymusic.radio.fragments.FragmentMain;
import com.zxfdwka.bestcountrymusic.radio.fragments.FragmentOnDemandCat;
import com.zxfdwka.bestcountrymusic.radio.fragments.FragmentOnDemandDetails;
import com.zxfdwka.bestcountrymusic.radio.fragments.FragmentSearch;
import com.zxfdwka.bestcountrymusic.radio.interfaces.BackInterAdListener;
import com.zxfdwka.bestcountrymusic.radio.interfaces.InterAdListener;
import com.zxfdwka.bestcountrymusic.radio.interfaces.RadioViewListener;
import com.zxfdwka.bestcountrymusic.radio.interfaces.SuccessListener;
import com.zxfdwka.bestcountrymusic.radio.item.ItemOnDemandCat;
import com.zxfdwka.bestcountrymusic.radio.item.ItemRadio;
import com.zxfdwka.bestcountrymusic.mp3.utils.AdConsent;
import com.zxfdwka.bestcountrymusic.radio.utils.Constants;
import com.zxfdwka.bestcountrymusic.radio.utils.DBHelper;
import com.zxfdwka.bestcountrymusic.radio.utils.PlayService;
import com.zxfdwka.bestcountrymusic.radio.utils.RecyclerItemClickListener;
import com.zxfdwka.bestcountrymusic.radio.utils.SharedPref;
import com.zxfdwka.bestcountrymusic.radio.utils.StatusBarView;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class RadioBaseActivity extends AppCompatActivity{

    Toolbar toolbar;
    StatusBarView statusBarView;
    FragmentManager fm;
    SlidingUpPanelLayout slidingPanel;
    BottomSheetDialog dialog_desc;
    DBHelper dbHelper;
    AdConsent adConsent;
    ProgressDialog progressDialog;
    CircularProgressBar circularProgressBar, circularProgressBar_collapse;
    SeekBar seekbar_song;
    LinearLayout ll_ad, ll_collapse_color, ll_player_expand, ll_play_collapse;
    RelativeLayout rl_expand, rl_collapse, btn_sleep;
    ImageView imageView_player;
    RoundedImageView imageView_radio;
    ImageView imageView_play, imageView_fav, btn_volume, btn_previous_expand, btn_next_expand, ll_top_collapse;
//    FloatingActionButton fab_play_expand;
    TextView textView_name, textView_song, textView_freq_expand, textView_radio_expand, textView_song_expand, textView_song_duration, textView_total_duration, tv_views;
    Methods methods, methodsBack;
//    LoadAbout loadAbout;
    Boolean isExpand = false, isLoaded = false;
    SharedPref sharedPref;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;
    private Handler seekHandler = new Handler();
    View view_lollipop;
    Handler handler = new Handler();
    MenuItem menu_login, menu_profile;
    BottomSheetDialog dialog_report;
    RecyclerView rv_suggestion;
    RelativeLayout btn_play_music, btn_previous_scene2, btn_next_scene2, btn_play_scene2, rl_shadow_radio;
    ImageView iv_play_music, iv_pause_music, iv_thumb_scene2, iv_play_scene2;
    CardView btn_report;
    LinearLayout control_dragview, ll_suggest, ll_player_scene2, bg_musiccreen;
    TextView tv_songname_scene2;
    public BottomNavigationView bottomNavigationView;
    public int radio_nav = 540;
    public int demand_nav = 433;
    public int featured_nav = 248;
    public int favorite_nav = 387;
    public int current_nav = radio_nav;

    double current_offset = 0;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_base_radio);

        Constants.isAppOpen = true;

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        sharedPref = new SharedPref(this);

        sharedPref.setCheckSleepTime();

        view_lollipop = findViewById(R.id.view_lollipop);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view_lollipop.setVisibility(View.VISIBLE);
        }

        bg_musiccreen = findViewById(R.id.ll_bg_musicscreen);
        statusBarView = findViewById(R.id.statusBar);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        dbHelper = new DBHelper(this);
        methods = new Methods(RadioBaseActivity.this, interAdListener);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());
        fm = getSupportFragmentManager();

        methodsBack = new Methods(this, backInterAdListener);

        bottomNavigationView= findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                clickNav(item.getItemId());
                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                Log.e("Count", "entry count: " + fm.getBackStackEntryCount());
                return true;
            }
        });

        slidingPanel = findViewById(R.id.sliding_layout);
        slidingPanel.setDragView(rl_collapse);
//        navigationView = findViewById(R.id.nav_view);
//        Menu menu = navigationView.getMenu();
//        menu_login = menu.findItem(R.id.nav_login);
//        menu_profile = menu.findItem(R.id.nav_profile);

        changeLoginName();

        tv_views = findViewById(R.id.tv_views);
        btn_report = findViewById(R.id.btn_report);
        rv_suggestion = findViewById(R.id.rv_suggestion);
        circularProgressBar = findViewById(R.id.loader);
        circularProgressBar_collapse = findViewById(R.id.loader_collapse);
        seekbar_song = findViewById(R.id.seekbar_song);
        btn_sleep = findViewById(R.id.btn_sleep_expand);
        rl_shadow_radio = findViewById(R.id.rl_shadow_radio);
//        imageView_share = findViewById(R.id.imageView_share);
        imageView_fav = findViewById(R.id.imageView_fav_expand);
        imageView_player = findViewById(R.id.imageView_player);
        btn_previous_expand = findViewById(R.id.btn_previous_expand);
        btn_next_expand = findViewById(R.id.btn_next_expand);
        imageView_play = findViewById(R.id.imageView_player_play);
        //imageView_desc = findViewById(R.id.imageView_desc_expand);
        btn_volume = findViewById(R.id.btn_volume);
        textView_name = findViewById(R.id.textView_player_name);
        textView_song = findViewById(R.id.textView_song_name);
        ll_suggest = findViewById(R.id.ll_suggest);
        ll_player_scene2 = findViewById(R.id.ll_player_scene2);
        //imageView_report = findViewById(R.id.imageView_report_expand);

        iv_play_scene2 = findViewById(R.id.iv_play_scene2);
        iv_thumb_scene2 = findViewById(R.id.iv_thumb_scene2);
        btn_play_scene2 = findViewById(R.id.btn_play_scene2);
        btn_previous_scene2 = findViewById(R.id.btn_previous_scene2);
        btn_next_scene2 = findViewById(R.id.btn_next_scene2);
        tv_songname_scene2 = findViewById(R.id.tv_songname_scene2);

        iv_play_music = findViewById(R.id.iv_play_music);
        iv_pause_music = findViewById(R.id.iv_pause_music);
        btn_play_music = findViewById(R.id.btn_play_music);
//        fab_play_expand = findViewById(R.id.fab_play);
        imageView_radio = findViewById(R.id.imageView_radio);
        textView_radio_expand = findViewById(R.id.textView_radio_name_expand);
        textView_freq_expand = findViewById(R.id.textView_freq_expand);
        textView_song_expand = findViewById(R.id.textView_song_expand);
        textView_song_duration = findViewById(R.id.textView_song_duration);
        textView_total_duration = findViewById(R.id.textView_total_duration);

        ll_player_expand = findViewById(R.id.ll_player_expand);
        ll_play_collapse = findViewById(R.id.ll_play_collapse);
        ll_top_collapse = findViewById(R.id.ll_top_collapse);
//        rl_song_seekbar = findViewById(R.id.rl_song_seekbar);
        rl_collapse = findViewById(R.id.ll_collapse);
        ll_collapse_color = findViewById(R.id.ll_collapse_color);
        rl_expand = findViewById(R.id.ll_expand);
        rl_expand.setAlpha(0.0f);
        ll_ad = findViewById(R.id.ll_adView);

        setIfPlaying();

        adConsent = new AdConsent(this, new AdConsentListener() {
            @Override
            public void onConsentUpdate() {
//                methods.showBannerAd(ll_ad);

            }
        });

        if (methods.isConnectingToInternet()) {
//            loadAboutData();
            adConsent.checkForConsent();
        } else {

            Toast.makeText(this, getString(R.string.internet_not_connected), Toast.LENGTH_SHORT).show();
        }

        seekbar_song.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

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
                    Constants.exoPlayer_Radio.seekTo((int) Methods.getSeekFromPercentage(progress, Methods.calculateTime(Constants.arrayList_radio.get(Constants.pos).getDuration())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        rl_collapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        rl_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ll_top_collapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

//        slidingPanel.setDragView(rl_collapse);
        slidingPanel.setShadowHeight(0);
        slidingPanel.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset == 0.0f) {
                    isExpand = false;
                    rl_expand.setVisibility(View.GONE);
                } else if (slideOffset > 0.0f && slideOffset < 1.0f) {
                    if (isExpand) {

                        rl_collapse.setVisibility(View.VISIBLE);
                        rl_expand.setAlpha(slideOffset);
                        rl_collapse.setAlpha(1.0f - slideOffset);
                    } else {

                        rl_expand.setVisibility(View.VISIBLE);
                        rl_expand.setAlpha(0.0f + slideOffset);
                        rl_collapse.setAlpha(1.0f - slideOffset);
                    }
                } else {
                    isExpand = true;
                    rl_collapse.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
//                    viewpager.setCurrentItem(Constants.playPos);
                }
            }
        });



//        sliding_layout_main.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
//            @Override
//            public void onPanelSlide(View panel, float slideOffset) {
//
//
//            }
//
//            @Override
//            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
//                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
//                    Log.e("AAA", "expand");
//
//                    ObjectAnimator suggestDown = ObjectAnimator.ofFloat(ll_suggest, "translationY", 220f);
//                    suggestDown.setDuration(300);
//                    suggestDown.start();
//
//
//                    ll_player_expand.animate()
//                            .alpha(0f)
//                            .setDuration(100)
//                            .setListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    ll_player_expand.setVisibility(View.GONE);
//                                }
//                            });
//
//
//                    ll_player_scene2.animate()
//                            .alpha(1f)
//                            .setDuration(200)
//                            .setListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationStart(Animator animation) {
//                                    ll_player_scene2.setVisibility(View.VISIBLE);
//                                }
//                            });
//
//
//                }else{
//                    Log.e("AAA", "collapse");
//
//                    ObjectAnimator suggestUp = ObjectAnimator.ofFloat(ll_suggest, "translationY", 0f);
//                    suggestUp.setDuration(300);
//                    suggestUp.start();
//
//
//                    ll_player_expand.animate()
//                            .alpha(1f)
//                            .setDuration(200)
//                            .setListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationStart(Animator animation) {
//                                    ll_player_expand.setVisibility(View.VISIBLE);
//                                }
//                            });
//
//                    ll_player_scene2.animate()
//                            .alpha(0f)
//                            .setDuration(100)
//                            .setListener(new AnimatorListenerAdapter() {
//                                @Override
//                                public void onAnimationEnd(Animator animation) {
//                                    ll_player_scene2.setVisibility(View.GONE);
//                                }
//                            });
//                }
//            }
//        });


        imageView_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPlay(Constants.pos, Constants.playTypeRadio);
            }
        });

//        fab_play_expand.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(BaseActivity.this, R.color.colorPrimary)));
//
//        fab_play_expand.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                methods.showRateDialog();
//                clickPlay(Constants.pos, Constants.playTypeRadio);
//            }
//        });

        btn_play_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPlay(Constants.pos, Constants.playTypeRadio);
            }
        });

//        imageView_next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                methods.showRateDialog();
//                togglePlayPosition(true);
//            }
//        });

        btn_next_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePlayPosition(true);
            }
        });

//        btn_next_scene2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                methods.showRateDialog();
//                togglePlayPosition(true);
//            }
//        });

//        imageView_previous.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                methods.showRateDialog();
//                togglePlayPosition(false);
//            }
//        });

        btn_previous_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePlayPosition(false);
            }
        });

//        btn_previous_scene2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                methods.showRateDialog();
//                togglePlayPosition(false);
//            }
//        });

        imageView_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constants.arrayList_radio.size() > 0 && Constants.playTypeRadio) {
                    if (dbHelper.addORremoveFav(Constants.arrayList_radio.get(Constants.pos))) {
                        imageView_fav.setImageResource(R.drawable.ic_filled_heart);
                        Toast.makeText(RadioBaseActivity.this, getString(R.string.add_to_fav), Toast.LENGTH_SHORT).show();
                    } else {
                        imageView_fav.setImageResource(R.drawable.ic_unfill_heart);
                        Toast.makeText(RadioBaseActivity.this, getString(R.string.remove_from_fav), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

//        imageView_share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Constants.arrayList_radio.size() > 0) {
//                    Intent share = new Intent(Intent.ACTION_SEND);
//                    share.setType("text/plain");
//                    share.putExtra(Intent.EXTRA_TEXT, getString(R.string.listining_to) + " - " + Constants.arrayList_radio.get(Constants.pos).getRadioName() + "\n" + getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + getPackageName());
//                    startActivity(share);
//                }
//            }
//        });


//        imageView_desc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                methods.showRateDialog();
//                showBottomSheetDialog();
//            }
//        });

        btn_volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                methods.showRateDialog();
                changeVolume();
            }
        });
//
//        btn_sleep.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                methods.showRateDialog();
//                if (sharedPref.getIsSleepTimeOn()) {
//                    openTimeDialog();
//                } else {
//                    openTimeSelectDialog();
//                }
//            }
//        });

//        btn_report.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Constants.arrayList_radio.size() > 0) {
//                    showReportDialog();
//                }
//            }
//        });

        if (!Constants.pushRID.equals("0")) {
            progressDialog.show();
            loadRadio();
        }
        isLoaded = true;
//        navigationView.setCheckedItem(navigationView.getMenu().findItem(R.id.nav_home).getItemId());
        changeThemeColor();
        checkPer();

        FragmentMain f1 = new FragmentMain();
        loadFrag(f1, getResources().getString(R.string.radio), fm);
        getSupportActionBar().setTitle(getString(R.string.radio));
    }

    private void clickNav(int item) {
        switch (item) {
            case R.id.nav_home:

                if(current_nav == radio_nav){
                    return;
                }
                current_nav = radio_nav;

                FragmentMain f1 = new FragmentMain();
                loadFrag(f1, getResources().getString(R.string.radio), fm);
                break;
            case R.id.nav_ondemand:

                if(current_nav == demand_nav){
                    return;
                }
                current_nav = demand_nav;

                FragmentOnDemandCat f2 = new FragmentOnDemandCat();
                loadFrag(f2, getResources().getString(R.string.on_demand), fm);
                break;
            case R.id.nav_featured:

                if(current_nav == featured_nav){
                    return;
                }
                current_nav = featured_nav;

                FragmentFeaturedRadio ffeat = new FragmentFeaturedRadio();
                loadFrag(ffeat, getResources().getString(R.string.featured), fm);
                break;
            case R.id.nav_fav:

                if(current_nav == favorite_nav){
                    return;
                }
                current_nav = favorite_nav;

                FragmentFavourite ffav = new FragmentFavourite();
                loadFrag(ffav, getResources().getString(R.string.favourite), fm);
                break;

        }
    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {

        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.content_frame_activity, f1, name);
        ft.addToBackStack(name);
        ft.commit();
        getSupportActionBar().setTitle(name);
    }

    private void togglePlayPosition(Boolean isNext) {
        if (Constants.arrayList_radio.size() > 0) {
            if (PlayService.getInstance() == null) {
                PlayService.createInstance().initialize(RadioBaseActivity.this, Constants.arrayList_radio.get(Constants.pos));
            }
            Intent intent;
            intent = new Intent(RadioBaseActivity.this, PlayService.class);
            if (isNext) {
                intent.setAction(PlayService.ACTION_NEXT);
            } else {
                intent.setAction(PlayService.ACTION_PREVIOUS);
            }
            startService(intent);
        }
    }

    public void clickPlay(int position, Boolean isRadio) {
        if (Constants.pos < Constants.arrayList_radio.size()) {
            Constants.playTypeRadio = isRadio;
            Constants.pos = position;
            ItemRadio radio = Constants.arrayList_radio.get(Constants.pos);
            final Intent intent = new Intent(RadioBaseActivity.this, PlayService.class);

            if (PlayService.getInstance() != null) {
                ItemRadio playerCurrrentRadio = PlayService.getInstance().getPlayingRadioStation();
                if (playerCurrrentRadio != null) {
                    if (!radio.getRadioId().equals(PlayService.getInstance().getPlayingRadioStation().getRadioId())) {
                        PlayService.getInstance().initialize(RadioBaseActivity.this, radio);
                        intent.setAction(PlayService.ACTION_PLAY);
                    } else {
                        intent.setAction(PlayService.ACTION_TOGGLE);
                    }
                } else {
                    PlayService.getInstance().initialize(RadioBaseActivity.this, radio);
                    intent.setAction(PlayService.ACTION_PLAY);
                }
            } else {
                PlayService.createInstance().initialize(RadioBaseActivity.this, radio);
                intent.setAction(PlayService.ACTION_PLAY);
            }
            startService(intent);
            popUpSlidingPanel();
        }
    }

    private void startImageAnimation(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                imageView_radio.animate().rotationBy(360).withEndAction(this).setDuration(10000)
                        .setInterpolator(new LinearInterpolator()).start();
            }
        };

        imageView_radio.animate().rotationBy(360).withEndAction(runnable).setDuration(10000)
                .setInterpolator(new LinearInterpolator()).start();
    }

    private void stopImageAnimation(){
        imageView_radio.animate().cancel();
    }


    public void LoadDemandList(ArrayList<ItemOnDemandCat> arrayList_demand){

        ArrayList<ItemOnDemandCat> arrayList_random = new ArrayList<>();

        for (int i = 0; i < 6; i++)
        {
            // generating the index using Math.random()
            int index = (int)(Math.random() * arrayList_demand.size());

            if(arrayList_random.contains(arrayList_demand.get(index))){
                i--;
            }else{
                arrayList_random.add(arrayList_demand.get(index));
            }

        }

        AdapterSuggest adapterSuggest = new AdapterSuggest(arrayList_random);
        InterAdListener interAdListener = new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                int pos = getPosition(adapterSuggest.getID(position), arrayList_random);
                FragmentManager fm = getSupportFragmentManager();
                FragmentOnDemandDetails f1 = new FragmentOnDemandDetails(true);
                FragmentTransaction ft = fm.beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", arrayList_random.get(pos));
                f1.setArguments(bundle);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                //ft.hide(fm.findFragmentByTag(getString(R.string.on_demand)));
                ft.add(R.id.content_frame_activity, f1, arrayList_random.get(pos).getName());
                ft.addToBackStack(arrayList_random.get(pos).getName());
                ft.commit();

                slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        };

//        Methods suggest_method = new Methods(this, interAdListener);

//        rv_suggestion.setLayoutManager(new GridLayoutManager(this, 3));
//        rv_suggestion.setAdapter(adapterSuggest);

//        rv_suggestion.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//                suggest_method.showInter(position, "");
//            }
//        }));


    }

    private int getPosition(String id, ArrayList<ItemOnDemandCat> arrayList_demand) {
        int count = 0;
        for (int i = 0; i < arrayList_demand.size(); i++) {
            if (id.equals(arrayList_demand.get(i).getId())) {
                count = i;
                break;
            }
        }
        return count;
    }


    private void popUpSlidingPanel(){
        slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    public void changePlayPause(Boolean flag) {
        Constants.isPlaying = flag;

        if (flag) {
            ItemRadio itemRadio = PlayService.getInstance().getPlayingRadioStation();
            if (itemRadio != null) {
                changeText(itemRadio);

                iv_play_music.setVisibility(View.GONE);
                iv_pause_music.setVisibility(View.VISIBLE);


                imageView_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_btn_pause));
////                fab_play_expand.setImageDrawable(ContextCompat.getDrawable(BaseActivity.this, R.mipmap.fab_pause));
//
//                iv_play_music.setImageDrawable(getResources().getDrawable(R.drawable.pause_2));
//                iv_play_scene2.setImageDrawable(getResources().getDrawable(R.drawable.pause_2));

                startImageAnimation();
            }
        } else {
            if (Constants.arrayList_radio.size() > 0) {
                changeText(Constants.arrayList_radio.get(Constants.pos));
            }

            iv_pause_music.setVisibility(View.GONE);
            iv_play_music.setVisibility(View.VISIBLE);

            imageView_play.setImageDrawable(getResources().getDrawable(R.drawable.ic_btn_play));
////            fab_play_expand.setImageDrawable(ContextCompat.getDrawable(BaseActivity.this, R.mipmap.fab_play));
//
//            iv_play_music.setImageDrawable(getResources().getDrawable(R.drawable.play_2));
//            iv_play_scene2.setImageDrawable(getResources().getDrawable(R.drawable.play_2));

            stopImageAnimation();
        }
    }

    public void changeText(ItemRadio itemRadio) {
        if (Constants.playTypeRadio) {
            textView_freq_expand.setText(itemRadio.getRadioFreq() + " HZ");
            changeSongName(Constants.song_name);
            changeFav(itemRadio);

            textView_freq_expand.setVisibility(View.VISIBLE);
            textView_song_expand.setVisibility(View.VISIBLE);
            seekbar_song.setVisibility(View.GONE);

            textView_song_duration.setVisibility(View.GONE);
            textView_total_duration.setVisibility(View.GONE);

            if (FragmentHome.adapterRadioList != null && FragmentHome.adapterRadioList_mostview != null) {
                FragmentHome.adapterRadioList.notifyDataSetChanged();
                FragmentHome.adapterRadioList_mostview.notifyDataSetChanged();
                FragmentHome.adapterRadioList_featured.notifyDataSetChanged();
            }
        } else {
            textView_total_duration.setText(itemRadio.getDuration());
            textView_song.setText(getString(R.string.on_demand));
            textView_song_expand.setText(itemRadio.getRadioName());

            textView_song_duration.setVisibility(View.VISIBLE);
            textView_total_duration.setVisibility(View.VISIBLE);

            textView_freq_expand.setText(getString(R.string.on_demand));;
            textView_song_expand.setVisibility(View.GONE);
            seekbar_song.setVisibility(View.VISIBLE);
        }
        textView_name.setText(itemRadio.getRadioName());
        textView_radio_expand.setText(itemRadio.getRadioName());

        String url = methods.getImageThumbSize(itemRadio.getRadioImageurl(),"");
        String url1 = "";

        if (url.contains(Constants.BASE_SERVER_URL)){
            url1 = url;
        }
        else{
            url1 = Constants.BASE_SERVER_URL + url;
        }

        Picasso.get().load(url1)
                .placeholder(R.drawable.placeholder)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        Palette.generateAsync(bitmap, new Palette.PaletteAsyncListener() {
                            public void onGenerated(Palette palette) {
                                //textView_radio_expand.setTextColor(palette.getVibrantColor(0xF000000));

                                GradientDrawable gd = new GradientDrawable(
                                        GradientDrawable.Orientation.TOP_BOTTOM,
                                        new int[] {palette.getVibrantColor(0xFF616261), 0xFF131313});
                                gd.setCornerRadius(0f);
                                bg_musiccreen.setBackground(gd);

                            }
                        });

                        imageView_radio.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
        Picasso.get().load(url1)
                .placeholder(R.drawable.placeholder)
                .into(imageView_player);
    }

    public void changeFav(ItemRadio itemRadio) {
        if (dbHelper.checkFav(itemRadio)) {
            imageView_fav.setImageResource(R.drawable.ic_filled_heart);
        } else {
            imageView_fav.setImageResource(R.drawable.ic_unfill_heart);
        }
    }

    public void changeSongName(String songName) {
        Constants.song_name = songName;
        textView_song.setText(songName);
        textView_song_expand.setText(songName);
    }

    public void setIfPlaying() {
        if (PlayService.getInstance() != null) {
            PlayService.initNewContext(RadioBaseActivity.this);
            changePlayPause(PlayService.getInstance().isPlaying());
            seekUpdation();
        } else {
            changePlayPause(false);
        }
    }

//    public void loadAboutData() {
//        loadAbout = new LoadAbout(new AboutListener() {
//            @Override
//            public void onStart() {
//            }
//
//            @Override
//            public void onEnd(String success, String verifyStatus, String message) {
//                if (!verifyStatus.equals("-1")) {
//                    //adConsent.checkForConsent();
//                    dbHelper.addtoAbout();
//                } else {
//                    methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
//                }
//            }
//        }, methods.getAPIRequest(Constants.METHOD_ABOUT, 0, "", "", "", "", "", "", "", "", "", "", "", "", null));
//        loadAbout.execute();
//    }

//    public void openQuitDialog() {
//        TextView txt_Exit_dialog, txtTitle_Exit_dialog;
//        Button btnCancel_Exit_dialog, btnexit_Exit_dialog;
////        AlertDialog.Builder alert;
////        alert = new AlertDialog.Builder(BaseActivity.this, R.style.Widget_MaterialComponents_MaterialCalendar_Day);
////        alert.setTitle(R.string.app_name);
////        alert.setIcon(R.mipmap.app_icon);
////        alert.setMessage(getString(R.string.sure_quit));
////
////        alert.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
////            public void onClick(DialogInterface dialog, int whichButton) {
////                finish();
////            }
////        });
////
////        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
////            public void onClick(DialogInterface dialog, int which) {
////            }
////        });
////       alert.show();
//        Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.layout_dialog_exit_app);
//        Window window = dialog.getWindow();
//        if(window == null){
//            return;
//        }
//        txt_Exit_dialog = (TextView) dialog.findViewById(R.id.txt_Exit_dialog);
//        txtTitle_Exit_dialog = (TextView) dialog.findViewById(R.id.txtTitle_Exit_dialog);
//        btnCancel_Exit_dialog = (Button) dialog.findViewById(R.id.btnCancel_Exit_dialog);
//        btnexit_Exit_dialog = (Button) dialog.findViewById(R.id.btnexit_Exit_dialog);
//
//        txtTitle_Exit_dialog.setText("Confirm");
//        txt_Exit_dialog.setText(getString(R.string.sure_quit));
//        btnCancel_Exit_dialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//
//
//        btnexit_Exit_dialog.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.setCancelable(true);
//        dialog.show();
//    }

    private InterAdListener interAdListener = new InterAdListener() {
        @Override
        public void onClick(int position, String type) {
            if (type.equals("nav")) {
                clickNav(position);
            }
        }
    };

    private BackInterAdListener backInterAdListener = new BackInterAdListener() {
        @Override
        public void onClick() {
            RadioBaseActivity.super.onBackPressed();
        }
    };

    public void checkPer() {
        if ((ContextCompat.checkSelfPermission(RadioBaseActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(RadioBaseActivity.this, "android.permission.READ_PHONE_STATE") != PackageManager.PERMISSION_GRANTED)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_PHONE_STATE"}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (!Constants.pushRID.equals("0")) {
            progressDialog.show();
            loadRadio();
        }
        super.onNewIntent(intent);
    }

    private void loadRadio() {
        LoadRadioViewed loadRadioViewed = new LoadRadioViewed(new RadioViewListener() {
            @Override
            public void onEnd(String success) {
                Constants.pushRID = "0";
                progressDialog.dismiss();
                if (success.equals("1")) {
                    Constants.arrayList_radio.clear();
                    Constants.arrayList_radio.add(Constants.itemRadio);
                    clickPlay(0, true);
                }
            }
        }, methods.getAPIRequest(Constants.METHOD_SINGLE_RADIO, 0, "", Constants.pushRID, "", "", "", "", "", "", "", "", "", "", null));
        loadRadioViewed.execute();
    }

//    @SuppressLint("RestrictedApi")
//    public void showReportDialog() {
//        View view = getLayoutInflater().inflate(R.layout.layout_report, null);
//
//        dialog_report = new BottomSheetDialog(this);
//        dialog_report.setContentView(view);
//        dialog_report.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
//        dialog_report.show();
//
//        final AppCompatEditText editText = dialog_report.findViewById(R.id.editText_report);
//        Button button_report = dialog_report.findViewById(R.id.button_report);
//        button_report.setBackground(methods.getRoundDrawable(sharedPref.getFirstColor()));
//
//        ViewCompat.setBackgroundTintList(editText, ColorStateList.valueOf(sharedPref.getFirstColor()));
//
//        button_report.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (Constants.isLogged) {
//                    if (!TextUtils.isEmpty(editText.getText())) {
//                        String type = "";
//                        if (Constants.playTypeRadio) {
//                            type = "alexnguyen";
//                        } else {
//                            type = "song";
//                        }
////                        Toast.makeText(BaseActivity.this, "Reporting is disabled in demo app", Toast.LENGTH_SHORT).show();
//                        loadReport(type, editText.getText().toString());
//                    } else {
//                        methods.showToast(getString(R.string.write_report_details));
//                    }
//                } else {
//                    Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
//                    intent.putExtra("from", "app");
//                    startActivity(intent);
//                }
//            }
//        });
//    }

//    private void loadReport(String radioType, String radioDesc) {
//        if (methods.isConnectingToInternet()) {
//            LoadReport loadReport = new LoadReport(new SuccessListener() {
//                @Override
//                public void onStart() {
//                    progressDialog.show();
//                }
//
//                @Override
//                public void onEnd(String success, String registerSuccess, String message) {
//                    progressDialog.dismiss();
//                    if (success.equals("1")) {
//                        if (registerSuccess.equals("1")) {
//                            dialog_report.dismiss();
//                            methods.showToast(message);
//                        }
//                    } else {
//                        methods.showToast(getString(R.string.error_server));
//                    }
//                }
//            }, methods.getAPIRequest(Constants.METHOD_REPORT, 0, "", Constants.arrayList_radio.get(Constants.pos).getRadioId(), "", "", "", radioType, Constants.itemUser.getEmail(), "", "", "", Constants.itemUser.getId(), radioDesc, null));
//            loadReport.execute();
//        } else {
//            methods.showToast(getString(R.string.internet_not_connected));
//        }
//    }

    public void changeThemeColor() {
        Constants.isThemeChanged = false;

        int[][] state = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{android.R.attr.state_enabled},
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_focused},
                new int[]{android.R.attr.state_pressed}

        };

        int[] color = new int[]{
                sharedPref.getFirstColor(),
                Color.WHITE,
                Color.WHITE,
                Color.WHITE,
                Color.WHITE
        };

        ColorStateList ColorStateList1 = new ColorStateList(state, color);
        //navigationView.setItemTextColor(ColorStateList1);
        //navigationView.setItemIconTintList(ColorStateList1);
    }

    @Override
    public void onBackPressed() {

        if(slidingPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
            slidingPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return;
        }

        if(Constants.fragmentStatus == Constants.AT_HOME){
            finish();
            //super.onBackPressed();
            //Toast.makeText(this, "quit", Toast.LENGTH_SHORT).show();
        }else if(Constants.fragmentStatus == Constants.NEAR_HOME){
            loadFrag(new FragmentMain(), getResources().getString(R.string.radio), fm);
            getSupportActionBar().setTitle(getString(R.string.radio));
            bottomNavigationView.getMenu().getItem(0).setChecked(true);
        }else if(Constants.fragmentStatus == Constants.OTHER_HOME){

            String current_fragment = fm.getFragments().get(fm.getBackStackEntryCount() - 2).getTag();

            if(current_fragment.equals(getString(R.string.on_demand)) ||
                    current_fragment.equals(getString(R.string.favourite)) ||
                    current_fragment.equals(getString(R.string.featured))){
                Constants.fragmentStatus = Constants.NEAR_HOME;
            }

            getSupportActionBar().setTitle(fm.getFragments().get(fm.getBackStackEntryCount() - 2).getTag());

            super.onBackPressed();


        }

//        try{
//            String current_fragment = fm.getFragments().get(fm.getBackStackEntryCount() - 2).getTag();
//
//            if(current_fragment.equals(getString(R.string.radio))){
//                Toast.makeText(this, "quit", Toast.LENGTH_SHORT).show();
//            }
//        }catch (Exception e){
//            if(current_fragment.equals(getString(R.string.on_demand)) ||
//                    current_fragment.equals(getString(R.string.favourite)) ||
//                    current_fragment.equals(getString(R.string.featured))){
//                loadFrag(new FragmentHome(), getResources().getString(R.string.radio), fm);
//            }else{
//                if(fm.getFragments().get(fm.getBackStackEntryCount() - 2).getTag())
//            }
//        }



//        if (fm.getBackStackEntryCount() > 2) {
//            getSupportActionBar().setTitle(fm.getFragments().get(fm.getBackStackEntryCount() - 2).getTag());
////            methodsBack.showInter(999, "BackPress");
//            super.onBackPressed();
//        } else {
////            String tag = fm.getFragments().get(fm.getBackStackEntryCount() - 2).getTag();
////            if(!tag.equals(getString(R.string.radio))){
////                loadFrag(new FragmentHome(), getResources().getString(R.string.radio), fm);
////            }
////            openQuitDialog();
//
//            Log.e("Count", "last count: " + fm.getBackStackEntryCount());
//        }
    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            try {
                seekUpdation();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void seekUpdation() {
        try {
            if (!Constants.playTypeRadio && Constants.isAppOpen) {
                seekbar_song.setProgress(Methods.getProgressPercentage(Constants.exoPlayer_Radio.getCurrentPosition(), Methods.calculateTime(Constants.arrayList_radio.get(Constants.pos).getDuration())));
                textView_song_duration.setText(Methods.milliSecondsToTimer(Constants.exoPlayer_Radio.getCurrentPosition()));
                Log.e("duration", "" + Methods.milliSecondsToTimer(Constants.exoPlayer_Radio.getCurrentPosition()));
                seekbar_song.setSecondaryProgress(Constants.exoPlayer_Radio.getBufferedPercentage());
                if (PlayService.getInstance().isPlaying()) {
                    seekHandler.removeCallbacks(run);
                    seekHandler.postDelayed(run, 1000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setBuffer(Boolean flag) {
        if (flag) {

            circularProgressBar.setVisibility(View.VISIBLE);
            rl_shadow_radio.setVisibility(View.VISIBLE);
            circularProgressBar_collapse.setVisibility(View.VISIBLE);
            ll_play_collapse.setVisibility(View.INVISIBLE);
        } else {
            rl_shadow_radio.setVisibility(View.GONE);
            circularProgressBar.setVisibility(View.GONE);
            circularProgressBar_collapse.setVisibility(View.GONE);
            ll_play_collapse.setVisibility(View.VISIBLE);
        }
    }

    public void changeVolume() {
        final RelativePopupWindow popupWindow = new RelativePopupWindow(this);

        // inflate your layout or dynamically add view
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.layout_volume, null);
        ImageView imageView1 = view.findViewById(R.id.iv1);
        ImageView imageView2 = view.findViewById(R.id.iv2);
        imageView1.setColorFilter(Color.BLACK);
        imageView2.setColorFilter(Color.BLACK);

        VerticalSeekBar seekBar = view.findViewById(R.id.seekbar_volume);
        seekBar.getThumb().setColorFilter(sharedPref.getFirstColor(), PorterDuff.Mode.SRC_IN);
        seekBar.getProgressDrawable().setColorFilter(sharedPref.getSecondColor(), PorterDuff.Mode.SRC_IN);

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
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setContentView(view);
        popupWindow.showOnAnchor(btn_volume, RelativePopupWindow.VerticalPosition.ABOVE, RelativePopupWindow.HorizontalPosition.CENTER);
    }

//    private void openTimeSelectDialog() {
//        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
//        alt_bld.setTitle(getString(R.string.sleep_time));
//
//        LayoutInflater inflater = this.getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.layout_dailog_selecttime, null);
//        alt_bld.setView(dialogView);
//
//        final TextView tv_min = dialogView.findViewById(R.id.tv_min);
//        tv_min.setText("1 " + getString(R.string.min));
//        FrameLayout frameLayout = dialogView.findViewById(R.id.fl);
//
//        final IndicatorSeekBar seekbar = IndicatorSeekBar
//                .with(BaseActivity.this)
//                .min(1)
//                .max(120)
//                .progress(1)
//                .thumbColor(sharedPref.getSecondColor())
//                .indicatorColor(sharedPref.getFirstColor())
//                .trackProgressColor(sharedPref.getFirstColor())
//                .build();
//
//        seekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
//            @Override
//            public void onSeeking(SeekParams seekParams) {
//                tv_min.setText(seekParams.progress + " " + getString(R.string.min));
//            }
//
//            @Override
//            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
//
//            }
//        });
//
//        frameLayout.addView(seekbar);
//
//        alt_bld.setPositiveButton(getString(R.string.set), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                String hours = String.valueOf(seekbar.getProgress() / 60);
//                String minute = String.valueOf(seekbar.getProgress() % 60);
//
//                if (hours.length() == 1) {
//                    hours = "0" + hours;
//                }
//
//                if (minute.length() == 1) {
//                    minute = "0" + minute;
//                }
//
//                String totalTime = hours + ":" + minute;
//                long total_timer = methods.convertToMili(totalTime) + System.currentTimeMillis();
//
//                Random random = new Random();
//                int id = random.nextInt(100);
//
//                sharedPref.setSleepTime(true, total_timer, id);
//
//                Intent intent = new Intent(BaseActivity.this, SleepTimeReceiver.class);
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, intent, PendingIntent.FLAG_ONE_SHOT);
//                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, total_timer, pendingIntent);
//                } else {
//                    alarmManager.set(AlarmManager.RTC_WAKEUP, total_timer, pendingIntent);
//                }
//            }
//        });
//        alt_bld.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        AlertDialog alert = alt_bld.create();
//        alert.show();
//    }



//    private void openTimeDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this, R.style.AlertDialogTheme);
//        builder.setTitle(getString(R.string.sleep_time));
//        LayoutInflater inflater = this.getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.layout_dailog_time, null);
//        builder.setView(dialogView);
//
//        TextView textView = dialogView.findViewById(R.id.textView_time);
//
//        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//
//        builder.setPositiveButton(getString(R.string.stop), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent i = new Intent(BaseActivity.this, SleepTimeReceiver.class);
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(BaseActivity.this, sharedPref.getSleepID(), i, PendingIntent.FLAG_ONE_SHOT);
//                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                pendingIntent.cancel();
//                alarmManager.cancel(pendingIntent);
//                sharedPref.setSleepTime(false, 0, 0);
//            }
//        });
//
//        updateTimer(textView, sharedPref.getSleepTime());
//
//        builder.show();
//    }

    private void updateTimer(final TextView textView, long time) {
        long timeleft = time - System.currentTimeMillis();
        if (timeleft > 0) {
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(timeleft),
                    TimeUnit.MILLISECONDS.toMinutes(timeleft) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(timeleft) % TimeUnit.MINUTES.toSeconds(1));


            textView.setText(hms);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (sharedPref.getIsSleepTimeOn()) {
                        updateTimer(textView, sharedPref.getSleepTime());
                    }
                }
            }, 1000);
        }
    }

    private void changeLoginName() {
        if (menu_login != null) {
            if (Constants.isLogged) {
                menu_profile.setVisible(true);
                menu_login.setTitle(getResources().getString(R.string.logout));
                menu_login.setIcon(getResources().getDrawable(R.mipmap.logout));
            } else {
                menu_profile.setVisible(false);
                menu_login.setTitle(getResources().getString(R.string.login));
                menu_login.setIcon(getResources().getDrawable(R.mipmap.login));
            }
        }
    }

    @Override
    protected void onResume() {
        changeLoginName();

        Constants.isQuitDialog = true;
        setIfPlaying();

        if (isLoaded && Constants.isThemeChanged) {
            changeThemeColor();
            if (FragmentCity.adapterCity != null) {
                FragmentCity.adapterCity.notifyDataSetChanged();
                ViewCompat.setBackgroundTintList(FragmentCity.button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));
            }
            if (FragmentLanguage.adapterLanguage != null) {
                FragmentLanguage.adapterLanguage.notifyDataSetChanged();
                ViewCompat.setBackgroundTintList(FragmentLanguage.button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));
            }
            if (FragmentMain.appBarLayout != null) {
                FragmentMain.appBarLayout.setBackground(methods.getGradientDrawableToolbar());
            }

            if (FragmentOnDemandCat.button_try != null) {
                ViewCompat.setBackgroundTintList(FragmentOnDemandCat.button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));
            }

            if (FragmentOnDemandDetails.button_try != null) {
                ViewCompat.setBackgroundTintList(FragmentOnDemandDetails.button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));
            }

            if (FragmentSearch.button_try != null) {
                ViewCompat.setBackgroundTintList(FragmentSearch.button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));
            }

            if (FragmentFeaturedRadio.button_try != null) {
                ViewCompat.setBackgroundTintList(FragmentFeaturedRadio.button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));
            }

            if (FragmentLanguageDetails.button_try != null) {
                ViewCompat.setBackgroundTintList(FragmentLanguageDetails.button_try, ColorStateList.valueOf(sharedPref.getFirstColor()));
            }

//            if (FragmentSuggestion.button_submit != null) {
//                ViewCompat.setBackgroundTintList(FragmentSuggestion.button_submit, ColorStateList.valueOf(sharedPref.getFirstColor()));
//            }
        }
//        navigationView.setCheckedItem(navigationView.getMenu().findItem(R.id.nav_home).getItemId());
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Constants.isAppOpen = false;
        try {
            if (Constants.exoPlayer_Radio != null && !Constants.exoPlayer_Radio.getPlayWhenReady()) {
                Intent intent = new Intent(getApplicationContext(), PlayService.class);
                intent.setAction(PlayService.ACTION_STOP);
                startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}