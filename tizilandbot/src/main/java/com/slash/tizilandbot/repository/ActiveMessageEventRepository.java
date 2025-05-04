package com.slash.tizilandbot.repository;

import com.slash.tizilandbot.domain.ActiveMessageEvent;

import java.util.List;

public interface ActiveMessageEventRepository {
    void insert(ActiveMessageEvent event);
    List<ActiveMessageEvent> deleteExpiredEvents();
    ActiveMessageEvent deleteByMessageDiscordId(Long messageDiscordId);
    boolean existsByGuildDiscordId(Long guildDiscordId);
}
