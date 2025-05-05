package com.slash.tizilandbot.service;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

public interface PointEventService {
    void sendRandomEventMessage(Long guildDiscordId, Long channelDiscordId);
    void sendReactMessage(TextChannel channel);
    void handleReaction(MessageReactionAddEvent event);
    void sendClickButtonMessage(TextChannel channel);
    void handleClickButtonInteraction(ButtonInteractionEvent event);
    void sendQuestionMessage(TextChannel channel);
    void handleQuestionButtonInteraction(ButtonInteractionEvent event);
    boolean handleExpiredEvents();
    boolean existsActiveEvent(Long guildDiscordId);
}
