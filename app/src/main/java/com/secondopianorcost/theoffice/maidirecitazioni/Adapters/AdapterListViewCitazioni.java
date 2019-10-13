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
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.secondopianorcost.theoffice.maidirecitazioni.Database.DB_CONSTANT;
import com.secondopianorcost.theoffice.maidirecitazioni.Database.ExternalDbOpenHelper;
import com.theoffice.maidirecitazioni.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 04/03/2016.
 */
public class AdapterListViewCitazioni extends ArrayAdapter<String> {

    private int resource;
    private Context context;
    private List<String> values;
    private ArrayList<String> valuesOrigin;
    private SQLiteDatabase database;

    private ExternalDbOpenHelper dbHelper;

    public AdapterListViewCitazioni(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.values = objects;
        valuesOrigin = new ArrayList<String>();
        valuesOrigin.addAll(values);
        dbHelper = new ExternalDbOpenHelper(context, DB_CONSTANT.DB_NAME);
        database = dbHelper.openDataBase();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(resource, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.textView_nomeCitazione);
        final String nomeCitazione = values.get(position);
        textView.setText(nomeCitazione);

        final ImageButton imageButton = (ImageButton) rowView.findViewById(R.id.button_preferences);

        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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
        });//closing the setOnClickListener method  ;

        String s = values.get(position);
        //Fai qualcosa con la variabile s sempre se ti serve...

        return rowView;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase();
        values.clear();
        if (charText.length() == 0) {
            values.addAll(valuesOrigin);
        } else {
            for (String x : valuesOrigin) {
                if (x.toLowerCase().contains(charText)) {
                    values.add(x);
                }
            }
        }
        notifyDataSetChanged();
    }

}
