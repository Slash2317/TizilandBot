package tizilandbot.tizilandbot;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public enum Command {

    HELP("t!help", "shows list of cmds"),
    TIZILAND("t!tiziland", "shows invite links to tiziland"),
    RULES("t!rules", "shows where to find server rules"),
    TIZIPAGES("t!tizipages", "gives links to all tizi pages"),
    STAFF("t!staff", "gets the full list of current server staff"),
    EVENT_START("t!eventstart", "start event message"),
    EVENT_OVER("t!eventover", "stop event message"),
    ECHO("t!echo", "says the requested content", List.of("message"));

    public static final EnumSet<Command> STAFF_ONLY_COMMANDS = EnumSet.of(EVENT_START, EVENT_OVER);

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
