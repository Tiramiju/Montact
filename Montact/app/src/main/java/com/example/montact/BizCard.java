package com.example.montact;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class BizCard {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String imgName;
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }
}
