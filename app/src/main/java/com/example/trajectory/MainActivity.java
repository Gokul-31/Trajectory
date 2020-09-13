package com.example.trajectory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.trajectory.views.CarView;
import com.example.trajectory.views.Coins;
import com.example.trajectory.views.Obstacles;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout root;
    Obstacles[] obstacles;
    Button startBt;
    Button moveBt;
    ImageView imageView;
    TextView coinsDisp;
    TextView resultTitle;
    TextView result;
    ImageView refresh;
    Bitmap bitmap;
    Canvas canvas;
    Coins[] c = new Coins[5];
    TextView rules;
    TextView rulesTitle;


    //game
    int coins = 0;
    int[] neglect = new int[5];
    int neglectIndex = 0;
    String status = "incomplete";
    String resultText;

    ArrayList<Point> pts = new ArrayList<>();
    Handler handler;
    Runnable r;

    boolean hitObstacle = false;
//    boolean completedEarly=false;
    int totalWidth;
    int totalHeight;
    int xCarInitial;
    int yCarIntial;
    int xDestInitial;
    int yDestInitial;
    int xTouch;
    int yTouch;
    int xTouchOld;
    int yTouchOld;
    int xCar;
    int yCar;
    int xCarOld;
    int yCarOld;
    int xObs;
    int yObs;

    //paddings
    int initalpaddings = 125;
    int destRect = 50;
    int touchPadding = 100;
    int rCar = 25;
    int checkpadding = 50;

    int cBack;
    int cCar;
    int cDest;
    int cTrack;
    int cDum;

    //animations and paths
    Path trajectory;
    ObjectAnimator carAnimator;
    CarView car;

    Paint pCar;
    Paint pTrack;
    Paint pDest;
    Paint pDum;
    Paint pBackStroke;

    //checkers
    boolean trajStart = false;

    final String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        root = findViewById(R.id.root);
        startBt = findViewById(R.id.startBt);
        imageView = findViewById(R.id.imageV);
        moveBt = findViewById(R.id.moveBt);
        coinsDisp = findViewById(R.id.coins_display);
        rules = findViewById(R.id.rules);
        rulesTitle = findViewById(R.id.rules_title);
        result = findViewById(R.id.result);
        resultTitle = findViewById(R.id.results_title);
        refresh = findViewById(R.id.restart);

        car = findViewById(R.id.car);
        c[0] = findViewById(R.id.coin1);
        c[1] = findViewById(R.id.coin2);
        c[2] = findViewById(R.id.coin3);
        c[3] = findViewById(R.id.coin4);
        c[4] = findViewById(R.id.coin5);
        obstacles = new Obstacles[3];
        obstacles[0] = findViewById(R.id.ob1);
        obstacles[1] = findViewById(R.id.ob2);
        obstacles[2] = findViewById(R.id.ob3);

        for (int i = 0; i < 5; i++) {
            neglect[i] = -1;
        }

        //color
        cBack = R.color.black;
        cCar = R.color.orange;
        cDest = R.color.pink;
        cTrack = R.color.colorAccent;
        cDum = R.color.white;

        trajectory = new Path();

        //Paint
        pCar = new Paint();
        pCar.setStyle(Paint.Style.FILL);
        pCar.setColor(getColor(cCar));

        pDest = new Paint();
        pDest.setStyle(Paint.Style.FILL);
        pDest.setColor(getColor(cDest));

        pTrack = new Paint();
        pTrack.setStyle(Paint.Style.STROKE);
        pTrack.setColor(getColor(cTrack));
        pTrack.setStrokeWidth(20);

        pDum = new Paint();
        pDum.setStyle(Paint.Style.STROKE);
        pDum.setColor(getColor(cDum));
        pDum.setStrokeWidth(20);

        pBackStroke = new Paint();
        pBackStroke.setStyle(Paint.Style.STROKE);
        pBackStroke.setColor(getColor(cBack));
        pBackStroke.setStrokeWidth(25);

        startBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //disappear
                startBt.setVisibility(View.GONE);
                rules.setVisibility(View.GONE);
                rulesTitle.setVisibility(View.INVISIBLE);
                root.setBackground(null);
                moveBt.setVisibility(View.GONE);
                //appear
                for (Coins coin : c) {
                    coin.setVisibility(View.VISIBLE);
                }
                for (Obstacles o : obstacles) {
                    o.setVisibility(View.VISIBLE);
                }
                car.setVisibility(View.VISIBLE);
                coinsDisp.setVisibility(View.VISIBLE);

                //initialise the canvas
                totalWidth = imageView.getWidth();
                totalHeight = imageView.getHeight();
                Log.i(TAG, "Initialise canvas width: " + totalWidth);
                Log.i(TAG, "Initialise canvas height: " + totalHeight);
                //works
                bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888);
                imageView.setImageBitmap(bitmap);
                canvas = new Canvas(bitmap);

                xCarInitial = totalWidth / 2;
                yCarIntial = totalHeight - initalpaddings;
                xDestInitial = totalWidth / 2;
                yDestInitial = initalpaddings;

                initial();
                setImageListener();
            }
        });

        moveBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveCar();
            }
        });
    }

