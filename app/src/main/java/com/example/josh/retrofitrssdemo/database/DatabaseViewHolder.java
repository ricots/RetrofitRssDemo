package com.example.josh.retrofitrssdemo.database;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.josh.retrofitrssdemo.R;

/**
 * Created by Josh on 4/22/2016.
 */
public class DatabaseViewHolder extends RecyclerView.ViewHolder {

    public FrameLayout root;
    public TextView title, description, pubDate;
    public ImageButton trashImageButton, shareImageButton, browserImageButton;
    public RelativeLayout relExpandAreaFav, relTopAreaFav;

    public DatabaseViewHolder(View itemView){
        super(itemView);
        root = (FrameLayout)itemView.findViewById(R.id.root);
        title = (TextView)itemView.findViewById(R.id.bill_fav_title);
        description = (TextView)itemView.findViewById(R.id.bill_fav_description);
        pubDate = (TextView)itemView.findViewById(R.id.bill_fav_pubdate);
        trashImageButton = (ImageButton)itemView.findViewById(R.id.fav_trash_button);
        shareImageButton = (ImageButton)itemView.findViewById(R.id.fav_share_button);
        relExpandAreaFav = (RelativeLayout) itemView.findViewById(R.id.rel_bottom_area_fav);
        relTopAreaFav = (RelativeLayout)itemView.findViewById(R.id.rel_top_area_fav);
        browserImageButton = (ImageButton)itemView.findViewById(R.id.fav_open_in_browser_button);
        //emptyList = (TextView)itemView.findViewById(R.id.empty_textview_database);

    }
}
