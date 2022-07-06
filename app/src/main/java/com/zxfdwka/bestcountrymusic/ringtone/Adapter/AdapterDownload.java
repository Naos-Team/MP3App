package com.zxfdwka.bestcountrymusic.ringtone.Adapter;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
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
import com.zxfdwka.bestcountrymusic.ringtone.Listener.ClickListenerRecorder;
import com.zxfdwka.bestcountrymusic.ringtone.SharedPref.Setting;
import com.zxfdwka.bestcountrymusic.ringtone.item.Itemdownload;

import java.io.File;
import java.util.ArrayList;

public class AdapterDownload extends RecyclerView.Adapter<AdapterDownload.MyViewHolder> {

    private Context context;
    private ArrayList<Itemdownload> arrayList;
    private ArrayList<Itemdownload> filteredArrayList;
    private ClickListenerRecorder recyclerClickListener;
    private NameFilter filter;
    private String type;

    DefaultBandwidthMeter bandwidthMeter;
    DataSource.Factory dataSourceFactory;
    ExtractorsFactory extractorsFactory;

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title, tv_artist, views;
        public ImageView play, pause, option, ringtone;
        RelativeLayout linearLayout;


        MyViewHolder(View view) {
            super(view);

            tv_title = (TextView) itemView.findViewById(R.id.tv_songlist_name);
            tv_artist = (TextView) itemView.findViewById(R.id.tv_songlist_cat);

            linearLayout = itemView.findViewById(R.id.thiva);

            play = itemView.findViewById(R.id.play);
            pause = itemView.findViewById(R.id.pause);

            views = itemView.findViewById(R.id.tv_songlist_vie);
            option = itemView.findViewById(R.id.iv_option);

            ringtone = itemView.findViewById(R.id.ringtone);
        }
    }

    public AdapterDownload(Context context, ArrayList<Itemdownload> arrayList, ClickListenerRecorder recyclerClickListener, String type) {
        this.arrayList = arrayList;
        this.filteredArrayList = arrayList;
        this.context = context;
        this.type = type;
        this.recyclerClickListener = recyclerClickListener;


        bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "nemosofts_dow"), bandwidthMeter);
        extractorsFactory = new DefaultExtractorsFactory();

        Setting.exoPlayer_do = ExoPlayerFactory.newSimpleInstance(context, trackSelector);



    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_ringtone_download, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final Itemdownload song = arrayList.get(position);

        holder.tv_title.setText(song.getTitle());
        holder.tv_artist.setText(song.getDuration());
        holder.views.setText(" ");

        final String finalUrl =  song.getMp3();

        Setting.exoPlayer_do.addListener(new Player.EventListener() {
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
                    holder.play.setVisibility(View.VISIBLE);
                    holder.pause.setVisibility(View.GONE);
                }
                if (playbackState == Player.STATE_READY && playWhenReady) {
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
                Setting.exoPlayer_do.setPlayWhenReady(false);
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


        holder.pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Setting.exoPlayer_do.getPlayWhenReady()) {
                    Setting.exoPlayer_do.setPlayWhenReady(false);
                    Picasso.get()
                            .load(R.drawable.play_ringtone)
                            .placeholder(R.drawable.play_ringtone)
                            .into(holder.pause);
                } else {
                    Setting.exoPlayer_do.setPlayWhenReady(true);
                    Picasso.get()
                            .load(R.drawable.pause_ringtone)
                            .placeholder(R.drawable.pause_ringtone)
                            .into(holder.pause);
                }
            }
        });


        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerClickListener.onClick(getPosition(arrayList.get(holder.getAdapterPosition()).getId()));

                MediaSource mediaSource;
                if (finalUrl.endsWith("_Other")) {
                    finalUrl.replace("_Other", "");
                }

                dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "nemosofts_dow"), bandwidthMeter);
                mediaSource = new ExtractorMediaSource(Uri.parse(finalUrl),
                        dataSourceFactory, extractorsFactory, null, null);
                Setting.exoPlayer_do.prepare(mediaSource);

                Setting.exoPlayer_do.setPlayWhenReady(true);


                Picasso.get()
                        .load(R.drawable.pause_ringtone)
                        .placeholder(R.drawable.pause_ringtone)
                        .into(holder.pause);

                Picasso.get()
                        .load(R.drawable.play_ringtone)
                        .placeholder(R.drawable.play_ringtone)
                        .into(holder.play);


                holder.play.setVisibility(View.GONE);
                holder.pause.setVisibility(View.VISIBLE);

            }
        });


        if (Setting.exoPlayer_do.getPlayWhenReady() & Setting.arrayList_play_do.get(Setting.playPos_do).getId().equals(song.getId())) {
            holder.play.setVisibility(View.GONE);
            holder.pause.setVisibility(View.VISIBLE);
        } else {
            holder.pause.setVisibility(View.GONE);
            holder.play.setVisibility(View.VISIBLE);
        }

        holder.ringtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog(holder.getAdapterPosition());
            }
        });

        holder.option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOptionPopUp(holder.option, holder.getAdapterPosition());
            }
        });

    }


    private void openOptionPopUp(ImageView imageView, final int pos) {
        Context wrapper = new ContextThemeWrapper(context, R.style.YOURSTYLE);
        android.widget.PopupMenu popup = new android.widget.PopupMenu(wrapper, imageView);
        popup.getMenuInflater().inflate(R.menu.popup_song_off_ringtone, popup.getMenu());


        popup.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.popup_delete:
                        openDeleteDialog(pos);
                        break;
                    case R.id.popup_sha:
                        openshaDialog(pos);
                        break;

                }
                return true;
            }
        });
        popup.show();
    }

    private void openshaDialog(final int pos) {
        final File file = new File(arrayList.get(pos).getMp3());

        Uri uri = Uri.parse(file.getAbsolutePath());
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/*");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(share, arrayList.get(pos).getTitle()));
    }


    private void openDeleteDialog(final int pos) {
        final File file = new File(arrayList.get(pos).getMp3());
        AlertDialog.Builder dialog;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new AlertDialog.Builder(context, R.style.ThemeDialog);
        } else {
            dialog = new AlertDialog.Builder(context);
        }
        dialog.setTitle(context.getString(R.string.delete));
        dialog.setMessage(context.getString(R.string.sure_delete));
        dialog.setPositiveButton(context.getString(R.string.delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String where = MediaStore.MediaColumns.DATA + "=?";
                final String[] selectionArgs = new String[] {
                        file.getAbsolutePath()
                };
                final ContentResolver contentResolver = context.getContentResolver();
                final Uri filesUri = MediaStore.Files.getContentUri("external");

                contentResolver.delete(filesUri, where, selectionArgs);

                if (file.exists()) {
                    contentResolver.delete(filesUri, where, selectionArgs);
                    file.delete();
                    arrayList.remove(pos);
                    notifyItemRemoved(pos);
                    Toast.makeText(context, context.getString(R.string.file_deleted), Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialog.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }


    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public Itemdownload getItem(int pos) {
        return arrayList.get(pos);
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
                ArrayList<Itemdownload> filteredItems = new ArrayList<>();

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
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            arrayList = (ArrayList<Itemdownload>) results.values;
            notifyDataSetChanged();
        }
    }



    private void showBottomSheetDialog(final int pos) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.layout_audio_setas_ringtone, null);

        BottomSheetDialog dialog_setas = new BottomSheetDialog(context);
        dialog_setas.setContentView(view);
        dialog_setas.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        dialog_setas.show();

        LinearLayout ll_set_ring = dialog_setas.findViewById(R.id.ll_set_ring);
        LinearLayout ll_set_noti = dialog_setas.findViewById(R.id.ll_set_noti);
        LinearLayout ll_set_alarm = dialog_setas.findViewById(R.id.ll_set_alarm);

        ll_set_ring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRingtone(pos, "ring");
            }
        });

        ll_set_noti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRingtone(pos, "noti");
            }
        });

        ll_set_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRingtone(pos, "alarm");
            }
        });
    }

    private void setRingtone(final int pos, final String type) {
        boolean settingsCanWrite = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            settingsCanWrite = Settings.System.canWrite(context);

            if (!settingsCanWrite) {
                // If do not have write settings permission then open the Can modify system settings panel.
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                context.startActivity(intent);
            } else {
                loadRingTone(pos, type);
            }
        } else {
            loadRingTone(pos, type);
        }
    }

    private void loadRingTone(final int pos, final String type) {
        switch (type) {
            case "ring":
                setAsRingtone(arrayList.get(pos));
                break;
            case "noti":
                setNoti(arrayList.get(pos));
                break;
            case "alarm":
                setAlarm(arrayList.get(pos));
                break;
        }
    }

    private void setAsRingtone(Itemdownload itemdownload) {
        String filePath = itemdownload.getMp3();

        File ringtoneFile = new File(filePath);

        MediaMetadataRetriever md = new MediaMetadataRetriever();
        md.setDataSource(ringtoneFile.getAbsolutePath());
        String title = ringtoneFile.getName();
        String duration = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String artist = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

        ContentValues content = new ContentValues();
        content.put(MediaStore.MediaColumns.DATA, filePath);
        content.put(MediaStore.MediaColumns.TITLE, title);
        content.put(MediaStore.MediaColumns.SIZE, ringtoneFile.length());
        content.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        content.put(MediaStore.Audio.Media.ARTIST, artist);
        content.put(MediaStore.Audio.Media.DURATION, duration);
        content.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        content.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        content.put(MediaStore.Audio.Media.IS_ALARM, true);
        content.put(MediaStore.Audio.Media.IS_MUSIC, true);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(ringtoneFile.getAbsolutePath());

        context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + ringtoneFile.getAbsolutePath() + "\"", null);
        Uri newUri = context.getContentResolver().insert(uri, content);

        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);

        Toast.makeText(context, context.getString(R.string.ringtone_set), Toast.LENGTH_SHORT).show();
    }

    private void setAlarm(Itemdownload itemdownload) {
        String filePath = itemdownload.getMp3();

        File ringtoneFile = new File(filePath);

        MediaMetadataRetriever md = new MediaMetadataRetriever();
        md.setDataSource(ringtoneFile.getAbsolutePath());
        String title = ringtoneFile.getName();
        String duration = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String artist = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

        ContentValues content = new ContentValues();
        content.put(MediaStore.MediaColumns.DATA, filePath);
        content.put(MediaStore.MediaColumns.TITLE, title);
        content.put(MediaStore.MediaColumns.SIZE, ringtoneFile.length());
        content.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        content.put(MediaStore.Audio.Media.ARTIST, artist);
        content.put(MediaStore.Audio.Media.DURATION, Integer.parseInt(duration));
        content.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        content.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        content.put(MediaStore.Audio.Media.IS_ALARM, true);
        content.put(MediaStore.Audio.Media.IS_MUSIC, true);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(ringtoneFile.getAbsolutePath());

        context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + ringtoneFile.getAbsolutePath() + "\"", null);
        Uri newUri = context.getContentResolver().insert(uri, content);

        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM, newUri);

        Toast.makeText(context, context.getString(R.string.alarm_set), Toast.LENGTH_SHORT).show();
    }

    private void setNoti(Itemdownload itemdownload) {

        String filePath = itemdownload.getMp3();

        File ringtoneFile = new File(filePath);

        MediaMetadataRetriever md = new MediaMetadataRetriever();
        md.setDataSource(ringtoneFile.getAbsolutePath());
        String title = ringtoneFile.getName();
        String duration = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        String artist = md.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

        ContentValues content = new ContentValues();
        content.put(MediaStore.MediaColumns.DATA, filePath);
        content.put(MediaStore.MediaColumns.TITLE, title);
        content.put(MediaStore.MediaColumns.SIZE, ringtoneFile.length());
        content.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
        content.put(MediaStore.Audio.Media.ARTIST, artist);
        content.put(MediaStore.Audio.Media.DURATION, duration);
        content.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        content.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        content.put(MediaStore.Audio.Media.IS_ALARM, true);
        content.put(MediaStore.Audio.Media.IS_MUSIC, true);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(ringtoneFile.getAbsolutePath());

        context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + ringtoneFile.getAbsolutePath() + "\"", null);
        Uri newUri = context.getContentResolver().insert(uri, content);

        RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, newUri);

        Toast.makeText(context, context.getString(R.string.noti_set), Toast.LENGTH_SHORT).show();
    }


}