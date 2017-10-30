package com.example.andress.androidgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.andress.androidgame.gamesession.FirebaseMessage;
import com.example.andress.androidgame.gamesession.ImageAdapter;
import com.example.andress.androidgame.settings.Difficulty;
import com.example.andress.androidgame.settings.Pokemon;
import com.example.andress.androidgame.storage.DatabaseHelper;
import com.example.andress.androidgame.storage.FirebaseKey;
import com.example.andress.androidgame.storage.SharedPreferencesKey;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This is the activity where the game session is played. This activity follows this process:
 *
 *    1. Get the game session id in Firebase and the type of session [OWNER or GUEST] from the
 *    match settings activity intent to set the FirebaseMessage listeners to the game session
 *    child.
 *
 *    2. Gets the Difficulty, Pokemon and Username from the SharedPreferences of the local client
 *    to set up the layout and views.
 *
 *    3. Setups the HUDs and the counter of available movements.
 *
 *    4. Create the timers handlers.
 *
 *    5. Fetch the opponents username from Firebase and the initial movements to setup the
 *    grid view.
 *
 *    6. Starts the listener to the Firebase server.
 */
public class GameSessionActivity extends AppCompatActivity {
    public static final String TAG = "GameSessionActivity";

    private static final String OWNER = "owner";
    private static final String GUEST = "guest";
    private static final String WIN = "win";
    private static final String LOSE = "lose";
    private static final String TIE = "tie";

    // Game session data
    private String gameSessionType;
    private String gameSessionId;

    // Game Play Flags
    private boolean gameOver = false;
    private boolean gameHasStarted = false;
    private boolean clientReady = false;
    private boolean myTurn; // true if owner, false if guest
    private int movementsLeft;

    // Firebase
    private DatabaseReference firebaseGameSessionRef;
    private ValueEventListener listenForMessage;

    // Local data [Used to setup the game session]
    private SharedPreferences sharedPreferences;
    private Difficulty difficulty;

    // Local player data
    private String usernameLocal;
    private Pokemon pokemonLocal;
    private int timeLeftLocal;

    // Opponent data
    private String usernameOpponent;
    private Pokemon pokemonOpponent;
    private int timeLeftOpponent;

    // Timers
    public Handler handler;
    private Timer localTimer;
    private TimerTask localTimerTask;
    private Timer opponentTimer;
    private TimerTask opponentTimerTask;

    // Grid view data
    private Integer[] gridViewArray;

    // UI Game Over Panel
    private RelativeLayout gameOverPanel;
    private TextView gameOverTextViewResult;

    // UI [HUD Top = local player]
    private RelativeLayout hudBarTop;
    private TextView hudTileTop;
    private TextView hudUsernameTop;
    private TextView hudActionTop;
    private TextView hudTimerTop;

    // IU [HUD Bottom = opponent]
    private RelativeLayout hudBarBottom;
    private TextView hudTileBottom;
    private TextView hudUsernameBottom;
    private TextView hudActionBottom;
    private TextView hudTimerBottom;

