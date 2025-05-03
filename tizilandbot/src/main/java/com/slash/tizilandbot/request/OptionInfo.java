package com.slash.tizilandbot.request;

import net.dv8tion.jda.api.interactions.commands.OptionType;

public record OptionInfo(String name, String description, OptionType optionType) {

}
