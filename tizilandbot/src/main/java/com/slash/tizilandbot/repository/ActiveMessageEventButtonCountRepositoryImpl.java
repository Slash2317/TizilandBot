package com.slash.tizilandbot.repository;

import com.slash.tizilandbot.Application;
import com.slash.tizilandbot.domain.MemberButtonCount;
import com.slash.tizilandbot.utils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ActiveMessageEventButtonCountRepositoryImpl implements ActiveMessageEventButtonCountRepository {

    @Override
    public boolean incrementButtonCountByMessageDiscordIdAndMemberDiscordId(Long messageDiscordId, Long memberDiscordId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Application.getDataSource().getConnection();
            stmt = conn.prepareStatement("UPDATE active_message_event_button_count SET count = count + 1 WHERE message_discord_id = ? AND member_discord_id = ?");
            stmt.setLong(1, messageDiscordId);
            stmt.setLong(2, memberDiscordId);
            return stmt.executeUpdate() != 0;
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
    public void insertButtonCount(Long messageDiscordId, Long memberDiscordId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = Application.getDataSource().getConnection();
            stmt = conn.prepareStatement("""
                    INSERT INTO active_message_event_button_count(active_message_event_id, message_discord_id, member_discord_id, count)
                    SELECT active_message_event_id, ?, ?, ? FROM active_message_event WHERE message_discord_id = ?""");
            stmt.setLong(1, messageDiscordId);
            stmt.setLong(2, memberDiscordId);
            stmt.setInt(3, 1);
            stmt.setLong(4, messageDiscordId);
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
    public MemberButtonCount findMaxMemberIdByEventId(Integer eventId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Application.getDataSource().getConnection();
            stmt = conn.prepareStatement("SELECT member_discord_id, count FROM active_message_event_button_count WHERE active_message_event_id = ? ORDER BY count DESC LIMIT 1");
            stmt.setInt(1, eventId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return new MemberButtonCount(rs.getLong(1), rs.getInt(2));
            }
            return null;
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        finally {
            DbUtils.closeQuietly(conn, stmt, rs);
        }
    }
}
