package tizilandbot.tizilandbot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RequestContext {

    private final Command command;
    private final String arguments;
    private final MessageReceivedEvent event;

    public RequestContext(Command command, String arguments, MessageReceivedEvent event) {
        this.command = command;
        this.arguments = arguments;
        this.event = event;
    }

    public Command getCommand() {
        return command;
    }

    public String getArguments() {
        return arguments;
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }

    public static RequestContext from(MessageReceivedEvent event) {
        Command command = Command.getCommand(event.getMessage().getContentRaw());
        if (command != null) {
            if (event.getMessage().getContentRaw().length() > command.getCommandName().length() + 1) {
                String parameters = event.getMessage().getContentRaw().substring(command.getCommandName().length() + 1);
                return new RequestContext(command, parameters, event);
            }
            return new RequestContext(command, null, event);
        }
        return new RequestContext(null, null, event);
    }
}
