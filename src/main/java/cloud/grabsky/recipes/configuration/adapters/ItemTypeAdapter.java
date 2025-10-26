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
package cloud.grabsky.recipes.configuration.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemType;

import java.lang.reflect.Type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public enum ItemTypeAdapter implements JsonDeserializer<ItemType> {
    INSTANCE; // SINGLETON

    @Override
    public @NotNull ItemType deserialize(final @NotNull JsonElement element, final @NotNull Type classType, final @NotNull JsonDeserializationContext context) throws JsonParseException {
        // Getting the element value as string.
        final String value = element.getAsString();
        // Creating a NamespacedKey instance from the value. Can be null if invalid.
        final @Nullable NamespacedKey key = NamespacedKey.fromString(value);
        // Throwing an exception if key is null or NamespacedKey validation fails.
        if (key == null)
            throw new JsonParseException("Key \"" + value + "\" is not a valid resource location / namespaced key.");
        // Getting the item type from the registry.
        final @Nullable ItemType type = RegistryAccess.registryAccess().getRegistry(RegistryKey.ITEM).get(key);
        // Throwing an exception if item type is null.
        if (type == null)
            throw new JsonParseException("Key \"" + value + "\" does not point to any item type.");
        // Returning...
        return type;
    }

}
