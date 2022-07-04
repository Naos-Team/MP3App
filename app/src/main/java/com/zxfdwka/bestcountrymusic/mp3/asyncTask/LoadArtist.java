package com.zxfdwka.bestcountrymusic.mp3.asyncTask;

import android.os.AsyncTask;

import com.zxfdwka.bestcountrymusic.mp3.interfaces.ArtistListener;
import com.zxfdwka.bestcountrymusic.mp3.item.ItemArtist;
import com.zxfdwka.bestcountrymusic.mp3.utils.Constant;
import com.zxfdwka.bestcountrymusic.mp3.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadArtist extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private ArtistListener artistListener;
    private ArrayList<ItemArtist> arrayList = new ArrayList<>();
    private String verifyStatus = "0", message = "";
    private int total_records = -1;

    public LoadArtist(ArtistListener artistListener, RequestBody requestBody) {
        this.artistListener = artistListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        artistListener.onStart();
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
                    String id = objJson.getString(Constant.TAG_ID);
                    String name = objJson.getString(Constant.TAG_ARTIST_NAME);
                    String image = objJson.getString(Constant.TAG_ARTIST_IMAGE);
                    String thumb = objJson.getString(Constant.TAG_ARTIST_THUMB);

                    ItemArtist objItem = new ItemArtist(id, name, image, thumb);
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
        artistListener.onEnd(s, verifyStatus, message, arrayList, total_records);
        super.onPostExecute(s);
    }
}