package com.RedAlien.RedAlienShop;

public class ItemCheckoutlist {
    private byte[] image;
    private String title;
    private String brand;
    private String price;
    private int amount;

    public ItemCheckoutlist(byte[] image, String title, String brand, String price, int amount) {
        this.image = image;
        this.title = title;
        this.brand = brand;
        this.price = price;
        this.amount = amount;
    }

    public byte[] getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getBrand() {
        return brand;
    }

    public String getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }
}
