package com.RedAlien.RedAlienShop.Helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "shop.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // image, brand, title, pricec, commnets, comments_count
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE user(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username VARCHAR(30), " +
                "password VARCHAR(15), " +
                "points INTEGER );");

        db.execSQL("CREATE TABLE wishlist(" +
                "str_position VARCHAR(3) PRIMARY KEY, " +
                "image BLOB, " +
                "brand VARCHAR(20), " +
                "title VARCHAR(30), " +
                "price VARCHAR(8), " +
                "comments_counts VARCHAR(3) );");

        db.execSQL("CREATE TABLE basket(" +
                "image BLOB," +
                "brand VARCHAR(20)," +
                "title VARCHAR(30)," +
                "price VARCHAR(8)," +
                "amount INTEGER DEFAULT 1"+
                ");");

        db.execSQL("CREATE TABLE card(" +
                "id INTEGER," +
                "username VARCHAR(30), " +
                "cardnumber VARCHAR(18)," +
                "year VARCHAR(5)," +
                "cvc VARCHAR(5)," +
                "password VARCHAR(4)," +
                "FOREIGN KEY(id) REFERENCES user(\"id\")," +
                "FOREIGN KEY(username) REFERENCES user(\"username\"));");

        db.execSQL("CREATE TABLE account(" +
                "id INTEGER," +
                "username VARCHAR(30), " +
                "bank VARCHAR(10)," +
                "account_number VARCHAR(15)," +
                "FOREIGN KEY(id) REFERENCES user(\"id\")," +
                "FOREIGN KEY(username) REFERENCES user(\"username\"));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS wishlist");
        onCreate(db);
    }
}
