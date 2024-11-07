package com.RedAlien.RedAlienShop.Helper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;

public class MyContentProvider extends ContentProvider {
    private static final String TAG = "MyContentProvider";
    private static final String AUTHORITY ="com.RedAlien.provider";
    private static final String URL = "content://" + AUTHORITY;
    public static final Uri CONTENT_URI_USER = Uri.parse(URL + "/user");
    public static final Uri CONTENT_URI_WISHLIST = Uri.parse(URL + "/wishlist");
    public static final Uri CONTENT_URI_BASKET = Uri.parse(URL + "/basket");
    public static final Uri CONTENT_URI_CARD = Uri.parse(URL + "/card");
    public static final Uri CONTENT_URI_ACCOUNT = Uri.parse(URL + "/account");
    private static final UriMatcher uriMatcher;

    private static final int USER = 1;
    private static final int WISHLIST = 2;
    private static final int BASKET = 3;
    private static final int CARD = 4;
    private static final int ACCOUNT = 5;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "user", USER);
        uriMatcher.addURI(AUTHORITY, "wishlist", WISHLIST);
        uriMatcher.addURI(AUTHORITY, "basket", BASKET);
        uriMatcher.addURI(AUTHORITY, "card", CARD);
        uriMatcher.addURI(AUTHORITY, "account", ACCOUNT);
    }

    private SQLiteDatabase db;

    public MyContentProvider() {}

    @Override
    public boolean onCreate() {
        DBHelper dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    // 테스트 결과, shell에서 content gettype --uri ... 를 통해서는 호출 할 수 없음 > 예외 발생함
    @Override
    public String getType(Uri uri) {
        isUnauthorizedAccess();

        switch (uriMatcher.match(uri)){
            case USER:
                return "vnd.android.cursor.dir/vnd.com.RedAlien.provider.user";
            case WISHLIST:
                return "vnd.android.cursor.dir/vnd.com.RedAlien.provider.wishlist";
            case BASKET:
                return "vnd.android.cursor.dir/vnd.com.RedAlien.provider.basket";
            case CARD:
                return "vnd.android.cursor.dir/vnd.com.RedAlien.provider.card";
            case ACCOUNT:
                return "vnd.android.cursor.dir/vnd.com.RedAlien.provider.account";
            default:
                return "시발련아";
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
//        isUnauthorizedAccess();
        long rowID;
        Uri _uri;

        switch (uriMatcher.match(uri)){
            case USER:
                // 해당 테이블에 새로 삽입된 row번호가 반환됨
                rowID = db.insert("user", "", values);
                if (rowID > 0){
                    // content://com.RedAlien.provider/user/<rowId> 형식의 URI를 반환한다.
                    // 해당 메소드를 통하여,새로 삽입된 레코드에 직접 접근할 수 있는 URI를 생성한다
                    _uri = ContentUris.withAppendedId(CONTENT_URI_USER, rowID);
                    // _uri에 해당하는 데이터가 변경되었음을 알린다
                    // 해당 데이터를 사용하고 있는 모든 클라이언트가 이를 인식하고, 데이터를 갱신할 수 있다
                    getContext().getContentResolver().notifyChange(uri, null);

                    return _uri;
                }
            case WISHLIST:
                rowID = db.insert("wishlist", "", values);
                if (rowID > 0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_WISHLIST, rowID);
                    getContext().getContentResolver().notifyChange(uri, null);

                    return _uri;
                }
            case BASKET:
                rowID = db.insert("basket", "", values);
                if (rowID > 0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_BASKET, rowID);
                    getContext().getContentResolver().notifyChange(uri, null);

                    return _uri;
                }
            case CARD:
                rowID = db.insert("card", "", values);
                if (rowID > 0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_CARD, rowID);
                    getContext().getContentResolver().notifyChange(uri, null);

                    return _uri;
                }
            case ACCOUNT:
                rowID = db.insert("account", "", values);
                if (rowID > 0){
                    _uri = ContentUris.withAppendedId(CONTENT_URI_ACCOUNT, rowID);
                    getContext().getContentResolver().notifyChange(uri, null);

                    return _uri;
                }
        }
        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
//        isUnauthorizedAccess();

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder(); // db 쿼리문을 쉽게 작성할 수 있도록 도와주는 유틸리티 클래스
        // 기존 SQLiteDatabase는 한 개 테이블만 쿼리문을 작성할 수 있으나, 이것을 사용할 경우, 여러 테이블에 대한 쿼리문 실행 가능

        switch (uriMatcher.match(uri)){
            case USER:
                qb.setTables("user");
                break;
            case WISHLIST:
                qb.setTables("wishlist");
                break;
            case BASKET:
                qb.setTables("basket");
                break;
            case CARD:
                qb.setTables("card");
                break;
            case ACCOUNT:
                qb.setTables("account");
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // 데이터를 가리키는 uri의 데이터가 변경될 경우, cursor가 이를 감지하도록 등록한다
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        Log.i(TAG, "query() 호출 !");

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
//        isUnauthorizedAccess();
        int count; // update로 영향받은 row수

        switch (uriMatcher.match(uri)){
            case USER:
                count = db.update("user", values, selection, selectionArgs);
                break;
            case WISHLIST:
                count = db.update("wishlist", values, selection, selectionArgs);
                break;
            case BASKET:
                count = db.update("basket", values, selection, selectionArgs);
                break;
            case CARD:
                count = db.update("card", values, selection, selectionArgs);
                break;
            case ACCOUNT:
                count = db.update("account", values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        isUnauthorizedAccess();
        int count; // delete로 영향을 받은 행수

        switch (uriMatcher.match(uri)){
            case USER:
                count = db. delete("user", selection, selectionArgs);
                break;
            case WISHLIST:
                count = db.delete("wishlist", selection, selectionArgs);
                break;
            case BASKET:
                count = db.delete("basket", selection, selectionArgs);
                break;
            case CARD:
                count = db.delete("card", selection, selectionArgs);
                break;
            case ACCOUNT:
                count = db.delete("account", selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    // shell에서 content query --uri ... 와 같은 비정상적인 접근 방지
    public void isUnauthorizedAccess(){
        int callingUID = Binder.getCallingUid();  // 호출한 UID
        String[] callingPackages = getContext().getPackageManager().getPackagesForUid(callingUID);

        if (callingUID == 0) throw new SecurityException("Unauthorized Access from root");
        if (callingPackages != null){
            for (String callingPackage : callingPackages){
                if( callingPackage.equals("com.android.shell") || callingPackage.equals("com.android.adb") ){
                    throw new SecurityException("Unauthorized Access from shell");
                }
            }
        }
    }
}