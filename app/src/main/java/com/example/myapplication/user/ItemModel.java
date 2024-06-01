package com.example.myapplication.user;

import java.util.ArrayList;

public class ItemModel {
    private String itemId,userId, finalNameText, finalDescriptionText;
    private ArrayList<String> url;

    public ItemModel(String userId, String finalNameText, String finalDescriptionText, ArrayList<String> url) {
        this.userId = userId;
        this.finalNameText = finalNameText;
        this.finalDescriptionText = finalDescriptionText;
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getFinalNameText() {
        return finalNameText;
    }

    public void setFinalNameText(String finalNameText) {
        this.finalNameText = finalNameText;
    }

    public String getFinalDescriptionText() {
        return finalDescriptionText;
    }

    public void setFinalDescriptionText(String finalDescriptionText) {
        this.finalDescriptionText = finalDescriptionText;
    }

    public ArrayList<String> getUrl() {
        return url;
    }

    public void setUrl(ArrayList<String> url) {
        this.url = url;
    }


    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
