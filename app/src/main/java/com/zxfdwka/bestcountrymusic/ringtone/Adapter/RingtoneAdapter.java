package com.zxfdwka.bestcountrymusic.ringtone.Adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;
import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.ringtone.Constant.Constant;
import com.zxfdwka.bestcountrymusic.ringtone.DBHelper.DBHelper;
import com.zxfdwka.bestcountrymusic.ringtone.Listener.ClickListenerRecorder;
import com.zxfdwka.bestcountrymusic.ringtone.Method.Methods;
import com.zxfdwka.bestcountrymusic.ringtone.SharedPref.Setting;
import com.zxfdwka.bestcountrymusic.ringtone.item.ItemRingtone;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;


public class RingtoneAdapter extends RecyclerView.Adapter {
    private DBHelper dbHelper;
    private ArrayList<ItemRingtone> arrayList;
    private ArrayList<ItemRingtone> filteredArrayList;
    private NameFilter filter;
    private Context context;
    private Methods methods;
    private ClickListenerRecorder recyclerClickListener;

    DefaultBandwidthMeter bandwidthMeter;
    DataSource.Factory dataSourceFactory;
    ExtractorsFactory extractorsFactory;


    public String ringtonename;

    File pathfile = new File(Environment.getExternalStorageDirectory(), Constant.appFolderName);
    File outputfile = new File(pathfile, "ringtone.mp3");


    ProgressDialog dialog;



    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private int columnWidth = 0;

    private class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title, tv_artist, views,total_downlod;
        public ImageView play, pause, ringtone;
        RelativeLayout linearLayout;
        private ProgressBar progressbar_new;
        private ImageView imageView_fav, bg_play_pause;


