package com.example.trajectory;

public class Car {
    static int xCarNow;
    static int yCarNow;

    public static int getxCarNow() {
        return xCarNow;
    }

    public static void setxCarNow(int xCarNow) {
        Car.xCarNow = xCarNow;
    }

    public static int getyCarNow() {
        return yCarNow;
    }

    public static void setyCarNow(int yCarNow) {
        Car.yCarNow = yCarNow;
    }
}
