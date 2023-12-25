package tizilandbot.tizilandbot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TizilandBotListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        RequestContext requestContext = RequestContext.from(event);

        if (requestContext.getCommand() == Command.HELP) {
            event.getChannel().sendMessage("oh help yourself").queue();
        }
        else if (requestContext.getCommand() == Command.TIZILAND) {
            event.getChannel().sendMessage("The only links to join Tiziland and invite people are:\n" +
                    ":tiziland_link: Discord Invite Link: https://discord.gg/9XTkWVbycs\n" +
                    ":tiziland_link: Bit.ly Invite Link: http://bit.ly/tiziland").queue();
        }
    }
}
