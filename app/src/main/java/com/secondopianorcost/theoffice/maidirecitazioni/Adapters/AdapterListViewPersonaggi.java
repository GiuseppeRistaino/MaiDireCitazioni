package com.secondopianorcost.theoffice.maidirecitazioni.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.theoffice.maidirecitazioni.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Giuseppe on 03/03/2016.
 */
public class AdapterListViewPersonaggi extends ArrayAdapter<String> {

    private int resource;
    private Context context;
    private List<String> values;
    private ArrayList<String> valuesOrigin;

    public AdapterListViewPersonaggi(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.values = objects;
        valuesOrigin = new ArrayList<String>();
        valuesOrigin.addAll(values);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(resource, parent, false);

        TextView textView = (TextView) rowView.findViewById(R.id.textView_nomePersonaggio);
        textView.setText(values.get(position));

        ImageView imgView = (ImageView) rowView.findViewById(R.id.imageView_personaggio);
        String nomePg = values.get(position);
        nomePg = nomePg.toLowerCase();
        nomePg = nomePg.replaceAll(" ", "_");

        int imgPg = context.getResources().getIdentifier(context.getPackageName() + ":drawable/" + nomePg, null, null);
        imgView.setImageResource(imgPg);

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
