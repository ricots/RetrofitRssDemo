package com.example.josh.retrofitrssdemo.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Josh on 5/26/2016.
 */
public class BillProvider extends ContentProvider {

    //TODO: Configure ContentProvider

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DatabaseHelper mMovieDbHelper;

    static final int BILL = 100;
    static final int BILL_TITLE = 101;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = BillContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, BillContract.PATH_BILL, BILL);
        matcher.addURI(authority, BillContract.PATH_BILL + "/#", BILL_TITLE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case BILL:
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        BillContract.BillTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case BILL_TITLE:
                retCursor = mMovieDbHelper.getReadableDatabase().query(
                        BillContract.BillTable.TABLE_NAME,
                        projection,
                        BillContract.BillTable._ID + " = ? ",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case BILL:
                return BillContract.BillTable.CONTENT_TYPE;
            case BILL_TITLE:
                return BillContract.BillTable.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case BILL:
                long _id = db.insert(BillContract.BillTable.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = BillContract.BillTable.buildBillUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case BILL:
                rowsDeleted = db.delete(
                        BillContract.BillTable.TABLE_NAME, selection, selectionArgs);
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        BillContract.BillTable.TABLE_NAME + "'");
                break;
            case BILL_TITLE:
                rowsDeleted = db.delete(BillContract.BillTable.TABLE_NAME,
                        BillContract.BillTable._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        BillContract.BillTable.TABLE_NAME + "'");
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        if (values == null) {
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch (match) {
            case BILL:
                rowsUpdated = db.update(BillContract.BillTable.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case BILL_TITLE:
                rowsUpdated = db.update(BillContract.BillTable.TABLE_NAME, values,
                        BillContract.BillTable._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
