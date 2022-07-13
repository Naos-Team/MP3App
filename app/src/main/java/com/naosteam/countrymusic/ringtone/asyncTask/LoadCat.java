package com.naosteam.countrymusic.ringtone.asyncTask;

import android.os.AsyncTask;

import com.naosteam.countrymusic.ringtone.JsonUtils.JsonUtils;
import com.naosteam.countrymusic.ringtone.SharedPref.Setting;
import com.naosteam.countrymusic.ringtone.interfaces.CatListener;
import com.naosteam.countrymusic.ringtone.item.ListltemCategory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import okhttp3.RequestBody;


public class LoadCat extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private CatListener catListener;
    private ArrayList<ListltemCategory> arrayList = new ArrayList<>();
    private String verifyStatus = "0", message = "";

    public LoadCat(CatListener catListener, RequestBody requestBody) {
        this.catListener = catListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        catListener.onStart();
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
                    JSONObject obj = jsonArray.getJSONObject(i);

                        String cid = obj.getString(Setting.TAG_CID);
                        String category_name = obj.getString(Setting.TAG_CAT_NAME);
                        String category_image = obj.getString(Setting.TAG_CAT_IMAGE);
                        String category_image_thumb = obj.getString(Setting.TAG_CAT_IMAGE);

                        ListltemCategory objItem = new ListltemCategory(cid, category_name, category_image,category_image_thumb);
                        arrayList.add(objItem);
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
        catListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}