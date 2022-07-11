package com.zxfdwka.bestcountrymusic.radio.asyncTasks;

import android.os.AsyncTask;
import com.zxfdwka.bestcountrymusic.radio.interfaces.SuccessListener;
import com.zxfdwka.bestcountrymusic.radio.utils.Constants;
import com.zxfdwka.bestcountrymusic.radio.utils.JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadReport extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private SuccessListener successListener;
    private String success = "0", message = "";

    public LoadReport(SuccessListener successListener, RequestBody requestBody) {
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
            String json = JSONParser.okhttpPost(Constants.SERVER_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Constants.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);

                success = c.getString(Constants.TAG_SUCCESS);
                message = c.getString(Constants.TAG_MSG);
            }
            return "1";
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