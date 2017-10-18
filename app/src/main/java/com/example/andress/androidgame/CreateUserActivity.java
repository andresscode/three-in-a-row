package com.example.andress.androidgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.andress.androidgame.storage.SharedPreferencesKey;

/**
 * This activity creates the username into the SharedPreferences of the device to be used
 * for the game. This activity just will be launched when the game is started for first time.
 * The username cannot be changed after that. The username is used to identify visually each
 * player during a game.
 */
public class CreateUserActivity extends AppCompatActivity {
    public static final String TAG = "CreateUserActivity";

    // Data
    private SharedPreferences sharedPreferences;

    // UI
    private EditText editTextUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        // Data
         sharedPreferences= getSharedPreferences(SharedPreferencesKey.FILE_NAME.name(), 0);
        Log.i(TAG, "SharedPreferences initialized");

        // UI
        editTextUsername = (EditText) findViewById(R.id.createUserActivityEditTextUsername);
    }

    public void setUsername(View view) {
        // Removing all whitespaces
        String strUsername = editTextUsername.getText().toString().replaceAll("\\s+", "");
        // Validating username
        if (strUsername.isEmpty()) {
            Log.d(TAG, "Username is empty");
            Toast toast = Toast.makeText(this, R.string.activity_create_user_toast_message, Toast.LENGTH_SHORT);
            toast.show();
        } else { sharedPreferences.edit().putString(SharedPreferencesKey.USERNAME.name(), strUsername).commit();
            Log.d(TAG, "Username set to " + strUsername);
            // TODO: Create HowToPlayActivity and start it instead of MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}