package it.multicoredev.nbtr;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import it.multicoredev.nbtr.command.RecipesCommand;
import it.multicoredev.nbtr.configuration.PluginConfiguration;
import it.multicoredev.nbtr.configuration.adapters.MaterialAdapter;
import it.multicoredev.nbtr.configuration.adapters.RecipeChoiceAdapter;
import it.multicoredev.nbtr.listeners.DiscoverTriggerListener;
import it.multicoredev.nbtr.model.recipes.RecipeWrapper;
import it.multicoredev.nbtr.registry.CustomItemRegistry;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.spec.ArrayCommentStyle;
import revxrsal.spec.CommentedConfiguration;
import revxrsal.spec.Specs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
@Accessors(fluent = true)
public class NBTRecipes extends JavaPlugin {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Material.class, MaterialAdapter.INSTANCE)
            .registerTypeAdapter(RecipeChoice.class, RecipeChoiceAdapter.INSTANCE)
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create();

    @Getter(AccessLevel.PUBLIC)
    private static NBTRecipes instance;

    @Getter(AccessLevel.PUBLIC)
    private File configurationFile;

    @Getter(AccessLevel.PUBLIC)
    private CommentedConfiguration commentedConfiguration;

    @Getter(AccessLevel.PUBLIC)
    private PluginConfiguration configuration;

    @Getter(AccessLevel.PUBLIC)
    private CustomItemRegistry customItemRegistry;

    @Getter(AccessLevel.PUBLIC)
    private final List<RecipeWrapper> recipes = new ArrayList<>();

    @Getter(AccessLevel.PUBLIC)
    private final List<NamespacedKey> registeredRecipes = new ArrayList<>();

    @Getter(AccessLevel.PUBLIC)
    private Lamp<BukkitCommandActor> lamp;

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
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
        } catch (final ClassNotFoundException e) {
            isFolia = false;
        }
    }

    private static final ThreadLocal<Yaml> YAML = ThreadLocal.withInitial(() -> {
        final DumperOptions options = new DumperOptions();
        options.setSplitLines(false);
        options.setProcessComments(false);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(options);
    });

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
        this.lamp = BukkitLamp.builder(this)
                // Registering @Dependency dependencies.
                .dependency(NBTRecipes.class, this)
                // Registering command response handler for String object.
                .responseHandler(String.class, (value, context) -> {
                    // Forwarding returned message to the command sender.
                    if (value.isEmpty() == false)
                        context.actor().reply(MiniMessage.miniMessage().deserialize(value));
                })
                .build();
        // Registering command(s).
        lamp.register(RecipesCommand.INSTANCE);
        // Starting bStats...
        // new Metrics(this, 17319);
    }

    public boolean onReload() {
        this.configurationFile = new File(this.getDataFolder(), "config.yml");
        // Initializing instance of CommentedConfiguration.
        this.commentedConfiguration = new CommentedConfiguration(configurationFile.toPath(), CommentedConfiguration.GSON, ArrayCommentStyle.COMMENT_FIRST_ELEMENT, YAML.get());
        // Loading configuration file.
        this.configuration = Specs.fromConfig(PluginConfiguration.class, commentedConfiguration);
        // Saving default contents to the configuration file.
        this.configuration.save();
        // Reloading and mapping configuration file contents to the PluginConfiguration instance.
        this.configuration.reload();
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
        // Sending information to the console. (Commented out because it is inaccurate)
        // this.getLogger().info("Registered " + registeredRecipes.size() + " out of " + recipes.size() + " recipes.");
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
                this.loadRecipes(file);
            else if (file.getName().toLowerCase().endsWith(".json") == true) {
                try {
                    final RecipeWrapper recipe = GSON.fromJson(new FileReader(file), RecipeWrapper.class);
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
                    if (this.configuration.minimizeExceptionsStacktrace() == false) {
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
            // Support for overriding vanilla commands. PluginConfiguration namespace must be set to "minecraft" for that to work.
            if (key.getNamespace().equals("minecraft") == true && this.getServer().getRecipe(key) != null) {
                // Removing the original recipe. It won't be added back until the server restart or "minecraft:reload" command is executed.
                this.getServer().removeRecipe(key);
                // Sending information to the console.
                this.getLogger().warning("Recipe \"" + key + "\" is now overriding vanilla recipe with the same key.");
            }
            // Registering the recipe...
            try {
                this.getServer().addRecipe(recipe.toBukkit(), false);
                // Adding the recipe key to the list of registered recipes.
                registeredRecipes.add(key);
            } catch (final IllegalArgumentException e) {
                // FAILING SILENTLY; Error and stacktrace should already be printed when loading.
            }
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
        if (this.configuration.namespace() == null)
            return getName().toLowerCase(Locale.ROOT);
        // Getting a namespace with all non-matching characters ignored.
        final String namespace = ignoreNonMatchingCharacters(this.configuration.namespace(), NAMESPACE_PATTERN);
        // Throwing IllegalArgumentException if namespace turned out to be empty.
        if (namespace.isEmpty() == true)
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


    /* PLUGIN LOADER; FOR USE WITH PLUGIN-YML FOR GRADLE */

    @SuppressWarnings("UnstableApiUsage")
    public static final class PluginLoader implements io.papermc.paper.plugin.loader.PluginLoader {

        @Override
        public void classloader(final @NotNull PluginClasspathBuilder classpathBuilder) throws IllegalStateException {
            final MavenLibraryResolver resolver = new MavenLibraryResolver();
            // Parsing the file.
            try (final InputStream in = getClass().getResourceAsStream("/paper-libraries.json")) {
                final PluginLibraries libraries = new Gson().fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), PluginLibraries.class);
                // Adding repositories to the maven library resolver.
                libraries.asRepositories().forEach(resolver::addRepository);
                // Adding dependencies to the maven library resolver.
                libraries.asDependencies().forEach(resolver::addDependency);
                // Adding library resolver to the classpath builder.
                classpathBuilder.addLibrary(resolver);
            } catch (final IOException e) {
                throw new IllegalStateException(e);
            }
        }

        @RequiredArgsConstructor(access = AccessLevel.PUBLIC)
        private static class PluginLibraries {

            private final Map<String, String> repositories;
            private final List<String> dependencies;

            public Stream<RemoteRepository> asRepositories() {
                return repositories.entrySet().stream().map(entry -> {
                    try {
                        final String MAVEN_CENTRAL_DEFAULT_MIRROR = (String) MavenLibraryResolver.class.getField("MAVEN_CENTRAL_DEFAULT_MIRROR").get(null);
                        // Replacing Maven Central repository with a pre-configured mirror.
                        // See: https://docs.papermc.io/paper/dev/getting-started/paper-plugins/#loaders
                        if (entry.getValue().contains("maven.org") == true || entry.getValue().contains("maven.apache.org") == true) {
                            return new RemoteRepository.Builder(entry.getKey(), "default", MAVEN_CENTRAL_DEFAULT_MIRROR).build();
                        }
                        return new RemoteRepository.Builder(entry.getKey(), "default", entry.getValue()).build();
                    } catch (final NoSuchFieldError | NoSuchFieldException | IllegalAccessException e) {
                        return new RemoteRepository.Builder(entry.getKey(), "default", "https://maven-central.storage-download.googleapis.com/maven2").build();
                    }
                });
            }

            public Stream<Dependency> asDependencies() {
                return dependencies.stream().map(value -> new Dependency(new DefaultArtifact(value), null));
            }
        }
    }

}
