package com.example.josh.retrofitrssdemo;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.josh.retrofitrssdemo.database.CursorAdapter;
import com.example.josh.retrofitrssdemo.database.FavoritesDataSource;

import java.text.Normalizer;

/**
 * Created by Josh on 4/22/2016.
 */
public class FavoriteBillsActivity extends AppCompatActivity {

    // TODO CursorLoader?
    RecyclerView recyclerView;
    FavoritesDataSource dataSource;
    Cursor cursor;
    CursorAdapter mAdapter;
    public final static String TAGG = FavoriteBillsActivity.class.getSimpleName();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorites, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Enter bill # or keyword");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAGG, "onQueryTextSubmit");
                cursor  = dataSource.searchTasks(query);
                if (cursor == null){
                    Toast.makeText(FavoriteBillsActivity.this, "No Records Found", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FavoriteBillsActivity.this, cursor.getCount() + " records found", Toast.LENGTH_SHORT).show();
                }
                mAdapter.swapCursor(cursor);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAGG, "onQueryTextSubmit");
                cursor = dataSource.searchTasks(newText);
                if (cursor != null){
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
        if (id == R.id.action_help){
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
        if (id == R.id.action_count){
            Toast.makeText(FavoriteBillsActivity.this, cursor.getCount() + " Items Saved", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public static CharSequence highlight(String search, String originalText){
        String normalizedText = Normalizer.normalize(originalText,
                Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
        int start = normalizedText.indexOf(search);
        if (start < 0){
            return originalText;
        } else {
            Spannable highlighted = new SpannableString(originalText);
            while (start >= 0){
                int spanStart = Math.min(start, originalText.length());
                int spanEnd = Math.min(start + search.length(), originalText.length());
                highlighted.setSpan(new BackgroundColorSpan(R.color.colorAccent), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = normalizedText.indexOf(search, spanEnd);
            }
            return highlighted;
        }
    }

}
