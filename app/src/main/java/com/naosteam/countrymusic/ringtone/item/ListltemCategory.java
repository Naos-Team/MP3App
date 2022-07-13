package com.naosteam.countrymusic.ringtone.item;

public class ListltemCategory {

    private String cid;
    private String category_name;
    private String category_image;
    private String category_image_thumb;

    public ListltemCategory(String cid, String category_name, String category_image, String category_image_thumb) {
        this.cid = cid;
        this.category_name = category_name;
        this.category_image = category_image;
        this.category_image_thumb = category_image_thumb;
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
