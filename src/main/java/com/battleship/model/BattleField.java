package com.battleship.model;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class BattleField {
    private final int size;
    private final char[][] grid;
    private final Map<Coordinate, String> ownership; // (Coordinate -> Ship ID)
    private final Logger logger;

    public BattleField(int size) {
        this.size = size;
        this.grid = new char[size][size];
        this.ownership = new HashMap<>();
        this.logger = Logger.getLogger(BattleField.class.getName());
        initializeGrid();
    }

    private void initializeGrid() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = '.';
            }
        }
    }

    public int getSize() {
        return size;
    }

    public char[][] getGrid() {
        return grid;
    }

    public void placeShip(Player player, Ship ship) {
        Coordinate start = ship.getStartCoordinate();
        int shipSize = ship.getSize();
        String shipString = player.getName().equalsIgnoreCase("A") ? "A-" + ship.getId() : "B-" + ship.getId();

        for (int i = 0; i < shipSize; i++) {
            for (int j = 0; j < shipSize; j++) {
                int x = start.getX() + i;
                int y = start.getY() + j;

                if (x < size && y < size) {
                    grid[x][y] = player.getName().charAt(0); // 'A' for Player A or 'B' for Player B
                    ownership.put(new Coordinate(x, y), shipString);
                }
            }
        }

        logger.info(String.format("Placed ship %s for Player %s at coordinates (%d, %d)", shipString, player.getName(), start.getX(), start.getY()));
    }

    public String getShipIdAtCoordinate(int x, int y) {
        return ownership.get(new Coordinate(x, y));
    }

}
