package com.example.andress.androidgame;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.andress.androidgame.storage.DatabaseHelper;
import com.example.andress.androidgame.ui.kenvector.TextView;

/**
 * This activity shows the Standings saved from every game played by the user. Uses the SQLite
 * database to retrieve the data of how many games has been won, loss, tied and, the fastest
 * time used to finish the game per difficulty (easy, normal and hard).
 */
public class StandingsActivity extends AppCompatActivity {
    public static final String TAG = "StandingsActivity";

    // Database
    SQLiteDatabase db;

    // UI
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standings);

        // UI
        tableLayout = (TableLayout) findViewById(R.id.standingsActivityTableLayout);

        // Fetching data from db to populate table layout
        db = new DatabaseHelper(this).getReadableDatabase();
        Log.i(TAG, "SQLite database initialized");
        Cursor cursor = db.rawQuery("SELECT * FROM standings;", null);
        int cols = cursor.getColumnCount() - 1; // Subtract 1 to eliminate id column
        Log.d(TAG, "Number of columns in table: " + cols);
        while (cursor.moveToNext()) {
            TableRow row = new TableRow(this);
            for (int i = 0; i < cols; i++) {
                // Increment by 1 because SQLite ignores id column
                // and is has a base index of 1 not 0
                TextView textView = new TextView(this);
                if (i == cols - 1 && !cursor.getString(cols).equals("N/A"))
                    textView.setText(cursor.getString(i + 1) + " sec");
                else
                    textView.setText(cursor.getString(i + 1));
                // First column shouldn't be centered
                if (i != 0) {
                    textView.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                row.addView(textView);
            }
            tableLayout.addView(row);
        }
        cursor.close();
        Log.i(TAG, "Cursor has been closed");
        db.close();
        Log.i(TAG, "SQLite database has been closed");
        db = null;
    }
}
