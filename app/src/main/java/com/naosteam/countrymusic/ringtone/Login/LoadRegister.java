package com.naosteam.countrymusic.ringtone.Login;

import android.os.AsyncTask;

import com.naosteam.countrymusic.ringtone.JsonUtils.JsonUtils;
import com.naosteam.countrymusic.ringtone.SharedPref.Setting;

import okhttp3.RequestBody;

public class LoadRegister extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private SuccessListener successListener;
    private String success = "0", message = "";

    public LoadRegister(SuccessListener successListener, RequestBody requestBody) {
        this.successListener = successListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        successListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JsonUtils.okhttpPost(Setting.SERVER_URL, requestBody);

            if(json.equals("success")){
                return "1";
            }else {
                return "0";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        successListener.onEnd(s, success, message);
        super.onPostExecute(s);
    }
}