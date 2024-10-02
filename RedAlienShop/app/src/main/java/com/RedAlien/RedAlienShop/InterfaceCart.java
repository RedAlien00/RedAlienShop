package com.RedAlien.RedAlienShop;

import java.util.ArrayList;

public interface InterfaceCart {

    // hashMap으로 저장하다가, 구매하기 누를 때 `
    public int updateCartlistAmount(String title, String brand, int amount);

//    public int getCartlistAmount(String title, String brand );
//
//    public void removeHashMap(int eachPrice);
//
//    public void updateHashMap(int eachPrice, int amount);

    public void updateCartlistTotalPrice();

    public void removeCartlistProduct(String title, String brand);

    public void checkDataEmptyAndSetEmptyView(ArrayList<ItemCartlist> arrayList);
}
