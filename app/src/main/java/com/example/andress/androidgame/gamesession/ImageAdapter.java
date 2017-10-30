package com.example.andress.androidgame.gamesession;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by Andress on 29/9/17.
 *
 * Setups the grid view in the game session.
 */
public class ImageAdapter extends BaseAdapter {
    private Context context;
    private Integer[] array;
    private final float scale;

    public ImageAdapter(Context context, Integer[] array) {
        this.context = context;
        this.array = array;
        this.scale = context.getResources().getDisplayMetrics().density;
    }

    @Override
    public int getCount() {
        return array.length;
    }

    @Override
    public Integer getItem(int i) {
        return array[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if (view == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            // Changing size of the tiles in the grid view
            switch (getCount()) {
                case 16:
                    imageView.setLayoutParams(new GridView.LayoutParams((int) (64 * scale), (int) (64 * scale)));
                    break;
                case 25:
                    imageView.setLayoutParams(new GridView.LayoutParams((int) (48 * scale), (int) (48 * scale)));
                    break;
                case 36:
                    imageView.setLayoutParams(new GridView.LayoutParams((int) (36 * scale), (int) (36 * scale)));
                    break;
                default:
                    imageView.setLayoutParams(new GridView.LayoutParams((int) (64 * scale), (int) (64 * scale)));
                    break;
            }
        } else {
            imageView = (ImageView) view;
        }
        imageView.setImageResource(array[i]);
        return imageView;
    }
}
