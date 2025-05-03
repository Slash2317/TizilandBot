package com.slash.tizilandbot.handler;

import com.slash.tizilandbot.request.RequestContext;

public class TizilandRequestHandler {

    public void handleInviteCommand(RequestContext requestContext) {
        requestContext.sendMessage("""
                    <:realtizi_info:1187109742947074068> To invite someone, you can only use the following links:
                    <:realtizi_link:1187466090096361563> Discord Invite Link: <https://discord.gg/R9Cf8PRQvS>
                    <:realtizi_link:1187466090096361563> Short Invite Link: <http://bit.ly/realtizidiscord> | <https://tinyurl.com/realtizidiscord> | <https://t.ly/A8qjH>""");
    }

    public void handleRulesCommand(RequestContext requestContext) {
        requestContext.sendMessage(":scroll: You can read our rules here: https://discord.com/channels/1108179404137447484/1108181346033094736");
    }

    public void handleTizipagesCommand(RequestContext requestContext) {
        requestContext.sendMessage("""
                    Here are all the pages of Tizi:
                    <https://bit.ly/tiziabout>
                    <https://bit.ly/tizisocial>
                    <https://bit.ly/tizi-links>""");
    }
    public void handleStaffCommand(RequestContext requestContext) {
        requestContext.sendMessage("""
                    **Here's a list of our current staff!**
                    
                    :crown: OWNER : Tizi!! `(tiziandfrodo)`
                    :tools: COMMUNITY MANAGER : Xanth `(._.xanth._.)`
                    :star2: ADMINISTRATOR(S) : Astral `(astral.null)`""");
    }
}
