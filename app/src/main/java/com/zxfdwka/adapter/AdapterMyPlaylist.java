package com.zxfdwka.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.interfaces.ClickListenerPlayList;
import com.zxfdwka.item.ItemArtist;
import com.zxfdwka.item.ItemMyPlayList;
import com.zxfdwka.utils.Constant;
import com.zxfdwka.utils.DBHelper;
import com.zxfdwka.utils.Methods;
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
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;


public class AdapterMyPlaylist extends RecyclerView.Adapter {

    private DBHelper dbHelper;
    private Context context;
    private ArrayList<ItemMyPlayList> arrayList;
    private ArrayList<ItemMyPlayList> filteredArrayList;
    private NameFilter filter;
    private ClickListenerPlayList clickListenerPlayList;
    private int columnWidth = 0;
    private Boolean isOnline;
    private Methods methods;

    private Boolean isAdLoaded = false;
    private List<UnifiedNativeAd> mNativeAdsAdmob = new ArrayList<>();
    private NativeAdsManager mNativeAdsManager;
    private ArrayList<NativeAd> mNativeAdsFB = new ArrayList<>();

    public AdapterMyPlaylist(Context context, ArrayList<ItemMyPlayList> arrayList, ClickListenerPlayList clickListenerPlayList, Boolean isOnline) {
        this.arrayList = arrayList;
        this.filteredArrayList = arrayList;
        this.context = context;
        this.isOnline = isOnline;
        this.clickListenerPlayList = clickListenerPlayList;
        dbHelper = new DBHelper(context);
        methods = new Methods(context);
        columnWidth = methods.getColumnWidth(2, 5);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView_more, imageView1, imageView2, imageView3, imageView4;
        RelativeLayout rl;

        MyViewHolder(View view) {
            super(view);
            rl = view.findViewById(R.id.rl_myplaylist);
            textView = view.findViewById(R.id.tv_myplaylist);
            imageView_more = view.findViewById(R.id.iv_more_myplaylist);
            imageView1 = view.findViewById(R.id.iv_myplaylist1);
            imageView2 = view.findViewById(R.id.iv_myplaylist2);
            imageView3 = view.findViewById(R.id.iv_myplaylist3);
            imageView4 = view.findViewById(R.id.iv_myplaylist4);
        }
    }

    private static class ADViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rl_native_ad;

        private ADViewHolder(View view) {
            super(view);
            rl_native_ad = view.findViewById(R.id.rl_native_ad);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType >= 1000) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ads, parent, false);
            return new ADViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_my_playlist, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {
            ((MyViewHolder) holder).textView.setText(arrayList.get(position).getName());

            if (isOnline) {
                Picasso.get()
                        .load(arrayList.get(position).getArrayListUrl().get(3))
                        .placeholder(R.drawable.placeholder_song)
                        .into(((MyViewHolder) holder).imageView1);
                Picasso.get()
                        .load(arrayList.get(position).getArrayListUrl().get(2))
                        .placeholder(R.drawable.placeholder_song)
                        .into(((MyViewHolder) holder).imageView2);
                Picasso.get()
                        .load(arrayList.get(position).getArrayListUrl().get(1))
                        .placeholder(R.drawable.placeholder_song)
                        .into(((MyViewHolder) holder).imageView3);
                Picasso.get()
                        .load(arrayList.get(position).getArrayListUrl().get(0))
                        .placeholder(R.drawable.placeholder_song)
                        .into(((MyViewHolder) holder).imageView4);
            } else {
                Picasso.get()
                        .load(Uri.parse(arrayList.get(position).getArrayListUrl().get(3)))
                        .placeholder(R.drawable.placeholder_song)
                        .into(((MyViewHolder) holder).imageView1);
                Picasso.get()
                        .load(Uri.parse(arrayList.get(position).getArrayListUrl().get(2)))
                        .placeholder(R.drawable.placeholder_song)
                        .into(((MyViewHolder) holder).imageView2);
                Picasso.get()
                        .load(Uri.parse(arrayList.get(position).getArrayListUrl().get(1)))
                        .placeholder(R.drawable.placeholder_song)
                        .into(((MyViewHolder) holder).imageView3);
                Picasso.get()
                        .load(Uri.parse(arrayList.get(position).getArrayListUrl().get(0)))
                        .placeholder(R.drawable.placeholder_song)
                        .into(((MyViewHolder) holder).imageView4);
            }

            ((MyViewHolder) holder).rl.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, columnWidth));

            ((MyViewHolder) holder).rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListenerPlayList.onClick(holder.getAdapterPosition());
                }
            });

            ((MyViewHolder) holder).imageView_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openOptionPopUp(((MyViewHolder) holder).imageView_more, holder.getAdapterPosition());
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
        }
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayList.get(position) == null) {
            return 1000 + position;
        } else {
            return position;
        }
    }

    public void addAds(UnifiedNativeAd unifiedNativeAd) {
        mNativeAdsAdmob.add(unifiedNativeAd);
        isAdLoaded = true;
        notifyDataSetChanged();
    }

    public void setFBNativeAdManager(NativeAdsManager mNativeAdsManager) {
        this.mNativeAdsManager = mNativeAdsManager;
        isAdLoaded = true;
    }

    public int getRealPos(int pos, ArrayList<ItemArtist> arrayListTemp) {
        return arrayListTemp.indexOf(arrayList.get(pos));
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

    public ItemMyPlayList getItem(int pos) {
        return arrayList.get(pos);
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
                ArrayList<ItemMyPlayList> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    String nameList = filteredArrayList.get(i).getName();
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

            arrayList = (ArrayList<ItemMyPlayList>) results.values;
            notifyDataSetChanged();
        }
    }

    private void openOptionPopUp(ImageView imageView, final int pos) {
        PopupMenu popup = new PopupMenu(context, imageView);
        popup.getMenuInflater().inflate(R.menu.popup_playlist, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.popup_option_playlist:
                        dbHelper.removePlayList(arrayList.get(pos).getId(), isOnline);
                        arrayList.remove(pos);
                        notifyItemRemoved(pos);
                        Toast.makeText(context, context.getString(R.string.remove_playlist), Toast.LENGTH_SHORT).show();
                        if (arrayList.size() == 0) {
                            clickListenerPlayList.onItemZero();
                        }
                        break;
                }
                return true;
            }
        });
        popup.show();
    }
}