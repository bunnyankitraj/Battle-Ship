package com.battleship.dao;

import com.battleship.model.BattleField;
import com.battleship.model.Player;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class GameRepository {

    private final ConcurrentMap<String, Player> players = new ConcurrentHashMap<>(); // gameId_player -> Player
    private final ConcurrentMap<String, BattleField> battlefields = new ConcurrentHashMap<>(); // gameId -> battleField
    private final ConcurrentMap<String, Boolean> gameStatus = new ConcurrentHashMap<>(); // gameId -> status : to keep track of current game
    private final ConcurrentMap<String, Boolean> gameCompleted = new ConcurrentHashMap<>(); // gameId -> completed

    public void savePlayer(String gameId, String playerType, Player player) {
        players.put(gameId + "_" + playerType, player);
    }

    public Player getPlayer(String gameId, String playerType) {
        return players.get(gameId + "_" + playerType);
    }

    public void saveBattleField(String gameId, BattleField battleField) {
        battlefields.put(gameId, battleField);
    }

    public BattleField getBattleField(String gameId) {
        return battlefields.get(gameId);
    }

    public void setGameInProgress(String gameId, boolean inProgress) {
        gameStatus.put(gameId, inProgress);
    }

    public boolean isGameInProgress(String gameId) {
        return gameStatus.getOrDefault(gameId, false);
    }

    public void setGameCompleted(String gameId, boolean completed) {
        gameCompleted.put(gameId, completed);
    }

    public boolean isGameCompleted(String gameId) {
        return gameCompleted.getOrDefault(gameId, false);
    }

    public BattleField getBattleFieldByGameId(String gameId){
        return battlefields.get(gameId);
    }

}
