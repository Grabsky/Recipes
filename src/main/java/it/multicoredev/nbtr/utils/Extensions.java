package it.multicoredev.nbtr.utils;

import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Utility class containing extension methods through the Lombok's @ExtensionMethod annotation.
public final class Extensions {

    public static String repl(final @NotNull String self, @NotNull Object... replacements) {
        // Returning the original string if no replacements were specified.
        if (replacements == null)
            return self;
        // Throwing exception when
        if (replacements.length % 2 != 0)
            throw new IllegalArgumentException("Invalid arguments. Replacements must be in key-value pairs.");
        // Preparing the result string.
        String result = self;
        // Iterating over specified replacements key-value pairs and replacing them in the string.
        for (int index = 0; index < replacements.length; index += 2)
            result = result.replace(String.valueOf(replacements[index]), String.valueOf(replacements[index + 1]));
        // Returning the result.
        return result;
    }

    /**
     * Sends message to the sender using the MiniMessage formatting or does nothing if the message is null or empty.
     */
    public static void sendTextMessage(final @NotNull CommandSender sender, final @Nullable String message) {
        // Returning in case message is null or empty.
        if (message == null || message.isEmpty() == true)
            return;
        // Sending message to the target.
        sender.sendRichMessage(message);
    }

}
