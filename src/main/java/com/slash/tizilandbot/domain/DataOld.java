package com.slash.tizilandbot.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataOld {

    private Map<String, List<ChannelInfo>> guildIdToGhostPingChannels = new HashMap<>();

    public Map<String, List<ChannelInfo>> getGuildIdToGhostPingChannels() {
        return guildIdToGhostPingChannels;
    }

    public void setGuildIdToGhostPingChannels(Map<String, List<ChannelInfo>> guildIdToGhostPingChannels) {
        this.guildIdToGhostPingChannels = guildIdToGhostPingChannels;
    }
}
