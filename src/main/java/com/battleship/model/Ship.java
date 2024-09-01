package com.battleship.model;

public class Ship {
    private final String id;
    private final Coordinate startCoordinate;
    private final int size;
    private boolean destroyed;

    public Ship(String id, Coordinate startCoordinate, int size, boolean destroyed) {
        this.id = id;
        this.startCoordinate = startCoordinate;
        this.size = size;
        this.destroyed = destroyed;
    }

    public String getId() {
        return id;
    }

    public Coordinate getStartCoordinate() {
        return startCoordinate;
    }

    public int getSize() {
        return size;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }
}
