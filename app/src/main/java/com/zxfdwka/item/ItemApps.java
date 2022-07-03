package com.zxfdwka.item;

import java.io.Serializable;

public class ItemApps implements Serializable{

	private String id, name, url, image, thumb;

	public ItemApps(String id, String name, String url, String image, String thumb) {
		this.id = id;
		this.name = name;
		this.image = image;
		this.thumb = thumb;
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getImage() {
		return image;
	}

	public String getURL() {
		return url;
	}

	public String getThumb() {
		return thumb;
	}
}
