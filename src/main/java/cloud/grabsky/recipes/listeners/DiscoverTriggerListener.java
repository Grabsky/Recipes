/*
 * BSD 3-Clause License
 *
 * Copyright (c) 2023, Lorenzo Magni
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
package cloud.grabsky.recipes.listeners;

import cloud.grabsky.recipes.Recipes;
import cloud.grabsky.recipes.model.recipes.RecipeWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class DiscoverTriggerListener implements Listener {

    private final Recipes plugin;

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Bukkit.getAsyncScheduler().runNow(plugin, (task) -> {
            final Player player = event.getPlayer();
            // Preparing the list of recipes to discover.
            final List<NamespacedKey> recipes = plugin.recipes().stream().filter(recipe -> {
                // Excluding already discovered recipes.
                if (player.hasDiscoveredRecipe(recipe.getKey()) == true)
                    return false;
                // Recipes with no criteria specified should be added to the list with no further checks.
                if (recipe.getDiscoverTrigger() == null)
                    return true;
                    // Otherwise, testing each choice individually.
                else if (recipe.getDiscoverTrigger().getRequiredItems() != null && recipe.getDiscoverTrigger().getRequiredItems().isEmpty() == false) {
                    // Iterating over contents of player's inventory.
                    for (final @Nullable ItemStack item : player.getInventory().getContents()) {
                        if (item == null || item.getType() == Material.AIR)
                            continue;
                        // Iterating over list of choices that can discover recipe for the player.
                        for (final RecipeChoice choice : recipe.getDiscoverTrigger().getRequiredItems()) {
                            // Testing item against the current choice.
                            if (choice.test(item) == true)
                                return true;
                        }
                    }
                }
                // Otherwise, excluding the recipe.
                return false;
            }).map(RecipeWrapper::getKey).toList();
            // Discovering the recipes for the player.
            player.getScheduler().run(plugin, (_task) -> player.discoverRecipes(recipes), null);
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickupItem(final EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            // Getting the item that was picked up. Must be called immediately because on the next tick it is already empty.
            final ItemStack item = event.getItem().getItemStack().clone();
            // Scheduling further logic off the main thread.
            Bukkit.getAsyncScheduler().runNow(plugin, (task) -> {
                // Preparing the list of recipes to discover.
                final List<NamespacedKey> recipes = plugin.recipes().stream().filter(recipe -> {
                    // Excluding already discovered recipes.
                    if (player.hasDiscoveredRecipe(recipe.getKey()) == true)
                        return false;
                    // Recipes with no criteria specified should be added to the list with no further checks.
                    if (recipe.getDiscoverTrigger() == null)
                        return true;
                        // Otherwise, testing each choice individually.
                    else if (recipe.getDiscoverTrigger().getRequiredItems() != null && recipe.getDiscoverTrigger().getRequiredItems().isEmpty() == false) {
                        // Iterating over list of choices that can discover recipe for the player.
                        for (final RecipeChoice choice : recipe.getDiscoverTrigger().getRequiredItems()) {
                            // Testing item against the current choice.
                            if (choice.test(item) == true)
                                return true;
                        }
                    }
                    // Otherwise, excluding the recipe.
                    return false;
                }).map(RecipeWrapper::getKey).toList();
                // Discovering the recipes for the player.
                player.getScheduler().run(plugin, (_task) -> player.discoverRecipes(recipes), null);
            });
        }
    }

}
