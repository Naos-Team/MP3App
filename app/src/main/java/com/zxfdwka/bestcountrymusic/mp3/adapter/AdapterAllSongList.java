package com.zxfdwka.bestcountrymusic.mp3.adapter;

import static android.Manifest.permission.READ_PHONE_STATE;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.zxfdwka.bestcountrymusic.mp3.activity.BaseActivity;
import com.zxfdwka.bestcountrymusic.mp3.activity.PlayerService;
import com.zxfdwka.bestcountrymusic.mp3.activity.R;
import com.zxfdwka.bestcountrymusic.mp3.asyncTask.GetRating;
import com.zxfdwka.bestcountrymusic.mp3.asyncTask.LoadRating;
import com.zxfdwka.bestcountrymusic.mp3.fragment.FragmentOptionMusic;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.ClickListenerPlayList;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.OptionMusicListener;
import com.zxfdwka.bestcountrymusic.mp3.interfaces.RatingListener;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemMyPlayList;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemSong;
import com.zxfdwka.bestcountrymusic.mp3.utils.Constant;
import com.zxfdwka.bestcountrymusic.mp3.utils.DBHelper;
import com.zxfdwka.bestcountrymusic.mp3.utils.GlobalBus;
import com.zxfdwka.bestcountrymusic.mp3.utils.Methods;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdsManager;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import es.claucookie.miniequalizerlibrary.EqualizerView;


