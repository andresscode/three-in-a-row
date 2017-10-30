package com.example.andress.androidgame.gamesession;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Andress on 1/10/17.
 *
 * Model for sending messages to Firebase. Holds the data of the player who is sending the message,
 * a flag if the game over is true or false, the time left in the clients counter to keep the
 * timers in sync and the index of the movement in the grid view to update keep the grid view in
 * sync.
 */

@IgnoreExtraProperties
public class FirebaseMessage {
    // Types
    public static final String CHILD_PLAYER = "player";
    public static final String CHILD_MOVEMENT = "movement";
    public static final String CHILD_GAME_OVER = "gameOver";
    public static final String CHILD_TIME_LEFT = "timeLeft";

    public String player;
    public boolean gameOver;
    public int timeLeft;
    public int movement;

    public FirebaseMessage() {}

    public FirebaseMessage(String player, boolean gameOver, int timeLeft, int movement) {
        this.player = player;
        this.gameOver = gameOver;
        this.timeLeft = timeLeft;
        this.movement = movement;
    }
}
