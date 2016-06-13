package com.example.josh.retrofitrssdemo;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.josh.retrofitrssdemo.adapter.RssAdapter;
import com.example.josh.retrofitrssdemo.database.FavoritesDataSource;
import com.example.josh.retrofitrssdemo.model.Rss;
import com.example.josh.retrofitrssdemo.network.RssInterface;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String KEY_DATA = "DATAA";
    public static final String TAG = MainActivity.class.getSimpleName();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout refreshLayout;
    ProgressBar progressBar;
    RssAdapter adapter;
    Rss rssList;
    FavoritesDataSource dataSource;

    // TODO: Fix "IllegalStateException: attempt to re-open an already closed object..." when returning from FavoritesActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //recyclerView.setAdapter(new RssAdapter(MainActivity.this, items));
        //recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView = (RecyclerView)findViewById(R.id.recycler);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh);

        if (refreshLayout != null) {
            refreshLayout.setOnRefreshListener(MainActivity.this);
        }

        refreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.progress_colors));
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList(KEY_DATA) != null) {
            rssList.getChannel().mItems = savedInstanceState.getParcelableArrayList(KEY_DATA);
            progressBar.setVisibility(View.GONE);
            adapter = new RssAdapter(MainActivity.this, rssList.getChannel().getItems());
            if (recyclerView.getAdapter() != null){
                recyclerView.swapAdapter(adapter, false);
            } else {
                recyclerView.setAdapter(adapter);
            }
        } else {
            getData();
        }


        /*
        START:
        Default Retrofit Call
         */

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://www.legislature.mi.gov/")
//                .addConverterFactory(SimpleXmlConverterFactory.create())
//                .build();
//        RssInterface rssInterface = retrofit.create(RssInterface.class);
//        Call<Rss> rssCall = rssInterface.getBillItems();
//        rssCall.enqueue(new Callback<Rss>() {
//            @Override
//            public void onResponse(Call<Rss> call, Response<Rss> response) {
//                Rss rss = response.body();
//                recyclerView.setAdapter(new RssAdapter(MainActivity.this, rss.getChannel().getItems()));
//
//            }
//            @Override
//            public void onFailure(Call<Rss> call, Throwable t) {
//                t.printStackTrace();
//                Log.e("Error:: ", t.getMessage());
//            }
//        });
        /*
        END:
        Default Retrofit Call
         */

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (rssList != null){
            outState.putParcelableArrayList(KEY_DATA, (ArrayList<? extends Parcelable>) rssList.getChannel().mItems);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //final MenuItem searchItem = menu.findItem(R.id.action_search);
        //final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //searchView.setQueryHint(getString(R.string.search_hint));
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (AppCompatDelegate.getDefaultNightMode()) {
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                menu.findItem(R.id.menu_night_mode_system).setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_AUTO:
                menu.findItem(R.id.menu_night_mode_auto).setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                menu.findItem(R.id.menu_night_mode_night).setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                menu.findItem(R.id.menu_night_mode_day).setChecked(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_favorites:
                Intent intent = new Intent(MainActivity.this, FavoriteBillsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_tabs:
                Intent intent1 = new Intent(MainActivity.this, TabActivityTest.class);
                startActivity(intent1);
                return true;

            case R.id.menu_night_mode_system:
                setNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case R.id.menu_night_mode_day:
                setNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case R.id.menu_night_mode_night:
                setNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case R.id.menu_night_mode_auto:
                setNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setNightMode(@AppCompatDelegate.NightMode int nightMode) {
        AppCompatDelegate.setDefaultNightMode(nightMode);

        if (Build.VERSION.SDK_INT >= 11) {
            recreate();
        }
    }

    @Override
    public void onRefresh() {
        getData();
    }

    /*
    START:
    Retrofit Call V2
     */
    public void getData(){
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        /*
        Add line below for Stetho. Also uncomment MyApplication.java & in Gradle. Make change in Manifest.
         */
        //httpClient.addNetworkInterceptor(new StethoInterceptor());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.legislature.mi.gov/")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(httpClient.build())
                .build();
        RssInterface rssInterface = retrofit.create(RssInterface.class);
        Call<Rss> rssCall = rssInterface.getBillItems();

        rssCall.enqueue(new Callback<Rss>() {
            @Override
            public void onResponse(Call<Rss> call, Response<Rss> response) {
                Rss rss = response.body();
                recyclerView.setAdapter(new RssAdapter(MainActivity.this, rss.getChannel().getItems()));
                adapter = new RssAdapter(MainActivity.this, rss.getChannel().getItems());
                if (recyclerView.getAdapter() != null){
                    recyclerView.swapAdapter(adapter, false);
                } else {
                    recyclerView.setAdapter(adapter);
                }
                progressBar.setVisibility(View.GONE);
                refreshLayout.setRefreshing(false);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<Rss> call, Throwable t) {
                t.printStackTrace();
                Log.e("Error:: ", t.getMessage());
            }
        });
    }
    /*
    END:
    Retrofit Call V2
     */


}

