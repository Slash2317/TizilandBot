package com.slash.tizilandbot.handler;

import com.slash.tizilandbot.request.RequestContext;

public class TizilandRequestHandler {

    public void handleTizilandCommand(RequestContext requestContext) {
        requestContext.event().getChannel().sendMessage("""
                    **Here are our current invite links!** :link:
                    
                    :link: Discord Invite Link: <https://discord.gg/R9Cf8PRQvS>
                    :paperclips: Bit.ly Invite Link: <https://bit.ly/tiziland_ok>""").queue();
    }

    public void handleRulesCommand(RequestContext requestContext) {
        requestContext.event().getChannel().sendMessage(":scroll: You can read our rules here: https://discord.com/channels/1108179404137447484/1108181346033094736").queue();
    }

    public void handleTizipagesCommand(RequestContext requestContext) {
        requestContext.event().getChannel().sendMessage("""
                    Here are all the pages of Tizi:
                    <https://bit.ly/tiziabout>
                    <https://bit.ly/tizisocial>
                    <https://bit.ly/tizi-links>""").queue();
    }
    public void handleStaffCommand(RequestContext requestContext) {
        requestContext.event().getChannel().sendMessage("""
                    **Here's a list of our current staff!**
                    
                    :crown: OWNER : Tizi!! `(tiziandfrodo)`
                    :tools: COMMUNITY MANAGER : Xanth `(._.xanth._.)`
                    :star2: ADMINISTRATOR(S) : Astral `(astral.null)`""").queue();
    }
}
