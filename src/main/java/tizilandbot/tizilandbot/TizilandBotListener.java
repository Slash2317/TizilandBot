package tizilandbot.tizilandbot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TizilandBotListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        RequestContext requestContext = RequestContext.from(event);

        if (requestContext.getCommand() == null) {
            return;
        }

        switch (requestContext.getCommand()) {
            case HELP -> event.getChannel().sendMessage(String.join("\n", getListOfCommands())).queue();
            case TIZILAND -> event.getChannel().sendMessage("""
                    The only links to join Tiziland and invite people are:
                    :tiziland_link: Discord Invite Link: https://discord.gg/9XTkWVbycs
                    :tiziland_link: Bit.ly Invite Link: http://bit.ly/tiziland""").queue();
            case RULES -> event.getChannel().sendMessage(":tiziland_rules: You can read our rules here: https://discord.com/channels/1108179404137447484/1108181346033094736").queue();
            case TIZIPAGES -> event.getChannel().sendMessage("""
                    Here are all the pages of Tizi:
                    -bit.ly/tiziabout
                    -bit.ly/tizisocial
                    -bit.ly/tizi-links""").queue();
            case STAFF -> event.getChannel().sendMessage("""
                    :crown: Owner: Tizi!! (tiziandfrodo)
                    :tools: Community Manager: Xanth (._.xanth._.)
                    :sparkles: Admins: N/A
                    :star2: Mods: Nathan (natxer43) | TireCuzWhyNot (tirecuzwhynot) | Ajax (arsunal)""").queue();
            case EVENT_START -> event.getChannel().sendMessage("The event just started, come join and participate! :star2:").queue();
            case EVENT_OVER -> event.getChannel().sendMessage("This event has been closed. Thanks for participating! :heart:").queue();
            case ECHO -> sendEcho(requestContext);
        }
    }

    private List<String> getListOfCommands() {
        List<String> commandDisplays = new ArrayList<>();
        for (Command command : Command.values()) {
            StringBuilder sb = new StringBuilder();
            sb.append(command.getCommandName());
            if (!command.getParameters().isEmpty()) {
                sb.append(" [" + String.join(", ", command.getParameters()) + "]");
            }
            sb.append(" (" + command.getDescription() + ")");
            commandDisplays.add(sb.toString());
        }
        return commandDisplays;
    }

    private void sendEcho(RequestContext requestContext) {
        if (requestContext.getArguments() != null && !requestContext.getArguments().isBlank()) {
            requestContext.getEvent().getChannel().sendMessage(requestContext.getArguments()).queue();
        }
    }
}
