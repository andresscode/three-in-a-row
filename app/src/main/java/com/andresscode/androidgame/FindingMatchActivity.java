package com.andresscode.androidgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.andresscode.androidgame.gamesession.GameSession;
import com.andresscode.androidgame.gamesession.Movement;
import com.andresscode.androidgame.settings.Difficulty;
import com.andresscode.androidgame.settings.Pokemon;
import com.andresscode.androidgame.storage.SharedPreferencesKey;
import com.andresscode.androidgame.R;
import com.andresscode.androidgame.gamesession.Player;
import com.andresscode.androidgame.storage.FirebaseKey;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * This Activity will find a match for the client, following this process:
 *
 *  1. The client will fetch the username, difficulty and pokemon saved in the SharedPreferences.
 *
 *  2. Using the value stored in difficulty from SharedPreferences, the Activity will check once
 *     the firebase/queue/{difficulty} url to verify if there are matches waiting for a Guest
 *     player to join.
 *
 *  3. If there is a match available to join, the client will fetch the GameSession object
 *     from the Firebase to add the Player object [Guest] with its own username and pokemon data
 *     from the SharedPreferences and change the isWaitingForGuest key to false to avoid other
 *     Guest users to join that game session and finally start the GameSessionGuestActivity.
 *
 *  4. If there are no matches waiting for a Guest user to join, the client will push a new
 *     GameSession object into the Firebase/queue/{difficulty}. The GameSession.owner must be
 *     filled with the client data username and pokemon from the SharedPreferences and the
 *     GameSession.guest must be set to null.
 *
 *  5. After creating a new game session into the Firebase server, a new OnDataChange event
 *     listener will be initialized to wait until a Guest user joins the game session to start a
 *     new GameSessionOwnerActivity.
 *
 *  6. If the client is acting as the Owner of the game session and is waiting for a Guest user to
 *     join and the Owner client presses the back button, the game session created will be deleted
 *     from the Firebase queue.
 *
 *  7. This activity must send as an extra the id of the game session on Firebase to the next
 *     activities (Owner and Guest activities).
 */
public class FindingMatchActivity extends AppCompatActivity {
    public static final String TAG = "FindingMatchActivity";
    public static final String FIREBASE_GAME_SESSION_ID = "com.example.andress.androidgame.FIREBASE_GAME_SESSION_ID";
    public static final String GAME_SESSION_TYPE = "com.example.andress.androidgame.GAME_SESSION_TYPE";

    // Types of player in game session
    private static final String OWNER = "owner";
    private static final String GUEST = "guest";

    // SharedPreferences
    private SharedPreferences sharedPreferences;

    // Firebase
    private DatabaseReference firebase;
    private ValueEventListener listenForGuest;

