package com.example.josh.retrofitrssdemo.database;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.josh.retrofitrssdemo.BillDetailActivity;
import com.example.josh.retrofitrssdemo.R;
import com.example.josh.retrofitrssdemo.model.Item;

import java.util.List;

/**
 * Created by Josh on 4/22/2016.
 */
public class CursorAdapter extends CursorRecyclerViewAdapter<DatabaseViewHolder> {

    LayoutInflater inflater;
    Context context;
    public List<Item> mItemList;
    Item mItem;
    FavoritesDataSource dataSource;
    String charText = "";

    public CursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public void onBindViewHolder(final DatabaseViewHolder viewHolder, final Cursor cursor) {
        int id = cursor.getInt(0);
        String title = cursor.getString(1);
        String description = cursor.getString(2);
        String pubDate = cursor.getString(3);
        String link = cursor.getString(4);
        String guid = cursor.getString(5);

        final Item item = new Item(title, description, pubDate, link, guid);

        viewHolder.title.setText(item.getTitle());
        viewHolder.description.setText(item.getDescription());
        viewHolder.pubDate.setText(item.getPubDate());

        dataSource = new FavoritesDataSource(context);
        dataSource.open(false);

        // Un-Saving/saving from within FavoriteBillsActivity
        //TODO: Handle proper item removal. NotifyItemChanged? Once Removed from favorites ...
        // Click heartImageButton --> AlertDialog asking are you sure you want to remove this item?
            // If yes, remove bill from database and update the list --> notifyItemChanged?
        viewHolder.trashImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = viewHolder.getAdapterPosition();
                /*
                May not need to even call ifBillExists. Just set heartImageButton as full and
                in the onClick, show an alertDialog with "are you sure you want to delete this item?".
                Or add a trash Icon instead**
                 */
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
                                dataSource.removeBill(item.getTitle());
                                notifyItemRemoved(position);
                                //TODO: Fix NotifyItemRangeChanged
                                //http://stackoverflow.com/questions/26517855/using-the-recyclerview-with-a-database
                                notifyItemRangeChanged(position, getItemCount());
                                getCursor().moveToPosition(position);
                                Toast.makeText(context, "Deleted: " + item.getTitle(), Toast.LENGTH_SHORT).show();

                                //notifyItemChanged(position);

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();


//                if (dataSource.ifBillExists(item.getTitle())){
//                    viewHolder.heartImageButton.setImageResource(R.drawable.ic_favorite_empty);
//                    dataSource.removeBill(item.getTitle());
//
//                    Snackbar snackbar = Snackbar.make(v, "Removed from Favorites", Snackbar.LENGTH_LONG);
//                    snackbar.show();
//                } else {
//                    viewHolder.heartImageButton.setImageResource(R.drawable.ic_favorite_full);
//                    dataSource.insertBill(item);
//                    Snackbar snackbar = Snackbar.make(v, "Re-Added " + item.getTitle() + " To Favorites", Snackbar.LENGTH_SHORT);
//                    snackbar.show();
//                }
            }
        });
//        if (dataSource.ifBillExists(item.getTitle())) {
//            viewHolder.heartImageButton.setImageResource(R.drawable.ic_favorite_full);
//        } else {
//            viewHolder.heartImageButton.setImageResource(R.drawable.ic_favorite_empty);
//        }

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
        // Delete in future
        viewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BillDetailActivity.class);
                intent.putExtra(BillDetailActivity.EXTRA_BILL, item);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public DatabaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.database_item_row, parent, false);
        return new DatabaseViewHolder(view);
    }
}
