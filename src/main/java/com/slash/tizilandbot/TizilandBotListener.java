package com.slash.tizilandbot;

import com.slash.tizilandbot.request.Command;
import com.slash.tizilandbot.request.RequestContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
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
                    **Here are our current invite links!** :link:
                    
                    :link: Discord Invite Link: https://discord.gg/ugVnJgeSgq
                    :paperclips: Bit.ly Invite Link: http://bit.ly/tiziland_dis""").queue();
            case RULES -> event.getChannel().sendMessage(":scroll: You can read our rules here: https://discord.com/channels/1108179404137447484/1108181346033094736").queue();
            case TIZIPAGES -> event.getChannel().sendMessage("""
                    Here are all the pages of Tizi:
                    -bit.ly/tiziabout
                    -bit.ly/tizisocial
                    -bit.ly/tizi-links""").queue();
            case STAFF -> event.getChannel().sendMessage("""
                    **Here's a list of our current staff!**
                    
                    :crown: OWNER : Tizi!! `(tiziandfrodo)`
                    :tools: COMMUNITY MANAGER : Xanth `(._.xanth._.)`
                    :star2: ADMINISTRATOR(S) : Astral `(astral.null)`""").queue();
            case VERIFY -> handleVerifyCommand(requestContext);
            case ECHO -> handleEchoCommand(requestContext);
        }
    }

    private void handleHelpCommand(RequestContext requestContext) {
        List<String> commandDisplays = new ArrayList<>();
        for (Command command : Command.values()) {
            if (command.isHidden()) {
                continue;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(command.getCommandName());
            if (!command.getParameters().isEmpty()) {
                sb.append(" [" + String.join(", ", command.getParameters()) + "]");
            }
            sb.append(" (" + command.getDescription() + ")");

            commandDisplays.add(sb.toString());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("""
                **TIZILAND BOT | COMMANDS**
                ------------------------------------------------""");
        sb.append("\n\n" + String.join("\n", commandDisplays));

        requestContext.event().getChannel().sendMessage(sb.toString()).queue();
    }

    private void handleVerifyCommand(RequestContext requestContext) {
        if (!isStaff(requestContext.event())) {
            return;
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode("#a020f0"))
                .setAuthor("Tiziland!!")
                .setTitle("Verification")
                .setDescription("""
                        To Verify in Tiziland, you must react with :white_check_mark: to this message.""");

        requestContext.event().getChannel().sendMessageEmbeds(embedBuilder.build()).setAllowedMentions(Collections.emptyList()).queue();
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
