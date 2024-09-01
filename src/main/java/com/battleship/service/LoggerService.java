package com.battleship.service;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@Service
public class LoggerService {
    private static Logger logger;
    private static FileHandler fileHandler;

    static {
        setupLogger();
    }

    private static void setupLogger() {
        try {
            logger = Logger.getLogger("BattleshipLogger");
            // Ensure we only create a single FileHandler instance
            if (fileHandler == null) {
                fileHandler = new FileHandler("battleship_game.log", true);
                SimpleFormatter formatter = new SimpleFormatter();
                fileHandler.setFormatter(formatter);
                logger.addHandler(fileHandler);
                logger.setLevel(Level.INFO);
            }
        } catch (IOException e) {
            System.err.println("Failed to initialize logger!");
            e.printStackTrace();
        }
    }

    public Logger getLogger() {
        return logger;
    }
}
