package com.zxfdwka.bestcountrymusic.ringtone.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zxfdwka.bestcountrymusic.ringtone.item.ItemRingtone;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "ri.db";

    private static String TAG_ID = "id";

    private static String TAG_cat_id = "cat_id";
    private static String TAG_title = "title";
    private static String TAG_radio_id = "radio_id";
    private static String TAG_url = "url";
    private static String TAG_total_views = "total_views";
    private static String TAG_total_download = "total_download";
    private static String TAG_cid = "cid";
    private static String TAG_category_name = "category_name";
    private static String TAG_category_image = "category_image";
    private static String TAG_category_image_thumb = "category_image_thumb";

    private SQLiteDatabase db;
    private final Context context;

    private static final String TABLE_FAV_RINGTONE = "song";

    // Creating table query
    private static final String CREATE_TABLE_FAV = "create table " + TABLE_FAV_RINGTONE + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_cat_id + " TEXT," +
            TAG_title + " TEXT," +
            TAG_radio_id + " TEXT," +
            TAG_url + " TEXT," +
            TAG_total_views + " TEXT," +
            TAG_total_download + " TEXT," +
            TAG_cid + " TEXT," +
            TAG_category_name + " TEXT," +
            TAG_category_image + " TEXT," +
            TAG_category_image_thumb + " TEXT);";


    private String[] columns_song = new String[]{TAG_ID, TAG_cat_id, TAG_title, TAG_radio_id, TAG_url, TAG_total_views,TAG_total_download,
            TAG_cid, TAG_category_name, TAG_category_image, TAG_category_image_thumb};


    public DBHelper(Context context) {
        super(context, DB_NAME, null, 5);
        this.context = context;
        db = getWritableDatabase();
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_FAV);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Boolean addORremoveFav(ItemRingtone itemRingtone) {
        Cursor cursor = db.query(TABLE_FAV_RINGTONE, columns_song, TAG_radio_id + "=" + itemRingtone.getId(), null, null, null, null);
        if(cursor.getCount() == 0) {
            addToFav(itemRingtone);
            return true;
        } else {
            removeFromFav(itemRingtone.getId());
            return false;
        }
    }

    public void addToFav(ItemRingtone itemRingtone) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_ID, itemRingtone.getId());
        contentValues.put(TAG_cat_id, itemRingtone.getCat_id());
        contentValues.put(TAG_title, itemRingtone.getTitle());
        contentValues.put(TAG_radio_id, itemRingtone.getRadio_id());
        contentValues.put(TAG_url, itemRingtone.getUrl_fm());
        contentValues.put(TAG_total_views, itemRingtone.getTotal_views());
        contentValues.put(TAG_total_download, itemRingtone.getTotal_download());
        contentValues.put(TAG_cid, itemRingtone.getCid());
        contentValues.put(TAG_category_name, itemRingtone.getCategory_name());
        contentValues.put(TAG_category_image, itemRingtone.getCategory_image());
        contentValues.put(TAG_category_image_thumb, itemRingtone.getCategory_image_thumb());

        db.insert(TABLE_FAV_RINGTONE, null, contentValues);
    }

    public void removeFromFav(String id) {
        db.delete(TABLE_FAV_RINGTONE, TAG_radio_id + "=" + id, null);
    }


    public Boolean checkFav(String id) {
        Cursor cursor = db.query(TABLE_FAV_RINGTONE, columns_song, TAG_radio_id + "=" + id, null, null, null, null);
        Boolean isFav = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return isFav;
    }

    public ArrayList<ItemRingtone> loadFavData() {
        ArrayList<ItemRingtone> arrayList = new ArrayList<>();
        try {
            Cursor cursor = db.query(TABLE_FAV_RINGTONE, columns_song, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                for (int i = 0; i < cursor.getCount(); i++) {

                    String id = cursor.getString(cursor.getColumnIndex(TAG_ID));
                    String cat_id = cursor.getString(cursor.getColumnIndex(TAG_cat_id));
                    String title = cursor.getString(cursor.getColumnIndex(TAG_title));
                    String radio_id = cursor.getString(cursor.getColumnIndex(TAG_radio_id));
                    String url_fm = cursor.getString(cursor.getColumnIndex(TAG_url));
                    String total_views = cursor.getString(cursor.getColumnIndex(TAG_total_views));
                    String total_download = cursor.getString(cursor.getColumnIndex(TAG_total_download));
                    String cid = cursor.getString(cursor.getColumnIndex(TAG_cid));
                    String category_name = cursor.getString(cursor.getColumnIndex(TAG_category_name));
                    String category_image = cursor.getString(cursor.getColumnIndex(TAG_category_image));
                    String category_image_thumb = cursor.getString(cursor.getColumnIndex(TAG_category_image_thumb));

                    ItemRingtone objItem = new ItemRingtone(id, cat_id, title, radio_id, url_fm, total_views,total_download, cid, category_name, category_image, category_image_thumb);
                    arrayList.add(objItem);

                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return arrayList;
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
