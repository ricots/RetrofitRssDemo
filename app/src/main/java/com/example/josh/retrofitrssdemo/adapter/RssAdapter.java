package com.example.josh.retrofitrssdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
    private int expandedPosition = -1;


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
    public void onBindViewHolder(final MyRssHolder holder, int position) {
        position = holder.getAdapterPosition();


        final Item billItem = itemList.get(position);
        holder.billTitle.setText(billItem.getTitle());
        holder.billDescription.setText(billItem.getDescription());


        // Converting Date and Time
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z");
        format.setTimeZone(TimeZone.getTimeZone("EST"));
        try {
            Date parsed = format.parse(billItem.getPubDate());
            TimeZone tz = TimeZone.getTimeZone("America/Detroit");
            SimpleDateFormat destFormat = new SimpleDateFormat("EEE, MMM dd, yyyy hh:mm a");
            destFormat.setTimeZone(tz);
            String result = destFormat.format(parsed);
            holder.billPubDate.setText(result);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dataSource = new FavoritesDataSource(context);
        dataSource.open(false);

        // Save item to database & check if item already saved
//        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (dataSource.ifBillExists(billItem.getTitle())) {
//                    holder.favoriteButton.setImageResource(R.drawable.ic_favorite_empty);
//                    dataSource.removeBill(billItem.getTitle());
//                    Toast.makeText(v.getContext(), "Removed " + billItem.getTitle() + " from Favorites", Toast.LENGTH_SHORT).show();
//                } else {
//                    holder.favoriteButton.setImageResource(R.drawable.ic_favorite_full);
//                    dataSource.insertBill(billItem);
//                }
//            }
//        });
        if (dataSource.ifBillExists(billItem.getTitle())) {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_full);
            //holder.btnLike.setImageResource(R.drawable.ic_favorite_full);
        } else {
            holder.favoriteButton.setImageResource(R.drawable.ic_favorite_empty);
            //holder.btnLike.setImageResource(R.drawable.ic_favorite_empty);
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

        /*
        If list is empty, display message. Alternative option below:
        https://www.reddit.com/r/androiddev/comments/3bjnxi/best_way_to_handle_recyclerview_empty_state/
         */
//        if (itemList.isEmpty()) {
//            holder.emptyText.setVisibility(View.VISIBLE);
//        } else {
//            holder.emptyText.setVisibility(View.GONE);
//        }

        // save item to database with animation
        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                itemList.get(holder.getAdapterPosition());
                if (dataSource.ifBillExists(billItem.getTitle())) {
                    holder.favoriteButton.setImageResource(R.drawable.ic_favorite_empty);
                    dataSource.removeBill(billItem.getTitle());
                    Toast.makeText(v.getContext(), "Removed " + billItem.getTitle() + " from Favorites", Toast.LENGTH_SHORT).show();
                } else {
                    holder.favoriteButton.setImageResource(R.drawable.ic_favorite_full);
                    dataSource.insertBill(billItem);
                }
                notifyItemChanged(adapterPosition, ACTION_LIKE_BUTTON_CLICKED);
            }
        });


        /*
        Expand/Collapse V1, layout xml set to "gone"
         */
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//        if (sharedPreferences.getBoolean("pref_collapse_all", true)) {
//            holder.relTopArea.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (holder.relExpandArea.getVisibility() == View.GONE) {
//                        holder.relExpandArea.setVisibility(View.VISIBLE);
//                    } else if (holder.relExpandArea.getVisibility() == View.VISIBLE) {
//                        holder.relExpandArea.setVisibility(View.GONE);
//                    }
//                }
//            });
//        }



        /*
        Expand/Collapse V2
         */
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPref.getBoolean("pref_collapse_all", true)) {
            if (position == expandedPosition) {
                holder.relExpandArea.setVisibility(View.VISIBLE);
            } else {
                holder.relExpandArea.setVisibility(View.GONE);
            }
            holder.relTopArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (expandedPosition >= 0) {
                        int prev = expandedPosition;
                        notifyItemChanged(prev);
                    }
                    expandedPosition = holder.getAdapterPosition();
                    notifyItemChanged(expandedPosition);
                }
            });
        }
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
        public ImageButton shareButton, favoriteButton, browserButton, btnLike;
        public CardView cardView;
        public RelativeLayout relExpandArea, relTopArea;
        public SharedPreferences mSharedPreferences;
        public FrameLayout rootFrame;

        public MyRssHolder(View itemView) {
            super(itemView);
            billTitle = (TextView) itemView.findViewById(R.id.bill_title);
            billDescription = (TextView) itemView.findViewById(R.id.bill_description);
            billPubDate = (TextView) itemView.findViewById(R.id.bill_pub_date);
            //emptyText = (TextView) itemView.findViewById(R.id.empty_textview);
            favoriteButton = (ImageButton) itemView.findViewById(R.id.heart_saved);
            shareButton = (ImageButton) itemView.findViewById(R.id.share_button);
            browserButton = (ImageButton) itemView.findViewById(R.id.open_in_browser_button);
            //btnLike = (ImageButton)itemView.findViewById(R.id.button_like_anim);
            cardView = (CardView) itemView.findViewById(R.id.card_view_item_row);
            relExpandArea = (RelativeLayout) itemView.findViewById(R.id.bottom_layout_item_row);
            relTopArea = (RelativeLayout) itemView.findViewById(R.id.top_layout_item_row);
            rootFrame = (FrameLayout) itemView.findViewById(R.id.frame_root);

            /*
            Expand/Collapse V1, layout xml set to "gone"
             */
//            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
//            if (mSharedPreferences.getBoolean("pref_collapse_all", true)) {
//                relExpandArea.setVisibility(View.GONE);
//            } else {
//                relExpandArea.setVisibility(View.VISIBLE);
//            }

            /*
            Expand/Collapse V2, layout xml set to "gone"
             */
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (mSharedPreferences.getBoolean("pref_collapse_all", true)){
                relExpandArea.setVisibility(View.GONE);
            } else {
                relExpandArea.setVisibility(View.VISIBLE);
            }
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