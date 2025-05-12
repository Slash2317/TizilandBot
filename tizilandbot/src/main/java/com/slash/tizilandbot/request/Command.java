package com.slash.tizilandbot.request;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.slash.tizilandbot.request.CommandGroup.*;

public enum Command {

    MUTE("mute", MODERATION, "Mutes a user (seconds)",
            List.of(new OptionInfo("user", "The user to mute", OptionType.USER),
                    new OptionInfo("duration", "How long the user will be muted for", OptionType.INTEGER))),
    UN_MUTE("unmute", MODERATION, "Un-mutes a user",
            List.of(new OptionInfo("user", "The user to un-mute", OptionType.USER))),
    KICK("kick", MODERATION, "Kicks a user",
            List.of(new OptionInfo("user", "The user to kick", OptionType.USER),
                    new OptionInfo("reason", "The reason for kicking the user", OptionType.STRING))),
    BAN("ban", MODERATION, "Bans a user",
            List.of(new OptionInfo("user", "The user to ban", OptionType.USER),
                    new OptionInfo("reason", "The reason for banning the user", OptionType.STRING))),
    UNBAN("unban", MODERATION, "Unbans a user",
            List.of(new OptionInfo("user", "The user to unban", OptionType.USER))),
    ROLE_GIVE("rolegive", MODERATION, "Adds a role to a user",
            List.of(new OptionInfo("user", "The user to add a role to", OptionType.USER),
                    new OptionInfo("role", "The role to add", OptionType.ROLE))),
    ROLE_REMOVE("roleremove", MODERATION, "Removes a role from a user",
            List.of(new OptionInfo("user", "The user to remove a role from", OptionType.USER),
                    new OptionInfo("role", "The role to remove", OptionType.ROLE))),
    PING_IN("pingin", MODERATION, "Ghost ping new users in the specified channel",
            List.of(new OptionInfo("channel", "The channel to add a ghost ping in", OptionType.CHANNEL))),
    REMOVE_PING_IN("removepingin", MODERATION, "Remove ghost ping in the specified channel",
            List.of(new OptionInfo("channel", "The channel to remove a ghost ping from", OptionType.CHANNEL))),
    VIEW_PING_IN("viewpingin", MODERATION, "Shows a list of channels ghost ping new users"),

    MEMBER_INFO("member info", SERVER, "Provides information about a certain user",
            List.of(new OptionInfo("user", "The user you want to view", OptionType.USER))),
    SERVER_INFO("server info", SERVER, "Provides server information"),
    INVITE("invite", SERVER, "Shows invite links to Tiziland!!"),
    RULES("rules", SERVER, "Shows where to find server rules"),
    STAFF("staff", SERVER, "Gets the full list of current server staff"),

    TIZIPAGES("tizipages", OFF_TOPIC, "Gives links to all tizi pages"),

    ECHO("echo", FUN, "Says the requested content",
            List.of(new OptionInfo("message", "The message you want to be echoed", OptionType.STRING))),
    ROCK_PAPER_SCISSORS("rps", FUN, "Play RPS with the bot",
            List.of(new OptionInfo("rock/paper/scissors", "The option to choose", OptionType.STRING))),
    ROCK_PAPER_SCISSORS_LIZARD_SPOCK("rpsls", FUN, "Play RPSLS with the bot",
            List.of(new OptionInfo("rock/paper/scissors/lizard/spock", "The option to choose", OptionType.STRING)), true),
    COIN_FLIP("flipcoin", FUN, "Flips a coin"),
    ROLL_DICE("rolldice", FUN, "Rolls a dice (default 6 sides)",
            List.of(new OptionInfo("sides:6", "The number of sides the dice has", OptionType.INTEGER))),

    HELP("help", MISC, "Shows list of commands"),
    ADD_POINTS("addPoints", MISC, "Adds points to a user",
            List.of(new OptionInfo("user", "The user to add points to", OptionType.USER),
                    new OptionInfo("points", "The number of points to be added", OptionType.INTEGER))),
    SUBTRACT_POINTS("subtractPoints", MISC, "Subtracts points from a user",
            List.of(new OptionInfo("user", "The user to subtract points from", OptionType.USER),
                    new OptionInfo("points", "The number of points to be subtracted", OptionType.INTEGER))),
    BALANCE("balance", MISC, "Shows how many points you have",
            List.of(new OptionInfo("user", "The user whose points you want to view", OptionType.USER)));

    private final String commandName;
    private final String description;
    private final List<OptionInfo> parameters;
    private final CommandGroup commandGroup;
    private final boolean hidden;

    Command(String commandName, CommandGroup commandGroup, String description, List<OptionInfo> parameters, boolean hidden) {
        this.commandName = commandName;
        this.commandGroup = commandGroup;
        this.description = description;
        this.parameters = parameters;
        this.hidden = hidden;
    }

    Command(String commandName, CommandGroup commandGroup, String description, List<OptionInfo> parameters) {
        this(commandName, commandGroup, description, parameters, false);
    }

    Command(String commandName, CommandGroup commandGroup, String description) {
        this(commandName, commandGroup, description, Collections.emptyList());
    }

    public String getCommandName() {
        return commandName;
    }

    public String getDescription() {
        return description;
    }

    public List<OptionInfo> getParameters() {
        return parameters;
    }

    public CommandGroup getCommandGroup() {
        return commandGroup;
    }

    public boolean isHidden() {
        return hidden;
    }

    public static Command getCommandByMessage(String message, String prefix) {
        String lowerCaseMessage = message.toLowerCase();
        for (Command command : Command.values()) {
            if (lowerCaseMessage.equals((prefix + command.commandName).toLowerCase()) ||
                    lowerCaseMessage.startsWith((prefix + command.commandName + " ").toLowerCase())) {
                return command;
            }
        }
        return null;
    }

    public static Command getCommandByName(String name) {
        String lowercaseName = name.toLowerCase();
        return Arrays.stream(Command.values()).filter(c -> c.commandName.equals(lowercaseName)).findFirst().orElse(null);
    }

    public String getCommandFormat(String prefix) {
        StringBuilder sb = new StringBuilder(prefix + commandName);

        if (!parameters.isEmpty()) {
            sb.append(" [" + String.join("] [", parameters.stream().map(OptionInfo::name).toList()) + "]");
        }
        return sb.toString();
    }

    public String getFullDescription(String prefix, boolean bold) {
        if (bold) {
            return "**" + getCommandFormat(prefix) + "** | " + description;
        }
        return getCommandFormat(prefix) + " | " + description;
    }
}
