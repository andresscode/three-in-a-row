package com.example.andress.androidgame.settings;

import com.example.andress.androidgame.R;

/**
 * Created by Andress on 27/9/17.
 *
 * This enum class holds the data for the Pokemons:
 *
 *  - str: string name.
 *  - id: identifier for each Pokemon in the radio group of the layout.
 *  - png: the drawable resource of the original png file.
 *  - radio: the drawable resource for the radio buttons.
 *  - tileGreen: the drawable resource for the user tiles on game (Green background).
 *  - tileGreenHud: the drawable resource for the user tiles on the HUD (Green background).
 *  - tileRed: the drawable resource for the opponent's tiles on game (Red background).
 *  - tileRedHud: the drawable resource for the opponent's tiles on the HUD (Red background).
 */

public enum Pokemon {
    PIKACHU ("Pikachu", R.id.matchSettingsActivityPokemonPikachu, R.drawable.pokemon_pikachu, R.drawable.radio_pokemon_pikachu, R.drawable.tile_green_pikachu, R.drawable.tile_green_pikachu_hud, R.drawable.tile_red_pikachu, R.drawable.tile_red_pikachu_hud),
    BULLBASAUR ("Bullbasaur", R.id.matchSettingsActivityPokemonBullbasaur, R.drawable.pokemon_bullbasaur, R.drawable.radio_pokemon_bullbasaur, R.drawable.tile_green_bullbasaur, R.drawable.tile_green_bullbasaur_hud, R.drawable.tile_red_bullbasaur, R.drawable.tile_red_bullbasaur_hud),
    CHARMANDER ("Charmander", R.id.matchSettingsActivityPokemonCharmander, R.drawable.pokemon_charmander, R.drawable.radio_pokemon_charmander, R.drawable.tile_green_charmander, R.drawable.tile_green_charmander_hud, R.drawable.tile_red_charmander, R.drawable.tile_red_charmander_hud),
    SQUIRTLE ("Squirtle", R.id.matchSettingsActivityPokemonSquirtle, R.drawable.pokemon_squirtle, R.drawable.radio_pokemon_squirtle, R.drawable.tile_green_squirtle, R.drawable.tile_green_squirtle_hud, R.drawable.tile_red_squirtle, R.drawable.tile_red_squirtle_hud),
    MEOWTH ("Meowth", R.id.matchSettingsActivityPokemonMeowth, R.drawable.pokemon_meowth, R.drawable.radio_pokemon_meowth, R.drawable.tile_green_meowth, R.drawable.tile_green_meowth_hud, R.drawable.tile_red_meowth, R.drawable.tile_red_meowth_hud),
    EEVEE ("Eevee", R.id.matchSettingsActivityPokemonEevee, R.drawable.pokemon_eevee, R.drawable.radio_pokemon_eevee, R.drawable.tile_green_eevee, R.drawable.tile_green_eevee_hud, R.drawable.tile_red_eevee, R.drawable.tile_red_eevee_hud),
    PSYDUCK ("Psyduck", R.id.matchSettingsActivityPokemonPsyduck, R.drawable.pokemon_psyduck, R.drawable.radio_pokemon_psyduck, R.drawable.tile_green_psyduck, R.drawable.tile_green_psyduck_hud, R.drawable.tile_red_psyduck, R.drawable.tile_red_psyduck_hud),
    SNORLAX ("Snorlax", R.id.matchSettingsActivityPokemonSnorlax, R.drawable.pokemon_snorlax, R.drawable.radio_pokemon_snorlax, R.drawable.tile_green_snorlax, R.drawable.tile_green_snorlax_hud, R.drawable.tile_red_snorlax, R.drawable.tile_red_snorlax_hud);

    private final String str;
    private final int id;
    private final int png;
    private final int radio;
    private final int tileGreen;
    private final int tileGreenHud;
    private final int tileRed;
    private final int tileRedHud;

    Pokemon(String str, int id, int png, int radio, int tileGreen, int tileGreenHud, int tileRed, int tileRedHud) {
        this.str = str;
        this.id = id;
        this.png = png;
        this.radio = radio;
        this.tileGreen = tileGreen;
        this.tileGreenHud = tileGreenHud;
        this.tileRed = tileRed;
        this.tileRedHud = tileRedHud;
    }

    public String str() {
        return str;
    }

    public int id() {
        return id;
    }

    public int png() {
        return png;
    }

    public int radio() {
        return radio;
    }

    public int tileGreen() {
        return tileGreen;
    }

    public int tileGreenHud() {
        return tileGreenHud;
    }

    public int tileRedHud() {
        return tileRedHud;
    }

    public int tileRed() {
        return tileRed;
    }

    public static Pokemon getByString(String str) {
        for (Pokemon p : Pokemon.values()) {
            if (p.str.equals(str)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Name not found on Pokemon Enum");
    }

    public static Pokemon getById(int id) {
        for (Pokemon p : Pokemon.values()) {
            if (p.id == id) {
                return p;
            }
        }
        throw new IllegalArgumentException("Id not found on Pokemon Enum");
    }

    @Override
    public String toString() {
        return str;
    }
}
