package com.example.josh.retrofitrssdemo;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.example.josh.retrofitrssdemo.database.CursorAdapter;
import com.example.josh.retrofitrssdemo.database.FavoritesDataSource;
import com.example.josh.retrofitrssdemo.model.Item;

/**
 * Created by Josh on 4/22/2016.
 */
public class FavoriteBillsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FavoritesDataSource dataSource;
    Cursor cursor;
    CursorAdapter mAdapter;
    Item mItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dataSource = new FavoritesDataSource(getApplicationContext());
        dataSource.open(true);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cursor = dataSource.getAllBills();
        mAdapter = new CursorAdapter(this, cursor);
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
        cursor = dataSource.getAllBills();
        mAdapter.swapCursor(cursor);
    }
}
