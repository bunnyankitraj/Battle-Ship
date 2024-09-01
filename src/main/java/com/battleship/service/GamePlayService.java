package com.battleship.service;

import com.battleship.dao.GameRepository;
import com.battleship.model.BattleField;
import com.battleship.model.Player;
import com.battleship.model.Ship;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Service
public class GamePlayService {

    private final GameRepository gameRepository;
    private final LoggerService loggerService;
    private final Random random;

    public GamePlayService(GameRepository gameRepository, LoggerService loggerService) {
        this.gameRepository = gameRepository;
        this.loggerService = loggerService;
        this.random = new Random();
    }

    public String startGame(String gameId, Player playerA, Player playerB) {
        BattleField battlefield = gameRepository.getBattleFieldByGameId(gameId);
        Set<String> firedCoordinates = new HashSet<>();
        ensureValidGameConfig(playerA, playerB);

        boolean gameOver = false;
        boolean playerATurn = true;

        loggerService.getLogger().info("Starting game...");

        while (!gameOver) {
            gameOver = playerATurn ? takeTurn(firedCoordinates,battlefield,playerA, playerB) : takeTurn(firedCoordinates,battlefield,playerB, playerA);
            playerATurn = !playerATurn;
        }

        String winner = playerATurn ? playerB.getName() : playerA.getName();
        loggerService.getLogger().info("Game Over. " + winner + " wins.");
        return winner;
    }

    private void ensureValidGameConfig(Player playerA, Player playerB) {
        if (playerA == null || playerB == null) {
            loggerService.getLogger().severe("Players are not initialized");
            throw new IllegalArgumentException("Players are not initialized.");
        }

        if (playerA.getFleet().isEmpty() || playerB.getFleet().isEmpty()) {
            loggerService.getLogger().severe("PlayerA or PlayerB do not have a fleet");
            throw new IllegalArgumentException("Player A or Player B do not have a fleet.");
        }
    }

    private boolean takeTurn(Set<String> firedCoordinates, BattleField battlefield, Player currentPlayer, Player opponentPlayer) {
        int x, y;
        do {
            x = random.nextInt(battlefield.getSize() / 2);
            y = random.nextInt(battlefield.getSize());
            if (currentPlayer.getName().equals("A")) {
                x = random.nextInt(battlefield.getSize() / 2, battlefield.getSize());
            }
        } while (firedCoordinates.contains(x + "," + y));

        firedCoordinates.add(x + "," + y);

        boolean hit = false;
        for (Ship ship : opponentPlayer.getFleet()) {
            if (!ship.isDestroyed() && isWithinBounds(ship, x, y)) {
                ship.setDestroyed(true);
                hit = true;
                logHit(currentPlayer, opponentPlayer, x, y, ship);
                if (isFleetDestroyed(opponentPlayer)) {
                    String winMessage = "Game Over. " + currentPlayer.getName() + " wins.";
                    loggerService.getLogger().info(winMessage);
                    System.out.println(winMessage);
                    return true;
                }
                break;
            }
        }

        if (!hit) {
            logMiss(currentPlayer, opponentPlayer, x, y);
        }
        return false;
    }

    private boolean isWithinBounds(Ship ship, int x, int y) {
        int startX = ship.getStartCoordinate().getX();
        int startY = ship.getStartCoordinate().getY();
        int size = ship.getSize();

        int endX = startX + size;
        int endY = startY + size;

        boolean withinHorizontalBounds = x >= startX && x < endX;
        boolean withinVerticalBounds = y >= startY && y < endY;

        return withinHorizontalBounds && withinVerticalBounds;
    }

    private boolean isFleetDestroyed(Player player) {
        for (Ship ship : player.getFleet()) {
            if (!ship.isDestroyed()) {
                return false;
            }
        }
        return true;
    }

    private void logHit(Player current, Player opponent, int x, int y, Ship ship) {
        String logMessage = String.format("%s's turn: Missile fired at (%d, %d): Hit %s's ship with id %s destroyed",
                current.getName(), x, y, opponent.getName(), ship.getId());
        System.out.println(logMessage);
        loggerService.getLogger().info(logMessage);
    }

    private void logMiss(Player current, Player opponent, int x, int y) {
        String logMessage = String.format("%s's turn: Missile fired at (%d, %d): Miss : Ships Remaining - %s: %d, %s: %d",
                current.getName(), x, y, current.getName(), countRemainingShips(current), opponent.getName(), countRemainingShips(opponent));
        System.out.println(logMessage);
        loggerService.getLogger().info(logMessage);
    }

    private int countRemainingShips(Player player) {
        int count = 0;
        for (Ship ship : player.getFleet()) {
            if (!ship.isDestroyed()) {
                count++;
            }
        }
        return count;
    }
}
