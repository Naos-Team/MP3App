package com.naosteam.countrymusic.ringtone.item;

import java.io.Serializable;

/**
 * Created by thivakaran
 */
public class ItemRingtone implements Serializable{

	private String id, cat_id, title, radio_id, url_fm, total_views, total_download, cid, category_name, category_image, category_image_thumb;


	public ItemRingtone(String id, String cat_id, String title, String radio_id, String url_fm, String total_views, String total_download, String cid, String category_name, String category_image, String category_image_thumb) {
		this.id = id;
		this.cat_id = cat_id;
		this.title = title;
		this.radio_id = radio_id;
		this.url_fm = url_fm;
		this.total_views = total_views;
		this.total_download = total_download;
		this.cid = cid;
		this.category_name = category_name;
		this.category_image = category_image;
		this.category_image_thumb = category_image_thumb;
	}



    public String getId() {
		return id;
	}

	public String getCat_id() {
		return cat_id;
	}

	public String getTitle() {
		return title;
	}

	public String getRadio_id() {
		return radio_id;
	}

	public String getUrl_fm() {
		return url_fm;
	}

	public String getTotal_views() {
		return total_views;
	}

	public String getTotal_download() {
		return total_download;
	}

	public String getCid() {
		return cid;
	}

	public String getCategory_name() {
		return category_name;
	}

	public String getCategory_image() {
		return category_image;
	}

	public String getCategory_image_thumb() {
		return category_image_thumb;
	}
}