package it.multicoredev.nbtr.command;

import it.multicoredev.nbtr.NBTRecipes;
import it.multicoredev.nbtr.registry.CustomItemRegistry;
import it.multicoredev.nbtr.utils.Extensions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Dependency;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
@ExtensionMethod(Extensions.class)
public enum RecipesCommand {
    INSTANCE; // SINGLETON

    @Dependency
    private NBTRecipes plugin;

    @Command("recipes reload")
    @CommandPermission("recipes.command.reload")
    public String onReload(final @NotNull CommandSender sender) {
        // Reloading the plugin.
        plugin.onReload();
        // Sending message to the sender.
        return plugin.configuration().messages().commandRecipesReloadSuccess();
    }

    @Command("recipes list_recipes")
    @CommandPermission("recipes.command.list_recipes")
    public String onListRecipes(final @NotNull CommandSender sender) {
        // Sending message if the recipes list is empty.
        if (plugin.recipes().isEmpty() == true)
            return plugin.configuration().messages().commandRecipesRecipesListFailureEmpty();
        // Sending header message to the sender.
        sender.sendTextMessage(plugin.configuration().messages().commandRecipesRecipesListHeader());
        // Preparing AtomicInteger counter which is used to count list entries.
        final AtomicInteger number = new AtomicInteger(0);
        // Iterating over the recipes list and sending keys to the sender.
        plugin.registeredRecipes().forEach(recipe -> {
            sender.sendTextMessage(plugin.configuration().messages().commandRecipesRecipesListEntry().repl("{number}", number.incrementAndGet(), "{recipe}", recipe.getKey()));
        });
        // Sending footer message to the sender.
        return plugin.configuration().messages().commandRecipesRecipesListFooter();
    }

    @Command("recipes list_items")
    @CommandPermission("recipes.command.list_items")
    public String onListItems(final @NotNull CommandSender sender) {
        // Sending message if the items list is empty.
        if (plugin.recipes().isEmpty() == true)
            return plugin.configuration().messages().commandRecipesItemsListFailureEmpty();
        // Sending header message to the sender.
        sender.sendTextMessage(plugin.configuration().messages().commandRecipesItemsListHeader());
        // Preparing AtomicInteger counter which is used to count list entries.
        final AtomicInteger number = new AtomicInteger(0);
        // Iterating over the custom items registry and sending item identifiers to the sender.
        plugin.customItemRegistry().all().keySet().forEach(identifier -> {
            // Sending the list entry to the sender.
            sender.sendTextMessage(plugin.configuration().messages().commandRecipesItemsListEntry().repl("{number}", number.incrementAndGet(), "{identifier}", identifier));
        });
        // Sending footer message to the sender.
        return plugin.configuration().messages().commandRecipesItemsListFooter();
    }

    @Command("recipes register_item")
    @CommandPermission("recipes.command.register_item")
    public String onRegisterItem(
            final @NotNull Player sender,
            final @NotNull String identifier
    ) {
        final ItemStack item = sender.getInventory().getItemInMainHand();
        // Checking whether the item exists and is not empty, and registering it.
        if (item.isEmpty() == false) {
            // Checking whether the item is already registered.
            final boolean isOverridden = plugin.customItemRegistry().get(identifier) != null;
            // Registering the item and sending message when successful.
            if (plugin.customItemRegistry().set(identifier, item) == true)
                return (isOverridden == true)
                        ? plugin.configuration().messages().commandRecipesRegisterItemSuccessOverridden().repl("{identifier}", identifier)
                        : plugin.configuration().messages().commandRecipesRegisterItemSuccess().repl("{identifier}", identifier);
            // Sending error message to the sender.
            return plugin.configuration().messages().commandRecipesRegisterItemFailureInvalidIdentifier().repl("{identifier}", identifier);
        }
        // Sending error message to the sender.
        return plugin.configuration().messages().commandRecipesRegisterItemFailureInvalidItem().repl("{identifier}", identifier);
    }

    @Command("recipes unregister_item")
    @CommandPermission("recipes.command.unregister_item")
    public String onUnregisterItem(
            final @NotNull CommandSender sender,
            final @NotNull @SuggestWith(CustomItemRegistry.Suggestions.class) String identifier
    ) {
        // Registering the item and sending message when successful.
        if (plugin.customItemRegistry().remove(identifier) == true)
            return plugin.configuration().messages().commandRecipesUnregisterItemSuccess().repl("{identifier}", identifier);
        // Sending error message to the sender.
        return plugin.configuration().messages().commandRecipesUnregisterItemFailureInvalidIdentifier().repl("{identifier}", identifier);
    }

    @Command("recipes give_item")
    @CommandPermission("recipes.command.give_item")
    public String onGiveItem(
            final @NotNull CommandSender sender,
            final @NotNull Player target,
            final @NotNull @SuggestWith(CustomItemRegistry.Suggestions.class) String identifier,
            final @Nullable @Optional Integer amount) {
        final @Nullable ItemStack item = plugin.customItemRegistry().get(identifier);
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
            return plugin.configuration().messages().commandRecipesGiveItemSuccess().repl("{target}", target.getName(), "{amount}", item.getAmount(), "{identifier}", identifier);
        }
        // Sending error message to the sender.
        return plugin.configuration().messages().commandRecipesGiveItemFailure().repl("{identifier}", identifier);
    }

}
