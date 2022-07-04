package com.zxfdwka.bestcountrymusic.mp3.asyncTask;

import android.os.AsyncTask;

import com.zxfdwka.bestcountrymusic.mp3.interfaces.GenresListener;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemGenres;
import com.zxfdwka.bestcountrymusic.mp3.utils.Constant;
import com.zxfdwka.bestcountrymusic.mp3.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadGenres extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private GenresListener genresListener;
    private ArrayList<ItemGenres> arrayList = new ArrayList<>();
    private String verifyStatus = "0", message = "";
    private int total_records = -1;

    public LoadGenres(GenresListener genresListener, RequestBody requestBody) {
        this.genresListener = genresListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        genresListener.onStart();
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
                    String id = obj.getString(Constant.TAG_GENRES_ID);
                    String name = obj.getString(Constant.TAG_GENRES_NAME);
                    String image = obj.getString(Constant.TAG_GENRES_IMAGE);

                    ItemGenres objItem = new ItemGenres(id, name, image);
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
        genresListener.onEnd(s, verifyStatus, message, arrayList, total_records);
        super.onPostExecute(s);
    }
}