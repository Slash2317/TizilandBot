package com.slash.tizilandbot.repository;

import com.slash.tizilandbot.domain.GhostPingChannel;

import java.util.List;

public interface GhostPingChannelRepository {
    List<GhostPingChannel> findAll(Long guildDiscordId);
    void save(Long guildDiscordId, List<GhostPingChannel> ghostPingChannels);
}
