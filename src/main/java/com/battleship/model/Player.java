package com.battleship.model;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private final String name;
    private final List<Ship> fleet;

    public Player(String name) {
        this.name = name;
        this.fleet = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Ship> getFleet() {
        return fleet;
    }
}
