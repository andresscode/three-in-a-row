package com.andresscode.androidgame.ui.kenvector;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by Andress on 26/9/17.
 *
 * Custom TextView with the Kenvector Future font. This TextView has a custom animation to
 * simulate typing.
 */

public class TextView extends AppCompatTextView {
    private CharSequence text;
    private int index;
    private long delay = 150; //Default 500ms delay

    public TextView(Context context) {
        super(context);
        setTypeface(context);
    }

    public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(context);
    }

    public TextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface(context);
    }

    private void setTypeface(Context context) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "kenvector_future.ttf");
        setTypeface(typeface);
    }

    private Handler handler = new Handler();
    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {
            setText(text.subSequence(0, index++));
            if(index <= text.length()) {
                handler.postDelayed(characterAdder, delay);
            }
        }
    };

    public void animateText(CharSequence text) {
        this.text = text;
        index = 0;

        setText("");
        handler.removeCallbacks(characterAdder);
        handler.postDelayed(characterAdder, delay);
    }

    public void setCharacterDelay(long millis) {
        delay = millis;
    }
}
