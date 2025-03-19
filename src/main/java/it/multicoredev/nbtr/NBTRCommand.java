package it.multicoredev.nbtr;

import it.multicoredev.mbcore.spigot.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * BSD 3-Clause License
 * <p>
 * Copyright (c) 2023, Lorenzo Magni
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p>
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * <p>
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
@RequiredArgsConstructor(access = AccessLevel.PUBLIC, onConstructor_ = @Internal)
public final class NBTRCommand {

    private final NBTRecipes plugin;

    @Command("nbtr reload")
    @CommandPermission("nbtr.command")
    public void onReload(final @NotNull CommandSender sender) {
        // Reloading the plugin.
        plugin.onReload();
        // Sending message to the sender.
        Text.get().send(Text.toMiniMessage(plugin.config().reloaded), sender);
    }

    @Command("nbtr list_recipes")
    @CommandPermission("nbtr.command")
    public void onListRecipes(final @NotNull CommandSender sender) {
        // Sending header message to the sender.
        Text.get().send(Text.toMiniMessage(plugin.config().recipesListHeader), sender);
        // Preparing AtomicInteger counter which is used to count list entries.
        final AtomicInteger number = new AtomicInteger(0);
        // Iterating over the recipes list and sending keys to the sender.
        plugin.getRecipes().forEach(recipe -> {
            // Sending the list entry to the sender.
            Text.get().send(Text.toMiniMessage(plugin.config().recipesListEntry).replace("{number}", number.incrementAndGet() + "").replace("{recipe}", recipe.getKey().asString()), sender);
        });
        // Sending footer message to the sender.
        Text.get().send(Text.toMiniMessage(plugin.config().recipesListFooter), sender);
    }

    @Command("nbtr list_items")
    @CommandPermission("nbtr.command")
    public void onListItems(final @NotNull CommandSender sender) {
        // Sending header message to the sender.
        Text.get().send(Text.toMiniMessage(plugin.config().customItemsListHeader), sender);
        // Preparing AtomicInteger counter which is used to count list entries.
        final AtomicInteger number = new AtomicInteger(0);
        // Iterating over the custom items registry and sending item identifiers to the sender.
        plugin.getCustomItemRegistry().all().keySet().forEach(identifier -> {
            // Sending the list entry to the sender.
            Text.get().send(Text.toMiniMessage(plugin.config().customItemsListEntry).replace("{number}", number.incrementAndGet() + "").replace("{item}", identifier), sender);
        });
        // Sending footer message to the sender.
        Text.get().send(Text.toMiniMessage(plugin.config().customItemsListFooter), sender);
    }

    @Command("nbtr register_item")
    @CommandPermission("nbtr.command")
    public void onRegisterItem(final @NotNull Player sender, final String identifier) {
        final ItemStack item = sender.getInventory().getItemInMainHand();
        // Checking whether the item exists and is not empty, and registering it.
        if (item.isEmpty() == false)
            plugin.getCustomItemRegistry().set(identifier, item);
    }

    @Command("nbtr give_item")
    @CommandPermission("nbtr.command")
    public void onGetItem(final @NotNull Player sender, final Player target, final String identifier) {
        final @Nullable ItemStack item = plugin.getCustomItemRegistry().get(identifier);
        // Checking whether the item exists and is not empty, and giving it to the target.
        if (item != null && item.isEmpty() == false)
            target.getInventory().addItem(item);
    }

}
