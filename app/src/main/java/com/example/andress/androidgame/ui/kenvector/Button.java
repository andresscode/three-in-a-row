package com.example.andress.androidgame.ui.kenvector;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

/**
 * Created by Andress on 26/9/17.
 *
 * Custom Button with the Kenvector Future font.
 */

public class Button extends AppCompatButton {
    public Button(Context context) {
        super(context);
        setTypeface(context);
    }

    public Button(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(context);
    }

    public Button(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(context);
    }

    private void setTypeface(Context context) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "kenvector_future.ttf");
        setTypeface(typeface);
    }
}
