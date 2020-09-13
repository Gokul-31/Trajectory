package com.example.trajectory.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.trajectory.R;

public class Obstacles extends View {

    Context context;
    int width;
    int height;
    int radius;
    Paint pObstacles;
    Paint pBlackRing;
    Paint pBlackText;

    public Obstacles(Context context) {
        super(context);
        this.context=context;
        init(null);
    }

    public Obstacles(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init(attrs);
    }

    public Obstacles(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init(attrs);
    }

    public Obstacles(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context=context;
        init(attrs);
    }

    void init(@Nullable AttributeSet set){
        pObstacles =new Paint();
        pObstacles.setStyle(Paint.Style.FILL);
        pObstacles.setColor(context.getColor(R.color.red));
        pBlackRing=new Paint();
        pBlackRing.setStyle(Paint.Style.STROKE);
        pBlackRing.setColor(context.getColor(R.color.black));
        pBlackRing.setStrokeWidth(3);
        pBlackText=new Paint();
        pBlackText.setStyle(Paint.Style.FILL);
        pBlackText.setColor(context.getColor(R.color.black));
        pBlackText.setTextSize(50);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        width=getWidth();
        height=getHeight();
        radius= width/2;

        canvas.drawCircle(radius,radius,radius, pObstacles);
        canvas.drawCircle(radius,radius,radius-3,pBlackRing);
        canvas.drawText("X",(float) radius/2,(float) 1.5*radius,pBlackText);
    }
}
