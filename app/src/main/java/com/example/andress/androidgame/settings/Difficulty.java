package com.example.andress.androidgame.settings;

import com.example.andress.androidgame.R;

/**
 * Created by Andress on 27/9/17.
 *
 * This enum holds the information about the difficulties:
 *
 *  - str: string name.
 *  - id: identifies the difficulty radio button in the layout.
 *  - rows: number of rows.
 *  - cols: number of columns.
 *  - time: time for the game.
 */

public enum Difficulty {
    EASY ("Easy", R.id.matchSettingsActivityDifficultyEasy, 4, 4, 60),
    NORMAL ("Normal", R.id.matchSettingsActivityDifficultyNormal, 5, 5, 50),
    HARD ("Hard", R.id.matchSettingsActivityDifficultyHard, 6, 6, 40);

    private final String str;
    private final int id;
    private final int rows;
    private final int cols;
    private final int time;

    Difficulty(String str, int id, int rows, int cols, int time) {
        this.str = str;
        this.id = id;
        this.rows = rows;
        this.cols = cols;
        this.time = time;
    }

    public String str() {
        return str;
    }

    public int id() {
        return id;
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    public int time() {
        return time;
    }

    public static Difficulty getByString(String str) {
        for (Difficulty d : Difficulty.values()) {
            if (d.str.equals(str)) {
                return d;
            }
        }
        throw new IllegalArgumentException("String not found on Difficulty Enum");
    }

    public static Difficulty getById(int id) {
        for (Difficulty d : Difficulty.values()) {
            if (d.id == id) {
                return d;
            }
        }
        throw new IllegalArgumentException("Id not found on Difficulty Enum");
    }

    @Override
    public String toString() {
        return str;
    }
}
