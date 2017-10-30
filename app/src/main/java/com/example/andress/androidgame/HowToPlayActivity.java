package com.example.andress.androidgame;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.andress.androidgame.gamesession.ImageAdapter;
import com.example.andress.androidgame.storage.SharedPreferencesKey;
import com.example.andress.androidgame.ui.kenvector.TextView;

/**
 * This activity takes the user through a simple tutorial to explain how the game works. This
 * activity is displayed for first time after the creation of the username and from then, is
 * available from the Main Activity menu.
 */
public class HowToPlayActivity extends AppCompatActivity {
    public static final String TAG = "HowToPlayActivity";

    // Steps
    private static final int WELCOME = 0;
    private static final int MATCH_SETTINGS = 1;
    private static final int SELECT_DIFFICULTY = 2;
    private static final int SELECT_POKEMON = 3;
    private static final int MATCH_SETTINGS_READY = 4;
    private static final int GAME_SESSION_01 = 5;
    private static final int GAME_SESSION_02 = 6;
    private static final int GAME_SESSION_03 = 7;
    private static final int GAME_SESSION_04 = 8;
    private static final int GAME_SESSION_05 = 9;
    private static final int GAME_SESSION_06 = 10;
    private static final int GAME_SESSION_07 = 11;
    private static final int GAME_SESSION_08 = 12;
    private static final int GAME_SESSION_09 = 13;
    private static final int FINISH = 14;

    private int stepCounter = 0;

    // SharedPreferences
    private SharedPreferences sharedPreferences;

    // Animations
    private Animation blink;
    private Animation zoomIn;
    private Animation pointToRight;
    private Animation pointToLeft;

    // UI Match Settings
    private TextView settingsTitle;
    private RelativeLayout settingsPanel;
    private ImageView difficultyPointer;
    private ImageView pokemonPointer;

    // UI Game Session
    private RelativeLayout hudBarTop;
    private TextView turn;
    private TextView timer;
    private RelativeLayout gridViewContainer;
    private GridView gridView;
    private Integer[] gridViewArray;
    private ImageAdapter imageAdapter;

