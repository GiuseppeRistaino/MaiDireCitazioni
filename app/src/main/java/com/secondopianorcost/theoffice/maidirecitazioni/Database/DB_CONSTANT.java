package com.secondopianorcost.theoffice.maidirecitazioni.Database;

/**
 * Created by Giuseppe on 03/03/2016.
 */
public class DB_CONSTANT {

    public static final String DB_NAME = "MaiDireCitazioni.db";
    public static final int DB_VERSION = 2;

    public static final String[] COLUMNS_PERSONAGGIO = new String[] {"id","nome", "file"};
    public static final String TABLE_NAME_PERSONAGGIO = "Personaggio";

    public static final String[] COLUMNS_CITAZIONE = new String[] { "personaggio", "citazione", "file"};
    public static final String TABLE_NAME_CITAZIONE = "Citazione";

    public static final String DYRECTORY = "MaiDireCit";
    public static final String FILE_SHARED = "lastHeared.mp3";
}
