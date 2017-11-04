package com.andresscode.androidgame.gamesession;

import android.support.annotation.Nullable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by Andress on 28/9/17.
 *
 * This class will hold the data for the game session object used by the FindingMatchActivity
 * to push the data required for the Firebase queue. The isWaitingForGuest field will be
 * initialized by default to true because, when a game session is created, this one will be
 * waiting for a Guest user to join. The guest field can be null when is created by a client
 * acting as Owner and will be set up after by any Guest user that joins the game session on
 * Firebase.
 */

@IgnoreExtraProperties
public class GameSession {
    public String firebaseId;
    public boolean isWaitingForGuest;
    public Player owner;
    public Player guest;
    public List<Movement> movements;

    public GameSession() {}

    public GameSession(String firebaseId, Player owner, @Nullable Player guest, List<Movement> movements) {
        this.firebaseId = firebaseId;
        this.isWaitingForGuest = true;
        this.owner = owner;
        this.guest = guest;
        this.movements = movements;
    }

    @Override
    public String toString() {
        return "GameSession { firebaseId = " + this.firebaseId + ", isWaitingForGuest = " +
                this.isWaitingForGuest + ", owner = " + this.owner + ", guest = " + this.guest + " }";
    }
}
