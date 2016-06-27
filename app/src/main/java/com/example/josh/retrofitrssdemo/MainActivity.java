package com.example.josh.retrofitrssdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.josh.retrofitrssdemo.adapter.FeedItemAnimatorTest;
import com.example.josh.retrofitrssdemo.adapter.RssAdapter;
import com.example.josh.retrofitrssdemo.model.Rss;
import com.example.josh.retrofitrssdemo.network.RssInterface;
import com.example.josh.retrofitrssdemo.tabLayoutTest.TabActivityTest;

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
    SwipeRefreshLayout refreshLayout;
    ProgressBar progressBar;
    RssAdapter adapter;
    Rss rssList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView)findViewById(R.id.recycler);
        refreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new FeedItemAnimatorTest());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this));

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


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("pref_collapse_all", true)){
            Toast.makeText(MainActivity.this, "Collapse is Checked", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(MainActivity.this, "NOT CHECKED", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (rssList != null){
            outState.putParcelableArrayList(KEY_DATA, (ArrayList<? extends Parcelable>) rssList.getChannel().mItems);
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

        if (!isNetworkAvailable()){
            Log.d(TAG, "No network connection");
            String msg = "Network connection is needed to get recent bill activity";
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        //recyclerView.setVisibility(View.INVISIBLE);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

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
                //recyclerView.setVisibility(View.VISIBLE);
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

    protected boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void setExpandAllCards() {
        Toast.makeText(MainActivity.this, "Expand all cards", Toast.LENGTH_SHORT).show();
    }

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