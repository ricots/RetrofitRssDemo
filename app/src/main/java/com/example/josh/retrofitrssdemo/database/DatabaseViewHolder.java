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
    public TextView title;
    public TextView description;
    public TextView pubDate;
    public ImageButton trashImageButton;
    public ImageButton shareImageButton;
    public RelativeLayout relExpandAreaFav, relTopAreaFav;

    public DatabaseViewHolder(View itemView){
        super(itemView);
        root = (FrameLayout)itemView.findViewById(R.id.root);
        title = (TextView)itemView.findViewById(R.id.bill_fav_title);
        description = (TextView)itemView.findViewById(R.id.bill_fav_description);
        pubDate = (TextView)itemView.findViewById(R.id.bill_fav_pubdate);
        trashImageButton = (ImageButton)itemView.findViewById(R.id.fav_trash_button);
        shareImageButton = (ImageButton)itemView.findViewById(R.id.fav_share_button);
        relExpandAreaFav = (RelativeLayout) itemView.findViewById(R.id.rlExpandAreaFav);
        relTopAreaFav = (RelativeLayout)itemView.findViewById(R.id.rel_top_area_fav);

    }
}
