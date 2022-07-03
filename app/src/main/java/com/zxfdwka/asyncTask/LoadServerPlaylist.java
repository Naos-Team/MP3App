package com.zxfdwka.asyncTask;

import android.os.AsyncTask;

import com.zxfdwka.interfaces.ServerPlaylistListener;
import com.zxfdwka.item.ItemServerPlayList;
import com.zxfdwka.utils.Constant;
import com.zxfdwka.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadServerPlaylist extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private ServerPlaylistListener serverPlaylistListener;
    private ArrayList<ItemServerPlayList> arrayList = new ArrayList<>();
    private String verifyStatus = "0", message = "";
    private int total_records = -1;

    public LoadServerPlaylist(ServerPlaylistListener serverPlaylistListener, RequestBody requestBody) {
        this.serverPlaylistListener = serverPlaylistListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        serverPlaylistListener.onStart();
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
                    String id = objJson.getString(Constant.TAG_PID);
                    String name = objJson.getString(Constant.TAG_PLAYLIST_NAME);
                    String image = objJson.getString(Constant.TAG_PLAYLIST_IMAGE);
                    String thumb = objJson.getString(Constant.TAG_PLAYLIST_THUMB);

                    ItemServerPlayList objItem = new ItemServerPlayList(id, name, image, thumb);
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
        serverPlaylistListener.onEnd(s, verifyStatus, message, arrayList, total_records);
        super.onPostExecute(s);
    }
}