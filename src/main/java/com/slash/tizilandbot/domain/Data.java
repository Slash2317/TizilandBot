package com.slash.tizilandbot.domain;

import java.util.ArrayList;
import java.util.List;

public class Data {

    private List<ChannelInfo> ghostPingChannels = new ArrayList<>();

    public List<ChannelInfo> getGhostPingChannels() {
        return ghostPingChannels;
    }

    public void setGhostPingChannels(List<ChannelInfo> ghostPingChannels) {
        this.ghostPingChannels = ghostPingChannels;
    }
}
