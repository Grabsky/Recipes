package it.multicoredev.nbtr.registry;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.multicoredev.nbtr.NBTRecipes;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public final class CustomItemRegistry {

    @Getter(AccessLevel.PUBLIC)
    private final NBTRecipes plugin;

    // Stores plugin-specified items in their raw form. This is populated by Gson.
    private final Map<String, String> internalMap = new HashMap<>();

    // Stores plugin-specified items backed by their identifier.
    private transient final Map<String, ItemStack> registry = new HashMap<>();

    // Represents the Gson instance used for (de)serialization.
    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().disableHtmlEscaping().create();

    // Represents the internal type of the map. Used for (de)serialization.
    private static final Type MAP_TYPE = new TypeToken<Map<String, String>>() {}.getType();

    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();
    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();

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
            final byte[] decoded = BASE64_DECODER.decode(encoded.getBytes(StandardCharsets.UTF_8));
            // Deserializing the bytes to an ItemStack object.
            registry.put(identifier, ItemStack.deserializeBytes(decoded));
        });
    }

    /** Returns the {@link ItemStack} object associated with the given identifier. */
    public @Nullable ItemStack get(final String identifier) {
        return registry.get(identifier);
    }

    /** Sets the {@link ItemStack} object to be associated with the given identifier. */
    public void set(final String identifier, final ItemStack item) {
        // Encoding the bytes through the Base64 encoder.
        final String bytes = new String(BASE64_ENCODER.encode(item.serializeAsBytes()), StandardCharsets.UTF_8);
        // Putting the encoded bytes in the internal map.
        internalMap.put(identifier, bytes);
        // Refreshing the registry...
        refresh();
    }

    public @NotNull @Unmodifiable Map<String, ItemStack> all() {
        return ImmutableMap.copyOf(registry);
    }

}
