package com.naosteam.countrymusic.ringtone.Activity;

import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.mp3.interfaces.InterScreenListener;
import com.naosteam.countrymusic.ringtone.Adapter.AdapterDownload;
import com.naosteam.countrymusic.ringtone.Constant.Constant;
import com.naosteam.countrymusic.ringtone.Listener.ClickListenerRecorder;
import com.naosteam.countrymusic.ringtone.Listener.InterAdListener;
import com.naosteam.countrymusic.ringtone.Method.Methods;
import com.naosteam.countrymusic.ringtone.SharedPref.Setting;
import com.naosteam.countrymusic.ringtone.item.Itemdownload;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class DownloadActivity extends AppCompatActivity{
    Methods methods;
    Toolbar toolbar;
    RecyclerView rv;
    AdapterDownload adapter;
    ArrayList<Itemdownload> arrayList;
    ProgressBar progressbar;
    com.naosteam.countrymusic.mp3.utils.Methods methods1;
    LinearLayout ll_ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (Setting.Dark_Mode) {
//            setTheme(R.style.AppTheme);
//        } else {
//            setTheme(R.style.AppTheme);
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_ringtone);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.download));

        methods1 = new com.naosteam.countrymusic.mp3.utils.Methods(this);
        ll_ad = findViewById(R.id.ll_ad);
        methods1.showSMARTBannerAd(ll_ad);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods1.showInterScreenAd(new InterScreenListener() {
                    @Override
                    public void onClick() {
                        finish();
                    }
                });
            }
        });

        arrayList = new ArrayList<>();
        progressbar = findViewById(R.id.progressbar);
        rv = findViewById(R.id.rv_song_by_cat);
        LinearLayoutManager llm = new LinearLayoutManager(DownloadActivity.this);
        rv.setLayoutManager(llm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setHasFixedSize(true);

        new LoadDownloadSongs().execute();

        methods = new Methods(this, new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                adapter.notifyDataSetChanged();
            }
        });
    }



    class LoadDownloadSongs extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            arrayList.clear();
            progressbar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            loadDownloaded();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (DownloadActivity.this != null) {
                setAdapter();
            }else {
                progressbar.setVisibility(View.GONE);
            }
        }
    }

    private void loadDownloaded() {
        File root = new File(Environment.getExternalStorageDirectory() + File.separator + Constant.appFolderName);
        File[] songs = root.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".mp3");
            }
        });

        if (songs != null) {
            for (int i = 0; i < songs.length; i++) {

                MediaMetadataRetriever md = new MediaMetadataRetriever();
                md.setDataSource(songs[i].getAbsolutePath());
                String title = songs[i].getName();
                String duration = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                duration = milliSecondsToTimerDownload(Long.parseLong(duration));
                String url = songs[i].getAbsolutePath();

                arrayList.add(new Itemdownload(String.valueOf(i), url, title, duration));

                Setting.arrayList_play_do.clear();
                Setting.arrayList_play_do.addAll(arrayList);
                Setting.playPos_do= 0;

            }
        }

    }

    public static String milliSecondsToTimerDownload(long milliseconds) {
        String finalTimerString = "";
        String hourString = "";
        String secondsString = "";
        String minutesString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there

        if (hours != 0) {
            hourString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        // Prepending 0 to minutes if it is one digit
        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = "" + minutes;
        }

        finalTimerString = hourString + minutesString + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    private void setAdapter() {
        adapter = new AdapterDownload(DownloadActivity.this, arrayList, new ClickListenerRecorder() {
            @Override
            public void onClick(int position) {
                methods.showInter(position, "");
                Setting.arrayList_play_do.clear();
                Setting.arrayList_play_do.addAll(arrayList);
                Setting.playPos_do = position;
            }

        }, "");
        rv.setAdapter(adapter);
        progressbar.setVisibility(View.GONE);
    }


    @Override
    public void onDestroy() {
        try {
            Setting.exoPlayer_do.stop();
            Setting.exoPlayer_do.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                try {
                    Setting.exoPlayer_do.stop();
                    Setting.exoPlayer_do.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        try {
            Setting.exoPlayer_do.stop();
            Setting.exoPlayer_do.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onBackPressed();
    }
}