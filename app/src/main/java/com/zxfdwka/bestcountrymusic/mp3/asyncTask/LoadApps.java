package com.zxfdwka.bestcountrymusic.mp3.asyncTask;

import android.os.AsyncTask;

import com.zxfdwka.bestcountrymusic.mp3.interfaces.AppsListener;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemApps;
import com.zxfdwka.bestcountrymusic.mp3.utils.Constant;
import com.zxfdwka.bestcountrymusic.mp3.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadApps extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private AppsListener appsListener;
    private ArrayList<ItemApps> arrayList = new ArrayList<>();
    private String verifyStatus = "0", message = "";
    private int total_records = -1;

    public LoadApps(AppsListener appsListener, RequestBody requestBody) {
        this.appsListener = appsListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        appsListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JsonUtils.okhttpPost(Constant.SERVER_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = jsonArray.getJSONObject(i);

                if(objJson.has("total_records")) {
                    total_records = Integer.parseInt(objJson.getString("total_records"));
                }

                if (!objJson.has(Constant.TAG_SUCCESS)) {
                    String id = objJson.getString(Constant.TAG_APP_ID);
                    String name = objJson.getString(Constant.TAG_APP_TITLE);
                    String image = objJson.getString(Constant.TAG_APP_IMAGE);
                    String thumb = objJson.getString(Constant.TAG_APP_THUMB);
                    String url = objJson.getString(Constant.TAG_APP_URL);

                    ItemApps objItem = new ItemApps(id, name, url, image, thumb);
                    arrayList.add(objItem);
                } else {
                    verifyStatus = objJson.getString(Constant.TAG_SUCCESS);
                    message = objJson.getString(Constant.TAG_MSG);
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
        appsListener.onEnd(s, verifyStatus, message, arrayList, total_records);
        super.onPostExecute(s);
    }
}