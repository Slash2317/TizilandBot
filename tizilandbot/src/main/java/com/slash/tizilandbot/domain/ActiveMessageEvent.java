package com.slash.tizilandbot.domain;

import java.time.OffsetDateTime;

public class ActiveMessageEvent {
    private Integer id;
    private Long guildDiscordId;
    private Long channelDiscordId;
    private Long messageDiscordId;
    private MessageEventType eventType;
    private Integer points;
    private OffsetDateTime timeCreated;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getGuildDiscordId() {
        return guildDiscordId;
    }

    public void setGuildDiscordId(Long guildDiscordId) {
        this.guildDiscordId = guildDiscordId;
    }

    public Long getChannelDiscordId() {
        return channelDiscordId;
    }

    public void setChannelDiscordId(Long channelDiscordId) {
        this.channelDiscordId = channelDiscordId;
    }

    public Long getMessageDiscordId() {
        return messageDiscordId;
    }

    public void setMessageDiscordId(Long messageDiscordId) {
        this.messageDiscordId = messageDiscordId;
    }

    public MessageEventType getEventType() {
        return eventType;
    }

    public void setEventType(MessageEventType eventType) {
        this.eventType = eventType;
    }

    public OffsetDateTime getTimeCreated() {
        return timeCreated;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public void setTimeCreated(OffsetDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }
}
