package com.zxfdwka.bestcountrymusic.ringtone.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.zxfdwka.bestcountrymusic.R;
import com.zxfdwka.bestcountrymusic.ringtone.Constant.Constant;
import com.zxfdwka.bestcountrymusic.ringtone.Method.API;
import com.zxfdwka.bestcountrymusic.ringtone.SharedPref.Setting;
import com.zxfdwka.bestcountrymusic.ringtone.SharedPref.SharedPref;
import com.zxfdwka.bestcountrymusic.ringtone.Utils.DemoVolleyMultipartRequest;
import com.zxfdwka.bestcountrymusic.ringtone.Utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class UploadRingtoneActivity extends AppCompatActivity {

    public static final int REQUEST_GALLERY = 1234;
    ArrayList<String> categorieslist = new ArrayList<>();
    ArrayAdapter aa3;
    Spinner spin2;
    ProgressDialog dialog;
    Button submitbtn;
    Button browsebtn;
    TextView uripathtv;
    Uri FilePath;
    EditText tvimagename;
    RelativeLayout relativelayout;

    SeekBar seek_bar;
    ImageView image_play;
    TextView ringtone_name, user;

    private Handler mHandler;
    private Runnable mRunnable;

    Boolean selected = false;
    private MediaPlayer mPlayer;
    SharedPref savePref;
    Toolbar toolbar2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_ringtone);

        toolbar2 = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar2);

        toolbar2.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        savePref = new SharedPref(this);
        spin2 = (Spinner) findViewById(R.id.spinner2list);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Setting.Dark_Mode){
                dialog = new ProgressDialog(UploadRingtoneActivity.this, R.style.ThemeDialog2);
            }else {
                dialog = new ProgressDialog(UploadRingtoneActivity.this, R.style.ThemeDialog);
            }
        } else {
            dialog = new ProgressDialog(UploadRingtoneActivity.this);
        }
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);


        relativelayout = findViewById(R.id.relativelayout);

        seek_bar = findViewById(R.id.seek_bar);
        image_play = findViewById(R.id.image_play);
        ringtone_name = findViewById(R.id.ringtone_name);

        user = findViewById(R.id.user);
        if (Setting.isLogged) {
            user.setText("Update By : "+Setting.itemUser.getName());
        }else{
            user.setText("");
        }

        seek_bar.setClickable(false);
        seek_bar.setProgress(0);
        seek_bar.setMax(0);
        seek_bar.setPadding(0, 0, 0, 0);
        seek_bar.setClickable(false);
        seek_bar.setProgress(0);
        seek_bar.setMax(0);
        seek_bar.setPadding(0, 0, 0, 0);
        seek_bar.setClickable(false);
        seek_bar.setEnabled(false);

        submitbtn = findViewById(R.id.submitbtn);

        uripathtv = findViewById(R.id.uripathtv);
        browsebtn = findViewById(R.id.browsebtn);
        tvimagename = findViewById(R.id.tvimagename);

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected) {
                    if (validation()) {
                        FileUpload();
                    }
                }
            }
        });

        browsebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("audio/mpeg");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Audio File"), REQUEST_GALLERY);
            }
        });

        categorieslist.add("Select a Category");


        if (this != null) {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(this));
            jsObj.addProperty("method_name", "Catrgory");
            params.put("data", API.toBase64(jsObj.toString()));
            client.post(Constant.Saver_Url+"upload_api.php?cat_list", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    if (this != null) {

                        Log.d("Response", new String(responseBody));
                        String res = new String(responseBody);

                        try {
                            JSONObject jsonObject = new JSONObject(res);

                            JSONArray array= jsonObject.getJSONArray("nemosofts");

                            for (int i = 0; i<array.length(); i++) {
                                JSONObject thiva = array.getJSONObject(i);

                                String cat_id = thiva.getInt("cid") + "";
                                String cat_name = thiva.getString("category_name");
                                String category_image = thiva.getString("category_image");
                                String ringtone_cat_image = thiva.getString("category_image_thumb");

                                String string = cat_name + " - " + cat_id;
                                categorieslist.add(string);

                                aa3 = new ArrayAdapter(UploadRingtoneActivity.this, android.R.layout.simple_spinner_item, categorieslist);
                                aa3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spin2.setAdapter(aa3);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                }
            });

        }
        mHandler = new Handler();
        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mPlayer != null && b) {

                    mPlayer.seekTo(i * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        image_play.setTag("notclicked");

        image_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (image_play.getTag().equals("notclicked")) {
                    image_play.setImageResource(R.drawable.pause);

                    Log.e("statusofbtn", "clicked");
                    image_play.setTag("clicked");

                    // If media player another instance already running then stop it first
                    stopPlaying();

                    // Initialize media player
                    mPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(String.valueOf(FilePath)));

                    // Start the media player
                    mPlayer.start();
                    Toast.makeText(getApplicationContext(), "Media Player is playing.", Toast.LENGTH_SHORT).show();

                    // Get the current audio stats
                    getAudioStats();
                    // Initialize the seek bar
                    initializeSeekBar();

                } else if (image_play.getTag().equals("clicked")) {
                    image_play.setImageResource(R.drawable.play);
                    Log.e("statusofbtn", "notclicked");
                    image_play.setTag("notclicked");

                    stopPlaying();

                }
            }
        });
    }



    private boolean validation() {
        if (tvimagename.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(getApplicationContext(), "Please Enter Name", Toast.LENGTH_SHORT).show();
            return false;
        }else if (Setting.isLogged == false) {
            Toast.makeText(getApplicationContext(), "Please Login", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY && data != null && data.getData() != null) {
            Uri imguri = data.getData();

            FilePath = imguri;

            uripathtv.setText("PATH : " + imguri);
            selected = true;
            relativelayout.setVisibility(View.VISIBLE);

            String fileName;
            if (FilePath.getScheme().equals("file")) {
                fileName = FilePath.getLastPathSegment();
            } else {
                Cursor cursor = null;
                try {
                    cursor = getContentResolver().query(FilePath, new String[]{
                            MediaStore.Images.ImageColumns.DISPLAY_NAME
                    }, null, null, null);

                    if (cursor != null && cursor.moveToFirst()) {
                        fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                        Log.d("ahjscbnx", "name is " + fileName);
                        ringtone_name.setText(fileName);

                    }
                } finally {

                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        }
    }


    protected void stopPlaying() {
        // If media player is not null then try to stop it
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
            Toast.makeText(this, "Stop playing.", Toast.LENGTH_SHORT).show();
            if (mHandler != null) {
                mHandler.removeCallbacks(mRunnable);
            }
        }
    }



    protected void getAudioStats() {
        int duration = mPlayer.getDuration() / 1000; // In milliseconds
        int due = (mPlayer.getDuration() - mPlayer.getCurrentPosition()) / 1000;
        int pass = duration - due;
    }


    protected void initializeSeekBar() {
        seek_bar.setMax(mPlayer.getDuration() / 1000);

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mPlayer != null) {
                    int mCurrentPosition = mPlayer.getCurrentPosition() / 1000; // In milliseconds
                    seek_bar.setProgress(mCurrentPosition);
                    getAudioStats();
                }
                mHandler.postDelayed(mRunnable, 1000);
            }
        };
        mHandler.postDelayed(mRunnable, 1000);
    }

    public void FileUpload() {

        dialog.show();

        String spinnerstr = spin2.getSelectedItem().toString();
        String[] split = spinnerstr.split("-");
        String part1 = split[0];
        String part2 = split[1];

        part2 = part2.replace(" ", "");
        final String finalPart = part2;
        DemoVolleyMultipartRequest multipartRequest = new DemoVolleyMultipartRequest(Request.Method.POST, Constant.Saver_Url + "upload_api.php?ringtone_upload", new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                try {
                    String data = new String(response.data);
                    JSONObject json = new JSONObject(data);

                    JSONArray array = json.getJSONArray("nemosofts");
                    JSONObject object = array.getJSONObject(0);
                    String msg = object.getString("msg");

                    Toast.makeText(UploadRingtoneActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
                    Log.e("ressponse", json.toString());

                    dialog.dismiss();

                    onBackPressed();

                } catch (Exception ex) {
                    Log.e("Error", ex.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UploadRingtoneActivity.this, "", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("cat_id", finalPart);
                parameters.put("user_id", Setting.itemUser.getId());
                parameters.put("rname", tvimagename.getText().toString());
                return parameters;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> imgData = new HashMap<>();
                byte[] Imagedata = new byte[0];
                try {
                    InputStream inputStream = getContentResolver().openInputStream(FilePath);

                    Imagedata = readBytes(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                long imagename = System.currentTimeMillis();
                imgData.put("ringtone", new DataPart(imagename + ".mp3", Imagedata, "audio/mpeg"));
                return imgData;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(UploadRingtoneActivity.this).addToRequestQueue(multipartRequest);
    }


    public byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
