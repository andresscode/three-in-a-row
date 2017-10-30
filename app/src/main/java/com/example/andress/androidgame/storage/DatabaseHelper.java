package com.example.andress.androidgame.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Andress on 27/9/17.
 *
 * This class creates and drops the SQLite database used for the Standings Activity.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TAG = "DatabaseHelper";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "application_database.db";

    // Create SQL
    private static final String CREATE_TABLE = "CREATE TABLE standings (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "difficulty TEXT NOT NULL UNIQUE," +
            "win TEXT NOT NULL," +
            "lose TEXT NOT NULL," +
            "tie TEXT NOT NULL," +
            "fastest TEXT NOT NULL);";

    private static final String INSERT_ROW_DIFFICULTY_EASY = "INSERT INTO standings (difficulty, win, lose, tie, fastest) VALUES (\"Easy\", \"0\", \"0\", \"0\", \"N/A\")";
    private static final String INSERT_ROW_DIFFICULTY_NORMAL = "INSERT INTO standings (difficulty, win, lose, tie, fastest) VALUES (\"Normal\", \"0\", \"0\", \"0\", \"N/A\")";
    private static final String INSERT_ROW_DIFFICULTY_HARD = "INSERT INTO standings (difficulty, win, lose, tie, fastest) VALUES (\"Hard\", \"0\", \"0\", \"0\", \"N/A\")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
        Log.i(TAG, "Table created in " + DATABASE_NAME);
        sqLiteDatabase.execSQL(INSERT_ROW_DIFFICULTY_EASY);
        Log.i(TAG, "Row created in " + DATABASE_NAME);
        sqLiteDatabase.execSQL(INSERT_ROW_DIFFICULTY_NORMAL);
        Log.i(TAG, "Row created in " + DATABASE_NAME);
        sqLiteDatabase.execSQL(INSERT_ROW_DIFFICULTY_HARD);
        Log.i(TAG, "Row created in " + DATABASE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS standings;");
        onCreate(sqLiteDatabase);
    }
}
