package com.example.andress.androidgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.andress.androidgame.settings.Difficulty;
import com.example.andress.androidgame.settings.Pokemon;
import com.example.andress.androidgame.storage.SharedPreferencesKey;

/**
 * This activity will handle the match settings to be used for pre-select the difficulty and
 * pokemon radio buttons and, for the data that must be passed to the Firebase server to
 * find a match with the desired settings.
 *
 *  - Difficulty: this is handled by the Difficulty enum class. The activity layout must arrange
 *                the difficulties in the same order of the ids in the java enum and each radio
 *                radio button in the layout will have a text parameter (transparent) to be used
 *                as the id to match the view and the java enum.
 *
 *  - Pokemon: this is handled by the Pokemon enum class. The activity layout must arrange
 *             the pokemons in the same order of the ids in the java enum and each radio
 *             radio button in the layout will have a text parameter (transparent) to be used
 *             as the id to match the view and the java enum.
 */
public class MatchSettingsActivity extends AppCompatActivity {
    public static final String TAG = "MatchSettingsActivity";

    // Data
    private SharedPreferences sharedPreferences;

    // UI
    private RadioGroup radioGroupDifficulty;
    private RadioGroup radioGroupPokemon;
    private RadioButton checkedPokemon;

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
    }

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
