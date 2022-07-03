package com.zxfdwka.item;

import java.io.Serializable;

public class ItemGenres implements Serializable{

	private String id, name, image;

	public ItemGenres(String id, String name, String image) {
		this.id = id;
		this.name = name;
		this.image = image;
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
}
