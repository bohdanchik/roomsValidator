package com.storage.roomValidate.models;

import java.util.ArrayList;

public class Coordinates {
    private ArrayList<Point> points = new ArrayList<>();

    public Coordinates() {
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }
}
