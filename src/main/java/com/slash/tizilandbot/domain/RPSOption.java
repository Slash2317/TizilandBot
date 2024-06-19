package com.slash.tizilandbot.domain;

import java.util.List;

public enum RPSOption {

    ROCK(":rock: Rock", List.of("rock"), List.of("SCISSORS", "LIZARD")),
    PAPER(":page_facing_up: Paper", List.of("paper"), List.of("ROCK", "SPOCK")),
    SCISSORS(":scissors: Scissors", List.of("scissors"), List.of("PAPER", "LIZARD")),
    LIZARD(":lizard: Lizard", List.of("lizard"), List.of("PAPER", "SPOCK")),
    SPOCK(":vulcan: Spock", List.of("spock"), List.of("ROCK", "SCISSORS"));

    public static final RPSOption[] STANDARD_OPTIONS = new RPSOption[] { ROCK, PAPER, SCISSORS };

    private final String message;
    private final List<String> triggerWords;
    private final List<String> beatsOptions;

    RPSOption(String message, List<String> triggerWords, List<String> beatsOptions) {
        this.message = message;
        this.triggerWords = triggerWords;
        this.beatsOptions = beatsOptions;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getTriggerWords() {
        return triggerWords;
    }

    public List<String> getBeatsOptions() {
        return beatsOptions;
    }

    public static RPSOption find(String trigger) {
        if (trigger == null) {
            return null;
        }

        String lowerCaseTrigger = trigger.trim().toLowerCase();
        for (RPSOption option: RPSOption.values()) {
            if (option.triggerWords.contains(lowerCaseTrigger)) {
                return option;
            }
        }
        return null;
    }
}
