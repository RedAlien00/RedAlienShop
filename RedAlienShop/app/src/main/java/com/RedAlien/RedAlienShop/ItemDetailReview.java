package com.RedAlien.RedAlienShop;

public class ItemDetailReview {
    String name;
    String comment;

    public ItemDetailReview(String name, String comment) {
        this.name = name;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }
}
