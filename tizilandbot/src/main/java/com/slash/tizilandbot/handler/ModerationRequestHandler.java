package com.slash.tizilandbot.handler;

import com.slash.tizilandbot.domain.GhostPingChannel;
import com.slash.tizilandbot.exception.InvalidPermissionException;
import com.slash.tizilandbot.repository.GhostPingChannelRepository;
import com.slash.tizilandbot.repository.GhostPingChannelRepositoryImpl;
import com.slash.tizilandbot.request.MessageRequestContext;
import com.slash.tizilandbot.request.RequestContext;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ModerationRequestHandler {

    private final GhostPingChannelRepository ghostPingChannelRepository;

    public ModerationRequestHandler(GhostPingChannelRepository ghostPingChannelRepository) {
        this.ghostPingChannelRepository = ghostPingChannelRepository;
    }

    public ModerationRequestHandler() {
        this(new GhostPingChannelRepositoryImpl());
    }

    public void handleMuteCommand(RequestContext requestContext) {
        try {
            if (requestContext.getMember() == null || !requestContext.getMember().hasPermission(Permission.MANAGE_ROLES)) {
                throw new InvalidPermissionException("You do not have permission to timeout other users");
            }

            String user = requestContext.getArgument("user", String.class);
            Integer duration = requestContext.getArgument("duration", Integer.class);

            if (user == null || duration == null) {
                throw new IllegalArgumentException("Invalid args");
            }

            Consumer<List<Member>> muteConsumer = members -> {
                if (members.isEmpty()) {
                    return;
                }

                for (Member member : members) {
                    requestContext.getGuild().timeoutFor(member, Duration.of(duration, ChronoUnit.SECONDS)).queue();
                }
            };

            List<Member> mentions = new ArrayList<>();
            if (requestContext instanceof MessageRequestContext messageRequestContext) {
                mentions = messageRequestContext.getMessage().getMentions().getMembers();
            }

            if (!mentions.isEmpty()) {
                muteConsumer.accept(mentions);
            }
            else {
                requestContext.getGuild().findMembers(m -> user.equals(m.getUser().getName())).onSuccess(muteConsumer);
            }
        }
        catch (InvalidPermissionException e) {
            requestContext.sendSimpleMessageEmbed(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            requestContext.sendSimpleMessageEmbed("The command must follow this format `" + requestContext.getCommand().getFullDescription(requestContext.getPrefix(), false) + "`");
        }
    }

    public void handleUnMuteCommand(RequestContext requestContext) {
        try {
            if (requestContext.getMember() == null || !requestContext.getMember().hasPermission(Permission.MANAGE_ROLES)) {
                throw new InvalidPermissionException("You do not have permission to remove the timeout for other users");
            }

            String user = requestContext.getArgument("user", String.class);
            if (user == null) {
                throw new IllegalArgumentException("Invalid args");
            }

            Consumer<List<Member>> unmuteConsumer = members -> {
                if (members.isEmpty()) {
                    return;
                }

                for (Member member : members) {
                    requestContext.getGuild().removeTimeout(member).queue();
                }
            };

            List<Member> mentions = new ArrayList<>();
            if (requestContext instanceof MessageRequestContext messageRequestContext) {
                mentions = messageRequestContext.getMessage().getMentions().getMembers();
            }

            if (!mentions.isEmpty()) {
                unmuteConsumer.accept(mentions);
            }
            else {
                requestContext.getGuild().findMembers(m -> user.equals(m.getUser().getName())).onSuccess(unmuteConsumer);
            }
        }
        catch (InvalidPermissionException e) {
            requestContext.sendSimpleMessageEmbed(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            requestContext.sendSimpleMessageEmbed("The command must follow this format `" + requestContext.getCommand().getFullDescription(requestContext.getPrefix(), false) + "`");
        }
    }

    public void handleKickCommand(RequestContext requestContext) {
        try {
            if (requestContext.getMember() == null || !requestContext.getMember().hasPermission(Permission.KICK_MEMBERS)) {
                throw new InvalidPermissionException("You do not have permission to kick other users");
            }

            String user = requestContext.getArgument("user", String.class);
            String reason = requestContext.getArgument("reason", String.class);
            if (user == null || reason == null) {
                throw new IllegalArgumentException("Invalid args");
            }

            Consumer<List<Member>> kickConsumer = members -> {
                if (members.isEmpty()) {
                    return;
                }

                for (Member member : members) {
                    requestContext.getGuild().kick(member).reason(reason).queue();
                }
            };

            List<Member> mentions = new ArrayList<>();
            if (requestContext instanceof MessageRequestContext messageRequestContext) {
                mentions = messageRequestContext.getMessage().getMentions().getMembers();
            }

            if (!mentions.isEmpty()) {
                kickConsumer.accept(mentions);
            }
            else {
                requestContext.getGuild().findMembers(m -> user.equals(m.getUser().getName())).onSuccess(kickConsumer);
            }
        }
        catch (InvalidPermissionException e) {
            requestContext.sendSimpleMessageEmbed(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            requestContext.sendSimpleMessageEmbed("The command must follow this format `" + requestContext.getCommand().getFullDescription(requestContext.getPrefix(), false) + "`");
        }
    }

    public void handleBanCommand(RequestContext requestContext) {
        try {
            if (requestContext.getMember() == null || !requestContext.getMember().hasPermission(Permission.BAN_MEMBERS)) {
                throw new InvalidPermissionException("You do not have permission to ban other users");
            }

            String user = requestContext.getArgument("user", String.class);
            String reason = requestContext.getArgument("reason", String.class);
            if (user == null || reason == null) {
                throw new IllegalArgumentException("Invalid args");
            }

            Consumer<List<Member>> banConsumer = members -> {
                if (members.isEmpty()) {
                    return;
                }

                for (Member member : members) {
                    requestContext.getGuild().ban(member, 0, TimeUnit.SECONDS).reason(reason).queue();
                }
            };

            List<Member> mentions = new ArrayList<>();
            if (requestContext instanceof MessageRequestContext messageRequestContext) {
                mentions = messageRequestContext.getMessage().getMentions().getMembers();
            }

            if (!mentions.isEmpty()) {
                banConsumer.accept(mentions);
            }
            else {
                requestContext.getGuild().findMembers(m -> user.equals(m.getUser().getName())).onSuccess(banConsumer);
            }
        }
        catch (InvalidPermissionException e) {
            requestContext.sendSimpleMessageEmbed(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            requestContext.sendSimpleMessageEmbed("The command must follow this format `" + requestContext.getCommand().getFullDescription(requestContext.getPrefix(), false) + "`");
        }
    }

    public void handleUnbanCommand(RequestContext requestContext) {
        try {
            if (requestContext.getMember() == null || !requestContext.getMember().hasPermission(Permission.BAN_MEMBERS)) {
                throw new InvalidPermissionException("You do not have permission to unban other users");
            }

            String username = requestContext.getArgument("user", String.class);
            if (username == null) {
                throw new IllegalArgumentException("Invalid args");
            }

            for (Guild.Ban ban : requestContext.getGuild().retrieveBanList()) {
                if (ban.getUser().getName().equals(username)) {
                    requestContext.getGuild().unban(ban.getUser()).queue();
                    break;
                }
            }
        }
        catch (InvalidPermissionException e) {
            requestContext.sendSimpleMessageEmbed(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            requestContext.sendSimpleMessageEmbed("The command must follow this format `" + requestContext.getCommand().getFullDescription(requestContext.getPrefix(), false) + "`");
        }
    }

    public void handleRoleGiveCommand(RequestContext requestContext) {
        try {
            if (requestContext.getMember() == null || !requestContext.getMember().hasPermission(Permission.MANAGE_ROLES)) {
                throw new InvalidPermissionException("You do not have permission to give roles to other users");
            }

            String user = requestContext.getArgument("user", String.class);
            String roleName = requestContext.getArgument("role", String.class);
            if (user == null) {
                throw new IllegalArgumentException("Invalid args");
            }

            List<Role> roles = new ArrayList<>();
            if (requestContext instanceof MessageRequestContext messageRequestContext) {
                roles.addAll(messageRequestContext.getMessage().getMentions().getRoles());
            }
            if (roles.isEmpty() && roleName != null) {
                roles.addAll(requestContext.getGuild().getRolesByName(roleName, true));
            }

            if (roles.isEmpty()) {
                return;
            }

            if (!requestContext.getMember().isOwner()) {
                if (requestContext.getMember().getRoles().isEmpty()) {
                    throw new InvalidPermissionException("You do not have permission to give roles to other users");
                }

                int maxRolePos = requestContext.getMember().getRoles().stream().mapToInt(Role::getPosition).max().getAsInt();

                if (maxRolePos <= roles.stream().mapToInt(Role::getPosition).max().getAsInt()) {
                    throw new InvalidPermissionException("You do not have a high enough role for this action");
                }
            }

            Consumer<List<Member>> roleGiveConsumer = members -> {
                if (members.isEmpty()) {
                    return;
                }

                for (Member member : members) {
                    for (Role role : roles) {
                        requestContext.getGuild().addRoleToMember(member, role).queue();
                    }
                }
            };

            List<Member> mentions = new ArrayList<>();
            if (requestContext instanceof MessageRequestContext messageRequestContext) {
                mentions = messageRequestContext.getMessage().getMentions().getMembers();
            }

            if (!mentions.isEmpty()) {
                roleGiveConsumer.accept(mentions);
            }
            else {
                requestContext.getGuild().findMembers(m -> user.equals(m.getUser().getName())).onSuccess(roleGiveConsumer);
            }
        }
        catch (InvalidPermissionException e) {
            requestContext.sendSimpleMessageEmbed(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            requestContext.sendSimpleMessageEmbed("The command must follow this format `" + requestContext.getCommand().getFullDescription(requestContext.getPrefix(), false) + "`");
        }
    }

    public void handleRoleRemoveCommand(RequestContext requestContext) {
        try {
            if (requestContext.getMember() == null || !requestContext.getMember().hasPermission(Permission.MANAGE_ROLES)) {
                throw new InvalidPermissionException("You do not have permission to remove roles from other users");
            }

            String user = requestContext.getArgument("user", String.class);
            String roleName = requestContext.getArgument("role", String.class);
            if (user == null) {
                throw new IllegalArgumentException("Invalid args");
            }

            List<Role> roles = new ArrayList<>();
            if (requestContext instanceof MessageRequestContext messageRequestContext) {
                roles.addAll(messageRequestContext.getMessage().getMentions().getRoles());
            }
            if (roles.isEmpty() && roleName != null) {
                roles.addAll(requestContext.getGuild().getRolesByName(roleName, true));
            }

            if (roles.isEmpty()) {
                return;
            }

            if (!requestContext.getMember().isOwner()) {
                if (requestContext.getMember().getRoles().isEmpty()) {
                    throw new InvalidPermissionException("You do not have permission to give roles to other users");
                }

                int maxRolePos = requestContext.getMember().getRoles().stream().mapToInt(Role::getPosition).max().getAsInt();

                if (maxRolePos <= roles.stream().mapToInt(Role::getPosition).max().getAsInt()) {
                    throw new InvalidPermissionException("You do not have a high enough role for this action");
                }
            }

            Consumer<List<Member>> roleRemoveConsumer = members -> {
                if (members.isEmpty()) {
                    return;
                }

                for (Member member : members) {
                    for (Role role : roles) {
                        requestContext.getGuild().removeRoleFromMember(member, role).queue();
                    }
                }
            };

            List<Member> mentions = new ArrayList<>();
            if (requestContext instanceof MessageRequestContext messageRequestContext) {
                mentions = messageRequestContext.getMessage().getMentions().getMembers();
            }

            if (!mentions.isEmpty()) {
                roleRemoveConsumer.accept(mentions);
            }
            else {
                requestContext.getGuild().findMembers(m -> user.equals(m.getUser().getName())).onSuccess(roleRemoveConsumer);
            }
        }
        catch (InvalidPermissionException e) {
            requestContext.sendSimpleMessageEmbed(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            requestContext.sendSimpleMessageEmbed("The command must follow this format `" + requestContext.getCommand().getFullDescription(requestContext.getPrefix(), false) + "`");
        }
    }

    public void handlePingInCommand(RequestContext requestContext) {
        try {
            if (requestContext.getMember() == null || !canManageChannels(requestContext)) {
                throw new InvalidPermissionException("You do not have permission to add ghost ping channels");
            }

            String channelArg = requestContext.getArgument("channel", String.class);

            List<GuildChannel> channels = new ArrayList<>();
            if (requestContext instanceof MessageRequestContext messageRequestContext) {
                channels.addAll(messageRequestContext.getMessage().getMentions().getChannels());
            }
            if (channels.isEmpty() && channelArg != null) {
                channels.add(requestContext.getGuild().getGuildChannelById(channelArg));
            }

            if (channels.isEmpty()) {
                throw new IllegalArgumentException("Invalid args");
            }

            Long guildDiscordId = requestContext.getGuild().getIdLong();
            List<GhostPingChannel> ghostPingChannels = ghostPingChannelRepository.findAll(guildDiscordId);
            for (GuildChannel channel : channels) {
                if (ghostPingChannels.stream().anyMatch(c -> c.getChannelDiscordId().equals(channel.getIdLong()) && c.getGuildDiscordId().equals(guildDiscordId))) {
                    continue;
                }

                GhostPingChannel ghostPingChannel = new GhostPingChannel();
                ghostPingChannel.setGuildDiscordId(guildDiscordId);
                ghostPingChannel.setChannelDiscordId(channel.getIdLong());
                ghostPingChannels.add(ghostPingChannel);
            }
            ghostPingChannelRepository.save(guildDiscordId, ghostPingChannels);

            String channelNames = getChannelNamesDisplay(requestContext.getGuild(), ghostPingChannels);
            requestContext.sendSimpleMessageEmbed("Channel(s) added. The current ghost ping channels are:\n" + channelNames);
        }
        catch (InvalidPermissionException e) {
            requestContext.sendSimpleMessageEmbed(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            requestContext.sendSimpleMessageEmbed("The command must follow this format `" + requestContext.getCommand().getFullDescription(requestContext.getPrefix(), false) + "`");
        }
    }

    public void handleRemovePingInCommand(RequestContext requestContext) {
        try {
            if (requestContext.getMember() == null || !canManageChannels(requestContext)) {
                throw new InvalidPermissionException("You do not have permission to remove ghost ping channels");
            }

            String channelArg = requestContext.getArgument("channel", String.class);

            List<GuildChannel> channels = new ArrayList<>();
            if (requestContext instanceof MessageRequestContext messageRequestContext) {
                channels.addAll(messageRequestContext.getMessage().getMentions().getChannels());
            }
            if (channels.isEmpty() && channelArg != null) {
                channels.add(requestContext.getGuild().getGuildChannelById(channelArg));
            }

            if (channels.isEmpty()) {
                throw new IllegalArgumentException("Invalid args");
            }

            Long guildDiscordId = requestContext.getGuild().getIdLong();
            List<Long> channelDiscordIds = channels.stream().map(ISnowflake::getIdLong).toList();

            List<GhostPingChannel> ghostPingChannels = ghostPingChannelRepository.findAll(guildDiscordId);
            if (ghostPingChannels.isEmpty()) {
                requestContext.sendSimpleMessageEmbed("There are currently no ghost ping channels.");
                return;
            }

            ghostPingChannels.removeIf(c -> channelDiscordIds.contains(c.getChannelDiscordId()));
            ghostPingChannelRepository.save(guildDiscordId, ghostPingChannels);

            if (ghostPingChannels.isEmpty()) {
                requestContext.sendSimpleMessageEmbed("Channel(s) removed. There are currently no ghost ping channels.");
            }
            else {
                String channelNames = getChannelNamesDisplay(requestContext.getGuild(), ghostPingChannels);
                requestContext.sendSimpleMessageEmbed("Channel(s) removed. The current ghost ping channels are:\n" + channelNames);
            }
        }
        catch (InvalidPermissionException e) {
            requestContext.sendSimpleMessageEmbed(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            requestContext.sendSimpleMessageEmbed("The command must follow this format `" + requestContext.getCommand().getFullDescription(requestContext.getPrefix(), false) + "`");
        }
    }

    public void handleViewPingInCommand(RequestContext requestContext) {
        try {
            if (requestContext.getMember() == null || !canManageChannels(requestContext)) {
                throw new InvalidPermissionException("You do not have permission to view ghost ping channels");
            }
            Long guildDiscordId = requestContext.getGuild().getIdLong();
            List<GhostPingChannel> ghostPingChannels = ghostPingChannelRepository.findAll(guildDiscordId);

            if (ghostPingChannels.isEmpty()) {
                requestContext.sendSimpleMessageEmbed("There are currently no ghost ping channels.");
                return;
            }
            String channelNames = getChannelNamesDisplay(requestContext.getGuild(), ghostPingChannels);
            requestContext.sendSimpleMessageEmbed("The current ghost ping channels are:\n" + channelNames);
        }
        catch (InvalidPermissionException e) {
            requestContext.sendSimpleMessageEmbed(e.getMessage());
        }
        catch (IllegalArgumentException e) {
            requestContext.sendSimpleMessageEmbed("The command must follow this format `" + requestContext.getCommand().getFullDescription(requestContext.getPrefix(), false) + "`");
        }
    }

    private boolean canManageChannels(RequestContext requestContext) {
        Member member = requestContext.getMember();
        if (member == null) {
            return false;
        }
        if (member.isOwner()) {
            return true;
        }
        List<Role> roles = member.getRoles();
        for (Role role : roles) {
            if (role.hasPermission(Permission.MANAGE_CHANNEL) || role.hasPermission(Permission.ADMINISTRATOR)) {
                return true;
            }
        }
        return false;
    }

    private String getChannelNamesDisplay(Guild guild, List<GhostPingChannel> ghostPingChannels) {
        List<Long> channelDiscordIds = ghostPingChannels.stream().map(GhostPingChannel::getChannelDiscordId).toList();

        LinkedHashMap<Long, GuildChannel> idToChannel = getChannels(channelDiscordIds, guild);
        return idToChannel.entrySet().stream().map(e -> "[" + e.getKey() + ", " + e.getValue().getName() + "]").collect(Collectors.joining("\n"));
    }

    private LinkedHashMap<Long, GuildChannel> getChannels(List<Long> channelIds, Guild guild) {
        LinkedHashMap<Long, GuildChannel> idToChannel = new LinkedHashMap<>();
        for (Long id : channelIds) {
            idToChannel.put(id, guild.getGuildChannelById(id));
        }
        return idToChannel;
    }
}
