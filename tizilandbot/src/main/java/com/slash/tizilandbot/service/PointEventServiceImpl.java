package com.slash.tizilandbot.service;

import com.slash.tizilandbot.Application;
import com.slash.tizilandbot.domain.ActiveMessageEvent;
import com.slash.tizilandbot.domain.DiscordUser;
import com.slash.tizilandbot.domain.MessageEventType;
import com.slash.tizilandbot.repository.ActiveMessageEventRepository;
import com.slash.tizilandbot.repository.ActiveMessageEventRepositoryImpl;
import com.slash.tizilandbot.repository.DiscordUserRepository;
import com.slash.tizilandbot.repository.DiscordUserRepositoryImpl;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

public class PointEventServiceImpl implements PointEventService {
    private static BigInteger MAX_VALUE = new BigInteger("2147483647");

    private final ActiveMessageEventRepository messageEventRepository;
    private final DiscordUserRepository discordUserRepository;

    public PointEventServiceImpl() {
        this(new ActiveMessageEventRepositoryImpl(), new DiscordUserRepositoryImpl());
    }

    public PointEventServiceImpl(ActiveMessageEventRepository messageEventRepository, DiscordUserRepository discordUserRepository) {
        this.messageEventRepository = messageEventRepository;
        this.discordUserRepository = discordUserRepository;
    }

    @Override
    public void sendRandomEventMessage(Long guildDiscordId, Long channelDiscordId) {
        Guild guild = Application.getJda().getGuildById(guildDiscordId);
        if (guild == null) {
            return;
        }
        TextChannel channel = guild.getTextChannelById(channelDiscordId);
        sendReactMessage(channel);
    }

    @Override
    public void sendReactMessage(TextChannel channel) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode(System.getProperty("embed.color")))
                .setTitle(":tada_purple: **React first to this message for points!**")
                .setDescription("React first to this message to get a certain amount of points!");

        channel.sendMessageEmbeds(embedBuilder.build()).setAllowedMentions(Collections.emptyList()).queue(m -> createAndSaveMessageEvent(channel, m));
    }

    @Override
    public void handleReaction(MessageReactionAddEvent event) {
        if (event.getMember() == null) {
            return;
        }
        if (event.getMessageAuthorIdLong() != Application.getJda().getSelfUser().getIdLong()) {
            return;
        }
        Message message = event.getChannel().getHistoryAround(event.getMessageId(), 1).complete().getRetrievedHistory().get(0);
        if (message == null) {
            return;
        }

        ActiveMessageEvent deletedEvent = messageEventRepository.deleteByMessageDiscordId(event.getMessageIdLong());
        if (deletedEvent == null) {
            //no active event
            return;
        }
        Member member = event.getMember();
        BigInteger points = new BigInteger(deletedEvent.getPoints().toString());
        addPointsToUser(points, member);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode(System.getProperty("embed.event_over_color")))
                .setTitle(":blobcry: **Event Ended**")
                .setDescription("Congratulations " + member.getAsMention() + "! You reacted first!")
                .appendDescription("\nYou just received **" + deletedEvent.getPoints().toString() + " Points!!**");
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).setAllowedMentions(Collections.emptyList()).queue();
    }

    @Override
    public void handleExpiredEvents() {
        List<ActiveMessageEvent> expiredEvents = messageEventRepository.deleteExpiredEvents();
        for (ActiveMessageEvent expiredEvent : expiredEvents) {
            Guild guild = Application.getJda().getGuildById(expiredEvent.getGuildDiscordId());
            if (guild == null) {
                continue;
            }
            TextChannel textChannel = guild.getTextChannelById(expiredEvent.getChannelDiscordId());
            if (textChannel == null) {
                continue;
            }

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.decode(System.getProperty("embed.event_over_color")))
                    .setTitle(":blobcry: **Event Ended**")
                    .setDescription("No one reacted for 5 minutes so this event has expired.");

            textChannel.sendMessageEmbeds(embedBuilder.build()).setAllowedMentions(Collections.emptyList()).queue();
        }
    }

    @Override
    public boolean existsActiveEvent(Long guildDiscordId) {
        return messageEventRepository.existsByGuildDiscordId(guildDiscordId);
    }

    private void addPointsToUser(BigInteger points, Member member) {
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
    }

    private void createAndSaveMessageEvent(TextChannel channel, Message message) {
        ActiveMessageEvent activeMessageEvent = new ActiveMessageEvent();
        activeMessageEvent.setGuildDiscordId(channel.getGuild().getIdLong());
        activeMessageEvent.setChannelDiscordId(channel.getIdLong());
        activeMessageEvent.setMessageDiscordId(message.getIdLong());
        activeMessageEvent.setEventType(MessageEventType.REACTION);
        activeMessageEvent.setPoints(2000);
        activeMessageEvent.setTimeCreated(message.getTimeCreated());
        messageEventRepository.insert(activeMessageEvent);
    }
}