    // UI Board
    private GridView gridView;
    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_session);

        // Hiding game over panel
        gameOverPanel = (RelativeLayout) findViewById(R.id.gameSessionActivityGameOverPanel);
        gameOverTextViewResult = (TextView) findViewById(R.id.gameSessionActivityTextViewGameOverResult);
        gameOverPanel.setVisibility(View.GONE);

        // Getting views
        getViewsFromLayout();

        // Getting data from FindMatchingActivity
        getDataFromPreviousActivity();

        // Fetching local data
        fetchLocalData();

        // Setting turns order
        setInitialTurn();

        // Setting up UI possible from local storage
        setViewsFromLocalStorage();

        // Setting timers
        // This handler is to change the views from the activity because each timer task is a
        // new thread, and those new threads cannot modify the views from the activity.
        // TODO: Create service to fix bug of local timer expiring while the app is on the background. Does not send notification to Firebase to finish the game session.
        handler = new Handler() {
            public void handleMessage(Message msg) {
                hudTimerTop.setText(String.valueOf(timeLeftLocal));
                if(timeLeftLocal == 0) {
                    gameOver = true;
                    firebaseGameSessionRef
                            .child("message")
                            .setValue(new FirebaseMessage(gameSessionType, gameOver, timeLeftLocal, -1));
                    gameOverView(LOSE);
                    stopLocalTimer();
                }
                if(timeLeftOpponent >= 0)
                    hudTimerBottom.setText(String.valueOf(timeLeftOpponent));
            }
        };
        setTimersTasks();
        startLocalTimer();
        starOpponentTimer();

        // Set Firebase references
        setFirebaseReference();

        // Fetching initial data from Firebase
        getInitialFirebaseData();

        // Start listener to game session messages
        startListenToGameSessionMessages();

        // Make the grid view clickable
        setGridViewListener();
    }

    /**
     * =============================================================================================
     * Helper Methods
     * =============================================================================================
     */

    /**
     * Getting the references of the views in the layout
     */
    private void getViewsFromLayout() {
        // HUD Top [local player]
        hudBarTop = (RelativeLayout) findViewById(R.id.gameSessionActivityHudBarTop);
        hudTileTop = (TextView) findViewById(R.id.gameSessionActivityHudTileTop);
        hudUsernameTop = (TextView) findViewById(R.id.gameSessionActivityHudUsernameTop);
        hudActionTop = (TextView) findViewById(R.id.gameSessionActivityHudActionTop);
        hudTimerTop = (TextView) findViewById(R.id.gameSessionActivityHudTimerTop);
        // HUD Bottom [opponent]
        hudBarBottom = (RelativeLayout) findViewById(R.id.gameSessionActivityHudBarBottom);
        hudTileBottom = (TextView) findViewById(R.id.gameSessionActivityHudTileBottom);
        hudUsernameBottom = (TextView) findViewById(R.id.gameSessionActivityHudUsernameBottom);
        hudActionBottom = (TextView) findViewById(R.id.gameSessionActivityHudActionBottom);
        hudTimerBottom = (TextView) findViewById(R.id.gameSessionActivityHudTimerBottom);
        // Grid view
        gridView = (GridView) findViewById(R.id.gameSessionActivityGridView);
    }

    /**
     * Getting the gameSessionType and gameSessionId to match Firebase.
     */
    private void getDataFromPreviousActivity() {
        Intent intent = getIntent();
        gameSessionId = intent.getStringExtra(FindingMatchActivity.FIREBASE_GAME_SESSION_ID);
        Log.d(TAG, "Game session ID: " + gameSessionId);
        gameSessionType = intent.getStringExtra(FindingMatchActivity.GAME_SESSION_TYPE);
        Log.d(TAG, "Game session type: " + gameSessionType);
    }

    /**
     * Fetching local data [difficulty, username (local), pokemon (local)]
     */
    private void fetchLocalData() {
        sharedPreferences = getSharedPreferences(SharedPreferencesKey.FILE_NAME.name(), 0);
        difficulty = Difficulty.getByString(sharedPreferences.getString(SharedPreferencesKey.DIFFICULTY.name(), null));
        Log.d(TAG, "Game session difficulty: " + difficulty.toString());
        usernameLocal = sharedPreferences.getString(SharedPreferencesKey.USERNAME.name(), null);
        Log.d(TAG, "Game session local username: " + usernameLocal);
        pokemonLocal = Pokemon.getByString(sharedPreferences.getString(SharedPreferencesKey.POKEMON.name(), null));
        Log.d(TAG, "Game session local pokemon: " + pokemonLocal.toString());
        timeLeftLocal = difficulty.time();
        timeLeftOpponent = difficulty.time();
    }

    /**
     * Initial turn [Owner always will start]
     */
    private void setInitialTurn() {
        switch (gameSessionType) {
            case OWNER:
                myTurn = true;
                break;
            case GUEST:
                myTurn = false;
                break;
            default:
                myTurn = false;
        }
        movementsLeft = difficulty.cols() * difficulty.rows() - 4;
        Log.d(TAG, "Movements left " + movementsLeft);
        Log.d(TAG, "myTurn: " + myTurn);
    }

    /**
     * Changes the background and action text of the HUDs when the players changes turn.
     */
    private void changeTurnHud() {
        if (myTurn) {
            hudBarTop.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.panel_blue_rounded, null));
            hudActionTop.setText(R.string.activity_game_session_text_view_hud_action_play);
            hudBarBottom.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.panel_grey_rounded, null));
            hudActionBottom.setText(R.string.activity_game_session_text_view_hud_action_wait);
        } else {
            hudBarTop.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.panel_grey_rounded, null));
            hudActionTop.setText(R.string.activity_game_session_text_view_hud_action_wait);
            hudBarBottom.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.panel_blue_rounded, null));
            hudActionBottom.setText(R.string.activity_game_session_text_view_hud_action_play);
        }
    }

    /**
     * Create an initial grid view without tiles (for displaying purposes)
     */
    private void setEmptyGridView() {
        gridViewArray = new Integer[difficulty.cols() * difficulty.rows()];
        for (int i = 0; i < gridViewArray.length; i++) {
            gridViewArray[i] = R.drawable.tile_pokeball;
        }
        imageAdapter = new ImageAdapter(this, gridViewArray);
        gridView.setAdapter(imageAdapter);
        switch (gridView.getCount()) {
            case 16:
                gridView.setNumColumns(4);
                break;
            case 25:
                gridView.setNumColumns(5);
                break;
            case 36:
                gridView.setNumColumns(6);
                break;
            default:
                gridView.setNumColumns(4);
                break;
        }
    }

    /**
     * Setting UI possible from local storage [HudBarTop, HudBarBottomBackground and Timers]
     */
    private void setViewsFromLocalStorage() {
        changeTurnHud();
        hudTileTop.setBackground(ResourcesCompat.getDrawable(getResources(), pokemonLocal.tileGreenHud(), null));
        hudUsernameTop.setText(usernameLocal);
        hudTimerTop.setText(String.valueOf(timeLeftLocal));
        hudTimerBottom.setText(String.valueOf(timeLeftOpponent));
        setEmptyGridView();
    }

    /**
     * Setting timers tasks
     */
    private void setTimersTasks() {
        localTimerTask = new TimerTask() {
            @Override
            public void run() {
            if (!gameOver && myTurn && gameHasStarted && movementsLeft > 0) {
                handler.obtainMessage().sendToTarget();
                timeLeftLocal--;
            }
            }
        };

        opponentTimerTask = new TimerTask() {
            @Override
            public void run() {
            if (!gameOver && !myTurn && gameHasStarted && movementsLeft > 0) {
                handler.obtainMessage().sendToTarget();
                timeLeftOpponent--;
            }
            }
        };
    }

    /**
     * Start timer
     */
    private void startLocalTimer() {
        localTimer = new Timer();
        localTimer.scheduleAtFixedRate(localTimerTask, 1000, 1000);
    }

    /**
     * Stop timer
     */
    private void stopLocalTimer() {
        if (localTimer !=  null)
            localTimer.cancel();
    }

    /**
     * Start timer
     */
    private void starOpponentTimer() {
        opponentTimer = new Timer();
        opponentTimer.scheduleAtFixedRate(opponentTimerTask, 1000, 1000);
    }

    /**
     * Stop timer
     */
    private void stopOpponentTimer() {
        if (opponentTimer != null)
            opponentTimer.cancel();
    }

    /**
     * Setting the URL reference to firebase/queue/{difficulty}/{gameSessionId}
     */
    private void setFirebaseReference() {
        firebaseGameSessionRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(FirebaseKey.REF_QUEUE.toString())
                .child(difficulty.toString().toLowerCase())
                .child(gameSessionId);
        Log.d(TAG, firebaseGameSessionRef.toString());
    }

    /**
     * This method will fetch the username and pokemon data of the opponent, the 4 initial random
     * movements that must be added to the grid view and will update the UI for the local client.
     */
    private void getInitialFirebaseData() {
        firebaseGameSessionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Selecting the right child for the opponent data
                DataSnapshot forHud;
                switch (gameSessionType) {
                    case OWNER:
                        forHud = dataSnapshot.child(GUEST);
                        break;
                    case GUEST:
                        forHud = dataSnapshot.child(OWNER);
                        break;
                    default:
                        forHud = null;
                }

                // Pokemon
                pokemonOpponent = Pokemon
                        .getByString(forHud
                                .child(FirebaseKey.CHILD_POKEMON.toString())
                                .getValue(String.class));
                hudTileBottom.setBackground(ResourcesCompat
                        .getDrawable(getResources(), pokemonOpponent.tileRedHud(), null));

                // Username
                usernameOpponent = forHud
                        .child(FirebaseKey.CHILD_USERNAME.toString())
                        .getValue(String.class);
                hudUsernameBottom.setText(usernameOpponent);

                // Getting initial movements
                DataSnapshot forGridView = dataSnapshot
                        .child(FirebaseKey.CHILD_MOVEMENTS.toString());

                for (DataSnapshot d : forGridView.getChildren()) {
                    int index = d
                            .child(FirebaseKey.CHILD_MOVEMENTS_INDEX.toString())
                            .getValue(Integer.class);
                    String player = d
                            .child(FirebaseKey.CHILD_MOVEMENTS_PLAYER.toString())
                            .getValue(String.class);
                    if (player.equals(gameSessionType)) {
                        gridViewArray[index] = pokemonLocal.tileGreen();
                    } else {
                        gridViewArray[index] = pokemonOpponent.tileRed();
                    }
                    imageAdapter.notifyDataSetChanged();
                }
                clientReady = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    /**
     * This method initializes a constant event listener to the game session messages ["message"]
     * child to sync the client with the Firebase server to keep both devices up to date with
     * the data transfer.
     */
    private void startListenToGameSessionMessages() {
        listenForMessage = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null &&
                        !dataSnapshot
                                .child(FirebaseMessage.CHILD_PLAYER)
                                .getValue(String.class)
                                .equals(gameSessionType)) {
                    if (!gameHasStarted && gameSessionType.equals(GUEST)) {
                        gameHasStarted = true;
                    }
                    FirebaseMessage message = dataSnapshot.getValue(FirebaseMessage.class);
                    timeLeftOpponent = message.timeLeft;
                    if (message.gameOver) {
                        gameOver = true;
                    }
                    if (message.movement != -1) {
                        gridViewArray[message.movement] = pokemonOpponent.tileRed();
                        imageAdapter.notifyDataSetChanged();
                        movementsLeft--;
                    }
                    if (gameOver) {
                        gameOverView(WIN);
                    } else if (movementsLeft == 0) {
                        gameOverView(TIE);
                    }else {
                        myTurn = true;
                        changeTurnHud();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, databaseError.getMessage());
            }
        };
        firebaseGameSessionRef.child("message").addValueEventListener(listenForMessage);
    }

    /**
     * The client could click an item from the grid view when myTurn is true and the
     * clientReady is true. Every click will trigger a message to the Firebase server
     * to be listened by the opponent and also the UI updates needed like changing the HUDs
     * colors, action texts and timers
     */
    private void setGridViewListener() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (clientReady && !gameOver && myTurn && movementsLeft > 0) {
                    // If the client clicks over empty tile will be a valid movement.
                    // The UI will be updated and a message will be send to Firebase with the
                    // movement data for the opponent to update its data
                    int tileClicked = imageAdapter.getItem(i);
                    if (tileClicked == R.drawable.tile_pokeball) {
                        gridViewArray[i] = pokemonLocal.tileGreen();
                        imageAdapter.notifyDataSetChanged();
                        movementsLeft--;
                        gameOver = gameOverByInvalidMovement(gridViewArray, i, pokemonLocal.tileGreen());
                        if (gameOver) {
                            gameOverView(LOSE);
                        } else if (movementsLeft == 0) {
                            gameOverView(TIE);
                        } else {
                            myTurn = false;
                            changeTurnHud();
                            if (!gameHasStarted && gameSessionType.equals(OWNER)) {
                                gameHasStarted = true;
                            }
                        }
                        firebaseGameSessionRef
                                .child("message")
                                .setValue(new FirebaseMessage(gameSessionType, gameOver, timeLeftLocal, i));
                    }
                }
            }
        });
    }

    /**
     * This algorithm check if there are three tiles in a row vertically or horizontally.
     *
     * @param array integers array of the grid view.
     * @param index int with the index of the new tile placed to be verified.
     * @param tile int with the id of the tile of the player.
     * @return true if the movement is invalid, otherwise, false.F
     */
    private boolean gameOverByInvalidMovement(Integer[] array, int index, int tile) {
        int size = array.length;
        int lastIndex = size - 1;
        int sqrt = (int) Math.sqrt(size); // Number of cols and rows
        int posLimit = sqrt - 1; // Bottom and Right boundary

        int maxReps = 2;
        boolean flagHor = false;
        int horizontal = 0;
        int tmpHor = index;
        boolean flagVer = false;
        int vertical = 0;
        int tmpVer = index;

        for (int i = 0; i < maxReps; i++) {
            if ((i == 0 || horizontal == -1) && tmpHor % sqrt - 1 >= 0 && tmpHor - 1 >= 0 && array[tmpHor - 1] == tile) {
                Log.d(TAG, "Validating left index");
                flagHor = true;
                horizontal--;
            }
            if ((i == 0 || horizontal == 1) && tmpHor % sqrt + 1 <= posLimit && tmpHor + 1 <= lastIndex && array[tmpHor + 1] == tile) {
                Log.d(TAG, "Validating right index");
                flagHor = true;
                horizontal++;
            }
            if ((i == 0 || vertical == -sqrt) && tmpVer / sqrt - 1 >= 0 && tmpVer - sqrt >= 0 && array[tmpVer - sqrt] == tile) {
                Log.d(TAG, "Validating top index");
                flagVer = true;
                vertical -= sqrt;
            }
            if ((i == 0 || vertical == sqrt) && tmpVer / sqrt + 1 <= posLimit && tmpVer + sqrt <= lastIndex && array[tmpVer + sqrt] == tile) {
                Log.d(TAG, "Validating bottom index");
                flagVer = true;
                vertical += sqrt;
            }
            if ((flagHor && horizontal == 0) || (flagVer && vertical == 0)) {
                Log.d(TAG, "First evaluation = flagHor:" + flagHor + " horizontal:" + horizontal + " flagVer:" + flagVer + " vertical:" + vertical);
                return true;
            }
            if (i == 0) {
                tmpHor += horizontal;
                tmpVer += vertical;
            } else if ((horizontal == 2 || horizontal == -2) || (vertical == (sqrt * 2) || vertical == (sqrt * -2))) {
                Log.d(TAG, "Second evaluation = horizontal:" + horizontal + " vertical:" + vertical);
                return true;
            }
        }
        return false;
    }

    /**
     * Setting the Game Over panel
     *
     * @param result string [WIN, LOSE, TIE]
     */
    private void gameOverView(String result) {
        stopLocalTimer();
        stopOpponentTimer();
        Animation zoomOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);
        addListenerToAnimation(zoomOut);
        hudBarBottom.startAnimation(zoomOut);
        switch (result) {
            case WIN:
                gameOverTextViewResult.setText(R.string.game_result_win);
                addRecordToDatabase(result);
                break;
            case LOSE:
                gameOverTextViewResult.setText(R.string.game_result_lose);
                addRecordToDatabase(result);
                break;
            case TIE:
                gameOverTextViewResult.setText(R.string.game_result_tie);
                addRecordToDatabase(result);
                break;
            default:
                gameOverTextViewResult.setText(R.string.game_result_error);
        }
    }

    private void addListenerToAnimation(Animation animation) {
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                hudBarBottom.setVisibility(View.GONE);
                gameOverPanel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * Saves the result into the SQLite database to be displayed in the standings activity.
     *
     * @param type string [WIN, LOSE, TIE]
     */
    private void addRecordToDatabase(String type) {
        int currentTime = difficulty.time() - timeLeftLocal;
        String dif = difficulty.str();
        SQLiteDatabase db = new DatabaseHelper(this).getWritableDatabase();
        switch (type) {
            case WIN:
                db.execSQL("UPDATE standings SET " + type + " = " + type + " + 1 WHERE difficulty = " + "\"" + dif + "\";");
                if (newBestTime(currentTime)) {
                    StringBuilder value = new StringBuilder();
                    value.append(String.valueOf(currentTime));
                    db.execSQL("UPDATE standings SET fastest = " + "\"" + value + "\"" + " WHERE difficulty = " + "\"" + dif + "\";");
                }
                break;
            case LOSE:
                db.execSQL("UPDATE standings SET " + type + " = " + type + " + 1 WHERE difficulty = " + "\"" + dif + "\";");
                break;
            case TIE:
                db.execSQL("UPDATE standings SET " + type + " = " + type + " + 1 WHERE difficulty = " + "\"" + dif + "\";");
                break;
        }
        db.close();
    }

    /**
     * Checks if a given time is better than the previous best time.
     *
     * @param timeToCompare int actual time to be compared.
     * @return true if the new time is better than the one in the database, otherwise, false.
     */
    private boolean newBestTime(int timeToCompare) {
        SQLiteDatabase db = new DatabaseHelper(this).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM standings WHERE difficulty = " + "\"" + difficulty.str() + "\";", null);
        cursor.moveToFirst();
        String valueOnDbStr = cursor.getString(cursor.getColumnIndex("fastest"));
        int valueOnDbInt;
        if (valueOnDbStr.equals("N/A")) {
            return true;
        } else {
            valueOnDbInt = Integer.parseInt(valueOnDbStr);
            if (timeToCompare < valueOnDbInt) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!gameOver) {
            gameOver = true;
            firebaseGameSessionRef
                    .child("message")
                    .setValue(new FirebaseMessage(gameSessionType, gameOver, timeLeftLocal, -1));
        }
        firebaseGameSessionRef.child("message").removeEventListener(listenForMessage);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    // Game Over buttons navigations
    public void goToFindMatch(View view) {
        Intent intent = new Intent(this, FindingMatchActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToMatchSettings(View view) {
        Intent intent = new Intent(this, MatchSettingsActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToMainActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
