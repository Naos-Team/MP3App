package com.zxfdwka.bestcountrymusic.ringtone.Load;

import android.os.AsyncTask;

import com.zxfdwka.bestcountrymusic.ringtone.JsonUtils.JsonUtils;
import com.zxfdwka.bestcountrymusic.ringtone.Listener.RingtoneListener;
import com.zxfdwka.bestcountrymusic.ringtone.SharedPref.Setting;
import com.zxfdwka.bestcountrymusic.ringtone.item.ItemRingtone;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;


public class LoadSongs extends AsyncTask<String, String, String> {

    private RingtoneListener ringtoneListener;
    private ArrayList<ItemRingtone> arrayList;
    private RequestBody requestBody;

    public LoadSongs(RingtoneListener ringtoneListener, RequestBody requestBody) {
        this.ringtoneListener = ringtoneListener;
        arrayList = new ArrayList<>();
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        ringtoneListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected  String doInBackground(String... strings)  {
        String json = JsonUtils.okhttpPost(Setting.SERVER_URL, requestBody);

        try {
            JSONObject jOb = new JSONObject(json);
            boolean status = jOb.getString("status").equals("success");

            if (status){
                JSONArray jsonArray = jOb.getJSONArray("list");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject result = jsonArray.getJSONObject(i);

                    String id = result.getString("id");
                    String cat_id = result.getString("cat_id");
                    String title = result.getString("title");
                    String radio_id = result.getString("ringtone_id");
                    String url_fm = result.getString("url");
                    String total_views = result.getString("total_views");
                    String total_download = result.getString("total_download");
                    String cid = result.getString("cid");
                    String category_name = result.getString("category_name");
                    String category_image = result.getString("category_image");
                    String category_image_thumb = result.getString("category_image_thumb");

                    ItemRingtone objItem = new ItemRingtone(id, cat_id, title, radio_id, url_fm, total_views, total_download, cid, category_name, category_image, category_image_thumb);
                    arrayList.add(objItem);

                }
                return "1";
            }
            else{
                String err = jOb.getString("err_message");
                return "0";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        ringtoneListener.onEnd(String.valueOf(s),arrayList);
        super.onPostExecute(s);
    }

}

