package com.example.josh.retrofitrssdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.josh.retrofitrssdemo.adapter.RssAdapter;
import com.example.josh.retrofitrssdemo.model.Channel;
import com.example.josh.retrofitrssdemo.model.Item;
import com.example.josh.retrofitrssdemo.model.Rss;
import com.example.josh.retrofitrssdemo.network.RssInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_DATA = "DATA";
    public static final String TAG = MainActivity.class.getSimpleName();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout refreshLayout;
    List<Item> items;
    Channel channelList;
    RssAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView)findViewById(R.id.recycler);
        recyclerView.setAdapter(new RssAdapter(MainActivity.this, items));
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this));

//        final ImageButton fab = (ImageButton) findViewById(R.id.fab);
//        if (fab != null) {
//            fab.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    fab.setSelected(!fab.isSelected());
//                    fab.setImageResource(fab.isSelected() ? R.drawable.animated_plus : R.drawable.animated_minus);
//                    Drawable drawable = fab.getDrawable();
//                    if (drawable instanceof Animatable) {
//                        ((Animatable) drawable).start();
//                    }
//                }
//            });
//        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.legislature.mi.gov/")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();
        RssInterface rssInterface = retrofit.create(RssInterface.class);
        Call<Rss> rssCall = rssInterface.getBillItems();
        rssCall.enqueue(new Callback<Rss>() {
            @Override
            public void onResponse(Call<Rss> call, Response<Rss> response) {
                Rss rss = response.body();
                recyclerView.setAdapter(new RssAdapter(MainActivity.this, rss.getChannel().getItems()));

            }
            @Override
            public void onFailure(Call<Rss> call, Throwable t) {
                t.printStackTrace();
                Log.e("Error:: ", t.getMessage());
            }
        });

    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        if (channelList != null){
//            outState.getParcelableArrayList(KEY_DATA, (ArrayList<? extends Parcelable>)channelList.mItems)
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //final MenuItem searchItem = menu.findItem(R.id.action_search);
        //final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //searchView.setQueryHint(getString(R.string.search_hint));
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(final String query) {
//                Log.d(TAG, "QueryTextSubmit: " + query);
//                Bundle bundle = new Bundle();
//                bundle.putString("query", query);
//                Retrofit retrofit = new Retrofit.Builder()
//                        .baseUrl("http://www.legislature.mi.gov/")
//                        .addConverterFactory(SimpleXmlConverterFactory.create())
//                        .build();
//                RssInterface rssInterface = retrofit.create(RssInterface.class);
//                Call<Rss> searchCall = rssInterface.searchBills(query);
//                searchCall.enqueue(new Callback<Rss>() {
//                    @Override
//                    public void onResponse(Call<Rss> call, Response<Rss> response) {
//                        Rss rss = response.body();
                        // ** Currently working with caveats **
                        //recyclerView.setAdapter(new RssAdapter(MainActivity.this, rss.getChannel().mItems));
//                        adapter = new RssAdapter(MainActivity.this, rss.getChannel().getItems());
//                        if (recyclerView.getAdapter() != null){
//                            recyclerView.swapAdapter(adapter, false);
//                        } else {
//                            recyclerView.setAdapter(adapter);
//                        }
                        /*
                        Issues:
                        We need to get the Title from getChannel vs. from getItems
                        Handle closing the SearchView. Closing search should display the getBillItems

                        Also handle NPE if search query is null
                         */
//                    }
//                    @Override
//                    public void onFailure(Call<Rss> call, Throwable t) {
//                        t.printStackTrace();
//                        Log.e("Error:: ", t.getMessage());
//                    }
//                });
//                searchView.clearFocus();
//                return true;
//            }

//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorites) {
            Intent intent = new Intent(MainActivity.this, FavoriteBillsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.action_tabs){
            Intent intent = new Intent(MainActivity.this, TabActivityTest.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }




}
