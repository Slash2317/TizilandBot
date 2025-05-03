package com.slash.tizilandbot.repository;

import com.slash.tizilandbot.Application;
import com.slash.tizilandbot.domain.GhostPingChannel;
import com.slash.tizilandbot.utils.DbUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GhostPingChannelRepositoryImpl implements GhostPingChannelRepository {

    @Override
    public List<GhostPingChannel> findAll(Long guildDiscordId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Application.getDataSource().getConnection();
            stmt = conn.prepareStatement("SELECT ghost_ping_channel_id, channel_discord_id FROM ghost_ping_channel WHERE guild_discord_id = ?");
            stmt.setLong(1, guildDiscordId);
            rs = stmt.executeQuery();

            List<GhostPingChannel> ghostPingChannels = new ArrayList<>();
            while (rs.next()) {
                GhostPingChannel ghostPingChannel = new GhostPingChannel();
                ghostPingChannel.setId(rs.getInt(1));
                ghostPingChannel.setChannelDiscordId(rs.getLong(2));
                ghostPingChannel.setGuildDiscordId(guildDiscordId);
                ghostPingChannels.add(ghostPingChannel);
            }
            return ghostPingChannels;
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }

    @Override
    public void save(Long guildDiscordId, List<GhostPingChannel> ghostPingChannels) {
        List<Integer> existsingIds = ghostPingChannels.stream().map(GhostPingChannel::getId).filter(Objects::nonNull).toList();

        Connection conn = null;
        PreparedStatement stmtDelete = null;
        PreparedStatement stmtInsert = null;
        try {
            conn = Application.getDataSource().getConnection();

            if (!existsingIds.isEmpty()) {
                stmtDelete = conn.prepareStatement("DELETE FROM ghost_ping_channel WHERE guild_discord_id = ? AND ghost_ping_channel_id NOT IN (" + existsingIds.stream().map(Objects::toString).collect(Collectors.joining(",")) + ")");
            }
            else {
                stmtDelete = conn.prepareStatement("DELETE FROM ghost_ping_channel WHERE guild_discord_id = ?");
            }
            stmtDelete.setLong(1, guildDiscordId);
            stmtDelete.execute();

            List<GhostPingChannel> newChannels = ghostPingChannels.stream().filter(c -> c.getId() == null).toList();
            if (newChannels.isEmpty()) {
                return;
            }

            stmtInsert = conn.prepareStatement("INSERT INTO ghost_ping_channel(guild_discord_id, channel_discord_id) VALUES(?, ?)");
            for (GhostPingChannel channel : newChannels) {
                stmtInsert.setLong(1, channel.getGuildDiscordId());
                stmtInsert.setLong(2, channel.getChannelDiscordId());
                stmtInsert.addBatch();
            }
            stmtInsert.executeBatch();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally {
            DbUtils.closeQuietly(stmtInsert);
            DbUtils.closeQuietly(conn, stmtDelete, null);
        }
    }
}
