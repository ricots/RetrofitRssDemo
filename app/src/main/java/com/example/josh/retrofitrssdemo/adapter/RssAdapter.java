package com.example.josh.retrofitrssdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.josh.retrofitrssdemo.R;
import com.example.josh.retrofitrssdemo.database.FavoritesDataSource;
import com.example.josh.retrofitrssdemo.model.Item;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Josh on 4/21/2016.
 */
public class RssAdapter extends RecyclerView.Adapter<RssAdapter.MyRssHolder> {
    public static final String TAG = RssAdapter.class.getSimpleName();
    FavoritesDataSource dataSource;
    Context context;
    LayoutInflater inflater;
    List<Item> itemList;
    private boolean showLoadingView = false;
    public static final int VIEW_TYPE_DEFAULT = 1;
    public static final int VIEW_TYPE_LOADER = 2;
    public static final String ACTION_LIKE_BUTTON_CLICKED = "action_like_button_button";

    public RssAdapter(Context context, List<Item> items) {
        this.context = context;
        this.itemList = items;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyRssHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_row, parent, false);
        // Where should this be called?
        dataSource = new FavoritesDataSource(context);
        dataSource.open(false);

        return new MyRssHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyRssHolder holder, final int position) {
        final Item billItem = itemList.get(position);
        holder.billTitle.setText(billItem.getTitle());
        holder.billDescription.setText(billItem.getDescription());

        // Converting Date and Time
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z");
        format.setTimeZone(TimeZone.getTimeZone("EST"));
        try {
            Date parsed = format.parse(billItem.getPubDate());
            TimeZone tz = TimeZone.getTimeZone("America/Detroit");
            SimpleDateFormat destFormat = new SimpleDateFormat("EEE, MMM dd, yyyy hh:mm:ss a");
            destFormat.setTimeZone(tz);
            String result = destFormat.format(parsed);
            holder.billPubDate.setText(result);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dataSource = new FavoritesDataSource(context);
        dataSource.open(false);

        // Save item to database & check if item already saved
        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataSource.ifBillExists(billItem.getTitle())) {
                    holder.favoriteButton.setImageResource(R.drawable.ic_favorite_empty);
                    dataSource.removeBill(billItem.getTitle());
                    Toast.makeText(v.getContext(), "Removed " + billItem.getTitle() + " from Favorites", Toast.LENGTH_SHORT).show();
                } else {
                    holder.favoriteButton.setImageResource(R.drawable.ic_favorite_full);
                    dataSource.insertBill(billItem);
                }
            }
        });
        if (dataSource.ifBillExists(billItem.getTitle())) {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_full);
            holder.btnLike.setImageResource(R.drawable.ic_favorite_full);
        } else {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_empty);
            holder.btnLike.setImageResource(R.drawable.ic_favorite_empty);
        }

        // Share item
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out " + billItem.getTitle() + " here: " + billItem.getLink());
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, billItem.getTitle());
                shareIntent.setType("text/plain");
                context.startActivity(shareIntent);
            }
        });
        // Open item in browser
        holder.browserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(billItem.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
        // if list is empty, display message.
        if (itemList.isEmpty()){
            holder.emptyText.setVisibility(View.VISIBLE);
        } else {
            holder.emptyText.setVisibility(View.GONE);
        }

        // save item to database with animation
        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                itemList.get(position);
                if (dataSource.ifBillExists(billItem.getTitle())){
                    holder.btnLike.setImageResource(R.drawable.ic_favorite_empty);
                    dataSource.removeBill(billItem.getTitle());
                    Toast.makeText(v.getContext(), "Removed " + billItem.getTitle() + " from Favorites", Toast.LENGTH_SHORT).show();
                } else {
                    holder.btnLike.setImageResource(R.drawable.ic_favorite_full);
                    dataSource.insertBill(billItem);
                }
                notifyItemChanged(adapterPosition, ACTION_LIKE_BUTTON_CLICKED);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoadingView && position == 0) {
            return VIEW_TYPE_LOADER;
        } else {
            return VIEW_TYPE_DEFAULT;
        }
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    public class MyRssHolder extends RecyclerView.ViewHolder {

        public TextView billTitle, billDescription, billPubDate, emptyText;
        public ImageButton favoriteButton, shareButton, browserButton, btnLike;
        public CardView cardView;

        public MyRssHolder(View itemView) {
            super(itemView);
            billTitle = (TextView) itemView.findViewById(R.id.bill_title);
            billDescription = (TextView) itemView.findViewById(R.id.bill_description);
            billPubDate = (TextView) itemView.findViewById(R.id.bill_pub_date);
            favoriteButton = (ImageButton) itemView.findViewById(R.id.heart_saved);
            shareButton = (ImageButton) itemView.findViewById(R.id.share_button);
            browserButton = (ImageButton) itemView.findViewById(R.id.open_in_browser_button);
            cardView = (CardView)itemView.findViewById(R.id.card_view_item_row);
            emptyText = (TextView)itemView.findViewById(R.id.empty_textview);
            btnLike = (ImageButton)itemView.findViewById(R.id.button_like_anim);

        }
    }

    public void addAllBills(List<Item> list) {
        itemList.addAll(list);
        notifyDataSetChanged();
    }

    public void addAll(@NonNull Collection collection) {
        int curSize = getItemCount();
        itemList.addAll(collection);
        notifyItemRangeInserted(curSize, getItemCount());
    }
}


