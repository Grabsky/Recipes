package it.multicoredev.nbtr;

import com.google.gson.JsonParseException;
import it.multicoredev.mclib.json.GsonHelper;
import it.multicoredev.mclib.json.TypeAdapter;
import it.multicoredev.nbtr.configuration.Config;
import it.multicoredev.nbtr.configuration.adapters.MaterialAdapter;
import it.multicoredev.nbtr.configuration.adapters.RecipeChoiceAdapter;
import it.multicoredev.nbtr.listeners.DiscoverTriggerListener;
import it.multicoredev.nbtr.model.recipes.RecipeWrapper;
import it.multicoredev.nbtr.registry.CustomItemRegistry;
import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

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
// TO-DO: Recipe Editor (GUI) (?)
// TO-DO: Improve performance by skipping recipes that are already registered and have not changed.
public class NBTRecipes extends JavaPlugin {

    private static final GsonHelper GSON = new GsonHelper(
            new TypeAdapter(Material.class, MaterialAdapter.INSTANCE),
            new TypeAdapter(RecipeChoice.class, RecipeChoiceAdapter.INSTANCE)
    );

    @Getter(AccessLevel.PUBLIC)
    private static NBTRecipes instance;

    @Accessors(fluent = true)
    @Getter(AccessLevel.PUBLIC)
    private Config config;

    @Getter(AccessLevel.PUBLIC)
    private CustomItemRegistry customItemRegistry;

    @Getter(AccessLevel.PUBLIC)
    private final List<RecipeWrapper> recipes = new ArrayList<>();

    @Getter(AccessLevel.PUBLIC)
    private final List<NamespacedKey> registeredRecipes = new ArrayList<>();

    @Getter(AccessLevel.PUBLIC)
    private Lamp<BukkitCommandActor> commands;

    @Getter(AccessLevel.PUBLIC)
    private String namespace;

    private final File recipesDir = new File(getDataFolder(), "recipes");

    private static final Pattern NAMESPACE_PATTERN = Pattern.compile("[a-z0-9._-]+$");
    private static final Pattern KEY_PATTERN = Pattern.compile("[a-z0-9/._-]+$");

    /** Returns whether the server is running Folia or not. */
    @Getter(AccessLevel.PUBLIC)
    private static boolean isFolia;

