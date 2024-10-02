package com.RedAlien.RedAlienShop;

public class ItemCartlist {
    private static final String TAG = "ItemCartlist";

    private byte[] cartlist_image;
    private String cartlist_title;
    private String cartlist_brand;
    private String cartlist_eachPrice;
    private int cartlist_amount;

    public ItemCartlist(byte[] cartlist_image, String cartlist_title, String cartlist_brand, String cartlist_eachPrice, int cartlist_amount) {
        this.cartlist_image = cartlist_image;
        this.cartlist_title = cartlist_title;
        this.cartlist_brand = cartlist_brand;
        this.cartlist_eachPrice = cartlist_eachPrice;
        this.cartlist_amount = cartlist_amount;
    }

    public byte[] getCartlist_image() {
        return cartlist_image;
    }

    public String getCartlist_title() {
        return cartlist_title;
    }

    public String getCartlist_brand() {
        return cartlist_brand;
    }

    public String getCartlist_eachPrice() {
        return cartlist_eachPrice;
    }
    public int getCartlist_amount() {
        return cartlist_amount;
    }
}
