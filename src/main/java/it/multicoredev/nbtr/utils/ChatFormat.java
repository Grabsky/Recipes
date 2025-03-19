package it.multicoredev.nbtr.utils;


import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;

import org.jetbrains.annotations.NotNull;

public final class ChatFormat {

    /**
     * Checks if the given text contains MiniMessage tags.
     *
     * @param text The text to be checked.
     * @return {@code true} if the text contains MiniMessage tags, {@code false} otherwise.
     */
    public static boolean containsMiniMessage(final @NotNull String text) {
        return !MiniMessage.miniMessage().stripTags(text).equals(text);
    }

    /**
     * Checks if the given list contains any strings that contain MiniMessage tags.
     *
     * @param list The list of strings to be checked.
     * @return {@code true} if any string in the list contains MiniMessage tags, {@code false} otherwise.
     */
    public static boolean containsMiniMessage(final @NotNull List<String> list) {
        return list.stream().anyMatch(ChatFormat::containsMiniMessage);
    }

}
