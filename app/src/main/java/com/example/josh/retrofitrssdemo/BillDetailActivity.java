package com.example.josh.retrofitrssdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.josh.retrofitrssdemo.database.FavoritesDataSource;
import com.example.josh.retrofitrssdemo.model.Item;

/**
 * Created by Josh on 4/22/2016.
 */
public class BillDetailActivity extends AppCompatActivity {

    Item mItem;
    FavoritesDataSource dataSource;
    public static final String EXTRA_BILL = "bill";

    public TextView billTitle, billDescription, billPubDate;
    public FloatingActionButton fab;
    public CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dataSource = new FavoritesDataSource(getApplicationContext());
        dataSource.open(false);

        if (getIntent().hasExtra(EXTRA_BILL)){
            mItem = getIntent().getParcelableExtra(EXTRA_BILL);
        }
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mItem.getTitle());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        final CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        if (toolbarLayout != null) {
            toolbarLayout.setTitle(mItem.getTitle());
        }

        billTitle = (TextView)findViewById(R.id.bill_detail_title);
        billTitle.setText(mItem.getTitle());

        billDescription = (TextView)findViewById(R.id.bill_detail_description);
        billDescription.setText(mItem.getDescription());

        billPubDate = (TextView)findViewById(R.id.bill_detail_pubdate);
        billPubDate.setText(mItem.getPubDate());

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator_layout);
        fab = (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataSource.ifBillExists(mItem.getTitle())){
                    fab.setImageResource(R.drawable.ic_favorite_white_48dp);
                    dataSource.removeBill(mItem.getTitle());
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Removed from Favorites", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    fab.setImageResource(R.drawable.ic_favorite_red_48dp);
                    dataSource.insertBill(mItem);
                    Snackbar snackbar = Snackbar.make(coordinatorLayout, "Added " + mItem.title + " To Favorites!", Snackbar.LENGTH_SHORT);
                    View view = snackbar.getView();
                    TextView tv = (TextView)view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(getResources().getColor(R.color.snackbar_text));
                    snackbar.show();
                }
            }
        });
        dataSource = new FavoritesDataSource(getApplicationContext());
        dataSource.open(false);

        if (dataSource.ifBillExists(mItem.getTitle())){
            fab.setImageResource(R.drawable.ic_favorite_red_48dp);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (dataSource != null)
//            dataSource.close();
//        Log.d("abc", "closed");
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_browser:
                Uri uri = Uri.parse(mItem.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