    static {
        try {
            // Checking if the RegionizedServer class (Folia-specific) is present in the runtime.
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            // In case the class is found, it means that the server is running Folia.
            isFolia = true;
        } catch (final ClassNotFoundException e) {
            // In case the class is not found, it means that the server is not running Folia.
            isFolia = false;
        }
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void onEnable() {
        // Updating the plugin instance.
        instance = this;
        // Checking whether the plugin is running on a development build and displaying a warning message accordingly.
        if (this.getPluginMeta().getVersion().endsWith("-SNAPSHOT") == true)
            this.getLogger().warning("You're running a development build of NBTRecipes. Keep in mind that it may contain bugs and compatibility issues.");
        // Checking whether the server is running Folia and displaying a warning message accordingly.
        if (isFolia == true)
            this.getLogger().severe("Looks like you're using Folia. While the plugin should technically support it, due to the experimental state of this software some things may not work as expected. Make sure to report any issues to our issue tracker on GitHub.");
        // Creating new instance of CustomItemRegistry.
        this.customItemRegistry = new CustomItemRegistry(this);
        // Reloading the plugin, and disabling it if something goes wrong.
        if (this.onReload() == false)
            this.getServer().getPluginManager().disablePlugin(this);
        // Registering event listeners.
        this.getServer().getPluginManager().registerEvents(new DiscoverTriggerListener(this), this);
        // Initializing Lamp.
        this.commands = BukkitLamp.builder(this)
                .responseHandler(String.class, (value, context) -> {
                    // Returning in case message is null or empty.
                    if (value == null || value.isEmpty() == true)
                        return;
                    // Sending message to the sender.
                    context.actor().requirePlayer().sendRichMessage(value);
                })
                .build();
        // Registering command(s).
        this.commands.register(new NBTRCommand(this));
        // Starting bStats...
        new Metrics(this, 17319);
    }

    public boolean onReload() {
        // Reloading plugin configuration.
        try {
            if (recipesDir.isDirectory() == false)
                throw new IOException("File \"" + recipesDir.getAbsolutePath() + "\" is not a directory.");
            // Creating the "plugins/NBTRecipes/recipes" directory.
            if (recipesDir.exists() == false && recipesDir.mkdirs() == false)
                throw new IOException("Failed to create \"" + recipesDir.getAbsolutePath() + "\" directory.");
            // Loading the configuration.
            config = GSON.autoload(new File(getDataFolder(), "config.json"), new Config().init(), Config.class);
        } catch (final IOException e) {
            // Printing the stack trace.
            e.printStackTrace();
            // Returning...
            return false;
        }
        // Getting the configured plugin namespace that will be used for recipe registration.
        this.namespace = initializeNamespace();
        // Refreshing the custom item registry.
        customItemRegistry.refresh();
        // Unregistering all plugin-defined recipes.
        registeredRecipes.removeIf(getServer()::removeRecipe);
        // Clearing the list of recipes.
        recipes.clear();
        // Loading recipes.
        loadRecipes(recipesDir);
        // Registering the recipes.
        registerRecipes();
        // Sending information to the console.
        this.getLogger().info("Registered " + registeredRecipes.size() + " our of " + recipes.size() + " recipes.");
        // Returning...
        return true;
    }

    private void loadRecipes(File dir) {
        final File[] files = dir.listFiles();
        // Returning if there is no files in the specified dir.
        if (files == null || files.length == 0) {
            this.getLogger().info("No recipes defined in the \"" + dir.getName() + "\" directory.");
            return;
        }
        // Sorting and iterating through files in natural order to ensure that they are loaded in the same order every time.
        for (final File file : Stream.of(files).sorted(Comparator.naturalOrder()).toList()) {
            if (file.isDirectory() == true)
                loadRecipes(file);
            else if (file.getName().toLowerCase().endsWith(".json") == true) {
                try {
                    final RecipeWrapper recipe = GSON.load(file, RecipeWrapper.class);
                    if (recipe == null || recipe.isValid() == false) {
                        this.getLogger().warning("Recipe \"" + file.getName() + "\" is invalid.");
                        continue;
                    }
                    // Initializing the recipe.
                    recipe.init(getNamespacedKey(file));
                    // Adding the recipe to the list.
                    recipes.add(recipe);
                } catch (final IOException | JsonParseException | IllegalArgumentException | IllegalStateException e) {
                    this.getLogger().severe("Loading of recipe \"" + file.getName() + "\" failed due to following error(s):");
                    // Printing the full stack trace if 'minimize_exceptions_stacktrace' option is disabled.
                    if (config.minimizeExceptionsStacktrace == false) {
                        e.printStackTrace();
                        return;
                    }
                    // Otherwise, just printing the exception message.
                    this.getLogger().severe(" (1) " + e.getClass().getSimpleName() + ": " + e.getMessage());
                    if (e.getCause() != null)
                        this.getLogger().severe(" (2) " + e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage());
                }
            }
        }
    }

    private void registerRecipes() {
        recipes.forEach(recipe -> {
            final NamespacedKey key = recipe.getKey();
            // Support for overriding vanilla commands. Config namespace must be set to "minecraft" for that to work.
            if (key.getNamespace().equals("minecraft") == true && this.getServer().getRecipe(key) != null) {
                // Removing the original recipe. It won't be added back until the server restart or "minecraft:reload" command is executed.
                this.getServer().removeRecipe(key);
                // Sending information to the console.
                this.getLogger().warning("Recipe \"" + key + "\" is now overriding vanilla recipe with the same key.");
            }
            // Registering the recipe.
            this.getServer().addRecipe(recipe.toBukkit(), false);
            // Adding the recipe key to the list of registered recipes.
            registeredRecipes.add(key);
        });
        // Updating recipes.
        this.getServer().updateRecipes();
    }

    // Returns NamespacedKey from configured namespace and relative path of specified file.
    private @NotNull NamespacedKey getNamespacedKey(final @NotNull File file) throws IllegalArgumentException {
        // Returning a new NamespacedKey object from namespace and relative path of specified file.
        return new NamespacedKey(namespace, getKey(file));
    }

    // Returns configured namespace or, in case it's invalid, lower-case plugin name.
    private @NotNull String initializeNamespace() throws IllegalArgumentException {
        // Returning a configured namespace, or in case it's unspecified, lower-case plugin name.
        if (config().namespace == null)
            return getName().toLowerCase(Locale.ROOT);
        // Getting a namespace with all non-matching characters ignored.
        final String namespace = ignoreNonMatchingCharacters(config().namespace, NAMESPACE_PATTERN);
        // Throwing IllegalArgumentException if namespace turned out to be empty.
        if (namespace.isEmpty())
            throw new IllegalArgumentException("Namespace must contain at least one alphanumeric character.");
        // Returning the namespace.
        return namespace;
    }

    // Returns path in relation between recipes directory and specified file. This method also tries to translate some invalid characters.
    private @NotNull String getKey(final @NotNull File file) throws IllegalArgumentException {
        // Relativizing file path, converting to lower-case, and then applying replacements.
        final String relativePath = recipesDir.toPath().relativize(file.toPath()).toString().toLowerCase(Locale.ROOT)
                // Replacing spaces with underscores.
                .replace(" ", "_")
                // Replacing back-slashes with slashes. (for Windows)
                .replace("\\", "/")
                // Removing the '.json' file extension.
                .replace(".json", "");
        // Getting a namespace with all non-matching characters ignored.
        final String key = ignoreNonMatchingCharacters(relativePath, KEY_PATTERN);
        // Throwing IllegalArgumentException if key turned out to be empty.
        if (key.isEmpty())
            throw new IllegalArgumentException("Namespace must contain at least one alphanumeric character.");
        // Returning the key.
        return key;
    }

    // Returns only matching characters within a Pattern. It exists because there seems to be no method to create an "inverted" matcher.
    private static @NotNull String ignoreNonMatchingCharacters(final @NotNull CharSequence charSequence, final @NotNull Pattern pattern) {
        // Creating a Matcher with the specified CharSequence.
        final Matcher matcher = pattern.matcher(charSequence);
        // Creating a result StringBuilder, which will then be appended only with characters that matches the pattern.
        final StringBuilder builder = new StringBuilder();
        // Appending matching elements to the StringBuilder.
        while (matcher.find())
            builder.append(matcher.group());
        // Returning the result.
        return builder.toString();
    }

}
