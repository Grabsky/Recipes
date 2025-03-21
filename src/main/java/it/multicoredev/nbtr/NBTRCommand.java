package it.multicoredev.nbtr;

import it.multicoredev.nbtr.registry.CustomItemRegistry;
import it.multicoredev.nbtr.utils.Extensions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;

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
@SuppressWarnings("UnstableApiUsage")
@ExtensionMethod(Extensions.class)
@RequiredArgsConstructor(access = AccessLevel.PUBLIC, onConstructor_ = @Internal)
public final class NBTRCommand {

    private final NBTRecipes plugin;

    @Command("nbtr reload")
    @CommandPermission("nbtr.command.reload")
    public String onReload(final @NotNull CommandSender sender) {
        // Reloading the plugin.
        plugin.onReload();
        // Sending message to the sender.
        return plugin.config().reloaded;
    }

    @Command("nbtr list_recipes")
    @CommandPermission("nbtr.command.list_recipes")
    public String onListRecipes(final @NotNull CommandSender sender) {
        // Sending message if the recipes list is empty.
        if (plugin.getRecipes().isEmpty() == true)
            return plugin.config().recipesListFailureEmpty;
        // Sending header message to the sender.
        sender.sendTextMessage(plugin.config().recipesListSuccessHeader);
        // Preparing AtomicInteger counter which is used to count list entries.
        final AtomicInteger number = new AtomicInteger(0);
        // Iterating over the recipes list and sending keys to the sender.
        plugin.getRecipes().forEach(recipe -> {
            // Sending the list entry to the sender.
            sender.sendTextMessage(plugin.config().recipesListSuccessEntry.replace("{number}", number.incrementAndGet() + "").replace("{recipe}", recipe.getKey().asString()));
        });
        // Sending footer message to the sender.
        return plugin.config().recipesListSuccessFooter;
    }

    @Command("nbtr list_items")
    @CommandPermission("nbtr.command.list_items")
    public String onListItems(final @NotNull CommandSender sender) {
        // Sending message if the items list is empty.
        if (plugin.getRecipes().isEmpty() == true)
            return plugin.config().customItemsFailureEmpty;
        // Sending header message to the sender.
        sender.sendTextMessage(plugin.config().customItemsSuccessListHeader);
        // Preparing AtomicInteger counter which is used to count list entries.
        final AtomicInteger number = new AtomicInteger(0);
        // Iterating over the custom items registry and sending item identifiers to the sender.
        plugin.getCustomItemRegistry().all().keySet().forEach(identifier -> {
            // Sending the list entry to the sender.
            sender.sendTextMessage(plugin.config().customItemsSuccessListEntry.replace("{number}", number.incrementAndGet() + "").replace("{identifier}", identifier));
        });
        // Sending footer message to the sender.
        return plugin.config().customItemsSuccessListFooter;
    }

    @Command("nbtr register_item")
    @CommandPermission("nbtr.command.register_item")
    public String onRegisterItem(final @NotNull Player sender, final String identifier) {
        final ItemStack item = sender.getInventory().getItemInMainHand();
        // Checking whether the item exists and is not empty, and registering it.
        if (item.isEmpty() == false) {
            // Checking whether the item is already registered.
            final boolean isOverridden = plugin.getCustomItemRegistry().get(identifier) != null;
            // Registering the item and sending message when successful.
            if (plugin.getCustomItemRegistry().set(identifier, item) == true)
                return (isOverridden == true ? plugin.config().customItemsRegisterSuccessOverridden : plugin.config().customItemsRegisterSuccess).replace("{identifier}", identifier);
            // Sending error message to the sender.
            return plugin.config().customItemsRegisterFailureInvalidIdentifier.replace("{identifier}", identifier);
        }
        // Sending error message to the sender.
        return plugin.config().customItemsRegisterFailureInvalidItem.replace("{identifier}", identifier);
    }

    @Command("nbtr unregister_item")
    @CommandPermission("nbtr.command.unregister_item")
    public String onUnregisterItem(final @NotNull CommandSender sender, final @SuggestWith(CustomItemRegistry.Suggestions.class) String identifier) {
        // Registering the item and sending message when successful.
        if (plugin.getCustomItemRegistry().remove(identifier) == true)
            return plugin.config().customItemsUnregisterSuccess.replace("{identifier}", identifier);
        // Sending error message to the sender.
        return plugin.config().customItemsUnregisterFailureInvalidIdentifier.replace("{identifier}", identifier);
    }

    @Command("nbtr give_item")
    @CommandPermission("nbtr.command.give_item")
    public String onGiveItem(final @NotNull CommandSender sender, final Player target, final @SuggestWith(CustomItemRegistry.Suggestions.class) String identifier, final @Optional Integer amount) {
        final @Nullable ItemStack item = plugin.getCustomItemRegistry().get(identifier);
        // Checking whether the item exists and is not empty, and giving it to the target.
        if (item != null && item.isEmpty() == false) {
            // Setting the amount if specified.
            if (amount != null)
                item.setAmount(Math.min(amount, item.getMaxStackSize()));
            // Adding the item to the target's inventory.
            final HashMap<Integer, ItemStack> remainingItems = target.getInventory().addItem(item);
            // Dropping remaining items on the ground.
            if (remainingItems.isEmpty() == false)
                remainingItems.values().forEach(remainingItem -> target.getWorld().dropItem(target.getLocation(), remainingItem));
            // Sending message to the sender.
            return plugin.config().customItemsGiveSuccess.replace("{target}", target.getName()).replace("{amount}", item.getAmount() + "").replace("{identifier}", identifier);
        }
        // Sending error message to the sender.
        return plugin.config().customItemsGiveFailure.replace("{identifier}", identifier);
    }

}
