package com.slash.tizilandbot.handler;

import com.slash.tizilandbot.domain.DiscordUser;
import com.slash.tizilandbot.domain.RPSOption;
import com.slash.tizilandbot.exception.InvalidPermissionException;
import com.slash.tizilandbot.repository.DiscordUserRepository;
import com.slash.tizilandbot.repository.DiscordUserRepositoryImpl;
import com.slash.tizilandbot.request.Command;
import com.slash.tizilandbot.request.CommandGroup;
import com.slash.tizilandbot.request.MessageRequestContext;
import com.slash.tizilandbot.request.RequestContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

import java.awt.*;
import java.math.BigInteger;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MiscRequestHandler {

    private static BigInteger MAX_VALUE = new BigInteger("2147483647");

    private DiscordUserRepository discordUserRepository;

    public MiscRequestHandler() {
        this(new DiscordUserRepositoryImpl());
    }

    public MiscRequestHandler(DiscordUserRepository discordUserRepository) {
        this.discordUserRepository = discordUserRepository;
    }

    public void handleHelpCommand(RequestContext requestContext) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Tiziland Bot | Commands")
                .setAuthor("Tiziland Bot")
                .setColor(Color.decode("#a020f0"))
                .setDescription("Click on a button to see our commands that are pertaining to each category.");

        MessageCreateAction messageCreateAction = requestContext.getChannel().sendMessageEmbeds(embedBuilder.build()).setAllowedMentions(Collections.emptyList());

        for (CommandGroup commandGroup : CommandGroup.values()) {
            messageCreateAction.addActionRow(new ButtonImpl(commandGroup.getIdentifier(), commandGroup.getTitle(), ButtonStyle.PRIMARY, false, Emoji.fromFormatted(commandGroup.getEmojiUnicode())));
        }
        messageCreateAction.queue();
    }

    public void handleHelpButtonCommand(ButtonInteractionEvent event, CommandGroup commandGroup) {
        List<Command> commands = Arrays.stream(Command.values()).filter(c -> c.getCommandGroup() == commandGroup && !c.isHidden()).toList();
        String prefix = System.getProperty("prefix");

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(Emoji.fromFormatted(commandGroup.getEmojiUnicode()).getFormatted() + " " + commandGroup.getTitle() + " | Commands")
                .setAuthor("Tiziland Bot")
                .setColor(Color.decode("#a020f0"))
                .setDescription(commands.stream().map(c -> c.getFullDescription(prefix, false)).collect(Collectors.joining("\n")));

        event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
    }

    public void handleRPSCommand(RequestContext requestContext) {
        try {
            String optionName = requestContext.getArgument(Command.ROCK_PAPER_SCISSORS.getParameters().get(0).name(), String.class);
            if (optionName == null) {
                throw new IllegalArgumentException("No arguments");
            }

            RPSOption rpsOption = RPSOption.find(optionName);

            if (rpsOption == null) {
                throw new IllegalArgumentException("Invalid argument");
            }

            if (rpsOption == RPSOption.SPOCK || rpsOption == RPSOption.LIZARD) {
                requestContext.getChannel().sendMessage(String.format("...you're not like the rest. Try %srpsls next time", requestContext.getPrefix())).queue();
                return;
            }

            int option = (int) Math.floor(Math.random() * 3);
            RPSOption botOption = RPSOption.STANDARD_OPTIONS[option];

            if (rpsOption == botOption) {
                requestContext.sendSimpleMessageEmbed(botOption.getMessage() + "\nDraw!");
            }
            else if (rpsOption.getBeatsOptions().contains(botOption.name())) {
                requestContext.sendSimpleMessageEmbed(botOption.getMessage() + "\nYou win!");
            }
            else if (botOption.getBeatsOptions().contains(rpsOption.name())) {
                requestContext.sendSimpleMessageEmbed(botOption.getMessage() + "\nI win!");
            }
        }
        catch (IllegalArgumentException e) {
            requestContext.sendSimpleMessageEmbed("The command must follow this format `" + requestContext.getCommand().getFullDescription(requestContext.getPrefix(), false) + "`");
        }
    }

    public void handleRPSLSCommand(RequestContext requestContext) {
        try {
            String optionName = requestContext.getArgument(Command.ROCK_PAPER_SCISSORS_LIZARD_SPOCK.getParameters().get(0).name(), String.class);
            if (optionName == null) {
                throw new IllegalArgumentException("No arguments");
            }

            RPSOption userOption = RPSOption.find(optionName);

            if (userOption == null) {
                throw new IllegalArgumentException("Invalid argument");
            }


            int option = (int) Math.floor(Math.random() * 5);
            RPSOption botOption = RPSOption.values()[option];

            if (userOption == botOption) {
                requestContext.sendSimpleMessageEmbed(botOption.getMessage() + "\nDraw!");
            }
            else if (userOption.getBeatsOptions().contains(botOption.name())) {
                requestContext.sendSimpleMessageEmbed(botOption.getMessage() + "\nYou win!");
            }
            else if (botOption.getBeatsOptions().contains(userOption.name())) {
                requestContext.sendSimpleMessageEmbed(botOption.getMessage() + "\nI win!");
            }
        }
        catch (IllegalArgumentException e) {
            requestContext.sendSimpleMessageEmbed("The command must follow this format `" + requestContext.getCommand().getFullDescription(requestContext.getPrefix(), false) + "`");
        }
    }

    public void handleCoinFlipCommand(RequestContext requestContext) {
        int option = (int) Math.floor(Math.random() * 6000);

        if (option == 0) {
            requestContext.sendSimpleMessageEmbed(":new_moon_with_face: It landed on the side");
        }
        else {
            option = (int) Math.floor(Math.random() * 2);

            if (option == 0) {
                requestContext.sendSimpleMessageEmbed(":coin: Heads");
            }
            else {
                requestContext.sendSimpleMessageEmbed(":coin: Tails");
            }
        }
    }

    public void handleRollDiceCommand(RequestContext requestContext) {
        BigInteger sides = new BigInteger("6");
        String sidesOption = requestContext.getArgument("sides:6", String.class);
        if (sidesOption != null && !sidesOption.isBlank()) {
            try {
                sides = new BigInteger(sidesOption);
                if (sides.compareTo(BigInteger.ONE) < 0) {
                    sides = new BigInteger("6");
                }
            }
            catch (NumberFormatException ignored) {

            }
        }

        BigInteger randomNumber;
        Random random = new Random();
        do {
            randomNumber = new BigInteger(sides.bitLength(), random);
        } while (randomNumber.compareTo(sides) >= 0);

        requestContext.sendSimpleMessageEmbed(":game_die: " + randomNumber);
    }

    public void handleEchoCommand(RequestContext requestContext) {
        String message = requestContext.getArgument("message", String.class);
        if (message != null && !message.isBlank()) {
            requestContext.getChannel().sendMessage(message).queue(m -> {
                if (requestContext instanceof MessageRequestContext messageRequestContext) {
                    messageRequestContext.getMessage().delete().queue();
                }
            });
        }
    }

    public void handleAddPointsCommand(RequestContext requestContext) {
        try {
            if (requestContext.getMember() == null || !hasPointsPermission(requestContext.getMember())) {
                throw new InvalidPermissionException("You do not have permission to add points to users");
            }

            String user = requestContext.getArgument("user", String.class);
            String pointsArg = requestContext.getArgument("points", String.class);

            if (user == null || pointsArg == null) {
                throw new IllegalArgumentException("Invalid args");
            }

            BigInteger points = new BigInteger(pointsArg);

            if (points.compareTo(BigInteger.ZERO) < 0) {
                throw new IllegalArgumentException("points must be positive");
            }

            Consumer<List<Member>> consumer = members -> {
                if (members.isEmpty()) {
                    return;
                }

                for (Member member : members) {
                    DiscordUser discordUser = discordUserRepository.findByMemberDiscordId(member.getIdLong());
                    if (discordUser == null) {
                        discordUser = new DiscordUser();
                        discordUser.setMemberDiscordId(member.getIdLong());
                        BigInteger newPoints = points;
                        if (newPoints.compareTo(MAX_VALUE) > 0) {
                            newPoints = MAX_VALUE;
                        }

                        discordUser.setPoints(newPoints.intValue());
                        discordUserRepository.insert(discordUser);
                    }
                    else {
                        BigInteger newPoints = points.add(new BigInteger(discordUser.getPoints().toString()));
                        if (newPoints.compareTo(MAX_VALUE) > 0) {
                            newPoints = MAX_VALUE;
                        }
                        discordUser.setPoints(newPoints.intValue());
                        discordUserRepository.updatePointsById(newPoints.intValue(), discordUser.getId());
                    }

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(Color.decode(System.getProperty("embed.color")))
                            .setTitle("<:tiziland_plus:1358509532795502602> **Successfully added " + points + " point(s) to" + member.getEffectiveName() + ".**")
                            .setDescription(requestContext.getMember().getAsMention() + " added " + points + " points to " + member.getAsMention() + ".")
                            .appendDescription("\n\n**" + member.getEffectiveName() + "'s Balance**")
                            .appendDescription("\n" + discordUser.getPoints() + " Points.");

                    requestContext.sendMessageEmbeds(embedBuilder.build());
                }
            };

            List<Member> members = new ArrayList<>();
            if (requestContext instanceof MessageRequestContext messageRequestContext) {
                members = messageRequestContext.getMessage().getMentions().getMembers();
            }

            if (!members.isEmpty()) {
                consumer.accept(members);
            }
            else {
                requestContext.getGuild().findMembers(m -> user.equals(m.getUser().getName())).onSuccess(consumer);
            }
        }
        catch (InvalidPermissionException e) {
            requestContext.sendSimpleMessageEmbed(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            requestContext.sendSimpleMessageEmbed("The command must follow this format `" + requestContext.getCommand().getFullDescription(requestContext.getPrefix(), false) + "`");
        }
    }

    public void handleSubtractPointsCommand(RequestContext requestContext) {
        try {
            if (requestContext.getMember() == null || !hasPointsPermission(requestContext.getMember())) {
                throw new InvalidPermissionException("You do not have permission to subtract points from users");
            }

            String user = requestContext.getArgument("user", String.class);
            String pointsArg = requestContext.getArgument("points", String.class);

            if (user == null || pointsArg == null) {
                throw new IllegalArgumentException("Invalid args");
            }

            BigInteger points = new BigInteger(pointsArg);

            if (points.compareTo(BigInteger.ZERO) < 0) {
                throw new IllegalArgumentException("points must be positive");
            }

            Consumer<List<Member>> consumer = members -> {
                if (members.isEmpty()) {
                    return;
                }

                for (Member member : members) {
                    DiscordUser discordUser = discordUserRepository.findByMemberDiscordId(member.getIdLong());
                    if (discordUser == null) {
                        discordUser = new DiscordUser();
                        discordUser.setMemberDiscordId(member.getIdLong());
                        discordUser.setPoints(0);
                        discordUserRepository.insert(discordUser);
                    }
                    else {
                        BigInteger newPoints = new BigInteger(discordUser.getPoints().toString()).subtract(points);
                        if (newPoints.compareTo(BigInteger.ZERO) < 0) {
                            newPoints = BigInteger.ZERO;
                        }
                        discordUser.setPoints(newPoints.intValue());
                        discordUserRepository.updatePointsById(newPoints.intValue(), discordUser.getId());
                    }

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(Color.decode(System.getProperty("embed.color")))
                            .setTitle("<:tiziland_dash:1358509095363154010> **Successfully subtracted " + points + " point(s) from" + member.getEffectiveName() + ".**")
                            .setDescription(requestContext.getMember().getAsMention() + " subtracted " + points + " points from " + member.getAsMention() + ".")
                            .appendDescription("\n\n**" + member.getEffectiveName() + "'s Balance**")
                            .appendDescription("\n" + discordUser.getPoints() + " Points.");

                    requestContext.sendMessageEmbeds(embedBuilder.build());
                }
            };

            List<Member> members = new ArrayList<>();
            if (requestContext instanceof MessageRequestContext messageRequestContext) {
                members = messageRequestContext.getMessage().getMentions().getMembers();
            }

            if (!members.isEmpty()) {
                consumer.accept(members);
            }
            else {
                requestContext.getGuild().findMembers(m -> user.equals(m.getUser().getName())).onSuccess(consumer);
            }
        }
        catch (InvalidPermissionException e) {
            requestContext.sendSimpleMessageEmbed(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            requestContext.sendSimpleMessageEmbed("The command must follow this format `" + requestContext.getCommand().getFullDescription(requestContext.getPrefix(), false) + "`");
        }
    }

    public void handleBalanceCommand(RequestContext requestContext) {
        try {
            Consumer<List<Member>> consumer = members -> {
                if (members.isEmpty()) {
                    return;
                }

                for (Member member : members) {
                    DiscordUser discordUser = discordUserRepository.findByMemberDiscordId(member.getIdLong());
                    Integer points = 0;
                    if (discordUser != null) {
                        points = discordUser.getPoints();
                    }

                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(Color.decode(System.getProperty("embed.color")))
                            .setAuthor(member.getUser().getGlobalName(), null, member.getEffectiveAvatarUrl())
                            .setTitle("<:tiziland_coins:1358510312659095598> **" + member.getEffectiveName() + "'s Balance**")
                            .setDescription("**" + points.toString() + " Points.**");

                    requestContext.sendMessageEmbeds(embedBuilder.build());
                }
            };

            String user = requestContext.getArgument("user", String.class);
            if (user == null) {
                consumer.accept(List.of(requestContext.getMember()));
                return;
            }

            List<Member> members = new ArrayList<>();
            if (requestContext instanceof MessageRequestContext messageRequestContext) {
                members = messageRequestContext.getMessage().getMentions().getMembers();
            }

            if (!members.isEmpty()) {
                consumer.accept(members);
            }
            else {
                requestContext.getGuild().findMembers(m -> user.equals(m.getUser().getName())).onSuccess(consumer);
            }
        }
        catch (IllegalArgumentException e) {
            requestContext.sendSimpleMessageEmbed("The command must follow this format `" + requestContext.getCommand().getFullDescription(requestContext.getPrefix(), false) + "`");
        }
    }

    private boolean hasPointsPermission(Member member) {
        if (member == null) {
            return false;
        }
        if (member.isOwner()) {
            return true;
        }
        List<Role> roles = member.getRoles();
        for (Role role : roles) {
            if (role.getIdLong() == Long.parseLong(System.getProperty("points_role_id"))) {
                return true;
            }
        }
        return false;
    }
}
