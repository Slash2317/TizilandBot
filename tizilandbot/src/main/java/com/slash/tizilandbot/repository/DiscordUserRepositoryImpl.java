package com.slash.tizilandbot.repository;

import com.slash.tizilandbot.Application;
import com.slash.tizilandbot.domain.DiscordUser;
import com.slash.tizilandbot.utils.DbUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DiscordUserRepositoryImpl implements DiscordUserRepository {

    @Override
    public DiscordUser findByMemberDiscordId(Long discordMemberId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Application.getDataSource().getConnection();
            stmt = conn.prepareStatement("SELECT discord_user_id, member_discord_id, points FROM discord_user WHERE member_discord_id = ?");
            stmt.setLong(1, discordMemberId);
            rs = stmt.executeQuery();
            if (!rs.next()) {
                return null;
            }

            DiscordUser discordUser = new DiscordUser();
            discordUser.setId(rs.getInt("discord_user_id"));
            discordUser.setMemberDiscordId(rs.getLong("member_discord_id"));
            discordUser.setPoints(rs.getInt("points"));
            return discordUser;
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
    public void insert(DiscordUser discordUser) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Application.getDataSource().getConnection();
            stmt = conn.prepareStatement("INSERT INTO discord_user(member_discord_id, points) VALUES(?, ?)");
            stmt.setLong(1, discordUser.getMemberDiscordId());
            stmt.setInt(2, discordUser.getPoints());
            stmt.executeUpdate();
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
    public void updatePointsById(Integer points, Integer id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = Application.getDataSource().getConnection();
            stmt = conn.prepareStatement("UPDATE discord_user SET points = ? WHERE discord_user_id = ?");
            stmt.setInt(1, points);
            stmt.setInt(2, id);
            stmt.executeUpdate();
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