public class AdapterAllSongList extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<ItemSong> arrayList;
    private ArrayList<ItemSong> filteredArrayList;
    private ClickListenerPlayList recyclerClickListener;
    private NameFilter filter;
    private String type;
    private Methods methods;
    private DBHelper dbHelper;
    private Dialog dialog_rate;
    private BottomSheetDialog dialog_desc;

    private boolean is_currentList = false;

    private final int VIEW_PROG = -1;

    private Boolean isAdLoaded = false;
    private List<UnifiedNativeAd> mNativeAdsAdmob = new ArrayList<>();
    private NativeAdsManager mNativeAdsManager;
    private ArrayList<NativeAd> mNativeAdsFB = new ArrayList<>();

    public boolean isIs_currentList() {
        return is_currentList;
    }

    public void setIs_currentList(boolean is_currentList) {
        this.is_currentList = is_currentList;
    }

    public AdapterAllSongList(Context context, ArrayList<ItemSong> arrayList, ClickListenerPlayList recyclerClickListener, String type) {
        this.arrayList = arrayList;
        this.filteredArrayList = arrayList;
        this.context = context;
        this.type = type;
        this.recyclerClickListener = recyclerClickListener;
        methods = new Methods(context);
        dbHelper = new DBHelper(context);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView_song, textView_duration, textView_catname, tv_avg_rate, tv_views, tv_download;
        EqualizerView equalizer;
        ImageView imageView, imageView_option, iv_downlaod_icon;
        RelativeLayout rl;
        RatingBar ratingBar;

        MyViewHolder(View view) {
            super(view);
            rl = view.findViewById(R.id.ll_songlist);
            tv_views = view.findViewById(R.id.tv_songlist_views);
            tv_download = view.findViewById(R.id.tv_songlist_downloads);
            textView_song = view.findViewById(R.id.tv_songlist_name);
            textView_duration = view.findViewById(R.id.tv_songlist_duration);
            tv_avg_rate = view.findViewById(R.id.tv_songlist_avg_rate);
            equalizer = view.findViewById(R.id.equalizer_view);
            textView_catname = view.findViewById(R.id.tv_songlist_cat);
            imageView = view.findViewById(R.id.iv_songlist);
            imageView_option = view.findViewById(R.id.iv_songlist_option);
            ratingBar = view.findViewById(R.id.rb_songlist);
            iv_downlaod_icon = view.findViewById(R.id.iv_downlaod_icon);

            if (!Constant.isSongDownload) {
                tv_download.setVisibility(View.GONE);
                iv_downlaod_icon.setVisibility(View.GONE);
            }
        }
    }

    private static class ADViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rl_native_ad;

        private ADViewHolder(View view) {
            super(view);
            rl_native_ad = view.findViewById(R.id.rl_native_ad);
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        private static ProgressBar progressBar;

        private ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_PROG) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_progressbar, parent, false);
            return new ProgressViewHolder(v);
        } else if (viewType >= 1000) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ads, parent, false);
            return new ADViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recent_songs, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {

            ((MyViewHolder) holder).tv_views.setText(methods.format(Double.parseDouble(arrayList.get(position).getViews())));
            ((MyViewHolder) holder).tv_download.setText(methods.format(Double.parseDouble(arrayList.get(position).getDownloads())));

            ((MyViewHolder) holder).textView_song.setText(arrayList.get(position).getTitle());
            ((MyViewHolder) holder).textView_duration.setText(arrayList.get(position).getDuration());
            Picasso.get()
                    .load(arrayList.get(position).getImageSmall())
                    .placeholder(R.drawable.placeholder_song)
                    .into(((MyViewHolder) holder).imageView);

            ((MyViewHolder) holder).tv_avg_rate.setTypeface(((MyViewHolder) holder).tv_avg_rate.getTypeface(), Typeface.BOLD);
            ((MyViewHolder) holder).tv_avg_rate.setText(arrayList.get(position).getAverageRating());
            ((MyViewHolder) holder).ratingBar.setRating(Float.parseFloat(arrayList.get(position).getAverageRating()));

            if (PlayerService.getIsPlayling() && Constant.playPos <= holder.getAdapterPosition() && Constant.arrayList_play.get(Constant.playPos).getId().equals(arrayList.get(position).getId())) {
                ((MyViewHolder) holder).imageView.setVisibility(View.GONE);
                ((MyViewHolder) holder).equalizer.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).equalizer.animateBars();
            } else {
                ((MyViewHolder) holder).imageView.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).equalizer.setVisibility(View.GONE);
                ((MyViewHolder) holder).equalizer.stopBars();
            }

            if (arrayList.get(position).getCatName() != null && !arrayList.get(position).getCatName().equals("")) {
                ((MyViewHolder) holder).textView_catname.setText(arrayList.get(position).getCatName());
            } else {
                ((MyViewHolder) holder).textView_catname.setText(arrayList.get(position).getArtist());
            }

            ((MyViewHolder) holder).rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        recyclerClickListener.onClick(holder.getAdapterPosition());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            ((MyViewHolder) holder).imageView_option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    try {
//                        openOptionPopUp(((MyViewHolder) holder).imageView_option, holder.getAdapterPosition());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    FragmentOptionMusic fragmentOptionMusic = new FragmentOptionMusic(arrayList.get(holder.getAdapterPosition()), new OptionMusicListener() {
                        @Override
                        public void onDescription(ItemSong itemSong) {
                            View view = ((Activity) context).getLayoutInflater().inflate(R.layout.layout_desc, null);

                            dialog_desc = new BottomSheetDialog(context);
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

//                            webview_song_desc.loadData(text, mimeType, encoding);
                            webview_song_desc.loadDataWithBaseURL("blarg://ignored", text, mimeType, encoding, "");
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
                                    Toast.makeText(context, context.getResources().getString(R.string.err_no_songs_selected), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                methods.clickLogin();
                            }
                        }

                        @Override
                        public void onAddToPlaylist(ItemSong itemSong) {
                            if(is_currentList){
                                switch (type) {
                                    case "playlist":
                                        dbHelper.removeFromPlayList(itemSong.getId(), true);
                                        arrayList.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());
                                        Toast.makeText(context, context.getString(R.string.remove_from_playlist), Toast.LENGTH_SHORT).show();
                                        if (arrayList.size() == 0) {
                                            recyclerClickListener.onItemZero();
                                        }
                                        break;
                                    default:
                                        if(Constant.arrayList_play.get(Constant.playPos).equals(itemSong)){
                                            Toast.makeText(context, "The song is playing", Toast.LENGTH_SHORT).show();
                                        } else {
                                            arrayList.remove(itemSong);
                                            Constant.arrayList_play.remove(itemSong);
                                            notifyDataSetChanged();
                                        }
                                        break;
                                }
                            } else {
                                methods.openPlaylists(itemSong, true);
                            }
                        }

                        @Override
                        public void onAddToQueue(ItemSong itemSong) {
                            Constant.arrayList_play.add(itemSong);
                            GlobalBus.getBus().postSticky(new ItemMyPlayList("", "", null));
                            Toast.makeText(context, context.getResources().getString(R.string.add_to_queue), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSearchYTB(ItemSong itemSong) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_SEARCH);
                                intent.setPackage("com.google.android.youtube");
                                intent.putExtra("query", itemSong.getTitle());
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(context, context.getResources().getString(R.string.youtube_not_installed), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onShare(ItemSong itemSong) {
                            if (Constant.isOnline || Constant.isDownloaded) {
                                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                sharingIntent.setType("text/plain");
                                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.share_song));
                                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, context.getResources().getString(R.string.listening) + " - "
                                        + itemSong.getTitle() + "\n\nvia " + context.getResources().getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + context.getPackageName());
                                context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.share_song)));
                            } else {
                                if (checkPer()) {
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("audio/mp3");
                                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse(itemSong.getUrl()));
                                    share.putExtra(android.content.Intent.EXTRA_TEXT, context.getResources().getString(R.string.listening) + " - "
                                            + itemSong.getTitle() + "\n\nvia " + context.getResources().getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + context.getPackageName());
                                    context.startActivity(Intent.createChooser(share, context.getResources().getString(R.string.share_song)));
                                }
                            }
                        }

                        @Override
                        public void onRate(ItemSong itemSong) {
                            openRateDialog(itemSong);
                        }

                        @Override
                        public void onEndLike() {
                            notifyDataSetChanged();
                        }
                    }, methods, null, is_currentList);

                    fragmentOptionMusic.show(((FragmentActivity) context).getSupportFragmentManager(), fragmentOptionMusic.getTag());
                }
            });
        } else if (holder instanceof ADViewHolder) {
            if (isAdLoaded) {
                if (((ADViewHolder) holder).rl_native_ad.getChildCount() == 0) {
                    if (Constant.natveAdType.equals("admob")) {
                        if (mNativeAdsAdmob.size() >= 1) {

                            int i;
                            if (mNativeAdsAdmob.size() > 1) {
                                i = new Random().nextInt(mNativeAdsAdmob.size() - 1);
                            } else {
                                i = 0;
                            }

//                            CardView cardView = (CardView) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_admob, null);

                            UnifiedNativeAdView adView = (UnifiedNativeAdView) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_admob, null);
                            populateUnifiedNativeAdView(mNativeAdsAdmob.get(i), adView);
                            ((ADViewHolder) holder).rl_native_ad.removeAllViews();
                            ((ADViewHolder) holder).rl_native_ad.addView(adView);

                            ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                        }
                    } else {
                        NativeAdLayout fb_native_container = (NativeAdLayout) ((Activity) context).getLayoutInflater().inflate(R.layout.layout_native_ad_fb, null);

                        NativeAd ad;
                        if (mNativeAdsFB.size() >= 5) {
                            ad = mNativeAdsFB.get(new Random().nextInt(5));
                        } else {
                            ad = mNativeAdsManager.nextNativeAd();
                            mNativeAdsFB.add(ad);
                        }

                        LinearLayout adChoicesContainer = fb_native_container.findViewById(R.id.ad_choices_container);
                        AdOptionsView adOptionsView = new AdOptionsView(context, ad, fb_native_container);
                        adChoicesContainer.removeAllViews();
                        adChoicesContainer.addView(adOptionsView, 0);

                        // Create native UI using the ad metadata.
                        com.facebook.ads.MediaView nativeAdIcon = fb_native_container.findViewById(R.id.native_ad_icon);
                        TextView nativeAdTitle = fb_native_container.findViewById(R.id.native_ad_title);
                        com.facebook.ads.MediaView nativeAdMedia = fb_native_container.findViewById(R.id.native_ad_media);
                        TextView nativeAdSocialContext = fb_native_container.findViewById(R.id.native_ad_social_context);
                        TextView nativeAdBody = fb_native_container.findViewById(R.id.native_ad_body);
                        TextView sponsoredLabel = fb_native_container.findViewById(R.id.native_ad_sponsored_label);
                        Button nativeAdCallToAction = fb_native_container.findViewById(R.id.native_ad_call_to_action);

                        // Set the Text.
                        nativeAdTitle.setText(ad.getAdvertiserName());
                        nativeAdBody.setText(ad.getAdBodyText());
                        nativeAdSocialContext.setText(ad.getAdSocialContext());
                        nativeAdCallToAction.setVisibility(ad.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                        nativeAdCallToAction.setText(ad.getAdCallToAction());
                        sponsoredLabel.setText(ad.getSponsoredTranslation());

                        // Create a list of clickable views
                        List<View> clickableViews = new ArrayList<>();
                        clickableViews.add(nativeAdTitle);
                        clickableViews.add(nativeAdCallToAction);

                        // Register the Title and CTA button to listen for clicks.
                        ad.registerViewForInteraction(fb_native_container, nativeAdMedia, nativeAdIcon, clickableViews);

                        ((ADViewHolder) holder).rl_native_ad.addView(fb_native_container);

                        ((ADViewHolder) holder).rl_native_ad.setVisibility(View.VISIBLE);
                    }
                }
            }
        } else {
            try {
                if (getItemCount() < 10) {
                    ProgressViewHolder.progressBar.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size() + 1;
    }

    public boolean isHeader(int position) {
        return position == arrayList.size();
    }

    public void hideHeader() {
        try {
            ProgressViewHolder.progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return (!is_currentList) ? VIEW_PROG : 1000 + position;
        } else if (arrayList.get(position) == null) {
            return 1000 + position;
        } else {
            return position;
        }
    }

    public void addAds(UnifiedNativeAd unifiedNativeAd) {
        mNativeAdsAdmob.add(unifiedNativeAd);
        isAdLoaded = true;
    }

    public void setFBNativeAdManager(NativeAdsManager mNativeAdsManager) {
        this.mNativeAdsManager = mNativeAdsManager;
        isAdLoaded = true;
    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);
    }

    public void destroyNativeAds() {
        try {
            for (int i = 0; i < mNativeAdsAdmob.size(); i++) {
                mNativeAdsAdmob.get(i).destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getRealPos(int pos, ArrayList<ItemSong> arrayListTemp) {
        return arrayListTemp.indexOf(arrayList.get(pos));
    }

    private void openOptionPopUp(ImageView imageView, final int pos) {
        PopupMenu popup = new PopupMenu(context, imageView);
        popup.getMenuInflater().inflate(R.menu.popup_song, popup.getMenu());
        if (type.equals("playlist")) {
            popup.getMenu().findItem(R.id.popup_add_song).setTitle(context.getString(R.string.remove));
        }
        if (!Constant.isOnline) {
            popup.getMenu().findItem(R.id.popup_add_queue).setVisible(false);
        }
        if (!methods.isYoutubeAppInstalled()) {
            popup.getMenu().findItem(R.id.popup_youtube).setVisible(false);
        }
        if (!Constant.isSongDownload) {
            popup.getMenu().findItem(R.id.popup_download).setVisible(false);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.popup_add_song:
                        switch (type) {
                            case "playlist":
                                dbHelper.removeFromPlayList(arrayList.get(pos).getId(), true);
                                arrayList.remove(pos);
                                notifyItemRemoved(pos);
                                Toast.makeText(context, context.getString(R.string.remove_from_playlist), Toast.LENGTH_SHORT).show();
                                if (arrayList.size() == 0) {
                                    recyclerClickListener.onItemZero();
                                }
                                break;
                            default:
                                methods.openPlaylists(arrayList.get(pos), true);
                                break;
                        }
                        break;
                    case R.id.popup_add_queue:
                        Constant.arrayList_play.add(arrayList.get(pos));
                        GlobalBus.getBus().postSticky(new ItemMyPlayList("", "", null));
                        Toast.makeText(context, context.getString(R.string.add_to_queue), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.popup_youtube:
                        Intent intent = new Intent(Intent.ACTION_SEARCH);
                        intent.setPackage("com.google.android.youtube");
                        intent.putExtra("query", arrayList.get(pos).getTitle());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;
                    case R.id.popup_share:
                        methods.shareSong(arrayList.get(pos), true);
                        break;
                    case R.id.popup_download:
                        methods.download(arrayList.get(pos));
                        break;
                }
                return true;
            }
        });
        popup.show();
    }

    public Filter getFilter() {
        if (filter == null) {
            filter = new NameFilter();
        }
        return filter;
    }

    public Boolean checkPer() {
        if ((ContextCompat.checkSelfPermission(context, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((Activity) context).requestPermissions(new String[]{READ_PHONE_STATE}, 1);
            }
            return false;
        } else {
            return true;
        }
    }

    private void openRateDialog(ItemSong itemSong) {
        dialog_rate = new Dialog(context);
        dialog_rate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_rate.setContentView(R.layout.layout_review);

        final ImageView iv_close = dialog_rate.findViewById(R.id.iv_rate_close);
        final TextView textView = dialog_rate.findViewById(R.id.tv_rate);
        final RatingBar ratingBar = dialog_rate.findViewById(R.id.rb_add);
        final Button button = dialog_rate.findViewById(R.id.button_submit_rating);
        final Button button_later = dialog_rate.findViewById(R.id.button_later_rating);

        ratingBar.setStepSize(Float.parseFloat("1"));

        if (itemSong.getUserRating().equals("") || itemSong.getUserRating().equals("0")) {
            new GetRating(new RatingListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String isRateSuccess, String message, int rating) {
                    if (rating > 0) {
                        ratingBar.setRating(rating);
                        textView.setText(context.getString(R.string.thanks_for_rating));
                    } else {
                        ratingBar.setRating(1);
                    }
                    itemSong.setUserRating(String.valueOf(rating));

                }
            }, methods.getAPIRequest(Constant.METHOD_SINGLE_SONG, 0, "", itemSong.getId(), "", "", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "", null)).execute();
        } else {
            if (Integer.parseInt(itemSong.getUserRating()) != 0 && !itemSong.getUserRating().equals("")) {
                textView.setText(context.getString(R.string.thanks_for_rating));
                ratingBar.setRating(Integer.parseInt(itemSong.getUserRating()));
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
                            loadRatingApi(String.valueOf((int) ratingBar.getRating()), itemSong);
                        } else {
                            Toast.makeText(context, context.getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, context.getString(R.string.select_rating), Toast.LENGTH_SHORT).show();
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

    private void loadRatingApi(final String rate, ItemSong itemSong) {

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getResources().getString(R.string.loading));
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
                        itemSong.setAverageRating(String.valueOf(rating));
                        itemSong.setTotalRate(String.valueOf(Integer.parseInt(itemSong.getTotalRate() + 1)));
                        itemSong.setUserRating(String.valueOf(rate));
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
                dialog_rate.dismiss();
            }
        }, methods.getAPIRequest(Constant.METHOD_RATINGS, 0, "", itemSong.getId(), "", "", "", "", "", "", rate, "", "", "", "", Constant.itemUser.getId(), "", null));

        loadRating.execute();
    }

    private class NameFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint.toString().length() > 0) {
                ArrayList<ItemSong> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    if(filteredArrayList.get(i) != null) {
                        String nameList = filteredArrayList.get(i).getTitle();
                        if (nameList.toLowerCase().contains(constraint))
                            filteredItems.add(filteredArrayList.get(i));
                    }
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

            arrayList = (ArrayList<ItemSong>) results.values;
            notifyDataSetChanged();
        }
    }
}