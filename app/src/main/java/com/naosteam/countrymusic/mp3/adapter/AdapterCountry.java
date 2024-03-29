package com.naosteam.countrymusic.mp3.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.naosteam.countrymusic.R;
import com.naosteam.countrymusic.mp3.item.ItemCountry;
import com.naosteam.countrymusic.mp3.utils.Constant;
import com.naosteam.countrymusic.mp3.utils.Methods;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdsManager;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.naosteam.countrymusic.radio.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class AdapterCountry extends RecyclerView.Adapter {

    private Context context;
    private ArrayList<Object> arrayList;
    private ArrayList<Object> filteredArrayList;
    private NameFilter filter;
    private int columnWidth = 0;

    private final int VIEW_PROG = -1;

    private Boolean isAdLoaded = false;
    private List<UnifiedNativeAd> mNativeAdsAdmob = new ArrayList<>();
    private NativeAdsManager mNativeAdsManager;
    private ArrayList<NativeAd> mNativeAdsFB = new ArrayList<>();

    public AdapterCountry(Context context, ArrayList<Object> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        this.filteredArrayList = arrayList;
        Methods methods = new Methods(context);
        columnWidth = methods.getColumnWidth(3, 20);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RoundedImageView imageView;
        CardView cardView;
        LinearLayout ll;
        View vieww;

        MyViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.tv_cat);
            imageView = view.findViewById(R.id.iv_cat);
            cardView = view.findViewById(R.id.cv_cat);
            ll = view.findViewById(R.id.ll);
            vieww = view.findViewById(R.id.view_cat);
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

    public static class BannerAdsViewHolder extends RecyclerView.ViewHolder{
        public BannerAdsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == Constants.ITEM_BANNER_AD){
            View bannerAdView = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_ad_containter, parent, false);
            return new BannerAdsViewHolder(bannerAdView);
        }else if (viewType == VIEW_PROG) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_progressbar, parent, false);
            return new ProgressViewHolder(v);
        } else if (viewType >= 1000) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ads, parent, false);
            return new ADViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cat, parent, false);
            return new MyViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof MyViewHolder) {

            ItemCountry itemCountry = (ItemCountry) arrayList.get(position);

            ((MyViewHolder) holder).vieww.setLayoutParams(new FrameLayout.LayoutParams(columnWidth, columnWidth));
            ((MyViewHolder) holder).imageView.setLayoutParams(new FrameLayout.LayoutParams(columnWidth, columnWidth));
            ((MyViewHolder) holder).cardView.setLayoutParams(new LinearLayout.LayoutParams(columnWidth, columnWidth));
            ((MyViewHolder) holder).cardView.setRadius(columnWidth / 2);
            ((MyViewHolder) holder).imageView.setCornerRadius(columnWidth / 2);
            ((MyViewHolder) holder).textView.setText(itemCountry.getName());
            ((MyViewHolder) holder).imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.get()
                    .load(itemCountry.getImage())
                    .placeholder(R.drawable.placeholder_artist)
                    .into(((MyViewHolder) holder).imageView);
        }else if(holder instanceof BannerAdsViewHolder) {
            BannerAdsViewHolder adsViewHolder = (BannerAdsViewHolder) holder;
            AdView adView = (AdView) arrayList.get(position);
            ViewGroup adCardView = (ViewGroup) adsViewHolder.itemView;

            if(adCardView.getChildCount()>0){
                adCardView.removeAllViews();
            }
            adCardView.addView(adView);
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
            if (getItemCount() < 10) {
                ProgressViewHolder.progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    public void hideHeader() {
        try {
            ProgressViewHolder.progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isHeader(int position) {
        return position == arrayList.size();
    }

    @Override
    public int getItemCount() {
        if (arrayList != null) {
            return arrayList.size();
        } else {
            return 0;
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

    public int getRealPos(int pos, ArrayList<ItemCountry> arrayListTemp) {
        return arrayListTemp.indexOf(arrayList.get(pos));
    }

    @Override
    public int getItemViewType(int position) {
        if(arrayList.get(position) instanceof AdView){
            return Constants.ITEM_BANNER_AD;
        }else if (isHeader(position)) {
            return VIEW_PROG;
        } else if (arrayList.get(position) == null) {
            return 1000 + position;
        } else {
            return position;
        }
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

    public ItemCountry getItem(int pos) {
        if(arrayList.get(pos) instanceof ItemCountry){
            return (ItemCountry) arrayList.get(pos);
        }else{
            return null;
        }

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
                ArrayList<ItemCountry> filteredItems = new ArrayList<>();

                for (int i = 0, l = filteredArrayList.size(); i < l; i++) {
                    if (filteredArrayList.get(i) != null) {
                        String nameList = ((ItemCountry) filteredArrayList.get(i)).getName();
                        if (nameList.toLowerCase().contains(constraint))
                            filteredItems.add((ItemCountry) filteredArrayList.get(i));
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

            arrayList = (ArrayList<Object>) results.values;
            notifyDataSetChanged();
        }
    }
}