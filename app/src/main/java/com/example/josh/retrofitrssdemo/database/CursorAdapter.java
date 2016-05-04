package com.example.josh.retrofitrssdemo.database;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.josh.retrofitrssdemo.BillDetailActivity;
import com.example.josh.retrofitrssdemo.R;
import com.example.josh.retrofitrssdemo.model.Item;

/**
 * Created by Josh on 4/22/2016.
 */
public class CursorAdapter extends CursorRecyclerViewAdapter<DatabaseViewHolder> {

    LayoutInflater inflater;
    Context context;

    public CursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(final DatabaseViewHolder viewHolder, final Cursor cursor) {
        int id = cursor.getInt(0);
        String title = cursor.getString(1);
        String description = cursor.getString(2);
        String pubDate = cursor.getString(3);
        String link = cursor.getString(4);
        String guid = cursor.getString(5);

        final Item item = new Item(title, description, pubDate, link, guid);

        viewHolder.title.setText(item.getTitle());
        viewHolder.description.setText(item.getDescription());
        viewHolder.pubDate.setText(item.getPubDate());

        viewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BillDetailActivity.class);
                intent.putExtra(BillDetailActivity.EXTRA_BILL, item);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public DatabaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DatabaseViewHolder(inflater.inflate(R.layout.database_item, parent, false));
    }
}