    // UI Dialogue Box
    private RelativeLayout dialogueBox;
    private TextView dialogue;
    private ImageView dialogueArrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_play);

        getAnimations();
        getViews();

        // SharedPreferences
        sharedPreferences = getSharedPreferences(SharedPreferencesKey.FILE_NAME.name(), 0);

        // Showing welcome message
        nextDialogue(WELCOME);
    }

    /**
     * Loading of every animation used in the activity.
     */
    private void getAnimations() {
        blink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        zoomIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in);
        pointToRight = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.point_top_right);
        pointToLeft = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.point_bottom_left);
    }

    /**
     * Gets the references to every view in the activity. Then, calls the setViewStartingProperties()
     * method.
     */
    private void getViews() {
        // Match Settings
        settingsTitle = (TextView) findViewById(R.id.howToPlayActivitySettingsTitle);
        settingsPanel = (RelativeLayout) findViewById(R.id.howToPlayActivitySettingsPanel);
        difficultyPointer = (ImageView) findViewById(R.id.howToPlayActivityDifficultyPointer);
        pokemonPointer = (ImageView) findViewById(R.id.howToPlayActivityPokemonPointer);
        // Game Session
        hudBarTop = (RelativeLayout) findViewById(R.id.howToPlayActivityHudBarTop);
        turn = (TextView) findViewById(R.id.howToPlayActivityHudActionTop);
        timer = (TextView) findViewById(R.id.howToPlayActivityHudTimerTop);
        gridViewContainer = (RelativeLayout) findViewById(R.id.howToPlayActivityGridViewContainer);
        gridView = (GridView) findViewById(R.id.howToPlayActivityGridView);
        // Dialogue Box
        dialogueBox = (RelativeLayout) findViewById(R.id.howToPlayActivityDialogueBox);
        dialogue = (TextView) findViewById(R.id.howToPlayActivityTextViewDialogue);
        dialogueArrow = (ImageView) findViewById(R.id.howToPlayActivityImageViewDialogueArrow);
        setViewsStartingProperties();
    }

    /**
     * Hides the game settings and game session layouts at the beginning. Also, populates the
     * initial grid view, sets the touch listener to the dialogue box that will help the user
     * to move to the next step of the tutorial and finally sets the delay of the text typing
     * animation.
     */
    private void setViewsStartingProperties() {
        // Game Settings
        settingsTitle.setVisibility(View.GONE);
        settingsPanel.setVisibility(View.GONE);
        difficultyPointer.setVisibility(View.INVISIBLE);
        pokemonPointer.setVisibility(View.INVISIBLE);
        // Game Session
        hudBarTop.setVisibility(View.GONE);
        gridViewContainer.setVisibility(View.GONE);
        gridViewArray = new Integer[16]; // Example with easy board 4X4
        for (int i = 0; i < gridViewArray.length; i++) {
            if(i == 4 || i == 6)
                gridViewArray[i] = R.drawable.tile_green_pikachu;
            else if(i == 7 || i == 15)
                gridViewArray[i] = R.drawable.tile_red_bullbasaur;
            else
                gridViewArray[i] = R.drawable.tile_pokeball;
        }
        imageAdapter = new ImageAdapter(this, gridViewArray);
        gridView.setAdapter(imageAdapter);
        // Dialogue Box
        dialogueBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                nextDialogue(stepCounter);
                return false;
            }
        });
        dialogue.setCharacterDelay(60);
        dialogueArrow.setAnimation(blink);
    }

    /**
     * Handles the sequence of the tutorial steps.
     * @param step int with the value of the step to be displayed.
     */
    private void nextDialogue(int step) {
        stepCounter++;
        switch (step) {
            case WELCOME:
                // Adding username to the welcome message
                String username = sharedPreferences.getString(SharedPreferencesKey.USERNAME.name(), "Ash");
                String welcomeDialogueWithUsername = String.format(getResources().getString(R.string.activity_how_to_play_message_00_welcome), username);
                dialogue.animateText(welcomeDialogueWithUsername);
                break;
            case MATCH_SETTINGS:
                settingsTitle.setAnimation(zoomIn);
                settingsTitle.setVisibility(View.VISIBLE);
                settingsPanel.setAnimation(zoomIn);
                settingsPanel.setVisibility(View.VISIBLE);
                dialogue.animateText(getResources().getString(R.string.activity_how_to_play_message_01_match_settings));
                break;
            case SELECT_DIFFICULTY:
                difficultyPointer.setVisibility(View.VISIBLE);
                difficultyPointer.setAnimation(pointToRight);
                dialogue.animateText(getResources().getString(R.string.activity_how_to_play_message_02_difficulty));
                break;
            case SELECT_POKEMON:
                difficultyPointer.clearAnimation();
                difficultyPointer.setVisibility(View.GONE);
                pokemonPointer.setAnimation(pointToLeft);
                pokemonPointer.setVisibility(View.VISIBLE);
                dialogue.animateText(getResources().getString(R.string.activity_how_to_play_message_03_pokemon));
                break;
            case MATCH_SETTINGS_READY:
                pokemonPointer.clearAnimation();
                pokemonPointer.setVisibility(View.GONE);
                dialogue.animateText(getResources().getString(R.string.activity_how_to_play_message_04_match_settings_ready));
                break;
            case GAME_SESSION_01:
                settingsTitle.setVisibility(View.GONE);
                settingsPanel.setVisibility(View.GONE);
                hudBarTop.setVisibility(View.VISIBLE);
                gridViewContainer.setVisibility(View.VISIBLE);
                dialogue.animateText(getResources().getString(R.string.activity_how_to_play_message_05_game_session_01));
                break;
            case GAME_SESSION_02:
                dialogue.animateText(getResources().getString(R.string.activity_how_to_play_message_05_game_session_02));
                break;
            case GAME_SESSION_03:
                hudBarTop.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.panel_blue_rounded, null));
                turn.setText("Play");
                dialogue.animateText(getResources().getString(R.string.activity_how_to_play_message_05_game_session_03));
                break;
            case GAME_SESSION_04:
                timer.setText("59");
                dialogue.animateText(getResources().getString(R.string.activity_how_to_play_message_05_game_session_04));
                break;
            case GAME_SESSION_05:
                dialogue.animateText(getResources().getString(R.string.activity_how_to_play_message_05_game_session_05));
                break;
            case GAME_SESSION_06:
                gridViewArray[11] = R.drawable.tile_red_bullbasaur;
                imageAdapter.notifyDataSetChanged();
                dialogue.animateText(getResources().getString(R.string.activity_how_to_play_message_05_game_session_06));
                break;
            case GAME_SESSION_07:
                gridViewArray[5] = R.drawable.tile_green_pikachu;
                imageAdapter.notifyDataSetChanged();
                dialogue.animateText(getResources().getString(R.string.activity_how_to_play_message_05_game_session_07));
                break;
            case GAME_SESSION_08:
                dialogue.animateText(getResources().getString(R.string.activity_how_to_play_message_05_game_session_08));
                break;
            case GAME_SESSION_09:
                dialogue.animateText(getResources().getString(R.string.activity_how_to_play_message_05_game_session_09));
                break;
            case FINISH:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            default:break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
