package com.example.andress.androidgame.storage;

/**
 * Created by Andress on 28/9/17.
 *
 * This enum holds the child strings used in the Firebase database.
 */

public enum FirebaseKey {
    REF_QUEUE ("queue"),
    CHILD_OWNER ("owner"),
    CHILD_GUEST ("guest"),
    CHILD_USERNAME ("username"),
    CHILD_POKEMON ("pokemon"),
    CHILD_MOVEMENTS ("movements"),
    CHILD_MOVEMENTS_INDEX ("index"),
    CHILD_MOVEMENTS_PLAYER ("player");

    private final String name;

    FirebaseKey(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