    // Match settings
    private GameSession gameSession;
    private String username;
    private Difficulty difficulty;
    private Pokemon pokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finding_match);

        // Initializing SharedPreferences
        sharedPreferences = getSharedPreferences(SharedPreferencesKey.FILE_NAME.name(), 0);
        Log.i(TAG, "SharedPreferences initialized");

        // Fetching SharedPreferences values
        username = sharedPreferences.getString(SharedPreferencesKey.USERNAME.name(), "Ash");
        Log.d(TAG, "Username: " + username);
        difficulty = Difficulty.getByString(sharedPreferences.getString(SharedPreferencesKey.DIFFICULTY.name(), null));
        Log.d(TAG, "Difficulty:  " + difficulty.toString());
        pokemon = Pokemon.getByString(sharedPreferences.getString(SharedPreferencesKey.POKEMON.name(), "Pikachu"));
        Log.d(TAG, "Pokemon: " + pokemon.toString());

        // Initialing Firebase reference
        final String childDifficulty = difficulty.toString().toLowerCase(); // Lower case to match Firebase syntax
        firebase = FirebaseDatabase.getInstance().getReference(FirebaseKey.REF_QUEUE.toString()).child(childDifficulty);
        Log.i(TAG, "Firebase reference: " + firebase.toString());

        // Checking the desired difficulty queue in Firebase
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Checking if there is a match available
                gameSession = null;
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (d.child("isWaitingForGuest").getValue(boolean.class)) {
                        gameSession = d.getValue(GameSession.class);
                        break;
                    }
                }
                Log.d(TAG, "Game session value " + gameSession);

                // Joining match if was found
                // Starting game session activity as Guest
                if (gameSession != null) {
                    gameSession.isWaitingForGuest = false; // Set to false to avoid another client to join
                    gameSession.guest = new Player(username, pokemon.str()); // Create player as Guest
                    firebase.child(gameSession.firebaseId).setValue(gameSession);
                    Log.d(TAG, "Joined to " + gameSession.toString());
                    goToGameSessionActivity(GUEST);
                }

                // Create new match if there are no matches available to join
                else {
                    DatabaseReference toPush = firebase.push();
                    String gameSessionFirebaseId = toPush.getKey();
                    Player player = new Player(username, pokemon.str());
                    gameSession = new GameSession(gameSessionFirebaseId, player, null, getRandMovements()); // Player as Owner, the Guest must be null, and the initial 4 movements
                    toPush.setValue(gameSession);
                    Log.d(TAG, "Created new " + gameSession.toString());
                    waitForGuest(gameSessionFirebaseId); // Start listening for Guest joined
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, databaseError.getMessage());
            }
        });
    }

    /**
     * Listens for a Guest when joins the created game session.
     * Starting game session activity as Owner.
     *
     * @param childId string with the id of the Firebase game session.
     */
    private void waitForGuest(String childId) {
        listenForGuest = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null && dataSnapshot.child("isWaitingForGuest").getValue(boolean.class) == false) {
                    Log.d(TAG, "Guest has joined the match");
                    goToGameSessionActivity(OWNER);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, databaseError.getMessage());
            }
        }; firebase.child(childId).addValueEventListener(listenForGuest);
    }

    /**
     * Starts the game session activity.
     * @param gameSessionType Could be OWNER or GUEST
     */
    private void goToGameSessionActivity(String gameSessionType) {
        Intent intent = new Intent(this, GameSessionActivity.class);
        switch (gameSessionType) {
            case OWNER:
                intent.putExtra(GAME_SESSION_TYPE, OWNER);
                break;
            case GUEST:
                intent.putExtra(GAME_SESSION_TYPE, GUEST);
                break;
            default:
                throw new IllegalArgumentException("Error with the GameSession type");
        }
        // This listener is created only if the client hast to create the game session and then
        // wait for a guest to join. Must be removed before starting the game session as Owner
        // because these listeners keep working even if the activity has changed. Not removing
        // this listener will cause the GameSessionOwnerActivity to not work properly because
        // the game sessions activities will create a new listener to the same url and this
        // listener will be triggered from the game session activity and cause the game session
        // activity to create restart.
        if (listenForGuest != null) {
            firebase.child(gameSession.firebaseId).removeEventListener(listenForGuest);
        }
        intent.putExtra(FIREBASE_GAME_SESSION_ID, gameSession.firebaseId);
        startActivity(intent);
    }

    // Generates 4 random movements for the initial Grid view setup for the Game session.
    // The first two movements will be marked as "owner" and the next two as "guest"

    /**
     * Generates 4 random movements for the initial Grid view setup for the Game session.
     * The first two movements will be marked as "owner" and the next two as "guest".
     *
     * @return list with 4 unique movements.
     */
    private List<Movement> getRandMovements() {
        int len = difficulty.cols() * difficulty.rows();
        Random random = new Random();
        Set<Integer> movements = new HashSet<>();
        while (movements.size() < 4) {
            movements.add(random.nextInt(len));
        }
        List<Movement> list = new ArrayList<>();
        int count = 0;
        for (Integer i : movements) {
            if (count < 2) {
                list.add(new Movement(i, "owner"));
                count++;
            } else {
                list.add(new Movement(i, "guest"));
            }
        }
        return list;
    }

    @Override
    public void onBackPressed() {
        // Remove match created if the Owner presses back button
        if (gameSession != null && gameSession.isWaitingForGuest) {
            Log.d(TAG, "isWaitingForGuest = " + gameSession.isWaitingForGuest);
            firebase.child(gameSession.firebaseId).removeValue();
            Log.d(TAG, "Removed " + gameSession.toString());
        }

        // Go back to Main Activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
