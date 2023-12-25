package tizilandbot.tizilandbot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RequestContext {

    private final Command command;
    private final String parameters;
    private final MessageReceivedEvent event;

    public RequestContext(Command command, String parameters, MessageReceivedEvent event) {
        this.command = command;
        this.parameters = parameters;
        this.event = event;
    }

    public Command getCommand() {
        return command;
    }

    public String getParameters() {
        return parameters;
    }

    public MessageReceivedEvent getEvent() {
        return event;
    }

    public static RequestContext from(MessageReceivedEvent event) {
        Command command = Command.getCommand(event.getMessage().getContentRaw());
        if (command != null) {
            String parameters = event.getMessage().getContentRaw().substring(command.getCommandName().length());
            return new RequestContext(command, parameters, event);
        }
        return new RequestContext(null, null, event);
    }
}