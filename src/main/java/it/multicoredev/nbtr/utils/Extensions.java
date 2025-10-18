/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2025, Grabsky (michal.czopek.foss@proton.me)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
