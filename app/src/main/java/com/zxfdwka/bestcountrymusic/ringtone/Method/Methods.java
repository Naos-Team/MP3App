package com.zxfdwka.bestcountrymusic.ringtone.Method;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.ringtone.Constant.Constant;
import com.zxfdwka.bestcountrymusic.ringtone.JsonUtils.JsonUtils;
import com.zxfdwka.bestcountrymusic.ringtone.Listener.InterAdListener;
import com.zxfdwka.bestcountrymusic.ringtone.Login.ItemUser;
import com.zxfdwka.bestcountrymusic.ringtone.Login.LoginActivity;
import com.zxfdwka.bestcountrymusic.ringtone.SharedPref.MY_API;
import com.zxfdwka.bestcountrymusic.ringtone.SharedPref.Setting;
import com.zxfdwka.bestcountrymusic.ringtone.SharedPref.SharedPref;
import com.zxfdwka.bestcountrymusic.ringtone.item.ItemRingtone;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class Methods {

    private Context context;
    private InterAdListener interAdListener;

    private InterstitialAd interstitialAd;
    private com.facebook.ads.InterstitialAd interstitialAdFB;

    @SerializedName("song")
    String song_image = "song";

    @SuppressLint("CommitPrefEdits")
    public Methods(Context context) {
        this.context = context;
    }

    public Methods(Context context, InterAdListener interAdListener) {
        this.context = context;
        this.interAdListener = interAdListener;

        loadad();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public void setStatusColor2(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(context.getResources().getColor(R.color.colorAccent));
        }
    }

    public void okhttpViewPost(final ItemRingtone itemRingtone) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {

                String json = JsonUtils.okhttpPost(Setting.SERVER_URL, getAPIRequest(Setting.METHOD_SONGS, 0, "", itemRingtone.getId(), "", "", "", "", "", "","","","","","","","", null));
                return null;
            }
        }.execute();
    }

    public void download(final ItemRingtone itemRingtone) {
        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... strings) {

                String json = JsonUtils.okhttpPost(Setting.SERVER_URL, getAPIRequest(Setting.METHOD_DOWNLOAD_COUNT, 0, "", itemRingtone.getId(), "", "", "", "", "", "","","","","","","","", null));

                return null;
            }
        }.execute();
    }

    private void loadad() {
        if(Setting.getPurchases){
        }else {
            if (Setting.isAdmobInterAd) {
                interstitialAd = new InterstitialAd(context);
                AdRequest adRequest;
                if (ConsentInformation.getInstance(context).getConsentStatus() == ConsentStatus.PERSONALIZED) {
                    adRequest = new AdRequest.Builder()
                            .build();
                } else {
                    Bundle extras = new Bundle();
                    extras.putString("npa", "1");
                    adRequest = new AdRequest.Builder()
                            .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                            .build();
                }
                interstitialAd.setAdUnitId(Setting.ad_inter_id);
                interstitialAd.loadAd(adRequest);
            } else if (Setting.isFBInterAd) {
                interstitialAdFB = new com.facebook.ads.InterstitialAd(context, Setting.fb_ad_inter_id);
                interstitialAdFB.loadAd();
            }
        }

    }


    public void showInter(final int pos, final String type) {
        if(Setting.getPurchases){
            interAdListener.onClick(pos, type);
        }else {
            if (Setting.isAdmobInterAd) {
                Setting.adCount = Setting.adCount + 1;
                if (Setting.adCount % Setting.adShow == 0) {
                    interstitialAd.setAdListener(new com.google.android.gms.ads.AdListener() {
                        @Override
                        public void onAdClosed() {
                            interAdListener.onClick(pos, type);
                            super.onAdClosed();
                        }
                    });
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    } else {
                        interAdListener.onClick(pos, type);
                    }
                    loadad();
                } else {
                    interAdListener.onClick(pos, type);
                }
            } else if (Setting.isFBInterAd) {
                Setting.adCount = Setting.adCount + 1;
                if (Setting.adCount % Setting.adShowFB == 0) {
//                    interstitialAdFB.setAdListener(new InterstitialAdListener() {
//                        @Override
//                        public void onInterstitialDisplayed(com.facebook.ads.Ad ad) {
//
//                        }
//
//                        @Override
//                        public void onInterstitialDismissed(com.facebook.ads.Ad ad) {
//                            interAdListener.onClick(pos, type);
//                        }
//
//                        @Override
//                        public void onError(com.facebook.ads.Ad ad, AdError adError) {
//
//                        }
//
//                        @Override
//                        public void onAdLoaded(com.facebook.ads.Ad ad) {
//
//                        }
//
//                        @Override
//                        public void onAdClicked(com.facebook.ads.Ad ad) {
//
//                        }
//
//                        @Override
//                        public void onLoggingImpression(com.facebook.ads.Ad ad) {
//
//                        }
//                    });
//                    if (interstitialAdFB.isAdLoaded()) {
//                        interstitialAdFB.show();
//                    } else {
//                        interAdListener.onClick(pos, type);
//                    }
//                    loadad();
                } else {
                    interAdListener.onClick(pos, type);
                }
            } else {
                interAdListener.onClick(pos, type);
            }
        }
    }
    private void showPersonalizedAds(LinearLayout linearLayout) {
        if (Setting.isAdmobBannerAd) {
            AdView adView = new AdView(context);
            AdRequest adRequest = new AdRequest.Builder().addTestDevice("0336997DCA346E1612B610471A578EEB").build();
            adView.setAdUnitId(Setting.ad_banner_id);
            adView.setAdSize(AdSize.SMART_BANNER);
            linearLayout.addView(adView);
            adView.loadAd(adRequest);
        } else if (Setting.isFBBannerAd) {
            com.facebook.ads.AdView adView = new com.facebook.ads.AdView(context, Setting.fb_ad_banner_id, com.facebook.ads.AdSize.BANNER_HEIGHT_90);
            adView.loadAd();
            linearLayout.addView(adView);
        }
    }

    private void showNonPersonalizedAds(LinearLayout linearLayout) {
        if (Setting.isAdmobBannerAd) {
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            AdView adView = new AdView(context);
            AdRequest adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .build();
            adView.setAdUnitId(Setting.ad_banner_id);
            adView.setAdSize(AdSize.SMART_BANNER);
            linearLayout.addView(adView);
            adView.loadAd(adRequest);
        } else if (Setting.isFBBannerAd) {
            com.facebook.ads.AdView adView = new com.facebook.ads.AdView(context, Setting.fb_ad_banner_id, com.facebook.ads.AdSize.BANNER_HEIGHT_90);
            adView.loadAd();
            linearLayout.addView(adView);
        }
    }

    public void showBannerAd(LinearLayout linearLayout) {
        if(Setting.getPurchases){
        }else {
            if (isNetworkAvailable() && linearLayout != null) {
                if (ConsentInformation.getInstance(context).getConsentStatus() == ConsentStatus.NON_PERSONALIZED) {
                    showNonPersonalizedAds(linearLayout);
                } else {
                    showPersonalizedAds(linearLayout);
                }
            }
        }
    }


    public void forceRTLIfSupported(Window window) {
        if (Constant.isRTL.equals("true")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
    }


    public void getVerifyDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.ThemeDialog);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                finish();
            }
        });
        alertDialog.show();
    }


    public void clickLogin() {
        if (Setting.isLogged) {
            logout((Activity) context);
            Toast.makeText(context, context.getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("from", "app");
            context.startActivity(intent);
            logout_finish((Activity) context);
        }
    }
    public void logout_finish(Activity activity) {
        activity.finish();
    }

    public void changeAutoLogin(Boolean isAutoLogin) {
        SharedPref sharePref = new SharedPref(context);
        sharePref.setIsAutoLogin(isAutoLogin);
    }

    public void logout(Activity activity) {
        changeAutoLogin(false);
        Setting.isLogged = false;
        Setting.itemUser = new ItemUser("", "", "", "");
        Intent intent1 = new Intent(context, LoginActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.putExtra("from", "");
        context.startActivity(intent1);
        activity.finish();
    }

        public RequestBody getAPIRequest(String method, int page, String Nemosofts_key, String songID, String searchText, String searchType, String catID, String mID, String Name, String istID, String rate, String email, String password, String name, String phone, String userID, String reportMessage, File file) {
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new MY_API());
        jsObj.addProperty("method_name", method);
        jsObj.addProperty("package_name", context.getPackageName());

        switch (method) {

            case Setting.METHOD_LOGIN:
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                break;

            case Setting.METHOD_REGISTER:
                jsObj.addProperty("name", name);
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("phone", phone);
                break;

            case Setting.METHOD_ALL_SONGS:
                jsObj.addProperty("page", String.valueOf(page));
                break;

            case Setting.METHOD_MOST_VIEWED:
                jsObj.addProperty("page", String.valueOf(page));
                break;

            case Setting.METHOD_CAT:
                jsObj.addProperty("page", String.valueOf(page));
                break;

            case Setting.METHOD_SEARCH:
                jsObj.addProperty("page", String.valueOf(page));
                jsObj.addProperty("search_text", searchText);
                break;

            case Setting.METHOD_SONG_BY_CAT:
                jsObj.addProperty("cat_id", catID);
                jsObj.addProperty("page", String.valueOf(page));
                break;

            case Setting.METHOD_DOWNLOAD_COUNT:
                jsObj.addProperty("download_id", songID);
                break;

            case Setting.METHOD_SONGS:
                jsObj.addProperty("songs_id", songID);
                break;

            case Setting.METHOD_USER_BY_SONGS:
                jsObj.addProperty("user_by_id", userID);
                jsObj.addProperty("page", String.valueOf(page));
                break;

//            case Setting.TAG_ROOT:
//                jsObj.addProperty("key_id", Nemosofts_key);
//                break;
        }

        return new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("data", jsObj.toString())
                .build();
    }

    public String getPathImage(Uri uri) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String filePath = "";
                String wholeID = DocumentsContract.getDocumentId(uri);

                // Split at colon, use second item in the array
                String id = wholeID.split(":")[1];

                String[] column = {MediaStore.Images.Media.DATA};

                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";

                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

                int columnIndex = cursor.getColumnIndex(column[0]);

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
                return filePath;
            } else {

                if (uri == null) {
                    return null;
                }
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    int column_index = cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String retunn = cursor.getString(column_index);
                    cursor.close();
                    return retunn;
                }
                // this is our fallback here
                return uri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (uri == null) {
                return null;
            }
            // try to retrieve the image from the media store first
            // this will only work for images selected from gallery
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String returnn = cursor.getString(column_index);
                cursor.close();
                return returnn;
            }
            // this is our fallback here
            return uri.getPath();
        }
    }
}
