package com.slash.tizilandbot.request;

public enum CommandGroup {

    MODERATION("moderation_and_utility", "MODERATION & UTILITY", "\uD83D\uDEE0\uFE0F", "tools"),
    SERVER("server_and_members", "SERVER & MEMBERS", "\uD83E\uDDD1", "adult"),
    OFF_TOPIC("off_topic", "OFF TOPIC", "\uD83D\uDCA5", "boom"),
    FUN("fun", "FUN", "\uD83D\uDE04", "smile"),
    MISC("misc", "MISC", "❓", "question");

    private final String identifier;
    private final String title;
    private final String emojiUnicode;
    private final String emojiName;

    CommandGroup(String identifier, String title, String emojiUnicode, String emojiName) {
        this.identifier = identifier;
        this.title = title;
        this.emojiUnicode = emojiUnicode;
        this.emojiName = emojiName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getTitle() {
        return title;
    }

    public String getEmojiUnicode() {
        return emojiUnicode;
    }

    public String getEmojiName() {
        return emojiName;
    }
}
