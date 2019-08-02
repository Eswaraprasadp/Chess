package com.eswar.chess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {
    private static final String TABLE_NAME = "saved_games";
    private static final int DATABASE_VERSION = 1;
    private final static String COLUMN_NAME_ID = DatabaseHelper._ID;
    private final static String COLUMN_NAME_DATE = "date";
    private final static String COLUMN_NAME_MOVES = "moves";
    private final static String COLUMN_NAME_RESULT = "result";
    private final static String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_NAME_ID + " PRIMARY KEY, " +
            COLUMN_NAME_RESULT + " TEXT," +
            COLUMN_NAME_MOVES + " TEXT," +
            COLUMN_NAME_DATE + " TEXT)";

    private final static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    private final static String GET_ALL_ROWS = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_NAME_ID + " DESC";


    public DatabaseHelper(Context context){
        super(context, TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
        db.close();
    }

    public void add(GameRow row){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Gson gson = new Gson();

        values.put(COLUMN_NAME_DATE, row.getDate());
        values.put(COLUMN_NAME_RESULT, row.getResult());
        values.put(COLUMN_NAME_MOVES, gson.toJson(row.getMoveList()));

        long id = db.insert(TABLE_NAME, null, values);
        db.close();
//        values.put();
    }

    public ArrayList<GameRow> getAllRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(GET_ALL_ROWS, null);
        ArrayList<GameRow> games = new ArrayList<>();
        while (cursor.moveToNext()){
            String result = cursor.getString(1);
            String movesString = cursor.getString(2);
            String date = cursor.getString(3);
            Gson gson = new Gson();
            Type type = new TypeToken<List<Move>>(){}.getType();
            List<Move> moves = new ArrayList<>();
            moves = gson.fromJson(movesString, type);
            games.add(new GameRow(result, date, moves));
        }
        db.close();
        return games;
    }
}