        private MyViewHolder(View view) {
            super(view);
            tv_title = itemView.findViewById(R.id.tv_songlist_name);
            tv_artist = itemView.findViewById(R.id.tv_songlist_cat);
            bg_play_pause = itemView.findViewById(R.id.bg_play_pause);
            total_downlod = itemView.findViewById(R.id.total_downlod);

            linearLayout = itemView.findViewById(R.id.thiva);

            progressbar_new = itemView.findViewById(R.id.progressbar_new);

            play = itemView.findViewById(R.id.play);
            pause = itemView.findViewById(R.id.pause);

            views = itemView.findViewById(R.id.tv_songlist_vie);
            ringtone = itemView.findViewById(R.id.ringtone);
            imageView_fav = itemView.findViewById(R.id.imageView_fav_home);
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private static ProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    public RingtoneAdapter(Context context, ArrayList<ItemRingtone> arrayList, ClickListenerRecorder recyclerClickListener) {
        this.arrayList = arrayList;
        this.context = context;
        this.filteredArrayList = arrayList;

        this.recyclerClickListener = recyclerClickListener;
        dbHelper = new DBHelper(context);
        methods = new Methods(context);


        dialog = new ProgressDialog(context);
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);

        bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "nemosofts_rc"), bandwidthMeter);
        extractorsFactory = new DefaultExtractorsFactory();

        Setting.exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ringtone, parent, false);
            return new MyViewHolder(itemView);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_progressbar_ringtone, parent, false);
            return new ProgressViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (holder instanceof MyViewHolder) {
            Boolean isFav = checkFav(position);
            final ItemRingtone song = arrayList.get(position);


            if (isFav) {
                ((MyViewHolder) holder).imageView_fav.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark));
            } else {
                ((MyViewHolder) holder).imageView_fav.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark2));
            }

            ((MyViewHolder) holder).imageView_fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (dbHelper.addORremoveFav(arrayList.get(holder.getAdapterPosition()))) {
                        ((MyViewHolder) holder).imageView_fav.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark));
                        Toast.makeText(context, context.getString(R.string.add_to_fav_ringtone), Toast.LENGTH_SHORT).show();
                    } else {
                        ((MyViewHolder) holder).imageView_fav.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bookmark2));
                        Toast.makeText(context, context.getString(R.string.remove_from_fav_ringtone), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ((MyViewHolder) holder).tv_title.setText(song.getTitle());
            ((MyViewHolder) holder).tv_artist.setText(song.getCategory_name());
            ((MyViewHolder) holder).views.setText(format(Double.parseDouble((String)song.getTotal_views()))+" Views");

            ((MyViewHolder) holder).total_downlod.setText(format(Double.parseDouble((String)song.getTotal_download())));



            final String finalUrl =  song.getUrl_fm();
            final String name =  song.getTitle();

            ((MyViewHolder) holder).ringtone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ringtonename = name;
                    outputfile = new File(pathfile, name + ".mp3");
                    if (!outputfile.exists()) {
                        new DownloadFileAsync().execute(finalUrl);
                        methods.download(arrayList.get(position));
                    } else {
                        showBottomSheetDialog();
                    }

                }


            });

            Setting.exoPlayer.addListener(new Player.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                    Log.v("v", "onTracksChanged");
                }

                @Override
                public void onLoadingChanged(boolean isLoading) {
                    Log.v("v", "onLoadingChanged");

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == Player.STATE_ENDED) {
//                        if (Setting.Dark_Mode){
//                            ((MyViewHolder) holder).play.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white)));
//                            ((MyViewHolder) holder).pause.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white)));
//                        }else {
//                            ((MyViewHolder) holder).play.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white)));
//                            ((MyViewHolder) holder).pause.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.white)));
//                        }

                        ((MyViewHolder) holder).play.setVisibility(View.VISIBLE);
                        ((MyViewHolder) holder).pause.setVisibility(View.GONE);
                        ((MyViewHolder) holder).progressbar_new.setVisibility(View.GONE);
                    }
                    if (playbackState == Player.STATE_READY && playWhenReady) {
                        ((MyViewHolder) holder).progressbar_new.setVisibility(View.GONE);
                    }
                    Log.v("v", "onRepeatModeChanged" + playbackState + "-" + ExoPlayer.STATE_READY);
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {
                    Log.v("v", "onRepeatModeChanged");
                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    Log.v("v", "onPlayerError");
                    Setting.exoPlayer.setPlayWhenReady(false);
                    ((MyViewHolder) holder).progressbar_new.setVisibility(View.GONE);
                }

                @Override
                public void onPositionDiscontinuity(int reason) {
                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                    Log.v("v", "onPlaybackParametersChanged");
                }

                @Override
                public void onSeekProcessed() {
                }

            });


            ((MyViewHolder) holder).pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Setting.exoPlayer.getPlayWhenReady()) {
                        Setting.exoPlayer.setPlayWhenReady(false);
                        Picasso.get()
                                .load(R.drawable.play_ringtone)
                                .placeholder(R.drawable.play_ringtone)
                                .into(((MyViewHolder) holder).pause);
                    } else {
                        Setting.exoPlayer.setPlayWhenReady(true);
                        Picasso.get()
                                .load(R.drawable.pause_ringtone)
                                .placeholder(R.drawable.pause_ringtone)
                                .into(((MyViewHolder) holder).pause);
                    }
                }
            });


            ((MyViewHolder) holder).play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recyclerClickListener.onClick(getPosition(arrayList.get(holder.getAdapterPosition()).getId()));

                    methods.okhttpViewPost(arrayList.get(position));

                    MediaSource mediaSource;
                    if (finalUrl.endsWith("_Other")) {
                        finalUrl.replace("_Other", "");
                    }

                    dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "nemosofts_rc"), bandwidthMeter);
                    mediaSource = new ExtractorMediaSource(Uri.parse(finalUrl),
                            dataSourceFactory, extractorsFactory, null, null);
                    Setting.exoPlayer.prepare(mediaSource);

                    Setting.exoPlayer.setPlayWhenReady(true);

                    Picasso.get()
                            .load(R.drawable.pause_ringtone)
                            .placeholder(R.drawable.pause_ringtone)
                            .into(((MyViewHolder) holder).pause);

                    Picasso.get()
                            .load(R.drawable.play_ringtone)
                            .placeholder(R.drawable.play_ringtone)
                            .into(((MyViewHolder) holder).play);

                    ((MyViewHolder) holder).play.setVisibility(View.GONE);
                    ((MyViewHolder) holder).pause.setVisibility(View.VISIBLE);

                    ((MyViewHolder) holder).progressbar_new.setVisibility(View.VISIBLE);

                }
            });


            int step = 1;
            int final_step = 1;
            for (int i = 1; i < position + 1; i++) {
                if (i == position + 1) {
                    final_step = step;
                }
                step++;
                if (step > 7) {
                    step = 1;
                }
            }


            switch (step){
                case 1:
                    ((MyViewHolder) holder).bg_play_pause.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient1));
                    break;
                case 2:
                    ((MyViewHolder) holder).bg_play_pause.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient2));
                    break;
                case 3:
                    ((MyViewHolder) holder).bg_play_pause.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient3));
                    break;
                case 4:
                    ((MyViewHolder) holder).bg_play_pause.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient4));
                    break;
                case 5:
                    ((MyViewHolder) holder).bg_play_pause.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient5));
                    break;
                case 6:
                    ((MyViewHolder) holder).bg_play_pause.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient6));
                    break;
                case 7:
                    ((MyViewHolder) holder).bg_play_pause.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient7));
                    break;
                case 8:
                    ((MyViewHolder) holder).bg_play_pause.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient8));
                    break;
                case 9:
                    ((MyViewHolder) holder).bg_play_pause.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient9));
                    break;
                case 10:
                    ((MyViewHolder) holder).bg_play_pause.setBackground(ContextCompat.getDrawable(context, R.drawable.gradient10));
                    break;
            }



            if (Setting.exoPlayer.getPlayWhenReady() & Setting.arrayList_play_rc.get(Setting.playPos_rc).getRadio_id().equals(song.getRadio_id())) {
                ((MyViewHolder) holder).play.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.black)));
                ((MyViewHolder) holder).pause.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.black)));

                ((MyViewHolder) holder).play.setVisibility(View.GONE);
                ((MyViewHolder) holder).pause.setVisibility(View.VISIBLE);
            } else {
                ((MyViewHolder) holder).play.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.black)));
                ((MyViewHolder) holder).pause.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.black)));
                ((MyViewHolder) holder).progressbar_new.setVisibility(View.GONE);

                ((MyViewHolder) holder).pause.setVisibility(View.GONE);
                ((MyViewHolder) holder).play.setVisibility(View.VISIBLE);
            }


        } else {
            if (getItemCount() == 1) {
                ProgressViewHolder.progressBar.setVisibility(View.GONE);
            }
        }
    }

    public String format(Number number) {
        char[] arrc = new char[]{' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long l = number.longValue();
        double d = l;
        int n = (int)Math.floor((double)Math.log10((double)d));
        int n2 = n / 3;
        if (n >= 3 && n2 < arrc.length) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(new DecimalFormat("#0.0").format(d / Math.pow((double)10.0, (double)(n2 * 3))));
            stringBuilder.append(arrc[n2]);
            return stringBuilder.toString();
        }
        return new DecimalFormat("#,##0").format(l);
    }



    @Override
    public int getItemCount() {
        return arrayList.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int getPosition(String id) {
        int count=0;
        for(int i=0;i<filteredArrayList.size();i++) {
            if(id.equals(filteredArrayList.get(i).getId())) {
                count = i;
                break;
            }
        }
        return count;
    }

    public Filter getFilter() {
        if (filter == null) {
            filter = new NameFilter();
        }
        return filter;
    }

    private class NameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint.toString().length() > 0) {
                ArrayList<ItemRingtone> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getTitle();
                    if (nameList.toLowerCase().contains(constraint))
                        filteredItems.add(filteredArrayList.get(i));
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = filteredArrayList;
                    result.count = filteredArrayList.size();
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            arrayList = (ArrayList<ItemRingtone>) results.values;
            notifyDataSetChanged();
        }
    }


    private Boolean checkFav(int pos) {
        return dbHelper.checkFav(arrayList.get(pos).getId());
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    public boolean isHeader(int position) {
        return position == arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_PROG : VIEW_ITEM;
    }


    class DownloadFileAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;

            try {

                URL url = new URL(aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();

                int lenghtOfFile = conexion.getContentLength();
                Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(outputfile);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
            }
            return null;

        }

        protected void onProgressUpdate(String... progress) {
            Log.d("ANDRO_ASYNC", progress[0]);

        }

        @Override
        protected void onPostExecute(String unused) {
            showBottomSheetDialog();
            dialog.cancel();

        }
    }

    private void showBottomSheetDialog() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.layout_audio_setas_ringtone, null);

        final BottomSheetDialog dialog_setas = new BottomSheetDialog(context);
        dialog_setas.setContentView(view);
        dialog_setas.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        dialog_setas.show();

        LinearLayout ll_set_ring = dialog_setas.findViewById(R.id.ll_set_ring);
        LinearLayout ll_set_noti = dialog_setas.findViewById(R.id.ll_set_noti);
        LinearLayout ll_set_alarm = dialog_setas.findViewById(R.id.ll_set_alarm);

        ll_set_ring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRingtone("ring");
                dialog_setas.cancel();
            }
        });

        ll_set_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRingtone("noti");
                dialog_setas.cancel();
            }
        });

        ll_set_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRingtone( "alarm");
                dialog_setas.cancel();
            }
        });
    }

    private void setRingtone( final String type) {
        boolean settingsCanWrite = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            settingsCanWrite = Settings.System.canWrite(context);

            if (!settingsCanWrite) {
                // If do not have write settings permission then open the Can modify system settings panel.
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                context.startActivity(intent);
            } else {
                loadRingTone(type);
            }
        } else {
            loadRingTone(type);
        }
    }

    private void loadRingTone(final String type) {
        switch (type) {
            case "ring":
                setAsRingtone();
                break;
            case "noti":
                setNoti();
                break;
            case "alarm":
                setAlarm();
                break;
        }
    }

    private boolean checkSystemWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(context))
                return true;
            else
                openAndroidPermissionsMenu();
        }
        return false;
    }

    private void openAndroidPermissionsMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }

    private void setAsRingtone() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DATA, outputfile.getAbsolutePath());
                contentValues.put(MediaStore.MediaColumns.TITLE, ringtonename);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
                contentValues.put(MediaStore.MediaColumns.SIZE, outputfile.length());
                contentValues.put(MediaStore.Audio.Media.ARTIST, ringtonename);
                contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                contentValues.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
                contentValues.put(MediaStore.Audio.Media.IS_ALARM, true);
                contentValues.put(MediaStore.Audio.Media.IS_MUSIC, false);
                ContentResolver contentResolver = context.getContentResolver();
                Uri generalaudiouri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                contentResolver.delete(generalaudiouri, MediaStore.MediaColumns.DATA + "='" + outputfile.getAbsolutePath() + "'", null);
                Uri ringtoneuri = contentResolver.insert(generalaudiouri, contentValues);

            try {
                if (checkSystemWritePermission()) {
                    Toast.makeText(context, "Please wait...", Toast.LENGTH_LONG).show();
                    RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, ringtoneuri);
                    Toast.makeText(context, "Ringtone Set Successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Allow modify system settings ==> ON ", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.i("ringtoon", e.toString());
                Toast.makeText(context, "Unable to set as Ringtone ", Toast.LENGTH_SHORT).show();
            }

        } else {

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DATA, outputfile.getAbsolutePath());
            contentValues.put(MediaStore.MediaColumns.TITLE, ringtonename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
            contentValues.put(MediaStore.MediaColumns.SIZE, outputfile.length());
            contentValues.put(MediaStore.Audio.Media.ARTIST, ringtonename);
            contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            contentValues.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
            contentValues.put(MediaStore.Audio.Media.IS_ALARM, true);
            contentValues.put(MediaStore.Audio.Media.IS_MUSIC, false);
            ContentResolver contentResolver = context.getContentResolver();
            Uri generalaudiouri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            contentResolver.delete(generalaudiouri, MediaStore.MediaColumns.DATA + "='" + outputfile.getAbsolutePath() + "'", null);
            Uri ringtoneuri = contentResolver.insert(generalaudiouri, contentValues);


            try {
                if (checkSystemWritePermission()) {
                    Toast.makeText(context, "Please wait...", Toast.LENGTH_LONG).show();
                    RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, ringtoneuri);
                    Toast.makeText(context, "Ringtone Set Successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Allow modify system settings ==> ON ", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.i("ringtoon", e.toString());
                Toast.makeText(context, "Unable to set as Ringtone ", Toast.LENGTH_SHORT).show();
            }

        }


    }

    private void setAlarm() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DATA, outputfile.getAbsolutePath());
                contentValues.put(MediaStore.MediaColumns.TITLE, ringtonename);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
                contentValues.put(MediaStore.MediaColumns.SIZE, outputfile.length());
                contentValues.put(MediaStore.Audio.Media.ARTIST, ringtonename);
                contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                contentValues.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
                contentValues.put(MediaStore.Audio.Media.IS_ALARM, true);
                contentValues.put(MediaStore.Audio.Media.IS_MUSIC, false);
                ContentResolver contentResolver = context.getContentResolver();
                Uri generalaudiouri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                contentResolver.delete(generalaudiouri, MediaStore.MediaColumns.DATA + "='" + outputfile.getAbsolutePath() + "'", null);
                Uri ringtoneuri = contentResolver.insert(generalaudiouri, contentValues);

                try {
                    if (checkSystemWritePermission()) {
                        Toast.makeText(context, "Please wait...", Toast.LENGTH_LONG).show();
                        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM, ringtoneuri);

                        Toast.makeText(context, "Alarm Tone Set Successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Allow modify system settings ==> ON ", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.i("ringtoon", e.toString());
                    Toast.makeText(context, "Unable to set as Alarm Tone ", Toast.LENGTH_SHORT).show();
                }
        }else{

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DATA, outputfile.getAbsolutePath());
            contentValues.put(MediaStore.MediaColumns.TITLE, ringtonename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
            contentValues.put(MediaStore.MediaColumns.SIZE, outputfile.length());
            contentValues.put(MediaStore.Audio.Media.ARTIST, ringtonename);
            contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            contentValues.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
            contentValues.put(MediaStore.Audio.Media.IS_ALARM, true);
            contentValues.put(MediaStore.Audio.Media.IS_MUSIC, false);
            ContentResolver contentResolver = context.getContentResolver();
            Uri generalaudiouri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            contentResolver.delete(generalaudiouri, MediaStore.MediaColumns.DATA + "='" + outputfile.getAbsolutePath() + "'", null);
            Uri ringtoneuri = contentResolver.insert(generalaudiouri, contentValues);


            try {
                if (checkSystemWritePermission()) {
                    Toast.makeText(context, "Please wait...", Toast.LENGTH_LONG).show();
                    RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM, ringtoneuri);

                    Toast.makeText(context, "Alarm Tone Set Successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Allow modify system settings ==> ON ", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.i("ringtoon", e.toString());
                Toast.makeText(context, "Unable to set as Alarm Tone ", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void setNoti() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DATA, outputfile.getAbsolutePath());
            contentValues.put(MediaStore.MediaColumns.TITLE, ringtonename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
            contentValues.put(MediaStore.MediaColumns.SIZE, outputfile.length());
            contentValues.put(MediaStore.Audio.Media.ARTIST, ringtonename);
            contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            contentValues.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
            contentValues.put(MediaStore.Audio.Media.IS_ALARM, true);
            contentValues.put(MediaStore.Audio.Media.IS_MUSIC, false);
            ContentResolver contentResolver = context.getContentResolver();
            Uri generalaudiouri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            contentResolver.delete(generalaudiouri, MediaStore.MediaColumns.DATA + "='" + outputfile.getAbsolutePath() + "'", null);
            Uri ringtoneuri = contentResolver.insert(generalaudiouri, contentValues);

            try {
                if (checkSystemWritePermission()) {
                    Toast.makeText(context, "Please wait...", Toast.LENGTH_LONG).show();
                    RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, ringtoneuri);

                    Toast.makeText(context, "Notification Sound Set Successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Allow modify system settings ==> ON ", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.i("ringtoon", e.toString());
                Toast.makeText(context, "Unable to set as Notification ", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(context, context.getString(R.string.noti_set), Toast.LENGTH_SHORT).show();
        } else {

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DATA, outputfile.getAbsolutePath());
            contentValues.put(MediaStore.MediaColumns.TITLE, ringtonename);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
            contentValues.put(MediaStore.MediaColumns.SIZE, outputfile.length());
            contentValues.put(MediaStore.Audio.Media.ARTIST, ringtonename);
            contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            contentValues.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
            contentValues.put(MediaStore.Audio.Media.IS_ALARM, true);
            contentValues.put(MediaStore.Audio.Media.IS_MUSIC, false);
            ContentResolver contentResolver = context.getContentResolver();
            Uri generalaudiouri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            contentResolver.delete(generalaudiouri, MediaStore.MediaColumns.DATA + "='" + outputfile.getAbsolutePath() + "'", null);
            Uri ringtoneuri = contentResolver.insert(generalaudiouri, contentValues);

            try {
                if (checkSystemWritePermission()) {
                    Toast.makeText(context, "Please wait...", Toast.LENGTH_LONG).show();
                    RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, ringtoneuri);

                    Toast.makeText(context, "Notification Sound Set Successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Allow modify system settings ==> ON ", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.i("ringtoon", e.toString());
                Toast.makeText(context, "Unable to set as Notification ", Toast.LENGTH_SHORT).show();
            }
        }
    }
}