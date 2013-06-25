package com.lithidsw.gur.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class SavedTable {

    private SQLiteDatabase database;
    private DBHelper dbHelper;
    final private static String TABLE = DBHelper.TABLE_SAVED;

    public SavedTable(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void updatedItem(String title, String desc,
                                  String url, String md5, boolean album) {
        ContentValues value = new ContentValues();
        value.put(DBHelper.C_TITLE, title);
        value.put(DBHelper.C_DESC, desc);
        value.put(DBHelper.C_URL, url);
        value.put(DBHelper.C_MD5, md5);
        value.put(DBHelper.C_ALBUM, album? 1 : 0);
        open();
        database.insert(TABLE, null, value);
        close();
    }

    public ArrayList<String[]> getAllSaved() {
        ArrayList<String[]> list = new ArrayList<String[]>();
        open();
        Cursor c = null;
        c = database.query(TABLE, new String[]{"*"}, null, null, null, null, null);
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                String[] items = new String[6];
                items[0] = c.getString(c
                        .getColumnIndex(DBHelper.C_ID));
                items[1] = c.getString(c
                        .getColumnIndex(DBHelper.C_TITLE));
                items[2] = c.getString(c
                        .getColumnIndex(DBHelper.C_DESC));
                items[3] = c.getString(c
                        .getColumnIndex(DBHelper.C_MD5));
                items[4] = c.getString(c
                        .getColumnIndex(DBHelper.C_URL));

                String alb = c.getString(c
                        .getColumnIndex(DBHelper.C_ALBUM));

                items[5] = String.valueOf(alb.equals("1"));
                list.add(items);
                c.moveToNext();
            }
        }
        close();
        return list;
    }

    public int getSavedCount() {
        int count = 0;
        open();
        Cursor c = null;
        c = database.query(TABLE, new String[]{"*"}, null, null, null, null, "id DESC");
        if (c.moveToFirst()) {
            count = c.getInt(c.getColumnIndex(DBHelper.C_ID));
        }
        close();
        return count+1;
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

    public void deleteItem(String id) {
        open();
        database.delete(TABLE, DBHelper.C_ID + " = '" + id + "'", null);
        close();
    }

    public void resetAuto() {
        open();
        database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + TABLE + "'");
        close();
    }
}
