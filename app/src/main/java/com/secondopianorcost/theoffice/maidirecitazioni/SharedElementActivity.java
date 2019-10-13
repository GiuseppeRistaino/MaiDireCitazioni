package com.secondopianorcost.theoffice.maidirecitazioni;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.secondopianorcost.theoffice.maidirecitazioni.Adapters.RecyclerCitAdapters;
import com.secondopianorcost.theoffice.maidirecitazioni.Database.DB_CONSTANT;
import com.secondopianorcost.theoffice.maidirecitazioni.Database.ExternalDbOpenHelper;
import com.secondopianorcost.theoffice.maidirecitazioni.Entities.Citation;
import com.secondopianorcost.theoffice.maidirecitazioni.Listeners.CitClickListener;
import com.theoffice.maidirecitazioni.R;
import com.secondopianorcost.theoffice.maidirecitazioni.Entities.Character;

import java.util.ArrayList;

/**
 * Created by giuse on 23/12/2017.
 */

public class SharedElementActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, CitClickListener {

    Toolbar toolbar;

    RecyclerView recyclerView;
    RecyclerCitAdapters recyclerCitAdapters;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Citation> arrayList = new ArrayList<>();

    private SQLiteDatabase database;
    private ExternalDbOpenHelper dbHelper;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // inside your activity (if you did not enable transitions in your theme)
        // For AppCompat getWindow must be called before calling super.OnCreate().
        // Must be called before setContentView
//		getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_element);
        //supportPostponeEnterTransition();

        //Log.i("INFO", "start bundle");

        Bundle extras = getIntent().getExtras();
        Character character = extras.getParcelable(MainActivity.EXTRA_CHARACTER_ITEM);

        ImageView imageView = (ImageView) findViewById(R.id.logo_reveal);
        imageView.setImageResource(character.getFlag());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(character.getName());

        recyclerView = (RecyclerView) findViewById(R.id.cit_recyclerview);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        dbHelper = new ExternalDbOpenHelper(this, DB_CONSTANT.DB_NAME);
        database = dbHelper.openDataBase();

        String nome_personaggio = character.getName();
        String selection = DB_CONSTANT.COLUMNS_CITAZIONE[0] + " = ?";
        String[] selectionArgs = {nome_personaggio};
        Cursor c = database.query(DB_CONSTANT.TABLE_NAME_CITAZIONE, DB_CONSTANT.COLUMNS_CITAZIONE,
                selection, selectionArgs, null, null
                , DB_CONSTANT.COLUMNS_CITAZIONE[1]);

        c.moveToFirst();

        final String packageName = this.getPackageName();
        do {
            int flag = getResources().getIdentifier(packageName+":raw/" + c.getString(2), null, null);
            arrayList.add(new Citation(nome_personaggio, c.getString(1), flag));
        }
        while(c.moveToNext());

        recyclerCitAdapters = new RecyclerCitAdapters(arrayList, this, this);
        recyclerView.setAdapter(recyclerCitAdapters);

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
    public boolean onSupportNavigateUp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        }
        return true;
    }


    @Override
    public void onCitItemClick(int pos, Citation citation) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer = MediaPlayer.create(SharedElementActivity.this, citation.getFlag());
        //mediaPlayer.pause();
        mediaPlayer.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<Citation> newList = new ArrayList<>();
        for (Citation ch : arrayList) {
            String name = ch.getName().toLowerCase();
            if(name.contains(newText)) {
                newList.add(ch);
            }
        }
        recyclerCitAdapters.setFilter(newList);

        return true;
    }
}
