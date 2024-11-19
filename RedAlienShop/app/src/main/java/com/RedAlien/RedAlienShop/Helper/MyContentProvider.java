package com.RedAlien.RedAlienShop.Helper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.InstallSourceInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;

import java.util.Map;

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

    private static final Map<String, String> storePackages = Map.of(
            "com.android.vending", "Google Playstore",
            "com.skt.skaf.A000Z00040", "SKT OneStore",
            "com.kt.olleh.storefront", "KT OneStore",
            "android.lgt.appstore", "LG U+ OneStore",
            "com.lguplus.appstore", "OneStore",
            "com.sec.android.app.samsungapps", "Galaxy Apps",
            "com.sec.android.easyMover.Agent", "SAMSUNG smary switch"
    );

    private String[] callingPackages;
    private Context context;
    private SQLiteDatabase db;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "user", USER);
        uriMatcher.addURI(AUTHORITY, "wishlist", WISHLIST);
        uriMatcher.addURI(AUTHORITY, "basket", BASKET);
        uriMatcher.addURI(AUTHORITY, "card", CARD);
        uriMatcher.addURI(AUTHORITY, "account", ACCOUNT);
    }

    public MyContentProvider() {}

    @Override
    public boolean onCreate() {
        DBHelper dbHelper = new DBHelper(getContext());
        db = dbHelper.getWritableDatabase();
        context = getContext();
        return db != null;
    }

    // 테스트 결과, shell에서 content gettype --uri ... 를 통해서는 호출 할 수 없음 > 예외 발생함
    @Override
    public String getType(Uri uri) {
        isUnauthorizedAccess();
        isInstalledFromStore();

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
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        isUnauthorizedAccess();
        isInstalledFromStore();

        long rowID;
        Uri _uri;
        String tableName;

        switch (uriMatcher.match(uri)){
            case USER:
                tableName = "user";
                break;
            case WISHLIST:
                tableName = "wishlist";
                break;
            case BASKET:
                tableName = "basket";
                break;
            case CARD:
                tableName = "card";
                break;
            case ACCOUNT:
                tableName = "account";
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }
        // 해당 테이블에 데이터가 insert된 후, 해당 데이터의 row번호가 반환됨
        rowID = db.insert(tableName, "", values);
        if (rowID == -1 ) {
            throw new SQLException("Failed to add a record into " + uri);
        }

        // content://com.RedAlien.provider/user/<rowId> 형식의 URI를 반환한다.
        // 해당 메소드를 통하여,새로 삽입된 레코드에 직접 접근할 수 있는 URI를 생성한다
        _uri = ContentUris.withAppendedId(CONTENT_URI_USER, rowID);
        // _uri에 해당하는 데이터가 변경되었음을 알린다
        // 해당 데이터를 사용하고 있는 모든 클라이언트가 이를 인식하고, 데이터를 갱신할 수 있다
        getContext().getContentResolver().notifyChange(uri, null);
        return _uri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        isUnauthorizedAccess();
        isInstalledFromStore();

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
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        isUnauthorizedAccess();
        isInstalledFromStore();

        int count; // update로 영향받은 row수
        String tableName;

        switch (uriMatcher.match(uri)){
            case USER:
                tableName = "user";
                break;
            case WISHLIST:
                tableName = "wishlist";
                break;
            case BASKET:
                tableName = "basket";
                break;
            case CARD:
                tableName = "card";
                break;
            case ACCOUNT:
                tableName = "account";
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }
        count = db.update(tableName, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        isUnauthorizedAccess();
        isInstalledFromStore();

        int count; // delete로 영향을 받은 행수
        String tableName;

        switch (uriMatcher.match(uri)){
            case USER:
                tableName = "user";
                break;
            case WISHLIST:
                tableName = "wishlist";
                break;
            case BASKET:
                tableName = "basket";
                break;
            case CARD:
                tableName = "card";
                break;
            case ACCOUNT:
                tableName = "account";
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI " + uri);
        }
        count = db. delete(tableName, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    // shell에서 content query --uri ... 와 같은 비정상적인 접근 방지
    public void isUnauthorizedAccess(){
        int callingUID = Binder.getCallingUid();  // 호출한 UID
        callingPackages = getContext().getPackageManager().getPackagesForUid(callingUID);

        if (callingUID == 0) throw new SecurityException("Unauthorized Access from root");
        if (callingPackages != null){
            for (String callingPackage : callingPackages){
                if( callingPackage.equals("com.android.shell") || callingPackage.equals("com.android.adb") ){
                    throw new SecurityException("Unauthorized Access from shell");
                }
            }
        }
    }

    // Google Playstore를 통해 설치했다면  com.android.vending 가 반환됨
    // adb를 통해 설치 혹은 root권한으로 content를 통해 접근해도 null이 반환됨
    public void isInstalledFromStore(){
        PackageManager pm;
        if (context == null || callingPackages == null || (pm =context.getPackageManager()) == null) {
            String msg = (context == null) ? "context is null" :
                        (callingPackages == null) ? "callingPackages is null" :
                        "PackageManager is null";
            Log.i(TAG, msg);
            throw new SecurityException(msg);
        }

        String installer = null;
        try {
            for (String callingPackage : callingPackages) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    InstallSourceInfo installSourceInfo = pm.getInstallSourceInfo(callingPackage);
                    installer = installSourceInfo.getInstallingPackageName();
                } else {
                    installer = pm.getInstallerPackageName(callingPackage);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        if (installer == null) {
            throw new SecurityException("Client app is installed via ADB");
        }

        for (Map.Entry<String, String> entry : storePackages.entrySet()){
            String key   = entry.getKey();
            String value = entry.getValue();

            if (key.equals(installer)){
                Log.i(TAG, "Installed from " + value);
                return;
            }
        }
    }
}