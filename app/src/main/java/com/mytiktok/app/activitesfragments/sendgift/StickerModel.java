package com.mytiktok.app.activitesfragments.sendgift;

import com.mytiktok.app.Constants;
import com.mytiktok.app.simpleclasses.Variables;

import java.io.Serializable;

public class StickerModel implements Serializable {
    public String id,name,coins="0" ;
    private String image;
    public boolean isSelected;
    public int count;

    public String getImage() {
        if (!image.contains(Variables.http)) {
            image = Constants.BASE_URL + image;
        }
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
