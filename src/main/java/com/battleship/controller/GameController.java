package com.battleship.controller;

import com.battleship.dto.GenericResponse;
import com.battleship.dto.ShipPlacementRequest;
import com.battleship.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/initGame")
    public GenericResponse<String> initGame(@RequestParam String gameId, @RequestParam int size) {
        return gameService.initGame(gameId, size);
    }

    @PostMapping("/addShip")
    public GenericResponse<String> addShip(@RequestBody ShipPlacementRequest request) {
        return gameService.addShip(request);
    }

    @PostMapping("/startGame")
    public GenericResponse<String> startGame(@RequestParam String gameId) {
        return gameService.startGame(gameId);
    }

    @GetMapping("/viewBattleField")
    public GenericResponse<String> viewBattleField(@RequestParam String gameId) {
        return gameService.viewBattleField(gameId);
    }
}
