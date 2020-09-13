package com.example.trajectory.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;


import androidx.annotation.Nullable;

import com.example.trajectory.R;

public class CarView extends androidx.appcompat.widget.AppCompatImageView {

    Context context;
    int width;
    int height;
    int radius;
    int xPos;
    int yPos;
    int color= R.color.orange;

    public CarView(Context context) {
        super(context);
        this.context=context;
        init(null);
    }

    public CarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init(attrs);
    }

    public CarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init(attrs);
    }

    void init(@Nullable AttributeSet set){

    }

    @Override
    protected void onDraw(Canvas canvas) {
        setBackground(getResources().getDrawable(R.drawable.ic_car_top_view_svgrepo_com));
    }
}
