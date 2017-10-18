package com.example.andress.androidgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.andress.androidgame.storage.DatabaseHelper;
import com.example.andress.androidgame.storage.SharedPreferencesKey;

/**
 * This activity holds the principal menu of the game. From this activity the user can navigate
 * to these three other activities:
 *
 * 1. Find match
 * 2. Standings
 * 3. How to play
 *
 * This activity will check if the in the SharedPreferences if the app is being used for first
 * time to redirect the user to the user's creation activity, then, the How to play activity will
 * be shown to the user to explain how the game works.
 */
public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    // Data
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        DatabaseHelper dbh = new DatabaseHelper(this);
//        SQLiteDatabase db = dbh.getReadableDatabase();
//        dbh.onUpgrade(db, 0, 1);

        // Data
         sharedPreferences = getSharedPreferences(SharedPreferencesKey.FILE_NAME.name(), 0);
        Log.i(TAG, "SharedPreferences initialized");

        // Checking if the username is set already, otherwise start CreateUserActivity
        String sharedPreferencesUsername = sharedPreferences.getString(SharedPreferencesKey.USERNAME.name(), null);
        Log.i(TAG, "Username in SharedPreferences: " + sharedPreferencesUsername);
        if (sharedPreferencesUsername == null) {
            Intent intent = new Intent(this, CreateUserActivity.class);
            startActivity(intent);
        }

        // Set layout if username is set already
        setContentView(R.layout.activity_main);
    }

    public void goToMatchSettings(View view) {
        Intent intent = new Intent(this, MatchSettingsActivity.class);
        startActivity(intent);
    }

    public void goToStandings(View view) {
        Intent intent = new Intent(this, StandingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }
}
