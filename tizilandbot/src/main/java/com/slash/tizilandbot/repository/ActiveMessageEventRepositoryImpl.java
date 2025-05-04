package com.slash.tizilandbot.repository;

import com.slash.tizilandbot.Application;
import com.slash.tizilandbot.domain.ActiveMessageEvent;
import com.slash.tizilandbot.domain.MessageEventType;
import com.slash.tizilandbot.utils.DbUtils;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ActiveMessageEventRepositoryImpl implements ActiveMessageEventRepository {

    @Override
    public void insert(ActiveMessageEvent event) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Application.getDataSource().getConnection();
            stmt = conn.prepareStatement("INSERT INTO active_message_event(guild_discord_id, channel_discord_id, message_discord_id, event_type, points, time_created) VALUES(?, ?, ?, ?, ?, ?)");
            stmt.setLong(1, event.getGuildDiscordId());
            stmt.setLong(2, event.getChannelDiscordId());
            stmt.setLong(3, event.getMessageDiscordId());
            stmt.setString(4, event.getEventType().name());
            stmt.setInt(5, event.getPoints());
            stmt.setTimestamp(6, Timestamp.valueOf(event.getTimeCreated().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()));
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally {
            DbUtils.closeQuietly(conn, stmt, null);
        }
    }

    @Override
    public List<ActiveMessageEvent> deleteExpiredEvents() {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Application.getDataSource().getConnection();
            stmt = conn.prepareStatement("""
                    DELETE FROM active_message_event WHERE time_created < current_timestamp - (5 * interval '1 minute')
                    RETURNING active_message_event_id, guild_discord_id, channel_discord_id, message_discord_id, event_type, points, time_created""");
            rs = stmt.executeQuery();

            List<ActiveMessageEvent> activeMessageEvents = new ArrayList<>();
            while (rs.next()) {
                activeMessageEvents.add(mapResults(rs));
            }
            return activeMessageEvents;
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
    public ActiveMessageEvent deleteByMessageDiscordId(Long messageDiscordId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Application.getDataSource().getConnection();
            stmt = conn.prepareStatement("""
                    DELETE FROM active_message_event WHERE message_discord_id = ?
                    RETURNING active_message_event_id, guild_discord_id, channel_discord_id, message_discord_id, event_type, points, time_created""");
            stmt.setLong(1, messageDiscordId);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                return null;
            }
            return mapResults(rs);
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
    public boolean existsByGuildDiscordId(Long guildDiscordId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Application.getDataSource().getConnection();
            stmt = conn.prepareStatement("SELECT 1 FROM active_message_event WHERE guild_discord_id = ?");
            stmt.setLong(1, guildDiscordId);
            rs = stmt.executeQuery();

            return rs.next();
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }

    private ActiveMessageEvent mapResults(ResultSet rs) throws SQLException {
        ActiveMessageEvent event = new ActiveMessageEvent();
        event.setId(rs.getInt("active_message_event_id"));
        event.setGuildDiscordId(rs.getLong("guild_discord_id"));
        event.setChannelDiscordId(rs.getLong("channel_discord_id"));
        event.setMessageDiscordId(rs.getLong("message_discord_id"));
        event.setEventType(MessageEventType.valueOf(rs.getString("event_type")));
        event.setPoints(rs.getInt("points"));
        event.setTimeCreated(OffsetDateTime.ofInstant(rs.getTimestamp("time_created").toInstant(), ZoneId.of("UTC")));
        return event;
    }
}
