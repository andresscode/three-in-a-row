package com.andresscode.androidgame.gamesession;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Andress on 28/9/17.
 *
 * This class will hold the data of the players to display the correct username and pokemon tile.
 */

@IgnoreExtraProperties
public class Player {
    public String username;
    public String pokemon;

    public Player() {}

    public Player(String username, String pokemon) {
        this.username = username;
        this.pokemon = pokemon;
    }

    @Override
    public String toString() {
        return "Player { username = " + this.username + ", pokemon = "
                + this.pokemon + " }";
    }
}
