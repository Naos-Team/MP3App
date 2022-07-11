package com.zxfdwka.bestcountrymusic.ringtone.Login;

import android.os.AsyncTask;

import com.zxfdwka.bestcountrymusic.ringtone.JsonUtils.JsonUtils;
import com.zxfdwka.bestcountrymusic.ringtone.SharedPref.Setting;

import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.RequestBody;

public class LoadLogin extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private LoginListener loginListener;
    private String user_id="", user_name="", success="0", message = "";

    public LoadLogin(LoginListener loginListener, RequestBody requestBody) {
        this.loginListener = loginListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        loginListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JsonUtils.okhttpPost(Setting.SERVER_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            boolean status = mainJson.getString("status").equals("success");
            if (status){
                JSONArray jsonArray = mainJson.getJSONArray("list");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);
                    user_id = c.getString("user_id");
                    user_name = c.getString("name");
                }
                return "1";
            }
            else{
                String err = mainJson.getString("err_message");
                return "0";
            }


        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        loginListener.onEnd(s, success, message, user_id, user_name);
        super.onPostExecute(s);
    }
}