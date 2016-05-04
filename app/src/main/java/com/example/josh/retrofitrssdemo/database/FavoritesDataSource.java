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

//        if (cursor.moveToFirst()){
//            boolean exists = (cursor.getCount() > 0);
//            cursor.close();
//            return exists;
//        } else {
//            return false;
//        }

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
}
