package it.multicoredev.nbtr.utils;

import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Utility class containing extension methods through the Lombok's @ExtensionMethod annotation.
public final class Extensions {

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
