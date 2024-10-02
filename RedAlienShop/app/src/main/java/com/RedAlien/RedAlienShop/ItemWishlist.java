package com.RedAlien.RedAlienShop;

public class ItemWishlist {
    private String str_position;
    private byte[] image;
    private String brand;
    private String title;
    private String price;
    private String comments_count;

    public ItemWishlist(String str_position, byte[] image, String brand, String title, String price, String comments_count) {
        this.str_position = str_position;
        this.image = image;
        this.brand = brand;
        this.title = title;
        this.price = price;
        this.comments_count = comments_count;
    }

    public String getStr_position() {
        return str_position;
    }

    public void setStr_position(String str_position) {
        this.str_position = str_position;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getComments_count() {
        return comments_count;
    }

    public void setComments_count(String comments_count) {
        this.comments_count = comments_count;
    }
}
