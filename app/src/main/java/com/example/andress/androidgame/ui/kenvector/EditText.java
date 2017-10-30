package com.example.andress.androidgame.ui.kenvector;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

/**
 * Created by Andress on 26/9/17.
 *
 * Custom EditText with the Kenvector Future font.
 */

public class EditText extends AppCompatEditText {
    public static final String TAG = "EditTextKenvector";

    public EditText(Context context) {
        super(context);
        setTypeface(context);
    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(context);
    }

    public EditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(context);
    }

    private void setTypeface(Context context) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "kenvector_future.ttf");
        setTypeface(typeface);
    }
}
