package com.slash.tizilandbot.handler;

import com.slash.tizilandbot.domain.RPSOption;
import com.slash.tizilandbot.request.Command;
import com.slash.tizilandbot.request.CommandGroup;
import com.slash.tizilandbot.request.RequestContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MiscRequestHandler {

    public void handleHelpCommand(RequestContext requestContext) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Tiziland Bot | Commands")
                .setAuthor("Tiziland Bot")
                .setColor(Color.decode("#a020f0"))
                .setDescription("Click on a button to see our commands that are pertaining to each category.");

        MessageCreateAction messageCreateAction = requestContext.event().getChannel().sendMessageEmbeds(embedBuilder.build()).setAllowedMentions(Collections.emptyList());

        for (CommandGroup commandGroup : CommandGroup.values()) {
            messageCreateAction.addActionRow(new ButtonImpl(commandGroup.getIdentifier(), commandGroup.getTitle(), ButtonStyle.PRIMARY, false, Emoji.fromFormatted(commandGroup.getEmojiUnicode())));
        }
        messageCreateAction.queue();
    }

    public void handleHelpButtonCommand(ButtonInteractionEvent event, CommandGroup commandGroup) {
        List<Command> commands = Arrays.stream(Command.values()).filter(c -> c.getCommandGroup() == commandGroup && !c.isHidden()).toList();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(Emoji.fromFormatted(commandGroup.getEmojiUnicode()).getFormatted() + " " + commandGroup.getTitle() + " | Commands")
                .setAuthor("Tiziland Bot")
                .setColor(Color.decode("#a020f0"))
                .setDescription(commands.stream().map(Command::getFullDescription).collect(Collectors.joining("\n")));

        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
    }

    public void handleRPSCommand(RequestContext requestContext) {
        try {
            if (requestContext.arguments() == null || requestContext.arguments().isBlank()) {
                throw new IllegalArgumentException("No arguments");
            }

            RPSOption userOption = RPSOption.find(requestContext.arguments());

            if (userOption == null) {
                throw new IllegalArgumentException("Invalid argument");
            }

            if (userOption == RPSOption.SPOCK || userOption == RPSOption.LIZARD) {
                requestContext.event().getChannel().sendMessage("...you're not like the rest. Try f!rpsls next time").queue();
                return;
            }

            int option = (int) Math.floor(Math.random() * 3);
            RPSOption botOption = RPSOption.STANDARD_OPTIONS[option];

            if (userOption == botOption) {
                requestContext.event().getChannel().sendMessage(botOption.getMessage() + "\nDraw!").queue();
            }
            else if (userOption.getBeatsOptions().contains(botOption.name())) {
                requestContext.event().getChannel().sendMessage(botOption.getMessage() + "\nYou win!").queue();
            }
            else if (botOption.getBeatsOptions().contains(userOption.name())) {
                requestContext.event().getChannel().sendMessage(botOption.getMessage() + "\nI win!").queue();
            }
        }
        catch (IllegalArgumentException e) {
            requestContext.event().getChannel().sendMessage("The command must follow this format `" + requestContext.command().getFullDescription() + "`").queue();
        }
    }

    public void handleRPSLSCommand(RequestContext requestContext) {
        try {
            if (requestContext.arguments() == null || requestContext.arguments().isBlank()) {
                throw new IllegalArgumentException("No arguments");
            }

            RPSOption userOption = RPSOption.find(requestContext.arguments());

            if (userOption == null) {
                throw new IllegalArgumentException("Invalid argument");
            }


            int option = (int) Math.floor(Math.random() * 5);
            RPSOption botOption = RPSOption.values()[option];

            if (userOption == botOption) {
                requestContext.event().getChannel().sendMessage(botOption.getMessage() + "\nDraw!").queue();
            }
            else if (userOption.getBeatsOptions().contains(botOption.name())) {
                requestContext.event().getChannel().sendMessage(botOption.getMessage() + "\nYou win!").queue();
            }
            else if (botOption.getBeatsOptions().contains(userOption.name())) {
                requestContext.event().getChannel().sendMessage(botOption.getMessage() + "\nI win!").queue();
            }
        }
        catch (IllegalArgumentException e) {
            requestContext.event().getChannel().sendMessage("The command must follow this format `" + requestContext.command().getFullDescription() + "`").queue();
        }
    }

    public void handleCoinFlipCommand(RequestContext requestContext) {
        int option = (int) Math.floor(Math.random() * 6000);

        if (option == 0) {
            requestContext.event().getChannel().sendMessage(":new_moon_with_face: It landed on the side").queue();
        }
        else {
            option = (int) Math.floor(Math.random() * 2);

            if (option == 0) {
                requestContext.event().getChannel().sendMessage(":coin: Heads").queue();
            }
            else {
                requestContext.event().getChannel().sendMessage(":coin: Tails").queue();
            }
        }
    }

    public void handleRollDiceCommand(RequestContext requestContext) {
        int sides = 6;
        if (requestContext.arguments() != null && !requestContext.arguments().isBlank()) {
            try {
                sides = Integer.parseInt(requestContext.arguments());
                if (sides < 1) {
                    sides = 6;
                }
            }
            catch (NumberFormatException ignored) {

            }
        }

        int option = (int) Math.floor(Math.random() * sides) + 1;
        requestContext.event().getChannel().sendMessage(":game_die: " + option).queue();
    }

    public void handleEchoCommand(RequestContext requestContext) {
        if (requestContext.arguments() != null && !requestContext.arguments().isBlank()) {
            requestContext.event().getChannel().sendMessage(requestContext.arguments()).queue();
        }
    }

    public void handleInviteCommand(RequestContext requestContext) {
        String inviteUrl = requestContext.event().getJDA().getInviteUrl(
                Permission.MANAGE_ROLES,
                Permission.KICK_MEMBERS,
                Permission.BAN_MEMBERS,
                Permission.CREATE_INSTANT_INVITE,
                Permission.MODERATE_MEMBERS,
                Permission.MESSAGE_SEND,
                Permission.MESSAGE_SEND_IN_THREADS,
                Permission.MESSAGE_MANAGE,
                Permission.MESSAGE_EMBED_LINKS,
                Permission.USE_APPLICATION_COMMANDS
        );

        requestContext.event().getChannel().sendMessage("""
                Here is our bot invite link! :smiley:
                We're glad to hear you want to invite Frodo Utilities in your Discord Server, thank you so much!
                :link: BOT INVITE LINK: http://bit.ly/frodoutilitiesinvite
                :question: Having Trouble? Try this link instead:  [INVITE](""" + inviteUrl + ")").queue();
    }
}
