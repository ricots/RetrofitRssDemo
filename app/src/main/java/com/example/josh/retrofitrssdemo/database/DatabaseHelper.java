package com.example.josh.retrofitrssdemo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Josh on 4/22/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Table Information
    static final String TABLE_FAVORITES = "favorites";
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "Bills.db";

    // Table Columns
    static final String COLUMN_ID = "_id";
    static final String COLUMN_BILL_TITLE = "billtitle";
    static final String COLUMN_BILL_DESCRIPTION = "billdescription";
    static final String COLUMN_BILL_PUBDATE = "billpubdate";
    static final String COLUMN_BILL_GUID = "billguid";
    static final String COLUMN_BILL_LINK = "billlink";

    static final String DATABASE_CREATE = "create table "
            + TABLE_FAVORITES + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_BILL_TITLE + " TEXT NOT NULL, "
            + COLUMN_BILL_DESCRIPTION + " TEXT NOT NULL, "
            + COLUMN_BILL_PUBDATE + " TEXT NOT NULL, "
            + COLUMN_BILL_LINK + " TEXT NOT NULL, "
            + COLUMN_BILL_GUID + " TEXT NOT NULL);";

    private static DatabaseHelper mInstance = null;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance(Context context){
        if (mInstance == null)
            mInstance = new DatabaseHelper(context.getApplicationContext());
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(DatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion +
        " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(db);
    }
}
