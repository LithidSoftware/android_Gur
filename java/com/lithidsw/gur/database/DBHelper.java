package com.lithidsw.gur.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "gur.db";
    private static final int DATABASE_VERSION = 1;

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS ";

    public static final String TABLE_SAVED = "saved";
    public static final String TABLE_QUE = "que";

    public static final String C_ID = "id";
    public static final String C_TITLE = "title";
    public static final String C_DESC = "desc";
    public static final String C_URL = "url";
    public static final String C_MD5 = "md5";
    public static final String C_ALBUM = "album";

    public static final String C_PATH = "path";

    private static final String DATABASE_CREATE_SAVED = "CREATE TABLE "
            + TABLE_SAVED + "(" + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + C_TITLE
            + " TEXT NOT NULL, " + C_DESC + " TEXT NOT NULL, " + C_MD5
            + " TEXT NOT NULL, " + C_URL + " TEXT NOT NULL, " + C_ALBUM
            + " INTEGER NOT NULL);";

    private static final String DATABASE_CREATE_QUE = "CREATE TABLE "
            + TABLE_QUE + "(" + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + C_TITLE
            + " TEXT NOT NULL, " + C_PATH + " TEXT NOT NULL, " + C_MD5 + " TEXT NOT NULL);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_SAVED);
        database.execSQL(DATABASE_CREATE_QUE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE + TABLE_SAVED);
        db.execSQL(DROP_TABLE + TABLE_QUE);
        onCreate(db);
    }

}
