package com.example.josh.retrofitrssdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.josh.retrofitrssdemo.R;
import com.example.josh.retrofitrssdemo.database.FavoritesDataSource;
import com.example.josh.retrofitrssdemo.model.Item;

import java.util.Collection;
import java.util.List;

/**
 * Created by Josh on 4/21/2016.
 */
public class RssAdapter extends RecyclerView.Adapter<RssAdapter.MyRssHolder> {

    FavoritesDataSource dataSource;
    Context context;
    LayoutInflater inflater;
    List<Item> itemList;
    public static final String TAG = RssAdapter.class.getSimpleName();

    private int expandedPosition = -1;
    private int collapsedPosition = 0;
    private int mOriginalHeight = 0;
    private boolean isViewExpanded = false;

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
        holder.billPubDate.setText(billItem.getPubDate());

        dataSource = new FavoritesDataSource(context);
        dataSource.open(false);

        // Save bill to database.
        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataSource.ifBillExists(billItem.getTitle())) {
                    holder.favoriteButton.setImageResource(R.drawable.ic_favorite_empty);
                    dataSource.removeBill(billItem.getTitle());
                    Snackbar snackbar = Snackbar.make(v, "Removed from Favorites", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    holder.favoriteButton.setImageResource(R.drawable.ic_favorite_full);
                    dataSource.insertBill(billItem);
                    Snackbar snackbar = Snackbar.make(v, "Added " + billItem.getTitle() + " To Favorites!", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });
        // Calling again probably not necessary nor doing anything?
        dataSource = new FavoritesDataSource(context);
        dataSource.open(false);

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
        Experimenting using an AlertDialog vs. setting click listeners on the item_row.xml
        Probably best to keep code as implemented above. Important to quickly visualize which items are saved
        and quickly unsave/save items while scrolling through the list.
        --> Is it safe to do all this within the adapter?
         */

        /*
        START:
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
                See: http://stackoverflow.com/questions/8533394/icons-in-a-list-dialog for Adapter alternative.
                 */
            }
        });
        /*
        START:
        Expandable Recyclerview Testing
        Currently working, but only collapses when clicking on another card and not the same.
         */
        if (position == expandedPosition) {
            holder.relExpandArea.setVisibility(View.VISIBLE);
        } else {
            holder.relExpandArea.setVisibility(View.GONE);
        }
        holder.relTopArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // checking for an expanded view, collapse if you find one
                if (expandedPosition >= 0) {
                    int prev = expandedPosition;
                    notifyItemChanged(prev);
                }
                // set the current position to "expanded"
                expandedPosition = holder.getAdapterPosition();
                notifyItemChanged(expandedPosition);
            }
        });
        /*
        END:
        Expandable Recyclerview Testing
         */
    }

    @Override
    public int getItemCount() {
        //return itemList.size();
        return itemList == null ? 0 : itemList.size();
    }

    public class MyRssHolder extends RecyclerView.ViewHolder {

        public TextView billTitle, billDescription, billPubDate;
        public ImageButton favoriteButton, shareButton, browserButton, dialogButton;
        public RelativeLayout relExpandArea, relTopArea;

        public MyRssHolder(View itemView) {
            super(itemView);
            billTitle = (TextView) itemView.findViewById(R.id.bill_title);
            billDescription = (TextView) itemView.findViewById(R.id.bill_description);
            billPubDate = (TextView) itemView.findViewById(R.id.bill_pub_date);
            favoriteButton = (ImageButton) itemView.findViewById(R.id.heart_saved);
            shareButton = (ImageButton) itemView.findViewById(R.id.share_button);
            browserButton = (ImageButton) itemView.findViewById(R.id.open_in_browser_button);
            dialogButton = (ImageButton) itemView.findViewById(R.id.alert_dialog_button);
            relExpandArea = (RelativeLayout) itemView.findViewById(R.id.rlExpandArea);
            relTopArea = (RelativeLayout)itemView.findViewById(R.id.top_layout_item_row);


        }
    }

    public void addAllBills(List<Item> list) {
        itemList.addAll(list);
        notifyDataSetChanged();
    }

    //TODO: Delete if fail
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
