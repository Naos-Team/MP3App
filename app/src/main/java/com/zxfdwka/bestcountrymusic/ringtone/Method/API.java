package com.zxfdwka.bestcountrymusic.ringtone.Method;

import android.app.Activity;
import android.util.Base64;

import com.google.gson.annotations.SerializedName;


public class API {

    @SerializedName("package_name")
    private String package_name;

    public API(Activity activity) {
        package_name = activity.getApplication().getPackageName();
    }

    public static String toBase64(String input) {
        byte[] encodeValue = Base64.encode(input.getBytes(), Base64.DEFAULT);
        return new String(encodeValue);
    }

}
