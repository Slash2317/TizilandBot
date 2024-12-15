package com.slash.tizilandbot.domain;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

import java.util.*;
import java.util.stream.Collectors;

public class Data {

    private Map<String, List<String>> guildIdToGhostPingChannelIds = new HashMap<>();

    public Map<String, List<String>> getGuildIdToGhostPingChannelIds() {
        return guildIdToGhostPingChannelIds;
    }

    public void setGuildIdToGhostPingChannelIds(Map<String, List<String>> guildIdToGhostPingChannelIds) {
        this.guildIdToGhostPingChannelIds = guildIdToGhostPingChannelIds;
    }

    public String getChannelNamesDisplay(Guild guild) {
        LinkedHashMap<String, GuildChannel> idToChannel = getChannels(guildIdToGhostPingChannelIds.get(guild.getId()), guild);
        return idToChannel.entrySet().stream().map(e -> "[" + e.getKey() + ", " + e.getValue().getName() + "]").collect(Collectors.joining("\n"));
    }

    private LinkedHashMap<String, GuildChannel> getChannels(List<String> channelIds, Guild guild) {
        LinkedHashMap<String, GuildChannel> idToChannel = new LinkedHashMap<>();
        for (String id : channelIds) {
            idToChannel.put(id, guild.getGuildChannelById(id));
        }
        return idToChannel;
    }

    public static Data from(DataOld dataOld) {
        Data data = new Data();
        for (Map.Entry<String, List<ChannelInfo>> entry : dataOld.getGuildIdToGhostPingChannels().entrySet()) {
            data.getGuildIdToGhostPingChannelIds().put(entry.getKey(), entry.getValue().stream().map(ChannelInfo::getId).toList());
        }
        return data;
    }
}
