package com.example.josh.retrofitrssdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

    public RssAdapter(Context context, List<Item> items) {
        this.context = context;
        this.itemList = items;
        inflater = LayoutInflater.from(context);

    }

//    public RssAdapter(Context context){
//        inflater = LayoutInflater.from(context);
//        this.context = context;
//        itemList = new ArrayList<>();
//    }

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
        //holder.billPubDate.setText(billItem.getPubDate());


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
                    //Snackbar snackbar = Snackbar.make(v, "Added " + billItem.getTitle() + " To Favorites!", Snackbar.LENGTH_SHORT);
                    //snackbar.show();
                }
            }
        });
        if (dataSource.ifBillExists(billItem.getTitle())) {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_full);
        } else {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_empty);
        }

        // Share bill
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
        // Open bill in browser
        holder.browserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(billItem.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
        /*
        START:
        Experimenting with AlertDialog.
        Code snippet below adds working save functionality to AlertDialog.
        See: http://stackoverflow.com/questions/8533394/icons-in-a-list-dialog for Adapter alternative.
         */
        holder.dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(billItem.getTitle());

                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.dialog_custom, null);
                final ImageButton img = (ImageButton) view.findViewById(R.id.dialog_save);
                final TextView textDialog = (TextView) view.findViewById(R.id.dialog_text_save);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dataSource.ifBillExists(billItem.getTitle())) {
                            img.setImageResource(R.drawable.ic_favorite_empty);
                            textDialog.setText(R.string.dialog_save);
                            dataSource.removeBill(billItem.getTitle());
                        } else {
                            img.setImageResource(R.drawable.ic_favorite_full);
                            textDialog.setText(R.string.dialog_unsave);
                            dataSource.insertBill(billItem);
                        }
                    }
                });
                dataSource = new FavoritesDataSource(context);
                dataSource.open(false);
                if (dataSource.ifBillExists(billItem.getTitle())) {
                    img.setImageResource(R.drawable.ic_favorite_full);
                    textDialog.setText(R.string.dialog_unsave);
                } else {
                    img.setImageResource(R.drawable.ic_favorite_empty);
                    textDialog.setText(R.string.dialog_save);
                }
                builder.setView(view);
                builder.show();
                /*
                END:
                Code snippet above adds working save functionality to AlertDialog.
                 */
            }
        });

        if (itemList.isEmpty()){
            holder.emptyText.setVisibility(View.VISIBLE);
        } else {
            holder.emptyText.setVisibility(View.GONE);
        }




    }

    @Override
    public int getItemCount() {
        //return itemList.size();
        return itemList == null ? 0 : itemList.size();
    }

    public class MyRssHolder extends RecyclerView.ViewHolder {

        public TextView billTitle, billDescription, billPubDate, emptyText;
        public ImageButton favoriteButton, shareButton, browserButton, dialogButton;
        public CardView cardView;

        public MyRssHolder(View itemView) {
            super(itemView);
            billTitle = (TextView) itemView.findViewById(R.id.bill_title);
            billDescription = (TextView) itemView.findViewById(R.id.bill_description);
            billPubDate = (TextView) itemView.findViewById(R.id.bill_pub_date);
            favoriteButton = (ImageButton) itemView.findViewById(R.id.heart_saved);
            shareButton = (ImageButton) itemView.findViewById(R.id.share_button);
            browserButton = (ImageButton) itemView.findViewById(R.id.open_in_browser_button);
            dialogButton = (ImageButton) itemView.findViewById(R.id.alert_dialog_button);
            cardView = (CardView)itemView.findViewById(R.id.card_view_item_row);
            emptyText = (TextView)itemView.findViewById(R.id.empty_textview);

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

    public void clear() {
        itemList.clear();
        notifyDataSetChanged();
    }
}
