package com.slash.tizilandbot.handler;

import com.slash.tizilandbot.exception.InvalidPermissionException;
import com.slash.tizilandbot.request.RequestContext;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ModerationRequestHandler {

    public void handleMuteCommand(RequestContext requestContext) {
        try {
            if (requestContext.arguments() == null) {
                throw new IllegalArgumentException("No arguments supplied");
            }

            List<String> arguments = getArguments(requestContext.arguments(), " ", 2);
            if (arguments.size() < 2) {
                throw new IllegalArgumentException("Only 1 argument supplied");
            }

            if (requestContext.event().getMember() == null || !requestContext.event().getMember().hasPermission(Permission.MANAGE_ROLES)) {
                throw new InvalidPermissionException("You do not have permission to timeout other users");
            }

            String user = arguments.get(0);
            int duration = Integer.parseInt(arguments.get(1).trim());

            Consumer<List<Member>> muteConsumer = members -> {
                if (members.isEmpty()) {
                    return;
                }

                for (Member member : members) {
                    requestContext.event().getGuild().timeoutFor(member, Duration.of(duration, ChronoUnit.SECONDS)).queue();
                }
            };

            if (!requestContext.event().getMessage().getMentions().getMembers().isEmpty()) {
                muteConsumer.accept(requestContext.event().getMessage().getMentions().getMembers());
            }
            else {
                requestContext.event().getGuild().findMembers(m -> user.equals(m.getUser().getName())).onSuccess(muteConsumer);
            }
        }
        catch (InvalidPermissionException e) {
            requestContext.event().getChannel().sendMessage(e.getMessage()).queue();
        }
        catch (IllegalArgumentException e) {
            requestContext.event().getChannel().sendMessage("The command must follow this format `" + requestContext.command().getFullDescription() + "`").queue();
        }
    }

    public void handleUnMuteCommand(RequestContext requestContext) {
        try {
            if (requestContext.arguments() == null) {
                throw new IllegalArgumentException("No arguments supplied");
            }

            if (requestContext.event().getMember() == null || !requestContext.event().getMember().hasPermission(Permission.MANAGE_ROLES)) {
                throw new InvalidPermissionException("You do not have permission to remove the timeout for other users");
            }

            String user = requestContext.arguments();

            Consumer<List<Member>> unmuteConsumer = members -> {
                if (members.isEmpty()) {
                    return;
                }

                for (Member member : members) {
                    requestContext.event().getGuild().removeTimeout(member).queue();
                }
            };

            if (!requestContext.event().getMessage().getMentions().getMembers().isEmpty()) {
                unmuteConsumer.accept(requestContext.event().getMessage().getMentions().getMembers());
            }
            else {
                requestContext.event().getGuild().findMembers(m -> user.equals(m.getUser().getName())).onSuccess(unmuteConsumer);
            }
        }
        catch (InvalidPermissionException e) {
            requestContext.event().getChannel().sendMessage(e.getMessage()).queue();
        }
        catch (IllegalArgumentException e) {
            requestContext.event().getChannel().sendMessage("The command must follow this format `" + requestContext.command().getFullDescription() + "`").queue();
        }
    }

    public void handleKickCommand(RequestContext requestContext) {
        try {
            if (requestContext.arguments() == null) {
                throw new IllegalArgumentException("No arguments supplied");
            }

            List<String> arguments = getArguments(requestContext.arguments(), " ", 2);
            if (arguments.size() < 2) {
                throw new IllegalArgumentException("Only 1 argument supplied");
            }

            if (requestContext.event().getMember() == null || !requestContext.event().getMember().hasPermission(Permission.KICK_MEMBERS)) {
                throw new InvalidPermissionException("You do not have permission to kick other users");
            }

            String user = arguments.get(0);
            String reason = arguments.get(1).trim();

            Consumer<List<Member>> kickConsumer = members -> {
                if (members.isEmpty()) {
                    return;
                }

                for (Member member : members) {
                    requestContext.event().getGuild().kick(member).reason(reason).queue();
                }
            };

            if (!requestContext.event().getMessage().getMentions().getMembers().isEmpty()) {
                kickConsumer.accept(requestContext.event().getMessage().getMentions().getMembers());
            }
            else {
                requestContext.event().getGuild().findMembers(m -> user.equals(m.getUser().getName())).onSuccess(kickConsumer);
            }
        }
        catch (InvalidPermissionException e) {
            requestContext.event().getChannel().sendMessage(e.getMessage()).queue();
        }
        catch (IllegalArgumentException e) {
            requestContext.event().getChannel().sendMessage("The command must follow this format `" + requestContext.command().getFullDescription() + "`").queue();
        }
    }

    public void handleBanCommand(RequestContext requestContext) {
        try {
            if (requestContext.arguments() == null) {
                throw new IllegalArgumentException("No arguments supplied");
            }

            List<String> arguments = getArguments(requestContext.arguments(), " ", 2);
            if (arguments.size() < 2) {
                throw new IllegalArgumentException("Only 1 argument supplied");
            }

            if (requestContext.event().getMember() == null || !requestContext.event().getMember().hasPermission(Permission.BAN_MEMBERS)) {
                throw new InvalidPermissionException("You do not have permission to ban other users");
            }

            String user = arguments.get(0);
            String reason = arguments.get(1).trim();

            Consumer<List<Member>> banConsumer = members -> {
                if (members.isEmpty()) {
                    return;
                }

                for (Member member : members) {
                    requestContext.event().getGuild().ban(member, 0, TimeUnit.SECONDS).reason(reason).queue();
                }
            };

            if (!requestContext.event().getMessage().getMentions().getMembers().isEmpty()) {
                banConsumer.accept(requestContext.event().getMessage().getMentions().getMembers());
            }
            else {
                requestContext.event().getGuild().findMembers(m -> user.equals(m.getUser().getName())).onSuccess(banConsumer);
            }
        }
        catch (InvalidPermissionException e) {
            requestContext.event().getChannel().sendMessage(e.getMessage()).queue();
        }
        catch (IllegalArgumentException e) {
            requestContext.event().getChannel().sendMessage("The command must follow this format `" + requestContext.command().getFullDescription() + "`").queue();
        }
    }

    public void handleUnbanCommand(RequestContext requestContext) {
        try {
            if (requestContext.arguments() == null) {
                throw new IllegalArgumentException("No arguments supplied");
            }

            if (requestContext.event().getMember() == null || !requestContext.event().getMember().hasPermission(Permission.BAN_MEMBERS)) {
                throw new InvalidPermissionException("You do not have permission to unban other users");
            }

            String username = requestContext.arguments();

            for (Guild.Ban ban : requestContext.event().getGuild().retrieveBanList()) {
                if (ban.getUser().getName().equals(username)) {
                    requestContext.event().getGuild().unban(ban.getUser()).queue();
                    break;
                }
            }
        }
        catch (InvalidPermissionException e) {
            requestContext.event().getChannel().sendMessage(e.getMessage()).queue();
        }
        catch (IllegalArgumentException e) {
            requestContext.event().getChannel().sendMessage("The command must follow this format `" + requestContext.command().getFullDescription() + "`").queue();
        }
    }

    public void handleRoleGiveCommand(RequestContext requestContext) {
        try {
            if (requestContext.arguments() == null) {
                throw new IllegalArgumentException("No arguments supplied");
            }

            List<String> arguments = getArguments(requestContext.arguments(), " ", 2);
            if (arguments.size() < 2) {
                throw new IllegalArgumentException("Only 1 argument supplied");
            }

            if (requestContext.event().getMember() == null || !requestContext.event().getMember().hasPermission(Permission.MANAGE_ROLES)) {
                throw new InvalidPermissionException("You do not have permission to give roles to other users");
            }

            String user = arguments.get(0);
            String roleName = arguments.get(1).trim();

            List<Role> roles = requestContext.event().getGuild().getRolesByName(roleName, true);

            if (roles.isEmpty()) {
                return;
            }

            if (!requestContext.event().getMember().isOwner()) {
                if (requestContext.event().getMember().getRoles().isEmpty()) {
                    throw new InvalidPermissionException("You do not have permission to give roles to other users");
                }

                int maxRolePos = requestContext.event().getMember().getRoles().stream().mapToInt(Role::getPosition).max().getAsInt();

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
                        requestContext.event().getGuild().addRoleToMember(member, role).queue();
                    }
                }
            };

            if (!requestContext.event().getMessage().getMentions().getMembers().isEmpty()) {
                roleGiveConsumer.accept(requestContext.event().getMessage().getMentions().getMembers());
            }
            else {
                requestContext.event().getGuild().findMembers(m -> user.equals(m.getUser().getName())).onSuccess(roleGiveConsumer);
            }
        }
        catch (InvalidPermissionException e) {
            requestContext.event().getChannel().sendMessage(e.getMessage()).queue();
        }
        catch (IllegalArgumentException e) {
            requestContext.event().getChannel().sendMessage("The command must follow this format `" + requestContext.command().getFullDescription() + "`").queue();
        }
    }

    public void handleRoleRemoveCommand(RequestContext requestContext) {
        try {
            if (requestContext.arguments() == null) {
                throw new IllegalArgumentException("No arguments supplied");
            }

            List<String> arguments = getArguments(requestContext.arguments(), " ", 2);
            if (arguments.size() < 2) {
                throw new IllegalArgumentException("Only 1 argument supplied");
            }

            if (requestContext.event().getMember() == null || !requestContext.event().getMember().hasPermission(Permission.MANAGE_ROLES)) {
                throw new InvalidPermissionException("You do not have permission to remove roles other users");
            }

            String user = arguments.get(0);
            String roleName = arguments.get(1).trim();

            List<Role> roles = requestContext.event().getGuild().getRolesByName(roleName, true);

            if (roles.isEmpty()) {
                return;
            }

            if (!requestContext.event().getMember().isOwner()) {
                if (requestContext.event().getMember().getRoles().isEmpty()) {
                    throw new InvalidPermissionException("You do not have permission to give roles to other users");
                }

                int maxRolePos = requestContext.event().getMember().getRoles().stream().mapToInt(Role::getPosition).max().getAsInt();

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
                        requestContext.event().getGuild().removeRoleFromMember(member, role).queue();
                    }
                }
            };

            if (!requestContext.event().getMessage().getMentions().getMembers().isEmpty()) {
                roleRemoveConsumer.accept(requestContext.event().getMessage().getMentions().getMembers());
            }
            else {
                requestContext.event().getGuild().findMembers(m -> user.equals(m.getUser().getName())).onSuccess(roleRemoveConsumer);
            }
        }
        catch (InvalidPermissionException e) {
            requestContext.event().getChannel().sendMessage(e.getMessage()).queue();
        }
        catch (IllegalArgumentException e) {
            requestContext.event().getChannel().sendMessage("The command must follow this format `" + requestContext.command().getFullDescription() + "`").queue();
        }
    }

    private List<String> getArguments(String argumentsString, String delimiter, int numOfArguments) {
        if (argumentsString == null || argumentsString.isEmpty()) {
            return Collections.emptyList();
        }

        if (numOfArguments == 1) {
            return List.of(argumentsString);
        }
        List<String> splitArguments = List.of(argumentsString.split(delimiter));

        if (splitArguments.size() <= numOfArguments) {
            return splitArguments;
        }

        List<String> arguments = new ArrayList<>(splitArguments.subList(0, numOfArguments - 1));
        arguments.add(String.join(delimiter, splitArguments.subList(numOfArguments - 1, splitArguments.size())));
        return arguments;
    }
}
