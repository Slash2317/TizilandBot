package com.slash.tizilandbot.handler;

import com.slash.tizilandbot.request.RequestContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class InfoRequestHandler {

    public void handleServerInfoCommand(RequestContext requestContext) {
        Guild guild = requestContext.event().getGuild();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(guild.getName());

        if (guild.getOwner() != null) {
            embedBuilder.setColor(guild.getOwner().getColor())
                    .setDescription("**Owner:** " + guild.getOwner().getEffectiveName() + " (" + guild.getOwner().getUser().getName() + ")");
        }

        MessageEmbed embed = embedBuilder.addField("Member count", String.valueOf(guild.getMemberCount()), true)
                .addField("Created at", getTimeDisplay(guild.getTimeCreated()), true)
                .addBlankField(false)
                .addField("Boost count", String.valueOf(guild.getBoostCount()), true)
                .addField("Boost tier", getBoostTierDisplay(guild.getBoostTier()), true)
                .setImage(guild.getIconUrl())
                .setTimestamp(guild.getTimeCreated())
                .setFooter("ID: " + guild.getId(), guild.getIconUrl())
                .build();
        requestContext.event().getChannel().sendMessageEmbeds(embed).setAllowedMentions(Collections.emptyList()).queue();
    }

    private String getBoostTierDisplay(Guild.BoostTier boostTier) {
        return switch (boostTier) {
            case NONE -> "None";
            case TIER_1 -> "Tier 1";
            case TIER_2 -> "Tier 2";
            case TIER_3 -> "Tier 3";
            case UNKNOWN -> "Unknown";
        };
    }

    public void handleMemberInfoCommand(RequestContext requestContext) {
        List<Member> mentions = requestContext.event().getMessage().getMentions().getMembers();

        if (mentions.isEmpty()) {
            List<String> names = Arrays.stream(requestContext.arguments().split(" ")).map(String::trim).map(String::toLowerCase).toList();

            requestContext.event().getGuild().findMembers(m -> names.contains(m.getUser().getName().toLowerCase())).onSuccess(members -> {
                for (Member member : members) {
                    sendMemberInfo(member, requestContext);
                }
            });
        }
        else {
            for (Member member : mentions) {
                sendMemberInfo(member, requestContext);
            }
        }
    }

    private void sendMemberInfo(Member member, RequestContext requestContext) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(member.getColor())
                .setAuthor(member.getUser().getGlobalName(), null, member.getEffectiveAvatarUrl())
                .addField("Discord name", member.getUser().getName(), false);

        boolean isStaff = member.getRoles().stream().anyMatch(r -> r.getName().toLowerCase().contains("staff") && !r.getName().equalsIgnoreCase("ex-staff"));
        if (isStaff) {
            embedBuilder.setDescription(":white_check_mark: ***Staff member***");
        }
        else {
            embedBuilder.setDescription(":x: ***Common member***");
        }

        if (member.getNickname() != null) {
            embedBuilder.addField("Nickname", member.getNickname(), false);
        }

        String rolesString;
        if (member.getRoles().size() > 4) {
            rolesString = getRolesDisplayString(member.getRoles().subList(0, 4)) + "\n... " + (member.getRoles().size() - 4) + " more";
        }
        else {
            rolesString = getRolesDisplayString(member.getRoles());
        }

        MessageEmbed embed = embedBuilder.addField("Roles", rolesString, true)
                .addField("Created at", getTimeDisplay(member.getTimeCreated()), true)
                .addField("Joined at", getTimeDisplay(member.getTimeJoined()), true)
                .setImage(member.getEffectiveAvatarUrl())
                .setTimestamp(member.getTimeCreated())
                .setFooter("ID: " + member.getId(), member.getEffectiveAvatarUrl())
                .build();
        requestContext.event().getChannel().sendMessageEmbeds(embed).setAllowedMentions(Collections.emptyList()).queue();
    }

    private String getRolesDisplayString(List<Role> roles) {
        return roles.stream().map(r -> "<@&" + r.getId() + ">").collect(Collectors.joining("\n"));
    }

    private String getTimeDisplay(OffsetDateTime offsetDateTime) {
        return getFormattedTime(offsetDateTime, "f") + "\n(" + getFormattedTime(offsetDateTime, "R") + ")";
    }

    private String getFormattedTime(OffsetDateTime offsetDateTime, String format) {
        return "<t:" + offsetDateTime.toEpochSecond()+ ":" + format + ">";
    }
}
