package com.secondopianorcost.theoffice.maidirecitazioni;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.secondopianorcost.theoffice.maidirecitazioni.Adapters.AdapterListViewPersonaggi;
import com.secondopianorcost.theoffice.maidirecitazioni.Adapters.RecyclerAdapter;
import com.secondopianorcost.theoffice.maidirecitazioni.Database.DB_CONSTANT;
import com.secondopianorcost.theoffice.maidirecitazioni.Database.ExternalDbOpenHelper;
import com.secondopianorcost.theoffice.maidirecitazioni.Entities.Character;
import com.secondopianorcost.theoffice.maidirecitazioni.Listeners.CharacterClickListener;
import com.theoffice.maidirecitazioni.R;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, CharacterClickListener {

    private ListView listViewPersonaggi;
    private EditText editTextFiltraPersonaggi;
    private SQLiteDatabase database;
    private ExternalDbOpenHelper dbHelper;

    //private AdView adView;

    Toolbar toolbar;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Character> arrayList = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            permissionStart_post_lollipop();
        }

        setContentView(R.layout.activity_main);

        // Instantiate an AdView view
        //adView = new AdView(this, "220854225077402", AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        //LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        //adContainer.addView(adView);

        // Request an ad
        //adView.loadAd();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            exec_post_lollipop();
        }
        else {
            exec_pre_lollipop();
        }

    }

    public void goToUrl(View view) {
        String url = "https://www.facebook.com/MaiDireGol/";
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    /*
    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
    */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File myFile = new File(Environment.getExternalStorageDirectory() +"/" +DB_CONSTANT.FILE_SHARED);
        if(myFile.exists())
            myFile.delete();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_items, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        newText = newText.toLowerCase();
        ArrayList<Character> newList = new ArrayList<>();
        for (Character ch : arrayList) {
            String name = ch.getName().toLowerCase();
            if(name.contains(newText)) {
                newList.add(ch);
            }
        }
        recyclerAdapter.setFilter(newList);

        return true;
    }



    public static final String EXTRA_CHARACTER_ITEM = "animal_image_url";
    public static final String EXTRA_CHARACTER_IMAGE_TRANSITION_NAME = "animal_image_transition_name";

    @Override
    public void onCharacterItemClick(int pos, Character character, ImageView shareImageView) {
        Intent intent = new Intent(this, SharedElementActivity.class);
        /*
        Pair[] pair = new Pair[1];
        pair[0] = new Pair<View, String>(shareImageView, "logo_shared");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pair);
            startActivity(intent, options.toBundle());
        }
        */

        intent.putExtra(EXTRA_CHARACTER_ITEM, character);
        //intent.putExtra(EXTRA_CHARACTER_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(shareImageView));

        Log.i("LOG", ViewCompat.getTransitionName(shareImageView));
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                shareImageView,
                "logo");

        startActivity(intent, options.toBundle());

    }


    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void permissionStart_post_lollipop() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            //return;
        }
        //exec_post_lollipop();
    }

    private void exec_post_lollipop() {
        Log.i("LOG", "caricato il layout");
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.i("LOG", "sono in Lollipop");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        dbHelper = new ExternalDbOpenHelper(this, DB_CONSTANT.DB_NAME);
        database = dbHelper.openDataBase();

        Log.i("LOG", "caricato il database");

        Cursor c = database.query(DB_CONSTANT.TABLE_NAME_PERSONAGGIO, DB_CONSTANT.COLUMNS_PERSONAGGIO,
                null, null, null, null
                , DB_CONSTANT.COLUMNS_PERSONAGGIO[0]);
        Log.i("LOG", "impostato il cursore");
        c.moveToFirst();

        Log.i("LOG", "impostato il cursore");

        final String packageName = this.getPackageName();
        do {
            int flag = getResources().getIdentifier(packageName+":drawable/" + c.getString(2), null, null);
            arrayList.add(new Character(c.getString(1), flag));
        }
        while(c.moveToNext());

        recyclerAdapter = new RecyclerAdapter(arrayList, this);
        recyclerView.setAdapter(recyclerAdapter);

        Log.i("LOG", "settato tutto");


    }

    private void exec_pre_lollipop() {
        //listViewPersonaggi = (ListView) findViewById(R.id.listView_personaggi);
        //editTextFiltraPersonaggi = (EditText) findViewById(R.id.editText_filtraPersonaggi);

        Log.i("LOG", "sono in cacca");
        dbHelper = new ExternalDbOpenHelper(this, DB_CONSTANT.DB_NAME);
        database = dbHelper.openDataBase();

        Cursor c = database.query(DB_CONSTANT.TABLE_NAME_PERSONAGGIO, DB_CONSTANT.COLUMNS_PERSONAGGIO,
                null, null, null, null
                , DB_CONSTANT.COLUMNS_PERSONAGGIO[0]);
        c.moveToFirst();

        ArrayList<String> listaNomePersonaggi = new ArrayList<String>();
        do {
            listaNomePersonaggi.add(c.getString(1));
        }
        while (c.moveToNext());


        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.layout_pagina_armi, R.id.textView_layout_pagina_armi_nome,values);*/

        final AdapterListViewPersonaggi adapterPaginaPersonaggi = new AdapterListViewPersonaggi(
                this, R.layout.layout_listview_personaggi, listaNomePersonaggi);

        listViewPersonaggi.setAdapter(adapterPaginaPersonaggi);

        listViewPersonaggi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent openPageCit = new Intent(MainActivity.this, PaginaCitazioni.class);
                String extra = (String) parent.getItemAtPosition(position);
                openPageCit.putExtra("nomePersonaggio", extra);
                startActivity(openPageCit);
            }
        });

        listViewPersonaggi.setTextFilterEnabled(true);
        editTextFiltraPersonaggi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapterPaginaPersonaggi.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
