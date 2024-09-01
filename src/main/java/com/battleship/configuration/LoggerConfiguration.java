package com.battleship.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerConfiguration {

    @Bean
    public String loggerName() {
        return "BattleshipLogger";
    }
}
