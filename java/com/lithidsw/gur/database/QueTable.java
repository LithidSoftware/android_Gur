package com.lithidsw.gur.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;

public class QueTable {

    private SQLiteDatabase database;
    private DBHelper dbHelper;
    final private static String TABLE = DBHelper.TABLE_QUE;

    public QueTable(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void updatedItem(String title, String path, String md5) {
        ContentValues value = new ContentValues();
        value.put(DBHelper.C_TITLE, title);
        value.put(DBHelper.C_PATH, path);
        value.put(DBHelper.C_MD5, md5);
        open();
        database.insert(TABLE, null, value);
        close();
    }

    public String[] getLastestQue() {
        String[] items = new String[4];
        open();
        Cursor c = database.query(TABLE, new String[]{"*"}, null, null, null, null, null);
        if (c.moveToFirst()) {
            items[0] = c.getString(c.getColumnIndex(DBHelper.C_ID));
            items[1] = c.getString(c.getColumnIndex(DBHelper.C_TITLE));
            items[2] = c.getString(c.getColumnIndex(DBHelper.C_PATH));
            items[3] = c.getString(c.getColumnIndex(DBHelper.C_MD5));
        }
        close();

        return items;
    }

    public ArrayList<String[]> getAllQue() {
        ArrayList<String[]> list = new ArrayList<String[]>();
        open();
        Cursor c = null;
        c = database.query(TABLE, new String[]{"*"}, null, null, null, null, null);
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                String[] items = new String[4];
                items[0] = c.getString(c.getColumnIndex(DBHelper.C_ID));
                items[1] = c.getString(c.getColumnIndex(DBHelper.C_TITLE));
                items[2] = c.getString(c.getColumnIndex(DBHelper.C_PATH));
                items[3] = c.getString(c.getColumnIndex(DBHelper.C_MD5));
                list.add(items);
                c.moveToNext();
            }
        }
        close();
        return list;
    }

    public int getQueCount() {
        int count = 0;
        open();
        Cursor c = database.rawQuery("select count(*) from " + TABLE, null);
        if (c.moveToFirst()) {
            count = c.getInt(0);
        }
        close();
        return count;
    }

    public boolean isMd5(String md5) {
        boolean is = false;
        open();
        try {
            Cursor all;
            all = database.rawQuery("select * from " + TABLE + " where "
                    + DBHelper.C_MD5 + " = '" + md5 + "'", null);
            is = all.moveToFirst();
            close();
        } catch (SQLiteException e) {}
        return is;
    }

    public void deleteItem(String MD5) {
        open();
        database.delete(TABLE, DBHelper.C_MD5 + " = '" + MD5 + "'", null);
        close();
    }

    public void resetAuto() {
        open();
        database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE + "'");
        close();
    }
}
