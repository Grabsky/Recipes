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
package cloud.grabsky.recipes.model;

import cloud.grabsky.recipes.Recipes;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@SuppressWarnings("UnstableApiUsage") // ItemType
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class Item {

    @Getter(AccessLevel.PUBLIC)
    private final @Nullable ItemType type;

    // This is the identifier of plugin-defined custom item.
    @Getter(AccessLevel.PUBLIC)
    @SerializedName("registered_item")
    private final @Nullable String registeredItem;

    // Getter is not explicitly defined by Lombok, because method should default to 1 if unspecified.
    private final @Nullable Integer amount;

    @Getter(AccessLevel.PUBLIC)
    private final @Nullable String name;

    @Getter(AccessLevel.PUBLIC)
    private final @Nullable List<String> lore;

    @Getter(AccessLevel.PUBLIC)
    private final @Nullable JsonElement components;

    @SuppressWarnings("deprecation") // Suppressing @Deprecated warnings. Well aware Bukkit#getUnsafe must is a subject to change.
    public ItemStack toItemStack() throws IllegalArgumentException {
        if (registeredItem != null) {
            final @Nullable ItemStack item = Recipes.instance().customItemRegistry().get(registeredItem);
            // Throwing exception if custom item doesn't exist in the registry.
            if (item == null)
                throw new IllegalArgumentException("Custom item \"" + registeredItem + "\" does not exist.");
            // Setting amount if specified and greater than 0.
            if (amount != null && amount > 0)
                item.setAmount(Math.min(item.getMaxStackSize(), amount));
            // Returning the item.
            return item;
        } else if (type != null) {
            final ItemStack item = type.createItemStack();
            // Setting item components if specified. This is done first as it can be overridden by named properties in next steps.
            if (components != null) {
                // Applying raw / inline components string on ItemStack.
                if (components.isJsonPrimitive() == true && components.getAsString().isEmpty() == false)
                    Bukkit.getUnsafe().modifyItemStack(item, type.key().asString() + components.getAsString());
                // Otherwise, parsing a 'structured' format.
                else if (components.isJsonObject() == true && components.getAsJsonObject().isEmpty() == false) {
                    // Building components String. This will be what goes inside square brackets.
                    final StringBuilder componentsBuilder = new StringBuilder();
                    components.getAsJsonObject().entrySet().forEach(it -> {
                        // Reading as String directly and appending to the builder.
                        if (it.getValue().isJsonPrimitive() == true && it.getValue().isJsonArray() == false) {
                            final JsonPrimitive primitive = it.getValue().getAsJsonPrimitive();
                            // getAsString removes quotes. In some cases they must be re-added.
                            final String value = (primitive.isString() == true && primitive.getAsString().startsWith("{") == false && primitive.getAsString().startsWith("[") == false)
                                    ? "\"" + primitive.getAsString() + "\""
                                    : it.getValue().getAsString();
                            componentsBuilder.append(it.getKey()).append("=").append(value).append(",");
                        }
                    });
                    // Removing trailing comma.
                    componentsBuilder.deleteCharAt(componentsBuilder.length() - 1);
                    // Applying components on ItemStack.
                    Bukkit.getUnsafe().modifyItemStack(item, type.key().asString() + "[" + componentsBuilder + "]");
                }
            }
            // Setting amount if specified and greater than 0.
            if (amount != null && amount > 0)
                item.setAmount(Math.min(item.getMaxStackSize(), amount));
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
        throw new IllegalArgumentException("Impossible to parse item because neither \"registered_item\" nor \"type\" properties were specified.");
    }

    public boolean isValid() {
        return (registeredItem != null) ? (Recipes.instance().customItemRegistry().get(registeredItem) != null) : (type != null);
    }
}
