package com.slash.tizilandbot.request;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public enum Command {

    HELP("t!help", "shows list of cmds"),
    TIZILAND("t!tiziland", "shows invite links to tiziland"),
    RULES("t!rules", "shows where to find server rules"),
    TIZIPAGES("t!tizipages", "gives links to all tizi pages"),
    STAFF("t!staff", "gets the full list of current server staff"),
    VERIFY("t!verify", "get verified"),
    ECHO("t!echo", "says the requested content", List.of("message"));

    private final String commandName;
    private final String description;
    private final List<String> parameters;


    Command(String commandName, String description, List<String> parameters) {
        this.commandName = commandName;
        this.description = description;
        this.parameters = parameters;
    }

    Command(String commandName, String description) {
        this(commandName, description, Collections.emptyList());
    }

    public String getCommandName() {
        return commandName;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public static Command getCommand(String message) {
        for (Command command : Command.values()) {
            if (message.equals(command.commandName) || message.startsWith(command.commandName + " ")) {
                return command;
            }
        }
        return null;
    }
}
