package com.example.josh.retrofitrssdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.josh.retrofitrssdemo.R;
import com.example.josh.retrofitrssdemo.model.Item;

import java.util.List;

/**
 * Created by Josh on 4/21/2016.
 */
public class RssAdapter extends RecyclerView.Adapter<RssAdapter.MyRssHolder>{

    Context context;
    LayoutInflater inflater;
    List<Item> itemList;

    public RssAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public MyRssHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_row, parent, false);
        return new MyRssHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyRssHolder holder, int position) {
        final Item billItem = itemList.get(position);
        holder.billTitle.setText(billItem.getTitle());
        holder.billDescription.setText(billItem.getDescription());
        holder.billPubDate.setText(billItem.getPubDate());

        holder.billDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Uri uri = Uri.parse(billItem.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyRssHolder extends RecyclerView.ViewHolder {

        public TextView billTitle;
        public TextView billDescription;
        public TextView billPubDate;

        public MyRssHolder(View itemView){
            super(itemView);
            billTitle = (TextView)itemView.findViewById(R.id.bill_title);
            billDescription = (TextView)itemView.findViewById(R.id.bill_description);
            billPubDate = (TextView)itemView.findViewById(R.id.bill_pub_date);

        }
    }

}
