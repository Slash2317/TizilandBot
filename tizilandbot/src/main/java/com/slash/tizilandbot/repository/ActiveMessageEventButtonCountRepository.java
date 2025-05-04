package com.slash.tizilandbot.repository;

import com.slash.tizilandbot.domain.MemberButtonCount;

public interface ActiveMessageEventButtonCountRepository {
    boolean incrementButtonCountByMessageDiscordIdAndMemberDiscordId(Long messageDiscordId, Long memberDiscordId);
    void insertButtonCount(Long messageDiscordId, Long memberDiscordId);
    MemberButtonCount findMaxMemberIdByEventId(Integer eventId);
}
