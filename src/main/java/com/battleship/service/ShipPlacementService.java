package com.battleship.service;

import com.battleship.exception.InvalidShipPlacementException;
import com.battleship.model.BattleField;
import com.battleship.model.Coordinate;
import com.battleship.model.Player;
import com.battleship.model.Ship;
import org.springframework.stereotype.Service;

@Service
public class ShipPlacementService {

    private final LoggerService loggerService;

    public ShipPlacementService(LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    public void addShip(BattleField battlefield, Player player, String id, int size, int x, int y) {
        if (!isPlacementValid(battlefield, player, x, y, size)) {
            loggerService.getLogger().severe("Invalid ship placement for Player " + player.getName() + ". Ship ID: " + id);
            throw new InvalidShipPlacementException("Invalid ship placement for Player " + player.getName() + ". Ships cannot overlap or be placed outside the battlefield.");
        }

        Ship ship = new Ship(id, new Coordinate(x, y), size, false);
        player.getFleet().add(ship);
        battlefield.placeShip(player, ship);
        loggerService.getLogger().info("Added ship " + id + " of size " + size + " at (" + x + "," + y + ") for " + player.getName());
    }

    public boolean isPlacementValid(BattleField battlefield, Player player, int x, int y, int size) {
        if (!isWithinBattleFieldBoundary(battlefield,player, x, y, size)) {
            loggerService.getLogger().warning("Ship placement is outside the battlefield boundary for Player " + player.getName());
            return false;
        }

        if (isOverlapping(player, x, y, size)) {
            loggerService.getLogger().warning("Ship placement overlaps with an existing ship for Player " + player.getName());
            return false;
        }

        return true;
    }

    private boolean isWithinBattleFieldBoundary(BattleField battlefield, Player player, int x, int y, int size) {
        int boundaryStart = player.getName().equals("A") ? 0 : battlefield.getSize() / 2;
        int boundaryEnd = player.getName().equals("A") ? battlefield.getSize() / 2 - 1 : battlefield.getSize() - 1;

        return x >= boundaryStart && x + size - 1 <= boundaryEnd && y + size - 1 <= battlefield.getSize() - 1;
    }

    private boolean isOverlapping(Player player, int x, int y, int size) {
        for (Ship existingShip : player.getFleet()) {
            Coordinate start = existingShip.getStartCoordinate();
            int ex = start.getX();
            int ey = start.getY();
            int exSize = existingShip.getSize();

            boolean noOverlap = (x + size - 1 < ex || x > ex + exSize - 1) ||
                    (y + size - 1 < ey || y > ey + exSize - 1);

            if (!noOverlap) {
                loggerService.getLogger().warning("There is an overlapping ship at (" + ex + "," + ey + ") of size " + exSize + " for Player " + player.getName());
                return true;
            }
        }
        return false;
    }
}
