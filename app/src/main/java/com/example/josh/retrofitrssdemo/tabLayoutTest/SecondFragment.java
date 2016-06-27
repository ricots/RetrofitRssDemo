package com.example.josh.retrofitrssdemo.tabLayoutTest;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.josh.retrofitrssdemo.R;
import com.example.josh.retrofitrssdemo.database.CursorAdapter;
import com.example.josh.retrofitrssdemo.database.FavoritesDataSource;
import com.example.josh.retrofitrssdemo.model.Item;

import java.util.List;

/**
 * Created by Josh on 5/20/2016.
 */
public class SecondFragment extends Fragment {
    RecyclerView recyclerView;
    FavoritesDataSource dataSource;
    Cursor cursor;
    CursorAdapter mAdapter;
    private List<Item> listItem;

    public SecondFragment(){
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataSource = new FavoritesDataSource(getContext());
        dataSource.open(true);
        cursor = dataSource.getAllBills();
        //mAdapter = new CursorAdapter(getContext(), cursor);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorites_list_test, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //cursor = dataSource.getAllBills();
        mAdapter = new CursorAdapter(getContext(), cursor);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        dataSource.close();
    }

    @Override
    public void onStart() {
        super.onStart();
        cursor = dataSource.getAllBills();
        mAdapter.swapCursor(cursor);
    }
































}
