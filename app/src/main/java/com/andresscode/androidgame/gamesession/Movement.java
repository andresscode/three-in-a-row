package com.andresscode.androidgame.gamesession;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Andress on 30/9/17.
 *
 * This class will hold the data needed to send movements on Firebase.
 *
 * - index: will contain the number of the tile clicked on the grid view.
 * - player: will contain a string ["owner" || "guest"] to identify who is sending the movement.
 */

@IgnoreExtraProperties
public class Movement {
    public int index;
    public String player;

    public Movement() {}

    public Movement(int index, String player) {
        this.index = index;
        this.player = player;
    }

    @Override
    public String toString() {
        return "Movement { index = " + this.index + ", player = " + this.player + " }";
    }
}
