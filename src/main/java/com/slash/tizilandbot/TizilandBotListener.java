package com.slash.tizilandbot;

import com.slash.tizilandbot.request.Command;
import com.slash.tizilandbot.request.RequestContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
            case VERIFY_MSG -> handleVerifyMsgCommand(requestContext);
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
        if (!isVerifyChannel(requestContext.event())) {
            return;
        }
        if (requestContext.event().getMember() == null) {
            return;
        }

        List<Role> userRoles = requestContext.event().getMember().getRoles();
        if (userRoles.stream().anyMatch(r -> r.getName().equalsIgnoreCase("verified"))) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.decode("#a020f0"))
                    .setAuthor("Tiziland!!")
                    .setTitle("Error")
                    .setDescription("""
                        It seems you are already verified! If you are experiencing any issues, check out
                        :link: [ tiziland.dis/verifyissues ]( https://discord.com/channels/1170374164566249562/1170374165451264092/1251577671863898242 )""");

            requestContext.event().getChannel().sendMessageEmbeds(embedBuilder.build()).setAllowedMentions(Collections.emptyList())
                    .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
            return;
        }

        List<Role> roles = requestContext.event().getGuild().getRolesByName("verified", true);

        if (roles.isEmpty()) {
            return;
        }

        for (Role role : roles) {
            requestContext.event().getGuild().addRoleToMember(requestContext.event().getMember(), role).queue();
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode("#a020f0"))
                .setAuthor("Tiziland!!")
                .setTitle("Verification")
                .setDescription("""
                        You've been successfully verified. If you're experiencing issues, check out
                        :link:[ tiziland.dis/verifyissues ]( https://discord.com/channels/1108179404137447484/1108181838754742282 )""");

        requestContext.event().getChannel().sendMessageEmbeds(embedBuilder.build()).setAllowedMentions(Collections.emptyList())
                .queue(message -> message.delete().queueAfter(15, TimeUnit.SECONDS));
    }

    private void handleVerifyMsgCommand(RequestContext requestContext) {
        if (!isStaff(requestContext.event()) || !isVerifyChannel(requestContext.event())) {
            return;
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode("#a020f0"))
                .setAuthor("Tiziland!!")
                .setTitle("Verification")
                .setDescription("""
                        To verify in Tiziland, you must use the `t!verify` command in this channel.
                        
                        :warning: DISCLAIMER: If your verification is not working, check if the bot is online, as the bot depends on the owner's computer running. You'll have to wait until the bot is up. We're sorry and we are working to get the bot hosted 24/7!""");

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

    private static boolean isVerifyChannel(MessageReceivedEvent event) {
        String channelName = event.getChannel().getName().toLowerCase();
        return channelName.contains("staff") ||
        channelName.contains("important-shit") ||
        channelName.contains("verify");
    }
}
