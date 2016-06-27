package com.example.josh.retrofitrssdemo.tabLayoutTest;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.josh.retrofitrssdemo.R;
import com.example.josh.retrofitrssdemo.adapter.RssAdapter;
import com.example.josh.retrofitrssdemo.model.Rss;
import com.example.josh.retrofitrssdemo.network.RssInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by Josh on 5/20/2016.
 */
public class FirstFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String KEY_TITLE = "title";
    public static final String KEY_DATA = "DATA";
    RecyclerView recyclerView;
    RssAdapter adapter;
    ProgressBar progressBar;
    SwipeRefreshLayout refreshLayout;
    Rss rssList;

    public FirstFragment(){
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.progress_colors));
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);

        if (savedInstanceState != null && savedInstanceState.getParcelableArrayList(KEY_DATA) != null){
            rssList.getChannel().mItems = savedInstanceState.getParcelableArrayList(KEY_DATA);
            progressBar.setVisibility(View.GONE);
            adapter = new RssAdapter(getContext(), rssList.getChannel().getItems());
            if (recyclerView.getAdapter() != null){
                recyclerView.swapAdapter(adapter, false);
            } else {
                recyclerView.setAdapter(adapter);
            }
        } else {
            getData();
        }
        return view;
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

    public void getData(){
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);

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
                recyclerView.setAdapter(new RssAdapter(getActivity(), rss.getChannel().getItems()));
                adapter = new RssAdapter(getContext(), rss.getChannel().getItems());
                if (recyclerView.getAdapter() != null){
                    recyclerView.swapAdapter(adapter, false);
                } else {
                    recyclerView.setAdapter(adapter);
                }
                refreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<Rss> call, Throwable t) {
                t.printStackTrace();
                Log.e("Error:: ", t.getMessage());
            }
        });
    }
}
