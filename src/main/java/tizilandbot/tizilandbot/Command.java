package tizilandbot.tizilandbot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public enum Command {

    HELP("t!help"),
    TIZILAND("t!tiziland"),
    RULES("t!rules"),
    TIZIPAGES("t!tizipages"),
    STAFF("t!staff"),
    ECHO("t!echo");

    private final String commandName;

    Command(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
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
