package com.slash.tizilandbot;

import com.slash.tizilandbot.domain.GhostPingChannel;
import com.slash.tizilandbot.handler.*;
import com.slash.tizilandbot.repository.GhostPingChannelRepository;
import com.slash.tizilandbot.repository.GhostPingChannelRepositoryImpl;
import com.slash.tizilandbot.request.CommandGroup;
import com.slash.tizilandbot.request.MessageRequestContext;
import com.slash.tizilandbot.request.RequestContext;
import com.slash.tizilandbot.service.PointEventService;
import com.slash.tizilandbot.service.PointEventServiceImpl;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TizilandBotListener extends ListenerAdapter {
    private final InfoRequestHandler infoRequestHandler;
    private final ModerationRequestHandler moderationRequestHandler;
    private final MiscRequestHandler miscRequestHandler;
    private final TizilandRequestHandler tizilandRequestHandler;
    private final GhostPingChannelRepository ghostPingChannelRepository;
    private final PointEventService pointEventService;

    public TizilandBotListener() {
        this.infoRequestHandler = new InfoRequestHandler();
        this.moderationRequestHandler = new ModerationRequestHandler();
        this.miscRequestHandler = new MiscRequestHandler();
        this.tizilandRequestHandler = new TizilandRequestHandler();
        this.ghostPingChannelRepository = new GhostPingChannelRepositoryImpl();
        this.pointEventService = new PointEventServiceImpl();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.getMessage().getContentRaw().startsWith(System.getProperty("prefix"))) {
            return;
        }

        handleEvent(new MessageRequestContext(event, System.getProperty("prefix")));
    }

    private void handleEvent(RequestContext requestContext) {
        if (requestContext.getCommand() == null) {
            return;
        }
        switch (requestContext.getCommand()) {
            case MUTE -> moderationRequestHandler.handleMuteCommand(requestContext);
            case UN_MUTE -> moderationRequestHandler.handleUnMuteCommand(requestContext);
            case KICK -> moderationRequestHandler.handleKickCommand(requestContext);
            case BAN -> moderationRequestHandler.handleBanCommand(requestContext);
            case UNBAN -> moderationRequestHandler.handleUnbanCommand(requestContext);
            case ROLE_GIVE -> moderationRequestHandler.handleRoleGiveCommand(requestContext);
            case ROLE_REMOVE -> moderationRequestHandler.handleRoleRemoveCommand(requestContext);
            case PING_IN -> moderationRequestHandler.handlePingInCommand(requestContext);
            case REMOVE_PING_IN -> moderationRequestHandler.handleRemovePingInCommand(requestContext);
            case VIEW_PING_IN -> moderationRequestHandler.handleViewPingInCommand(requestContext);
            case MEMBER_INFO -> infoRequestHandler.handleMemberInfoCommand(requestContext);
            case SERVER_INFO -> infoRequestHandler.handleServerInfoCommand(requestContext);
            case INVITE -> tizilandRequestHandler.handleInviteCommand(requestContext);
            case RULES -> tizilandRequestHandler.handleRulesCommand(requestContext);
            case STAFF -> tizilandRequestHandler.handleStaffCommand(requestContext);
            case TIZIPAGES -> tizilandRequestHandler.handleTizipagesCommand(requestContext);
            case ECHO -> miscRequestHandler.handleEchoCommand(requestContext);
            case ROCK_PAPER_SCISSORS -> miscRequestHandler.handleRPSCommand(requestContext);
            case ROCK_PAPER_SCISSORS_LIZARD_SPOCK -> miscRequestHandler.handleRPSLSCommand(requestContext);
            case COIN_FLIP -> miscRequestHandler.handleCoinFlipCommand(requestContext);
            case ROLL_DICE -> miscRequestHandler.handleRollDiceCommand(requestContext);
            case HELP -> miscRequestHandler.handleHelpCommand(requestContext);
            case ADD_POINTS -> miscRequestHandler.handleAddPointsCommand(requestContext);
            case SUBTRACT_POINTS -> miscRequestHandler.handleSubtractPointsCommand(requestContext);
            case BALANCE -> miscRequestHandler.handleBalanceCommand(requestContext);
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();

        if (command.equals("help")) {
            EmbedBuilder embedBuilder = new EmbedBuilder();

            String prefix = System.getProperty("prefix");
            embedBuilder.setColor(Color.decode("#a020f0"))
                    .setDescription(String.format("""
                        **Welcome!** :smiley:
                        Currently, this bot, Tiziland Bot, does **not** support slash commands for coding and modifying to be easier.
                        Instead, please use the prefix `%s` in all your commands. For example, %shelp.
                        **For the actual commands list, please use the %shelp command in any chat this bot is enabled on.**""", prefix, prefix, prefix));

            event.replyEmbeds(embedBuilder.build()).setAllowedMentions(Collections.emptyList()).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("button_message_event")) {
            pointEventService.handleButtonInteraction(event);
            event.getInteraction().deferEdit().queue();
        }
        else {
            CommandGroup group = Arrays.stream(CommandGroup.values()).filter(g -> g.getIdentifier().equalsIgnoreCase(event.getComponentId())).findFirst().get();
            miscRequestHandler.handleHelpButtonCommand(event, group);
        }
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();
        commandData.add(Commands.slash("help", "Gives info about how to use this bot"));
        event.getGuild().updateCommands().addCommands(commandData).queue();
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Long guildId = event.getGuild().getIdLong();
        List<GhostPingChannel> ghostPingChannels = ghostPingChannelRepository.findAll(guildId);
        if (ghostPingChannels.isEmpty()) {
            return;
        }

        String user = event.getMember().getAsMention();
        for (GhostPingChannel ghostPingChannel : ghostPingChannels) {
            TextChannel channel = event.getGuild().getTextChannelById(ghostPingChannel.getChannelDiscordId());
            if (channel != null) {
                channel.sendMessage(user).queue(message -> message.delete().queue());
            }
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        pointEventService.handleReaction(event);
    }
}
