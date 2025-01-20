package com.RedAlien.RedAlienShop.Helper;

import android.content.Context;

import net.zetetic.database.sqlcipher.SQLiteDatabase;
import net.zetetic.database.sqlcipher.SQLiteOpenHelper;

public class CipherDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "shop.db";
    private static final int DATABASE_VERSION = 1;
    private static final String PASSWORD = "My Password";

    public CipherDBHelper(Context context) {
        super(context, DATABASE_NAME, PASSWORD, null, DATABASE_VERSION, DATABASE_VERSION, null, null, true);
        SQLiteDatabase.CursorFactory cursorFactory;

        System.loadLibrary("sqlcipher");
    }


    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

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
