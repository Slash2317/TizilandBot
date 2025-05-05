package com.slash.tizilandbot.service;

import com.slash.tizilandbot.Application;
import com.slash.tizilandbot.domain.*;
import com.slash.tizilandbot.repository.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

import java.awt.*;
import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PointEventServiceImpl implements PointEventService {
    private static BigInteger MAX_VALUE = new BigInteger("2147483647");
    private static List<EventQuestion> questions = List.of(
            new EventQuestion("When is Frodo's Birthday?", List.of(
                    new EventQuestionAnswer("May 16th", false),
                    new EventQuestionAnswer("June 7th", false),
                    new EventQuestionAnswer("June 16th", true)
            )),
            new EventQuestion("When was the server created?", List.of(
                    new EventQuestionAnswer("May 22nd", false),
                    new EventQuestionAnswer("May 12th", false),
                    new EventQuestionAnswer("May 16th", true)
            )),
            new EventQuestion("When is Tizi's Birthday?", List.of(
                    new EventQuestionAnswer("May 19th", false),
                    new EventQuestionAnswer("May 22nd", true),
                    new EventQuestionAnswer("May 2nd", false)
            )),
            new EventQuestion("Which of these names did Tiziland previously have?", List.of(
                    new EventQuestionAnswer("Tizi's Place", false),
                    new EventQuestionAnswer("real tizi.", true),
                    new EventQuestionAnswer("Frodo's Chill", false)
            )),
            new EventQuestion("What was our previous ARG named?", List.of(
                    new EventQuestionAnswer("TYL", false),
                    new EventQuestionAnswer("16.05.24", true),
                    new EventQuestionAnswer("ESW", false)
            ))
    );

    private final ActiveMessageEventRepository messageEventRepository;
    private final ActiveMessageEventButtonCountRepository buttonCountRepository;
    private final DiscordUserRepository discordUserRepository;

    public PointEventServiceImpl() {
        this(new ActiveMessageEventRepositoryImpl(), new ActiveMessageEventButtonCountRepositoryImpl(), new DiscordUserRepositoryImpl());
    }

    public PointEventServiceImpl(ActiveMessageEventRepository messageEventRepository, ActiveMessageEventButtonCountRepository buttonCountRepository, DiscordUserRepository discordUserRepository) {
        this.messageEventRepository = messageEventRepository;
        this.discordUserRepository = discordUserRepository;
        this.buttonCountRepository = buttonCountRepository;
    }

    @Override
    public void sendRandomEventMessage(Long guildDiscordId, Long channelDiscordId) {
        Guild guild = Application.getJda().getGuildById(guildDiscordId);
        if (guild == null) {
            return;
        }
        TextChannel channel = guild.getTextChannelById(channelDiscordId);

        int chance = (int) (Math.random() * 100);
        if (chance < 40) {
            sendReactMessage(channel);
        }
        else if (chance < 80){
            sendClickButtonMessage(channel);
        }
        else {
            sendQuestionMessage(channel);
        }
    }

    @Override
    public void sendReactMessage(TextChannel channel) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode(System.getProperty("embed.color")))
                .setTitle(":tada_purple: **React first to this message for points!**")
                .setDescription("React first to this message to get a certain amount of points!");

        channel.sendMessageEmbeds(embedBuilder.build()).setAllowedMentions(Collections.emptyList()).queue(m -> createAndSaveMessageEvent(channel, m, MessageEventType.REACTION, 1000));
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
        if (message.getTimeCreated().isBefore(OffsetDateTime.now().minusMinutes(5))) {
            return;
        }

        ActiveMessageEvent deletedEvent = messageEventRepository.deleteByMessageDiscordId(event.getMessageIdLong());
        if (deletedEvent == null) {
            //no active event
            return;
        }
        Member member = event.getMember();
        BigInteger points = new BigInteger(deletedEvent.getPoints().toString());
        addPointsToUser(points, member.getIdLong());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode(System.getProperty("embed.event_over_color")))
                .setTitle(":blobcry: **Event Ended**")
                .setDescription("Congratulations " + member.getAsMention() + "! You reacted first!")
                .appendDescription("\nYou just received **" + deletedEvent.getPoints().toString() + " Points!!**");
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).setAllowedMentions(Collections.emptyList()).queue();
    }

    @Override
    public void sendClickButtonMessage(TextChannel channel) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(":purple_siren: Click the buttons as many times as you can for points!")
                .setColor(Color.decode("#a020f0"))
                .setDescription("Click the button as many times as possible for points! The user who clicks the button the most will get a certain amount of points!");

        MessageCreateAction messageCreateAction = channel.sendMessageEmbeds(embedBuilder.build()).setAllowedMentions(Collections.emptyList());
        messageCreateAction.addActionRow(new ButtonImpl("button_message_event", "Click", ButtonStyle.PRIMARY, false, Emoji.fromFormatted("\uD83E\uDD11")));
        messageCreateAction.queue(m -> createAndSaveMessageEvent(channel, m, MessageEventType.BUTTON, 0));
    }

    @Override
    public void handleClickButtonInteraction(ButtonInteractionEvent event) {
        if (event.getMember() == null) {
            event.getInteraction().deferEdit().queue();
            return;
        }
        if (event.getMessage().getTimeCreated().isBefore(OffsetDateTime.now().minusMinutes(5))) {
            event.getInteraction().deferEdit().queue();
            return;
        }

        boolean updated = buttonCountRepository.incrementButtonCountByMessageDiscordIdAndMemberDiscordId(event.getMessageIdLong(), event.getMember().getIdLong());
        if (!updated) {
            buttonCountRepository.insertButtonCount(event.getMessageIdLong(), event.getMember().getIdLong());
        }
        event.getInteraction().deferEdit().queue();
    }

    @Override
    public void sendQuestionMessage(TextChannel channel) {
        int questionIndex = (int) (Math.random() * questions.size());
        EventQuestion question = questions.get(questionIndex);

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(":question: Answer correctly for points!")
                .setColor(Color.decode("#a020f0"))
                .setDescription(question.question());

        MessageCreateAction messageCreateAction = channel.sendMessageEmbeds(embedBuilder.build()).setAllowedMentions(Collections.emptyList());
        for (EventQuestionAnswer answer : question.answers()) {
            String componentId = answer.correct() ? "question_correct" : "question_incorrect";
            messageCreateAction.addActionRow(new ButtonImpl(componentId + question.answers().indexOf(answer), answer.answer(), ButtonStyle.PRIMARY, false, null));
        }
        messageCreateAction.queue(m -> createAndSaveMessageEvent(channel, m, MessageEventType.QUESTION, 2000));
    }

    @Override
    public void handleQuestionButtonInteraction(ButtonInteractionEvent event) {
        if (event.getMember() == null) {
            event.getInteraction().deferEdit().queue();
            return;
        }
        if (event.getMessage().getTimeCreated().isBefore(OffsetDateTime.now().minusMinutes(5))) {
            event.getInteraction().deferEdit().queue();
            return;
        }
        if (event.getComponentId().startsWith("question_incorrect")) {
            //show ephemereal message?
            event.getInteraction().deferEdit().queue();
            return;
        }

        ActiveMessageEvent deletedEvent = messageEventRepository.deleteByMessageDiscordId(event.getMessageIdLong());
        if (deletedEvent == null) {
            //no active event
            event.getInteraction().deferEdit().queue();
            return;
        }
        Member member = event.getMember();
        BigInteger points = new BigInteger(deletedEvent.getPoints().toString());
        addPointsToUser(points, member.getIdLong());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.decode(System.getProperty("embed.event_over_color")))
                .setTitle(":blobcry: **Event Ended**")
                .setDescription("Congratulations " + member.getAsMention() + "! You answered correctly first!")
                .appendDescription("\nYou just received **" + deletedEvent.getPoints().toString() + " Points!!**");
        event.getChannel().sendMessageEmbeds(embedBuilder.build()).setAllowedMentions(Collections.emptyList()).queue();
        event.getInteraction().deferEdit().queue();
    }

    @Override
    public boolean handleExpiredEvents() {
        List<ActiveMessageEvent> expiredEvents = messageEventRepository.findExpiredEvents();
        if (expiredEvents.isEmpty()) {
            return false;
        }

        List<ActiveMessageEvent> buttonEvents = expiredEvents.stream().filter(e -> e.getEventType() == MessageEventType.BUTTON).toList();
        List<ActiveMessageEvent> successfulEvents = handleExpiredClickButtonEvents(buttonEvents);

        messageEventRepository.deleteEventsByIdIn(expiredEvents.stream().map(ActiveMessageEvent::getId).toList());
        for (ActiveMessageEvent expiredEvent : expiredEvents) {
            if (successfulEvents.contains(expiredEvent)) {
                continue;
            }
            Guild guild = Application.getJda().getGuildById(expiredEvent.getGuildDiscordId());
            if (guild == null) {
                continue;
            }
            TextChannel textChannel = guild.getTextChannelById(expiredEvent.getChannelDiscordId());
            if (textChannel == null) {
                continue;
            }

            String action;
            if (expiredEvent.getEventType() == MessageEventType.BUTTON) {
                action = "clicked the button";
            }
            else if (expiredEvent.getEventType() == MessageEventType.REACTION){
                action = "reacted";
            }
            else {
                action = "answered correctly";
            }
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Color.decode(System.getProperty("embed.event_over_color")))
                    .setTitle(":blobcry: **Event Ended**")
                    .setDescription("No one " + action + " for 3 minutes so this event has expired.");

            textChannel.sendMessageEmbeds(embedBuilder.build()).setAllowedMentions(Collections.emptyList()).queue();
        }
        return true;
    }

    private List<ActiveMessageEvent> handleExpiredClickButtonEvents(List<ActiveMessageEvent> buttonEvents) {
        List<ActiveMessageEvent> successfulEvents = new ArrayList<>();
        for (ActiveMessageEvent buttonEvent : buttonEvents) {
            MemberButtonCount memberButtonCount = buttonCountRepository.findMaxMemberIdByEventId(buttonEvent.getId());
            if (memberButtonCount != null) {
                Integer points = memberButtonCount.count() * 100;
                addPointsToUser(new BigInteger(points.toString()), memberButtonCount.memberId());
                successfulEvents.add(buttonEvent);

                Guild guild = Application.getJda().getGuildById(buttonEvent.getGuildDiscordId());
                if (guild == null) {
                    continue;
                }
                TextChannel textChannel = guild.getTextChannelById(buttonEvent.getChannelDiscordId());
                if (textChannel == null) {
                    continue;
                }

                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setColor(Color.decode(System.getProperty("embed.event_over_color")))
                        .setTitle(":blobcry: **Event Ended**")
                        .setDescription("Congratulations <@" + memberButtonCount.memberId() + ">! You clicked the button " + memberButtonCount.count() + " times.")
                        .appendDescription("\nYou just received **" + points + " Points!!**");
                textChannel.sendMessageEmbeds(embedBuilder.build()).setAllowedMentions(Collections.emptyList()).queue();
            }
        }
        return successfulEvents;
    }

    @Override
    public boolean existsActiveEvent(Long guildDiscordId) {
        return messageEventRepository.existsByGuildDiscordId(guildDiscordId);
    }

    private void addPointsToUser(BigInteger points, Long memberId) {
        DiscordUser discordUser = discordUserRepository.findByMemberDiscordId(memberId);
        if (discordUser == null) {
            discordUser = new DiscordUser();
            discordUser.setMemberDiscordId(memberId);
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

    private void createAndSaveMessageEvent(TextChannel channel, Message message, MessageEventType eventType, Integer points) {
        ActiveMessageEvent activeMessageEvent = new ActiveMessageEvent();
        activeMessageEvent.setGuildDiscordId(channel.getGuild().getIdLong());
        activeMessageEvent.setChannelDiscordId(channel.getIdLong());
        activeMessageEvent.setMessageDiscordId(message.getIdLong());
        activeMessageEvent.setEventType(eventType);
        activeMessageEvent.setPoints(points);
        activeMessageEvent.setTimeCreated(message.getTimeCreated());
        messageEventRepository.insert(activeMessageEvent);
    }
}
