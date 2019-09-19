package com.ch.ch;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;

/**
 * 所有的操作都交由数据库 起转发作用
 * 除了oncreate运行在主线程 其他运行在其他线程
 */
public class BookProvider extends ContentProvider {

    //必须声明才能访问
    public static String AUTHORITY="com.ch.ch.BookProvider";

    //声明URI表示想要访问的数据类型
    public static final Uri BOOK_CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/book");
    public static final Uri USER_CONTENT_URI=Uri.parse("content://"+AUTHORITY+"/user");

    public static final int BOOK_URI_CODE=0;
    public static final int USER_URI_CODE=1;

    private static final UriMatcher urimathcer=new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //匹配对应 book表 和user表对应
        urimathcer.addURI(AUTHORITY,"book",BOOK_URI_CODE);
        urimathcer.addURI(AUTHORITY,"user",USER_URI_CODE);
    }

    private DbOpenHelper dbOpenHelper;
    private SQLiteDatabase db;


    @Override
    public boolean onCreate() {
        dbOpenHelper=new DbOpenHelper(getContext());
        db=dbOpenHelper.getWritableDatabase();
        db.execSQL("insert into book values(3,'android');");
        LogUtils.log("ContentProvider运行在线程："+Thread.currentThread().getName());
        return false;
    }


    private String getAccessTable(Uri uri){
        String tableName=null;
        switch (urimathcer.match(uri)){//匹配uri返回code
            case BOOK_URI_CODE:
                tableName=DbOpenHelper.BOOK_TABLE_NAME;
                break;
            case USER_URI_CODE:
                tableName=DbOpenHelper.USER_TABLE_NAME;
                break;
                default:break;
        }
        return tableName;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        LogUtils.log("query运行在线程："+Thread.currentThread().getName());
        String tableName=getAccessTable(uri);
        return db.query(tableName,projection,selection,selectionArgs,null,null,sortOrder,null);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        String table=getAccessTable(uri);
        db.insert(table,null,values);
        //flush
        getContext().getContentResolver().notifyChange(uri,null);
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
