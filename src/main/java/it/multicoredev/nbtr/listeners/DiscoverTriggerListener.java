package it.multicoredev.nbtr.listeners;

import it.multicoredev.nbtr.NBTRecipes;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
public final class DiscoverTriggerListener implements Listener {
    private final NBTRecipes plugin;

    public DiscoverTriggerListener(final @NotNull NBTRecipes plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final Player player = event.getPlayer();
            // Iterating over list of recipes added by the plugin.
            plugin.getRecipes().forEach(recipe -> {
                // Skipping already discovered recipes.
                if (player.hasDiscoveredRecipe(recipe.getKey()))
                    return;
                // Making player immediately discover recipes with no criteria specified.
                if (recipe.getDiscoverTrigger() == null)
                    Bukkit.getScheduler().callSyncMethod(plugin, () -> player.discoverRecipe(recipe.getKey()));
                // Otherwise, testing each choice individually.
                else if (recipe.getDiscoverTrigger().getRequiredChoices() != null && !recipe.getDiscoverTrigger().getRequiredChoices().isEmpty()) {
                    // Iterating over contents of player's inventory.
                    for (final @Nullable ItemStack item : player.getInventory().getContents()) {
                        if (item == null || item.getType() == Material.AIR)
                            continue;
                        // Iterating over list of choices that can discover recipe for the player.
                        for (final RecipeChoice choice : recipe.getDiscoverTrigger().getRequiredChoices()) {
                            // Testing item against the current choice.
                            if (choice.test(item)) {
                                // Item passed the choice test, discovering (current) recipe for the player.
                                Bukkit.getScheduler().callSyncMethod(plugin, () -> player.discoverRecipe(recipe.getKey()));
                                // Breaking from the choices loop, as this recipe has been discovered now.
                                break;
                            }
                        }
                    }
                }
            });
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickupItem(final EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player)
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                // Getting the event item.
                final ItemStack item = event.getItem().getItemStack();
                // Iterating over list of recipes added by the plugin.
                plugin.getRecipes().forEach(recipe -> {
                    // Skipping already discovered recipes.
                    if (player.hasDiscoveredRecipe(recipe.getKey()))
                        return;
                    // Skipping unspecified or empty discoveries.
                    if (recipe.getDiscoverTrigger() == null || recipe.getDiscoverTrigger().getRequiredChoices() == null || recipe.getDiscoverTrigger().getRequiredChoices().isEmpty())
                        return;
                    // Iterating over list of choices that can discover recipe for the player.
                    for (final RecipeChoice choice : recipe.getDiscoverTrigger().getRequiredChoices()) {
                        // Testing event item against the current choice.
                        if (choice.test(item)) {
                            // Event item passed the choice test, discovering (current) recipe for the player.
                            Bukkit.getScheduler().callSyncMethod(plugin, () -> player.discoverRecipe(recipe.getKey()));
                            // Breaking from the choices loop, as this recipe has been discovered now.
                            break;
                        }
                    }
                });
            });
    }

}
