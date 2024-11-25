package com.example.elaundryproject.models;

public class ModelMenu {

    private String tvTitle;
    private int imageRes;

    public ModelMenu(String tvTitle, int imageRes) {
        this.tvTitle = tvTitle;
        this.imageRes = imageRes;
    }

    public String getTvTitle() {
        return tvTitle;
    }

    public void setTvTitle(String tvTitle) {
        this.tvTitle = tvTitle;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }
}
