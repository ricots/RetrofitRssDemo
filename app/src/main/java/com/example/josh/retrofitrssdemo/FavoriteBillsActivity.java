package com.example.josh.retrofitrssdemo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.josh.retrofitrssdemo.database.CursorAdapter;
import com.example.josh.retrofitrssdemo.database.FavoritesDataSource;

/**
 * Created by Josh on 4/22/2016.
 */
public class FavoriteBillsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FavoritesDataSource dataSource;
    Cursor cursor;
    CursorAdapter mAdapter;
    TextView emptyList;
    public final static String TAG = FavoriteBillsActivity.class.getSimpleName();

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        emptyList = (TextView)findViewById(R.id.empty_textview_database);
        dataSource = new FavoritesDataSource(getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataSource.open(true);
        cursor = dataSource.getAllBills();
        mAdapter = new CursorAdapter(this, cursor);
        recyclerView.setAdapter(mAdapter);

        checkIfEmpty();

    }

    // TODO: handle empty state
    private void checkIfEmpty() {
        if (mAdapter.getItemCount()== 0){
            emptyList.setVisibility(View.VISIBLE);
        } else {
            emptyList.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        cursor = dataSource.getAllBills();
        mAdapter.swapCursor(cursor);
        //checkIfEmpty();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //dataSource.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorites, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Enter bill # or keyword");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // TODO: Try different implementation of searchView to fix IndexOutofBoundsException?
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Log.d(TAGG, "onQueryTextSubmit");
                cursor = dataSource.searchTasks(query);
                if (cursor == null) {
                    Toast.makeText(FavoriteBillsActivity.this, "No Records Found", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FavoriteBillsActivity.this, cursor.getCount() + " records found", Toast.LENGTH_SHORT).show();
                }
                mAdapter.swapCursor(cursor);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.d(TAGG, "onQueryTextSubmit");
                cursor = dataSource.searchTasks(newText);
                if (cursor != null) {
                    mAdapter.swapCursor(cursor);
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        if (id == R.id.action_help) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Search by bill number or description keyword.\n" +
                    "\n" +
                    "Examples:\n" +
                    "SB 907, 907, 0907, SB or HB, HJR MM or MM")
                    .setCancelable(false)
                    .setTitle("Search Help")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do stuff
                        }
                    });
            builder.setCancelable(true);
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        /*
        START:
        Coach Mark testing "help" overlay
         */
        if (id == R.id.action_coach) {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.coach_mark);
            dialog.setCanceledOnTouchOutside(true);
            View masterView = dialog.findViewById(R.id.coach_mark_master_view);
            masterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        /*
        END:
        Coach Mark testing "help" overlay
        Or, try following implementation instead:
        http://stackoverflow.com/questions/10216937/how-do-i-create-a-help-overlay-like-you-see-in-a-few-android-apps-and-ics
         */

        // TODO: "Remove/Delete All" option
        if (id == R.id.action_remove_all) {
            if (mAdapter.getItemCount() == 0){
                Toast.makeText(FavoriteBillsActivity.this, "Nothing to delete!", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Remove all from Favorites?")
                        .setCancelable(true)
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("DELETE ALL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dataSource.removeAll();
                                mAdapter.notifyItemRangeRemoved(0, mAdapter.getItemCount());
                                //mAdapter.notifyDataSetChanged();
                                mAdapter.swapCursor(dataSource.getAllBills());
                                Toast.makeText(FavoriteBillsActivity.this, "All items removed from Favorites", Toast.LENGTH_SHORT).show();
                                checkIfEmpty();
                            }
                        });
                builder.setCancelable(true);
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
