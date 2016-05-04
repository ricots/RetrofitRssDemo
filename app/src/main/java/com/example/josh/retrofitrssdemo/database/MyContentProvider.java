package com.example.josh.retrofitrssdemo.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Josh on 4/22/2016.
 */
public class MyContentProvider extends ContentProvider {

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/favorites";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/favorite";
    static final String PROVIDER_NAME = "com.example.josh.retrofitrssdemo.provider";
    static final String URL = "content://" + PROVIDER_NAME + '/' + DatabaseHelper.TABLE_FAVORITES;
    static final Uri CONTENT_URI = Uri.parse(URL);
    private static final int FAVORITES = 1;
    private static final int FAVORITE_ID = 2;
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(PROVIDER_NAME, DatabaseHelper.TABLE_FAVORITES, FAVORITES);
        sURIMatcher.addURI(PROVIDER_NAME, DatabaseHelper.TABLE_FAVORITES + "/#", FAVORITE_ID);
    }

    private SQLiteDatabase db;

    public MyContentProvider() {
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
        db = dbHelper.getWritableDatabase();
        return !(db == null);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        int rowsDeleted = 0;
        switch (uriType) {
            case FAVORITES:
                rowsDeleted = db.delete(DatabaseHelper.TABLE_FAVORITES, selection, selectionArgs);
                break;
            case FAVORITE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(DatabaseHelper.TABLE_FAVORITES, DatabaseHelper.COLUMN_BILL_TITLE + "=?", new String[]{id}); // + id, null);
                } else {
                    rowsDeleted = db.delete(DatabaseHelper.TABLE_FAVORITES, DatabaseHelper.COLUMN_BILL_TITLE + "=?" + id + " and " + selection, selectionArgs);
                }

//                if (TextUtils.isEmpty(selection)) {
//                    rowsDeleted = db.delete(DatabaseHelper.TABLE_FAVORITES, DatabaseHelper.COLUMN_ID + "=" + id, null);
//                } else {
//                    rowsDeleted = db.delete(DatabaseHelper.TABLE_FAVORITES, DatabaseHelper.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
//                }


                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        long id;
        switch (uriType) {
            case FAVORITES:
                id = db.insert(DatabaseHelper.TABLE_FAVORITES, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(DatabaseHelper.TABLE_FAVORITES + '/' + id);
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.
//        throw new UnsupportedOperationException("Not yet implemented");
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        checkColumns(projection);
        builder.setTables(DatabaseHelper.TABLE_FAVORITES);
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case FAVORITES:
                break;
            case FAVORITE_ID:
                builder.appendWhere(DatabaseHelper.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        Cursor cursor = builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        int rowsUpdated = 0;
        switch (uriType) {
            case FAVORITES:
                rowsUpdated = db.update(DatabaseHelper.TABLE_FAVORITES, values, selection, selectionArgs);
                break;
            case FAVORITE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = db.update(DatabaseHelper.TABLE_FAVORITES, values, DatabaseHelper.COLUMN_BILL_TITLE + "=?", new String[]{id}); //+ id, null);
                } else {
                    rowsUpdated = db.update(DatabaseHelper.TABLE_FAVORITES, values, DatabaseHelper.COLUMN_BILL_TITLE + "=?" + id + " and " + selection, selectionArgs);
                }
//                if (TextUtils.isEmpty(selection)) {
//                    rowsUpdated = db.update(DatabaseHelper.TABLE_FAVORITES, values, DatabaseHelper.COLUMN_BILL_TITLE + "=" + id, null);
//                } else {
//                    rowsUpdated = db.update(DatabaseHelper.TABLE_FAVORITES, values, DatabaseHelper.COLUMN_BILL_TITLE + "=" + id + " and " + selection, selectionArgs);
//                }


                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = {
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_BILL_TITLE,
                DatabaseHelper.COLUMN_BILL_DESCRIPTION,
                DatabaseHelper.COLUMN_BILL_PUBDATE,
                DatabaseHelper.COLUMN_BILL_LINK,
                DatabaseHelper.COLUMN_BILL_GUID};
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}
