package com.slash.tizilandbot.repository;

import com.slash.tizilandbot.domain.DiscordUser;

public interface DiscordUserRepository {
    DiscordUser findByMemberDiscordId(Long discordMemberId);
    void insert(DiscordUser discordUser);
    void updatePointsById(Integer points, Integer id);
}
