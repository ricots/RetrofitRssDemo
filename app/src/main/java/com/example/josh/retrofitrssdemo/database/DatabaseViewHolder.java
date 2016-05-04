package com.example.josh.retrofitrssdemo.database;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.josh.retrofitrssdemo.R;

/**
 * Created by Josh on 4/22/2016.
 */
public class DatabaseViewHolder extends RecyclerView.ViewHolder {

    public FrameLayout root;
    public TextView title;
    public TextView description;
    public TextView pubDate;

    public DatabaseViewHolder(View itemView){
        super(itemView);
        root = (FrameLayout)itemView.findViewById(R.id.root);
        title = (TextView)itemView.findViewById(R.id.bill_fav_title);
        description = (TextView)itemView.findViewById(R.id.bill_fav_description);
        pubDate = (TextView)itemView.findViewById(R.id.bill_fav_pubdate);
    }
}
