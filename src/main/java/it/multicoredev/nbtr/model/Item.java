package it.multicoredev.nbtr.model;

import com.google.gson.annotations.SerializedName;
import it.multicoredev.nbtr.NBTRecipes;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
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
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class Item {

    @Getter(AccessLevel.PUBLIC)
    private final @Nullable Material material;

    // Getter is not explicitly defined by Lombok, because method should default to 1 if unspecified.
    private final @Nullable Integer amount;

    @Getter(AccessLevel.PUBLIC)
    private final @Nullable String name;

    @Getter(AccessLevel.PUBLIC)
    private final @Nullable List<String> lore;

    @Getter(AccessLevel.PUBLIC)
    private final @Nullable String components;

    // This is the identifier of plugin-defined custom item.
    @Getter(AccessLevel.PUBLIC)
    @SerializedName("registered_item")
    private final @Nullable String registeredItem;

    @SuppressWarnings("deprecation") // Suppressing @Deprecated warnings. It's Paper that deprecates ChatColor methods and they're called only when running Spigot. It's also Bukkit#getUnsafe which we must use at this point.
    public ItemStack toItemStack() throws IllegalArgumentException {
        if (registeredItem != null) {
            final @Nullable ItemStack item = NBTRecipes.instance().customItemRegistry().get(registeredItem);
            // Throwing exception if custom item doesn't exist in the registry.
            if (item == null)
                throw new IllegalArgumentException("Custom item \"" + registeredItem + "\" does not exist.");
            // Setting amount if specified and greater than 0.
            if (amount != null && amount > 0)
                item.setAmount(Math.min(item.getMaxStackSize(), amount));
            // Returning the item.
            return item;
        }
        final ItemStack item = new ItemStack(material);
        // Setting NBT/Components if specified. This is called first as it can be overridden by named properties in next steps.
        if (components != null && !components.trim().isEmpty())
            Bukkit.getUnsafe().modifyItemStack(item, material.key().asString() + components);
        // Setting amount if specified and greater than 0.
        if (amount != null && amount > 0)
            item.setAmount(Math.min(material.getMaxStackSize(), amount));
        // Checking whether item has item meta.
        if (item.getItemMeta() != null) {
            final ItemMeta meta = item.getItemMeta();
            // Setting name if specified.
            if (name != null)
                meta.displayName(MiniMessage.miniMessage().deserialize(name));
            // Setting lore if specified.
            if (lore != null)
                meta.lore(lore.stream().map(line -> MiniMessage.miniMessage().deserialize(line)).toList());
            // Updating item meta.
            item.setItemMeta(meta);
        }
        // Finally, retuning the item.
        return item;
    }

    public boolean isValid() {
        return (material == null && registeredItem == null) == false;
    }
}