//    private void generateObstacles() {
//        boolean proper;
//        Random r = new Random();
//        for (int i = 0; i < 3; i++) {
//            proper = false;
//            while (!proper) {
//                xObs = (int) (Math.random() * totalWidth);
//                yObs = 100 + (int) (Math.random() * (totalHeight - 200));
//
//                obstacles[i].setX(xObs);
//                obstacles[i].setX(yObs);
//                proper = ifProper(i);
//            }
//            if(proper){
//                obstacles[i].setVisibility(View.VISIBLE);
//            }
//        }
//    }

//    private boolean ifProper(int i) {
//        boolean b = true;
//
//        for (int j = i; j >= 0; j--) {
//            b = !(intersectCheck(obstacles[i], obstacles[j]));
//        }
//        for (int j = 0; j < 5; j++) {
//            b = !(intersectCheck(obstacles[i], c[j]));
//        }
//        b = !(intersectCheck(car, obstacles[i]));
//        return b;
//    }

//    private boolean intersectCheck(View v1, View v2) {
//        if (v1 == null || v2 == null) {
//            return false;
//        }
//        final int[] view1Loc = new int[2];
//        v1.getLocationOnScreen(view1Loc);
//        final Rect view1Rect = new Rect(view1Loc[0],
//                view1Loc[1],
//                view1Loc[0] + v1.getWidth(),
//                view1Loc[1] + v1.getHeight());
//        int[] view2Loc = new int[2];
//        v2.getLocationOnScreen(view2Loc);
//        final Rect view2Rect = new Rect(view2Loc[0],
//                view2Loc[1],
//                view2Loc[0] + v2.getWidth(),
//                view2Loc[1] + v2.getHeight());
//        return view1Rect.intersect(view2Rect);
//    }


    private void initial() {
        canvas.drawColor(getColor(cBack));
        drawDest(xDestInitial, yDestInitial);
        trajStart = false;

    }

    private void initialWOCar() {
        canvas.drawColor(getColor(cBack));
        drawDest(xDestInitial, yDestInitial);
        trajStart = false;
    }

    private void moveCar() {
        initialWOCar();
        carAnimator = ObjectAnimator.ofFloat(car, "x", "y", trajectory);
        neglectIndex = 0;
        carAnimator.setDuration(2000);
        carAnimator.start();

        carAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                xCar = (int) ((float) valueAnimator.getAnimatedValue("x"));
                yCar = (int) ((float) valueAnimator.getAnimatedValue("y"));

                canvas.drawLine(xCarOld, yCarOld, xCar, yCar, pBackStroke);

                if (dash()) {
                    dashEnd();
                    hitObstacle=true;
                }

                if ((xCar < xDestInitial + touchPadding && xCar > xDestInitial - touchPadding) && (yCar < yDestInitial + touchPadding && yCar > yDestInitial - touchPadding)) {
                    status = "complete";
                    checkandEnd();
                }

                xCarOld = xCar;
                yCarOld = yCar;

                if (touchGold()) {
                    coins++;
                }

            }
        });
        carAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                coinsDisp.setText("Coins: " + coins);
                initialWOCar();
                checkandEnd();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
    }

    private void dashEnd() {

        imageView.setVisibility(View.INVISIBLE);
        root.setBackground(getResources().getDrawable(R.drawable.grey2));
        for (Coins coin : c) {
            coin.setVisibility(View.INVISIBLE);
        }
        for (Obstacles o : obstacles) {
            o.setVisibility(View.INVISIBLE);
        }
        car.setVisibility(View.INVISIBLE);
        coinsDisp.setVisibility(View.INVISIBLE);
        moveBt.setVisibility(View.INVISIBLE);

        result.setVisibility(View.VISIBLE);
        resultText = "You have hit an obstacle. Try again";
        result.setText(resultText);
        resultTitle.setVisibility(View.VISIBLE);
        resultTitle.setText("OOPS!!!");
        refresh.setVisibility(View.VISIBLE);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
            }
        });
    }

    private boolean dash() {
        for (int i = 0; i < 3; i++) {
            if (intersectStone(obstacles[i], car)) {
                return true;
            }
        }
        return false;
    }

    private boolean touchGold() {
        boolean addthis;
        for (int i = 0; i < 5; i++) {
            addthis = true;
            for (int j = 0; j < neglectIndex; j++) {
                if (neglect[j] == i) {
                    addthis = false;
                }
            }
            if (addthis) {
                if (intersect(c[i], car)) {
                    neglect[neglectIndex] = i;
                    neglectIndex++;
                    c[i].setVisibility(View.INVISIBLE);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean intersect(Coins coins, CarView car) {
        if (coins == null || car == null) {
            return false;
        }
        final int[] view1Loc = new int[2];
        coins.getLocationOnScreen(view1Loc);
        final Rect view1Rect = new Rect(view1Loc[0],
                view1Loc[1],
                view1Loc[0] + coins.getWidth(),
                view1Loc[1] + coins.getHeight());
        int[] view2Loc = new int[2];
        car.getLocationOnScreen(view2Loc);
        final Rect view2Rect = new Rect(view2Loc[0],
                view2Loc[1],
                view2Loc[0] + car.getWidth(),
                view2Loc[1] + car.getHeight());
        return view1Rect.intersect(view2Rect);
    }

    private boolean intersectStone(Obstacles o, CarView car) {
        if (o == null || car == null) {
            return false;
        }
        final int[] view1Loc = new int[2];
        o.getLocationOnScreen(view1Loc);
        final Rect view1Rect = new Rect(view1Loc[0],
                view1Loc[1],
                view1Loc[0] + o.getWidth(),
                view1Loc[1] + o.getHeight());
        int[] view2Loc = new int[2];
        car.getLocationOnScreen(view2Loc);
        final Rect view2Rect = new Rect(view2Loc[0],
                view2Loc[1],
                view2Loc[0] + car.getWidth(),
                view2Loc[1] + car.getHeight());
        return view1Rect.intersect(view2Rect);
    }

    private void partialWOCar() {
        if (trajStart) {
        }
        drawDest(xDestInitial, yDestInitial);
    }

    void drawDest(int xs, int ys) {
        Paint pBlackText = new Paint();
        canvas.drawRect(xs - destRect, ys - destRect, xs + destRect, ys + destRect, pDest);
        pBlackText.setStyle(Paint.Style.FILL);
        pBlackText.setColor(getColor(R.color.black));
        pBlackText.setTextSize(100);
        canvas.drawText("P", xs - 25, ys + 25, pBlackText);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setImageListener() {
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                xTouch = (int) motionEvent.getX();
                yTouch = (int) motionEvent.getY();

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if ((xTouch < xCarInitial + touchPadding && xTouch > xCarInitial - touchPadding) && (yTouch < yCarIntial + touchPadding && yTouch > yCarIntial - touchPadding)) {
                            pts.clear();
                            trajectory.reset();
                            moveBt.setVisibility(View.GONE);
                            initial();
                            trajStart = true;
                            xTouchOld = xTouch;
                            yTouchOld = yTouch;
                            xCarOld = xTouch;
                            yCarOld = yTouch;
                        } else {
                            trajStart = false;
                        }
                        if (trajStart) {
                            trajectory.moveTo(xTouch-50, yTouch);
                            pts.add(new Point(xTouch, yTouch));
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (trajStart) {
                            trajectory.lineTo(xTouch-50, yTouch);
                            pts.add(new Point(xTouch, yTouch));
                            canvas.drawLine(xTouchOld, yTouchOld, xTouch, yTouch, pTrack);
                            xTouchOld = xTouch;
                            yTouchOld = yTouch;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        partialWOCar();
                        if (trajStart) {
                            moveBt.setVisibility(View.VISIBLE);
                        }
                        trajStart = false;
                        break;
                }
                imageView.invalidate();
                return true;
            }
        });
    }

    private void reset() {
        refresh.setVisibility(View.INVISIBLE);
        resultTitle.setVisibility(View.INVISIBLE);
        resultTitle.setText("Result: ");
        result.setVisibility(View.INVISIBLE);
        trajectory.reset();
        coins = 0;
        trajStart = false;

        status = "incomplete";

        imageView.setVisibility(View.VISIBLE);
        for (Coins coin : c) {
            coin.setVisibility(View.VISIBLE);
        }
        for (Obstacles o : obstacles) {
            o.setVisibility(View.VISIBLE);
        }
        car.setVisibility(View.VISIBLE);
        car.setX(xCarInitial - 35);
        car.setY(yCarIntial - 35);
        initial();

        coinsDisp.setVisibility(View.VISIBLE);
        coinsDisp.setText("Coins: 0");
    }

    private void checkandEnd() {
        if (!hitObstacle) {
            if (status.equals("incomplete")) {
                if ((xCar < xDestInitial + touchPadding && xCar > xDestInitial - touchPadding) && (yCar < yDestInitial + touchPadding && yCar > yDestInitial - touchPadding)) {
                    status = "complete";
                }
            }

            imageView.setVisibility(View.INVISIBLE);
            root.setBackground(getResources().getDrawable(R.drawable.grey2));
            for (Coins coin : c) {
                coin.setVisibility(View.INVISIBLE);
            }
            for (Obstacles o : obstacles) {
                o.setVisibility(View.INVISIBLE);
            }
            car.setVisibility(View.INVISIBLE);
            coinsDisp.setVisibility(View.INVISIBLE);
            moveBt.setVisibility(View.INVISIBLE);

            result.setVisibility(View.VISIBLE);
            resultText = "STATUS: " + status + "\n\n" + "Coins: " + coins;
            result.setText(resultText);
            resultTitle.setVisibility(View.VISIBLE);
            refresh.setVisibility(View.VISIBLE);
            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reset();
                }
            });
        }
    }
}