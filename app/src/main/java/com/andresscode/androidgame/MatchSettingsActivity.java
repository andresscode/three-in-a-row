package com.andresscode.androidgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.andresscode.androidgame.settings.Difficulty;
import com.andresscode.androidgame.settings.Pokemon;
import com.andresscode.androidgame.storage.SharedPreferencesKey;
import com.andresscode.androidgame.R;
import com.andresscode.androidgame.ui.kenvector.Button;

/**
 * This activity setups the difficulty and the pokemon that will be used by the user to find
 * a new match. The difficulty and pokemon will be saved in the SharedPreferences to leave the
 * last selection as the default difficulty and pokemon for the next match.
 */
public class MatchSettingsActivity extends AppCompatActivity {
    public static final String TAG = "MatchSettingsActivity";

    // Data
    private SharedPreferences sharedPreferences;

    // UI
    private RadioGroup radioGroupDifficulty;
    private RadioGroup radioGroupPokemon;
    private RadioButton checkedPokemon;
    private Button btnAccept;

    // Helper fields
    private String difficultyStr;
    private int difficultyId;
    private String pokemonStr;
    private int pokemonId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_settings);

        // Fetching saved preferences of difficulty and pokemon
        sharedPreferences = getSharedPreferences(SharedPreferencesKey.FILE_NAME.name(), 0);
        Log.i(TAG, "SharedPreferences initialized");
        difficultyStr = sharedPreferences.getString(SharedPreferencesKey.DIFFICULTY.name(), Difficulty.EASY.toString());
        difficultyId = Difficulty.getByString(difficultyStr).id();
        Log.d(TAG, "SharedPreferences difficulty: " + difficultyStr);
        pokemonStr = sharedPreferences.getString(SharedPreferencesKey.POKEMON.name(), Pokemon.PIKACHU.toString());
        pokemonId = Pokemon.getByString(pokemonStr).id();
        Log.d(TAG, "SharedPreferences pokemon: " + pokemonStr);

        // Selecting difficulty and pokemon in the layout view
        radioGroupDifficulty = (RadioGroup) findViewById(R.id.matchSettingsActivityRadioGroupDifficulty);
        radioGroupDifficulty.check(difficultyId);
        radioGroupPokemon = (RadioGroup) findViewById(R.id.matchSettingsActivityRadioGroupPokemon);
        radioGroupPokemon.check(pokemonId);
        checkedPokemon = (RadioButton) findViewById(pokemonId);
        checkedPokemon.getParent().requestChildFocus(checkedPokemon, checkedPokemon);

        // Setting button
        btnAccept = (Button) findViewById(R.id.matchSettingsActivityButtonAccept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                goToFindingMatch(view);
            }
        });
    }

    /**
     * Saves the difficulty and pokemon selected by the user into the SharedPreferences, then,
     * starts the FindingMatchActivity.
     * @param view Button
     */
    public void goToFindingMatch(View view) {
        // Saving difficulty and pokemon preferences
        difficultyId = radioGroupDifficulty.getCheckedRadioButtonId();
        pokemonId = radioGroupPokemon.getCheckedRadioButtonId();
        sharedPreferences.edit().putString(SharedPreferencesKey.DIFFICULTY.name(), Difficulty.getById(difficultyId).toString()).commit();
        Log.d(TAG, "Difficulty set to " + Difficulty.getById(difficultyId).toString());
        sharedPreferences.edit().putString(SharedPreferencesKey.POKEMON.name(), Pokemon.getById(pokemonId).toString()).commit();
        Log.d(TAG, "Pokemon set to " + Pokemon.getById(pokemonId).toString());
        Intent intent = new Intent(this, FindingMatchActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
