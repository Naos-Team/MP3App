package com.zxfdwka.bestcountrymusic.mp3.asyncTask;

import android.os.AsyncTask;

import com.zxfdwka.bestcountrymusic.mp3.interfaces.CountryListener;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemCountry;
import com.zxfdwka.bestcountrymusic.mp3.utils.Constant;
import com.zxfdwka.bestcountrymusic.mp3.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadCountry extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private CountryListener countryListener;
    private ArrayList<ItemCountry> arrayList = new ArrayList<>();
    private String verifyStatus = "0", message = "";
    private int total_records = -1;

    public LoadCountry(CountryListener countryListener, RequestBody requestBody) {
        this.countryListener = countryListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        countryListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JsonUtils.okhttpPost(Constant.SERVER_URL, requestBody);

            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                if(obj.has("total_records")) {
                    total_records = Integer.parseInt(obj.getString("total_records"));
                }

                if (!obj.has(Constant.TAG_SUCCESS)) {
                    String id = obj.getString(Constant.TAG_COUNTRY_ID);
                    String name = obj.getString(Constant.TAG_COUNTRY_NAME);
                    String image = obj.getString(Constant.TAG_COUNTRY_IMAGE);

                    ItemCountry objItem = new ItemCountry(id, name, image);
                    arrayList.add(objItem);
                } else {
                    verifyStatus = obj.getString(Constant.TAG_SUCCESS);
                    message = obj.getString(Constant.TAG_MSG);
                }
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        countryListener.onEnd(s, verifyStatus, message, arrayList, total_records);
        super.onPostExecute(s);
    }
}