package com.andresscode.androidgame.storage;

/**
 * Created by Andress on 26/9/17.
 *
 * This enumeration class contains the keys for the SharedPreferences that will be used by
 * the application to store the data.
 */

public enum SharedPreferencesKey {
    FILE_NAME ("application_data"),
    USERNAME ("username"),
    DIFFICULTY ("difficulty"),
    POKEMON ("pokemon");

    private final String name;

    SharedPreferencesKey(String name) {
        this.name = name;
    }
}
