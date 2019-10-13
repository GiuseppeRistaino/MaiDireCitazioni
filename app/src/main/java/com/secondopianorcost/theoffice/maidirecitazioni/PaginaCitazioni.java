package com.secondopianorcost.theoffice.maidirecitazioni;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.secondopianorcost.theoffice.maidirecitazioni.Adapters.AdapterListViewCitazioni;
import com.secondopianorcost.theoffice.maidirecitazioni.Database.DB_CONSTANT;
import com.secondopianorcost.theoffice.maidirecitazioni.Database.ExternalDbOpenHelper;
import com.theoffice.maidirecitazioni.R;

import java.util.ArrayList;

/**
 * Created by Giuseppe on 03/03/2016.
 */
public class PaginaCitazioni extends Activity {

    private ListView listViewCitazioni;
    private EditText editTextFiltraCitazioni;
    private SQLiteDatabase database;
    private ExternalDbOpenHelper dbHelper;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citazioni);


        listViewCitazioni = (ListView) findViewById(R.id.listView_citazioni);
        editTextFiltraCitazioni = (EditText) findViewById(R.id.editText_filtraCitazioni);

        dbHelper = new ExternalDbOpenHelper(this, DB_CONSTANT.DB_NAME);
        database = dbHelper.openDataBase();

        ArrayList<String> listaNomeCitazioni = new ArrayList<String>();

        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            String nome_personaggio = extras.getString("nomePersonaggio");

            String selection = DB_CONSTANT.COLUMNS_CITAZIONE[0] + " = ?";
            String[] selectionArgs = {nome_personaggio};
            Cursor c = database.query(DB_CONSTANT.TABLE_NAME_CITAZIONE, DB_CONSTANT.COLUMNS_CITAZIONE,
                    selection, selectionArgs, null, null
                    , DB_CONSTANT.COLUMNS_CITAZIONE[1]);

            c.moveToFirst();

            do {
                listaNomeCitazioni.add(c.getString(1));
            }
            while(c.moveToNext());
        }

        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.layout_pagina_armi, R.id.textView_layout_pagina_armi_nome,values);*/

        final String packageName = this.getPackageName();

        final AdapterListViewCitazioni adapterPaginaCitazioni = new AdapterListViewCitazioni(
                this, R.layout.layout_listview_citazioni, listaNomeCitazioni);

        listViewCitazioni.setAdapter(adapterPaginaCitazioni);

        listViewCitazioni.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Fai partire l'audio
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                }
                String nomeCitazione = (String)parent.getItemAtPosition(position);
                String selection = DB_CONSTANT.COLUMNS_CITAZIONE[1] + " = ?";
                String[] selectionArgs = {nomeCitazione};
                Cursor c = database.query(DB_CONSTANT.TABLE_NAME_CITAZIONE, DB_CONSTANT.COLUMNS_CITAZIONE,
                        selection, selectionArgs, null, null
                        , null);
                c.moveToNext();
                int idAudio = getResources().getIdentifier(packageName+":raw/" + c.getString(2), null, null);
                mediaPlayer = MediaPlayer.create(PaginaCitazioni.this, idAudio);
                //mediaPlayer.pause();
                mediaPlayer.start();
            }
        });

        listViewCitazioni.setTextFilterEnabled(true);
        editTextFiltraCitazioni.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterPaginaCitazioni.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

}
