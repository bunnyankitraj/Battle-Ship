package com.battleship.service;

import com.battleship.dao.GameRepository;
import com.battleship.dto.GenericResponse;
import com.battleship.dto.ShipPlacementRequest;
import com.battleship.exception.BattlefieldNotInitializedException;
import com.battleship.model.BattleField;
import com.battleship.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final ShipPlacementService shipPlacementService;
    private final GamePlayService gamePlayService;
    private final LoggerService loggerService;

    @Autowired
    public GameService(GameRepository gameRepository, ShipPlacementService shipPlacementService, GamePlayService gamePlayService) {
        this.gameRepository = gameRepository;
        this.shipPlacementService = shipPlacementService;
        this.gamePlayService = gamePlayService;
        this.loggerService = new LoggerService();
    }

    public GenericResponse<String> initGame(String gameId, int size) {
        try {
            if (size <= 0 || size % 2 != 0) {
                loggerService.getLogger().severe("Battlefield size must be a positive even number.");
                return GenericResponse.error("Initialization failed", "Battlefield size must be a positive even number.");
            }

            if (gameRepository.getBattleField(gameId) != null) {
                loggerService.getLogger().severe("Game with ID " + gameId + " already exists.");
                return GenericResponse.error("Game initialization failed", "Game with ID " + gameId + " already exists.");
            }

            Player playerA = new Player("A");
            Player playerB = new Player("B");
            BattleField battlefield = new BattleField(size);

            gameRepository.savePlayer(gameId, "A", playerA);
            gameRepository.savePlayer(gameId, "B", playerB);
            gameRepository.saveBattleField(gameId, battlefield);
            gameRepository.setGameInProgress(gameId, false);
            gameRepository.setGameCompleted(gameId, false);

            loggerService.getLogger().info("Game initialized with battlefield size: " + size + "x" + size);
            return GenericResponse.success("Game initialized", "Game initialized with battlefield size " + size + " for game ID " + gameId);
        } catch (Exception e) {
            loggerService.getLogger().severe("Failed to initialize game: " + e.getMessage());
            return GenericResponse.error("Initialization failed", e.getMessage());
        }
    }

    public GenericResponse<String> addShip(ShipPlacementRequest request) {
        try {
            if (gameRepository.isGameInProgress(request.getGameId())) {
                return GenericResponse.error("Add ship failed", "Cannot add ships while the game is in progress.");
            }
            if (gameRepository.isGameCompleted(request.getGameId())) {
                return GenericResponse.error("Add ship failed", "Cannot add ships to a completed game.");
            }

            BattleField battlefield = gameRepository.getBattleField(request.getGameId());
            if (ObjectUtils.isEmpty(battlefield)) {
                loggerService.getLogger().severe("Battlefield is not initialized.");
                return GenericResponse.error("Add ship failed", "Battlefield is not initialized.");
            }

            Player playerA = gameRepository.getPlayer(request.getGameId(), "A");
            Player playerB = gameRepository.getPlayer(request.getGameId(), "B");


            boolean isPlacementValidA = shipPlacementService.isPlacementValid(battlefield,playerA, request.getxA(), request.getyA(), request.getSize());
            boolean isPlacementValidB = shipPlacementService.isPlacementValid(battlefield,playerB, request.getxB(), request.getyB(), request.getSize());

            if (!isPlacementValidA) {
                return GenericResponse.error("Add ship failed", "Invalid ship placement for Player A.");
            }

            if (!isPlacementValidB) {
                return GenericResponse.error("Add ship failed", "Invalid ship placement for Player B.");
            }

            shipPlacementService.addShip(battlefield,playerA, request.getId(), request.getSize(), request.getxA(), request.getyA());
            shipPlacementService.addShip(battlefield,playerB, request.getId(), request.getSize(), request.getxB(), request.getyB());

            return GenericResponse.success("Ship added", "Ship " + request.getId() + " added for both players in game ID " + request.getGameId());
        } catch (Exception e) {
            loggerService.getLogger().severe("Failed to add ship: " + e.getMessage());
            return GenericResponse.error("Add ship failed", e.getMessage());
        }
    }

    public GenericResponse<String> viewBattleField(String gameId) {
        try {
            BattleField battlefield = gameRepository.getBattleField(gameId);
            if (ObjectUtils.isEmpty(battlefield)) {
                loggerService.getLogger().severe("Battlefield is not initialized.");
                return GenericResponse.error("View battlefield failed", "Battlefield is not initialized.");
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Battlefield view:\n");
            char[][] grid = battlefield.getGrid();
            for (int i = 0; i < battlefield.getSize(); i++) {
                for (int j = 0; j < battlefield.getSize(); j++) {
                    char cell = grid[i][j];
                    if (cell == '.') {
                        sb.append("  .  ");
                    } else {
                        String shipId = battlefield.getShipIdAtCoordinate(i, j);
                        sb.append(shipId != null ? " " + shipId + " " : "  .  ");
                    }
                }
                sb.append('\n');
            }
            String battlefieldView = prettyFormat(sb.toString());
            loggerService.getLogger().info(battlefieldView);
            return GenericResponse.success("Battlefield view", battlefieldView);
        } catch (Exception e) {
            loggerService.getLogger().severe("Failed to view battlefield: " + e.getMessage());
            return GenericResponse.error("View battlefield failed", e.getMessage());
        }
    }

    public GenericResponse<String> startGame(String gameId) {
        if (gameRepository.isGameCompleted(gameId)) {
            String message = "Game with ID " + gameId + " has been completed.";
            loggerService.getLogger().warning(message);
            return GenericResponse.error("Start game failed", message);
        }
        String message = "Game with ID " + gameId + " is already in progress.";

        if (gameRepository.isGameInProgress(gameId)) {
            loggerService.getLogger().warning(message);
            return GenericResponse.error("Start game failed", message);
        }

        gameRepository.setGameInProgress(gameId, true);

        try {
            ensureBattlefieldInitialized(gameId);

            Player playerA = gameRepository.getPlayer(gameId, "A");
            Player playerB = gameRepository.getPlayer(gameId, "B");

            String winner = gamePlayService.startGame(gameId,playerA, playerB);

            return GenericResponse.success("Game started", "winner is " + winner + " for game" + gameId);
        } catch (Exception e) {
            loggerService.getLogger().severe("An error occurred during the game: " + e.getMessage());
            return GenericResponse.error("Start game failed", e.getMessage());
        } finally {
            gameRepository.setGameInProgress(gameId, false);
            gameRepository.setGameCompleted(gameId, true);
        }
    }

    private void ensureBattlefieldInitialized(String gameId) throws BattlefieldNotInitializedException {
        BattleField battlefield = gameRepository.getBattleField(gameId);
        if (ObjectUtils.isEmpty(battlefield)) {
            throw new BattlefieldNotInitializedException("Battlefield is not initialized.");
        }
    }

    private String prettyFormat(String input) {
        return input.replace("\n", System.lineSeparator());
    }
}
