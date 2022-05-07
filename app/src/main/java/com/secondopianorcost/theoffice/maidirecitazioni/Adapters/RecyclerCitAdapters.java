package com.secondopianorcost.theoffice.maidirecitazioni.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.secondopianorcost.theoffice.maidirecitazioni.Database.DB_CONSTANT;
import com.secondopianorcost.theoffice.maidirecitazioni.Database.ExternalDbOpenHelper;
import com.secondopianorcost.theoffice.maidirecitazioni.Entities.Citation;
import com.secondopianorcost.theoffice.maidirecitazioni.Listeners.CitClickListener;
import com.theoffice.maidirecitazioni.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by giuse on 24/12/2017.
 */

public class RecyclerCitAdapters extends RecyclerView.Adapter<RecyclerCitAdapters.MyViewHolder> {


    ArrayList<Citation> list = new ArrayList<>();
    private CitClickListener citClickListener;

    private SQLiteDatabase database;

    private ExternalDbOpenHelper dbHelper;
    private Context context;

    public RecyclerCitAdapters(ArrayList<Citation> list, CitClickListener citClickListener, Context context ) {
        this.list = list;
        this.citClickListener = citClickListener;
        this.context = context;
        dbHelper = new ExternalDbOpenHelper(context, DB_CONSTANT.DB_NAME);
        database = dbHelper.openDataBase();
    }

    @Override
    public RecyclerCitAdapters.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_cit_layout, parent, false);

        return new RecyclerCitAdapters.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        //holder.o_flag.setImageResource(list.get(position).getFlag());
        holder.o_name.setText(list.get(position).getName());

        final Citation citation = list.get(position);

        //ViewCompat.setTransitionName(holder.o_flag, citation.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                citClickListener.onCitItemClick(holder.getAdapterPosition(), citation);
            }
        });

        holder.o_dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(context, holder.o_dots, holder.o_name.getText().toString(), database);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView o_flag;
        TextView o_name;
        ImageButton o_dots;

        public MyViewHolder(View itemView) {
            super(itemView);

            o_flag = (ImageView) itemView.findViewById(R.id.flag);
            o_name = (TextView) itemView.findViewById(R.id.name);
            o_dots = (ImageButton) itemView.findViewById(R.id.dots);
        }

    }

    public void setFilter(ArrayList<Citation> newList) {

        list = new ArrayList<>();
        list.addAll(newList);
        notifyDataSetChanged();

    }


    public void share(final Context context, ImageButton imageButton, final String nomeCitazione, final SQLiteDatabase database) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(context, imageButton);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu_element_citazioni, popup.getMenu());
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                File dest = Environment.getExternalStorageDirectory();

                String selection = DB_CONSTANT.COLUMNS_CITAZIONE[1] + " = ?";
                String[] selectionArgs = {nomeCitazione};

                Cursor c = database.query(DB_CONSTANT.TABLE_NAME_CITAZIONE,
                        DB_CONSTANT.COLUMNS_CITAZIONE,
                        selection, selectionArgs, null, null
                        , null);
                c.moveToNext();
                int idAudio = context.getResources().getIdentifier(context.getPackageName()
                        +":raw/" + c.getString(2), null, null);

                InputStream in = context.getResources().openRawResource(idAudio);

                try
                {
                    OutputStream out = new FileOutputStream(new File(dest,
                            DB_CONSTANT.FILE_SHARED));
                    byte[] buf = new byte[1024];
                    int len;
                    while ( (len = in.read(buf, 0, buf.length)) != -1)
                    {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                }
                catch (Exception e) {}
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse(
                        Environment.getExternalStorageDirectory().toString() + "/"
                                +DB_CONSTANT.FILE_SHARED));
                share.setType("audio/*");
                context.startActivity(Intent.createChooser(share, "Condividi il suono "));
                return true;

                        /*
                        Uri uri = Uri.parse("android.resource://" + context.getPackageName()+ "/raw/armstrong");
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("audio/mp3");
                        share.putExtra(Intent.EXTRA_STREAM, uri);
                        context.startActivity(Intent.createChooser(share, "Share Sound File"));

                        return true;
                        */

            }
        });

        popup.show();//showing popup menu
    }



}
