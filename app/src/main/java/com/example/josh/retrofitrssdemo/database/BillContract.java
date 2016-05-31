package com.example.josh.retrofitrssdemo.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Josh on 5/26/2016.
 */
public class BillContract {
    //TODO: Configure ContentProvider if necessary


    // Name of the content provider
    public static final String CONTENT_AUTHORITY = "com.example.josh.retrofitrssdemo.database";

    // add scheme to the content provide name
    public static final Uri BASE_CONTENT_URI = Uri.parse("context://" + CONTENT_AUTHORITY);

    // location of the table
    public static final String PATH_BILL = "bill";

    /* Inner class that setups up bill table structure */
    public static final class BillTable implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BILL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BILL;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BILL;

        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_ID = "_id";

        public static final String COLUMN_BILL_TITLE = "billtitle";

        public static final String COLUMN_BILL_DESCRIPTION = "billdescription";

        public static final String COLUMN_BILL_PUBDATE = "billpubdate";

        public static final String COLUMN_BILL_GUID = "billguid";

        public static final String COLUMN_BILL_LINK = "billlink";

        public static final String DATE = "date";

        public static Uri buildBillUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }



}
