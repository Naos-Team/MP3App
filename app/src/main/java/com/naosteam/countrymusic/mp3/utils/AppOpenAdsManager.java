package com.naosteam.countrymusic.mp3.utils;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.naosteam.countrymusic.mp3.activity.SplashActivity;


public class AppOpenAdsManager {

    private static AppOpenAd mAppOpenAd;

    private final Context context;
    private final OpenAdsListener listener;
    private SharedPref sharedPref;

    @RequiresApi(api = Build.VERSION_CODES.P)
    public AppOpenAdsManager(Context context, OpenAdsListener listener) {
        this.context = context;
        this.listener = listener;

        sharedPref = new SharedPref(context);
        fetchAd();

    }

    public void fetchAd() {
        if(isAdAvailable()){
            return;
        }

        AdRequest request = getAdRequest();
        AppOpenAd.AppOpenAdLoadCallback loadCallback = new AppOpenAd.AppOpenAdLoadCallback() {

            @Override
            public void onAppOpenAdLoaded(AppOpenAd appOpenAd) {
                mAppOpenAd = appOpenAd;
                super.onAppOpenAdLoaded(appOpenAd);
            }

            @Override
            public void onAppOpenAdFailedToLoad(LoadAdError loadAdError) {
                super.onAppOpenAdFailedToLoad(loadAdError);
            }
        };
        AppOpenAd.load(
                context, "ca-app-pub-3940256099942544/3419835294", request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    };

    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    public boolean isAdAvailable() {
        if(mAppOpenAd != null){
            return true;
        }else{
            return false;
        }
    }

    public void showAdIfAvailable(){

        boolean isPremium = sharedPref.getIsPremium();

        if(isAdAvailable() && !isPremium){
            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback(){
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            mAppOpenAd = null;
                            fetchAd();
                            listener.onClick();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                        }
                    };

            mAppOpenAd.show((SplashActivity)context, fullScreenContentCallback);
        }else{
            fetchAd();
            listener.onClick();
        }
    }

    public interface OpenAdsListener {
        void onClick();
    }


}