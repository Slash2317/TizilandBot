package com.slash.tizilandbot.domain;

public class GhostPingChannel {

    private Integer id;
    private Long guildDiscordId;
    private Long channelDiscordId;

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
}
