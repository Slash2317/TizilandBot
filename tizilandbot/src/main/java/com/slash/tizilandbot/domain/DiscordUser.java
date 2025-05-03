package com.slash.tizilandbot.domain;

public class DiscordUser {

    private Integer id;
    private Long memberDiscordId;
    private Integer points;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getMemberDiscordId() {
        return memberDiscordId;
    }

    public void setMemberDiscordId(Long memberDiscordId) {
        this.memberDiscordId = memberDiscordId;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}
