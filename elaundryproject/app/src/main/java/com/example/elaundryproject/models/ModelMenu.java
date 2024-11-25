package com.example.elaundryproject.models;

public class ModelMenu {
    private String tvTitle; // Nama menu
    private int iconResource; // ID drawable

    public ModelMenu(String tvTitle, int iconResource) {
        this.tvTitle = tvTitle;
        this.iconResource = iconResource;
    }

    public String getTvTitle() {
        return tvTitle;
    }

    public int getIconResource() {
        return iconResource;
    }
}

