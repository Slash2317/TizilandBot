package com.slash.tizilandbot;

import com.slash.tizilandbot.request.Command;
import com.slash.tizilandbot.request.RequestContext;
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

        if (requestContext.command() == null) {
            return;
        }

        switch (requestContext.command()) {
            case HELP -> handleHelpCommand(requestContext);
            case TIZILAND -> event.getChannel().sendMessage("""
                    The only links to join Tiziland and invite people are:
                    :link: Discord Invite Link: https://discord.gg/9XTkWVbycs
                    :link: Bit.ly Invite Link: http://bit.ly/tiziland""").queue();
            case RULES -> event.getChannel().sendMessage(":scroll: You can read our rules here: https://discord.com/channels/1108179404137447484/1108181346033094736").queue();
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
            case EVENT_START -> handleEventStartCommand(requestContext);
            case EVENT_OVER -> handleEventOverCommand(requestContext);
            case ECHO -> handleEchoCommand(requestContext);
        }
    }

    private void handleHelpCommand(RequestContext requestContext) {
        List<String> regularCommandDisplays = new ArrayList<>();
        List<String> staffCommandDisplays = new ArrayList<>();
        for (Command command : Command.values()) {
            StringBuilder sb = new StringBuilder();
            sb.append(command.getCommandName());
            if (!command.getParameters().isEmpty()) {
                sb.append(" [" + String.join(", ", command.getParameters()) + "]");
            }
            sb.append(" (" + command.getDescription() + ")");

            if (Command.STAFF_ONLY_COMMANDS.contains(command)) {
                staffCommandDisplays.add(sb.toString());
            }
            else {
                regularCommandDisplays.add(sb.toString());
            }
        }

        requestContext.event().getChannel().sendMessage("**REGULAR COMMANDS**\n" +
                String.join("\n", regularCommandDisplays) +
                "\n\n**STAFF COMMANDS**\n" +
                String.join("\n", staffCommandDisplays)).queue();
    }

    private void handleEventStartCommand(RequestContext requestContext) {
        if (isStaff(requestContext.event())) {
            requestContext.event().getChannel().sendMessage("The event just started, come join and participate! :star2:").queue();
        }
    }

    private void handleEventOverCommand(RequestContext requestContext) {
        if (isStaff(requestContext.event())) {
            requestContext.event().getChannel().sendMessage("This event has been closed. Thanks for participating! :heart:").queue();
        }
    }

    private void handleEchoCommand(RequestContext requestContext) {
        if (requestContext.arguments() != null && !requestContext.arguments().isBlank()) {
            requestContext.event().getChannel().sendMessage(requestContext.arguments()).queue();
        }
    }

    private static boolean isStaff(MessageReceivedEvent event) {
        if (event.getMember() == null) {
            return false;
        }
        return event.getMember().getRoles().stream().anyMatch(r -> r.getName().contains("Staff"));
    }
}
