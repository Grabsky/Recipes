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
package cloud.grabsky.recipes.registry;

import cloud.grabsky.recipes.Recipes;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class CustomItemRegistry {

    private final Recipes plugin;

    // Stores plugin-specified items in their raw form. This is populated by Gson.
    private final Map<String, String> internalMap = new HashMap<>();

    // Stores plugin-specified items backed by their identifier.
    private transient final Map<String, ItemStack> registry = new HashMap<>();

    // Represents the Gson instance used for (de)serialization.
    private static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .setLenient()
            .create();

    // Represents the internal type of the map. Used for (de)serialization.
    private static final Type MAP_TYPE = new TypeToken<Map<String, String>>() {}.getType();

    // Represents the pattern used to validate identifiers.
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("[a-zA-Z0-9_\\-]+");

    public void refresh() {
        final File file = new File(plugin.getDataFolder(), "item_registry.json");
        // Creating the file if does not exist.
        if (file.exists() == false) {
            plugin.saveResource("item_registry.json", false);
            return;
        }
        // Trying...
        try {
            // Saving the internal map to the file if it is not empty.
            if (internalMap.isEmpty() == false) {
                // Preparing the BufferedWriter.
                final BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);
                // Writing the map to the file.
                GSON.toJson(internalMap, MAP_TYPE, writer);
                // Closing the writer which also flushes it.
                writer.close();
            }
            // Preparing the BufferedReader.
            final BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);
            // Deserializing the map from the JSON file.
            final Map<String, String> map = GSON.fromJson(reader, MAP_TYPE);
            // Closing the reader.
            reader.close();
            // Clearing the map.
            internalMap.clear();
            // Adding all entries from the map to the internal map.
            internalMap.putAll(map);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        // Clearing the registry.
        registry.clear();
        // Deserializing items defined as bytes to ItemStack objects.
        internalMap.forEach((identifier, encoded) -> {
            // Decoding the bytes through the Base64 encoder.
            final byte[] decoded = Base64.getDecoder().decode(encoded.getBytes(StandardCharsets.UTF_8));
            // Deserializing the bytes to an ItemStack object.
            registry.put(identifier, ItemStack.deserializeBytes(decoded));
        });
    }

    /** Returns the {@link ItemStack} object associated with the given identifier. */
    public @Nullable ItemStack get(final @NotNull String identifier) {
        return registry.get(identifier);
    }

    /**
     * Sets the {@link ItemStack} object to be associated with the given identifier.
     * Returns {@code true} if the identifier is valid and the item was successfully registered, {@code false} otherwise.
     */
    public boolean set(final @NotNull String identifier, final @NotNull ItemStack item) {
        // Checking whether the identifier is valid.
        if (IDENTIFIER_PATTERN.matcher(identifier).matches() == false)
            return false;
        // Setting the ItemStack's amount to 1.
        item.setAmount(1);
        // Encoding the bytes through the Base64 encoder.
        final String bytes = new String(Base64.getEncoder().encode(item.serializeAsBytes()), StandardCharsets.UTF_8);
        // Putting the encoded bytes in the internal map.
        internalMap.put(identifier, bytes);
        // Refreshing the registry...
        refresh();
        // Returning...
        return true;
    }

    /**
     * Removes the {@link ItemStack} object associated with the given identifier.
     * Returns {@code true} if item was successfully removed, {@code false} otherwise.
     */
    public boolean remove(final @NotNull String identifier) {
        // Checking whether the identifier is valid.
        if (internalMap.containsKey(identifier) == false)
            return false;
        // Removing entry form the internal map.
        internalMap.remove(identifier);
        // Refreshing the registry...
        refresh();
        // Returning...
        return true;
    }

    public @NotNull @Unmodifiable Collection<String> allKeys() {
        return Collections.unmodifiableCollection(registry.keySet());
    }

    public @NotNull @Unmodifiable Map<String, ItemStack> all() {
        return ImmutableMap.copyOf(registry);
    }

}
