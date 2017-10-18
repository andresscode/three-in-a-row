package com.example.andress.androidgame.gamesession;

import android.support.annotation.Nullable;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Andress on 1/10/17.
 */

//@IgnoreExtraProperties
//public class FirebaseMessage {
//    // Types
//    public static final String MOVEMENT = "movement";
//
//    public String type;
//    public Movement movement;
//
//    public FirebaseMessage() {}
//
//    public FirebaseMessage(String type, Movement movement) {
//        this.type = type;
//        this.movement = movement;
//    }
//}

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
