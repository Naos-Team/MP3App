package com.zxfdwka.bestcountrymusic.ringtone.asyncTask;

import android.content.Context;
import android.os.AsyncTask;

import com.zxfdwka.bestcountrymusic.ringtone.JsonUtils.JsonUtils;
import com.zxfdwka.bestcountrymusic.ringtone.Method.Methods;
import com.zxfdwka.bestcountrymusic.ringtone.SharedPref.Setting;
import com.zxfdwka.bestcountrymusic.ringtone.interfaces.AboutListener;

import org.json.JSONArray;
import org.json.JSONObject;
;


public class LoadAbout extends AsyncTask<String, String, String> {

    private Methods methods;
    private AboutListener aboutListener;
    private String message = "", verifyStatus = "0";

    public LoadAbout(Context context, AboutListener aboutListener) {
        this.aboutListener = aboutListener;
        methods = new Methods(context);
    }

    @Override
    protected void onPreExecute() {
        aboutListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JsonUtils.okhttpPost(Setting.SERVER_URL, methods.getAPIRequest(Setting.METHOD_APP_DETAILS, 0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", null));
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has(Setting.TAG_ROOT)) {
                JSONArray jsonArray = jsonObject.getJSONArray(Setting.TAG_ROOT);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);

                    if (!c.has(Setting.TAG_SUCCESS)) {
                        Setting.ad_banner_id = c.getString("banner_ad_id");
                        Setting.ad_inter_id = c.getString("interstital_ad_id");
                        Setting.isAdmobBannerAd = Boolean.parseBoolean(c.getString("banner_ad"));
                        Setting.isAdmobInterAd = Boolean.parseBoolean(c.getString("interstital_ad"));
                        Setting.ad_publisher_id = c.getString("publisher_id");

                        if(!c.getString("interstital_ad_click").equals("")) {
                            Setting.adShow = Integer.parseInt(c.getString("interstital_ad_click"));
                        }

                        Setting.fb_ad_banner_id = c.getString("facebook_banner_ad_id");
                        Setting.fb_ad_inter_id = c.getString("facebook_interstital_ad_id");
                        Setting.isFBBannerAd = Boolean.parseBoolean(c.getString("facebook_banner_ad"));
                        Setting.isFBInterAd = Boolean.parseBoolean(c.getString("facebook_interstital_ad"));

                        if(!c.getString("facebook_interstital_ad_click").equals("")) {
                            Setting.adShowFB = Integer.parseInt(c.getString("facebook_interstital_ad_click"));
                        }

                        Setting.company = c.getString("company");
                        Setting.email = c.getString("email");
                        Setting.website = c.getString("website");
                        Setting.contact = c.getString("contact");

                        Setting.purchase_code = c.getString("purchase_code");
                        Setting.nemosofts_key = c.getString("nemosofts_key");

                        Setting.SUBSCRIPTION_ID = c.getString("subscription_id");
                        Setting.MERCHANT_KEY =  c.getString("merchant_key");
                        if(!c.getString("subscription_days").equals("")) {
                            Setting.SUBSCRIPTION_DURATION = Integer.parseInt(c.getString("subscription_days"));
                        }
                    } else {
                        verifyStatus = c.getString(Setting.TAG_SUCCESS);
                        message = c.getString(Setting.TAG_MSG);
                    }
                }
            }
            return "1";
        } catch (Exception ee) {
            ee.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        aboutListener.onEnd(s, verifyStatus, message);
        super.onPostExecute(s);
    }
}