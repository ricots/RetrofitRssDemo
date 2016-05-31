package com.example.josh.retrofitrssdemo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.josh.retrofitrssdemo.model.Item;

/**
 * Created by Josh on 4/22/2016.
 */
public class FavoritesDataSource {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    public FavoritesDataSource(Context context){
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public void open(boolean readonly){
        database = readonly ? dbHelper.getReadableDatabase() : dbHelper.getWritableDatabase();
    }

    public void close(){
        database.close();
    }

    private Item cursorToBill(Cursor cursor){
        String title = cursor.getString(0);
        String description = cursor.getString(1);
        String pubDate = cursor.getString(2);
        String guid = cursor.getString(3);
        String link = cursor.getString(4);
        return new Item(title, description, pubDate, guid, link);
    }

    public long insertBill(Item billItem){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_BILL_TITLE, billItem.getTitle());
        values.put(DatabaseHelper.COLUMN_BILL_DESCRIPTION, billItem.getDescription());
        values.put(DatabaseHelper.COLUMN_BILL_PUBDATE, billItem.getPubDate());
        values.put(DatabaseHelper.COLUMN_BILL_GUID, billItem.getGuid());
        values.put(DatabaseHelper.COLUMN_BILL_LINK, billItem.getLink());

        return database.insert(DatabaseHelper.TABLE_FAVORITES, null, values);
    }

    public boolean ifBillExists(String title){
//        String query = "select rowid _id,* from favorites " + DatabaseHelper.TABLE_FAVORITES + " where " + DatabaseHelper.COLUMN_ID + " = " + id;
//        Cursor c = database.rawQuery(query, null);
        Cursor cursor = database.rawQuery("select 1 from " + DatabaseHelper.TABLE_FAVORITES + " where " + DatabaseHelper.COLUMN_BILL_TITLE + "=?"
         , new String[]{title});

        boolean b = cursor.moveToFirst();
        cursor.close();
        return b;
    }

    public int removeBill(String title){
        return database.delete(DatabaseHelper.TABLE_FAVORITES, DatabaseHelper.COLUMN_BILL_TITLE + "=? ", new String[]{title}); // + title, null);
    }

    public Cursor getAllBills(){
        return database.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_FAVORITES, null);
    }

    public void RemoveAll(){
        database.delete(DatabaseHelper.TABLE_FAVORITES, null, null);
    }

    public int getBillCount(){
        String countQuery = "SELECT * FROM " + DatabaseHelper.TABLE_FAVORITES;
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }


    public Cursor searchTasks(String search){
        // Credit to http://instinctcoder.com/android-studio-sqlite-search-searchview-actionbar/
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT rowid as " +
                DatabaseHelper.COLUMN_ID + "," +
                DatabaseHelper.COLUMN_BILL_TITLE + "," +
                DatabaseHelper.COLUMN_BILL_DESCRIPTION + "," +
                DatabaseHelper.COLUMN_BILL_PUBDATE + "," +
                DatabaseHelper.COLUMN_BILL_LINK + "," +
                DatabaseHelper.COLUMN_BILL_GUID +
                " FROM " + DatabaseHelper.TABLE_FAVORITES + " WHERE " + DatabaseHelper.COLUMN_BILL_TITLE + " LIKE '%" +search + "%' OR " + DatabaseHelper.COLUMN_BILL_DESCRIPTION + " LIKE '%" +search + "%'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor == null){
            return null;
        } else  if (!cursor.moveToFirst()){
            cursor.close();
            return null;
        }
        return cursor;
    }
}
