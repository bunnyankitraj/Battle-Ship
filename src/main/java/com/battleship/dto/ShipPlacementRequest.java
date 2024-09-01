package com.battleship.dto;

public class ShipPlacementRequest {
    private String gameId;
    private String id;
    private int size;
    private int xA;
    private int yA;
    private int xB;
    private int yB;


    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getxA() {
        return xA;
    }

    public void setxA(int xA) {
        this.xA = xA;
    }

    public int getyA() {
        return yA;
    }

    public void setyA(int yA) {
        this.yA = yA;
    }

    public int getxB() {
        return xB;
    }

    public void setxB(int xB) {
        this.xB = xB;
    }

    public int getyB() {
        return yB;
    }

    public void setyB(int yB) {
        this.yB = yB;
    }
}
