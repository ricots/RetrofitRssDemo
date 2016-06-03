package com.example.josh.retrofitrssdemo.database;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.josh.retrofitrssdemo.R;
import com.example.josh.retrofitrssdemo.model.Item;

/**
 * Created by Josh on 4/22/2016.
 */
public class CursorAdapter extends CursorRecyclerViewAdapter<DatabaseViewHolder> {

    // TODO CursorLoader?
    public static final String TAG = "CursAdapterItemCount";
    LayoutInflater inflater;
    Context context;
    FavoritesDataSource dataSource;
    //private int expandedPosition = -1;

    public CursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(final DatabaseViewHolder viewHolder, final Cursor cursor) {
        //final int position = getCursor().getPosition();
        int id = cursor.getInt(0);
        String title = cursor.getString(1);
        String description = cursor.getString(2);
        String pubDate = cursor.getString(3);
        String link = cursor.getString(4);
        String guid = cursor.getString(5);

        final Item item = new Item(title, description, pubDate, link, guid);

        Log.d(TAG, "Count: " + getItemCount());

        viewHolder.title.setText(item.getTitle());
        viewHolder.description.setText(item.getDescription());
        viewHolder.pubDate.setText(item.getPubDate());

        dataSource = new FavoritesDataSource(context);
        dataSource.open(false);
        /*
        START:
        Expandable Recyclerview
         */
//        if (position == expandedPosition) {
//            viewHolder.relExpandAreaFav.setVisibility(View.VISIBLE);
//        } else {
//            viewHolder.relExpandAreaFav.setVisibility(View.GONE);
//        }
//        viewHolder.relTopAreaFav.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//                if (expandedPosition >= 0) {
//                    int prev = expandedPosition;
//                    notifyItemChanged(prev);
//                }
//                expandedPosition = viewHolder.getAdapterPosition();
//                notifyItemChanged(expandedPosition);
//            }
//        });
        /*
        END:
        Expandable Recyclerview
         */


        // Removing Items from Favorites
        //TODO: Handle proper item removal. NotifyItemChanged? Once Removed from favorites ...
        viewHolder.trashImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final int position = viewHolder.getAdapterPosition();
                Log.d(TAG, "Clicked: " + position);
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                builder.setMessage("Delete from your Favorites?")
                        .setCancelable(true)
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // remove the element at particular position from the list
                                dataSource.removeBill(item.getTitle());
                                // notifies RecyclerView Adapter that data in adapter has been removed at a particular position
                                notifyItemRemoved(position);
                                // TODO: Fix notifyItemRangeChanged:
                                notifyItemRangeChanged(position, getItemCount());
                                swapCursor(dataSource.getAllBills());
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });


        // Share bill:
        viewHolder.shareImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out " + item.getTitle() + " here: " + item.getLink());
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, item.getTitle());
                shareIntent.setType("text/plain");
                context.startActivity(shareIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (getCursor() == null || getCursor().isClosed()){
            return 0;
        }
        return getCursor().getCount();
    }

    @Override
    public DatabaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.database_item_row, parent, false);
        return new DatabaseViewHolder(view);
    }
}