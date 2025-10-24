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
package cloud.grabsky.recipes.model.recipes;

import cloud.grabsky.recipes.model.DiscoverTrigger;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@JsonAdapter(RecipeWrapper.Adapter.class)
public abstract class RecipeWrapper {

    @Getter(AccessLevel.PUBLIC)
    protected transient NamespacedKey key;

    @SerializedName("type")
    protected String type;

    @Getter(AccessLevel.PUBLIC)
    @SerializedName("discover")
    protected DiscoverTrigger discoverTrigger;

    public RecipeWrapper(final @NotNull Type type) {
        this.type = type.getType();
    }

    public Type getType() {
        return Type.getFromString(type);
    }

    public void init(final @NotNull NamespacedKey namespacedKey) {
        this.key = namespacedKey;
    }

    /** Returns {@code true} if this recipe is valid. */
    public abstract boolean isValid();

    /** Converts this {@link RecipeWrapper} to Bukkit's {@link Recipe} object. */
    public abstract Recipe toBukkit();


    /** Represents a supported recipe type. */
    @RequiredArgsConstructor(access = AccessLevel.PUBLIC)
    public enum Type {
        SHAPED("crafting_shaped", ShapedRecipeWrapper.class),
        SHAPELESS("crafting_shapeless", ShapelessRecipeWrapper.class),
        SMELTING("smelting", SmeltingRecipeWrapper.class),
        BLASTING("blasting", BlastingRecipeWrapper.class),
        SMOKING("smoking", SmokingRecipeWrapper.class),
        CAMPFIRE("campfire_cooking", CampfireRecipeWrapper.class),
        SMITHING_RECIPE("smithing", SmithingRecipeWrapper.class),
        STONECUTTING_RECIPE("stonecutting", StonecuttingRecipeWrapper.class);

        @Getter(AccessLevel.PUBLIC)
        private final String type;

        @Getter(AccessLevel.PUBLIC)
        private final Class<? extends RecipeWrapper> recipeClass;

        // This can be cached as enums don't change in the runtime.
        private static final String[] TYPES = Arrays.stream(Type.values()).map(Type::getType).toArray(String[]::new);

        public static Type getFromString(String type) {
            for (Type t : Type.values()) {
                if (t.getType().equalsIgnoreCase(type)) {
                    return t;
                }
            }
            return null;
        }

    }

    public static final class Adapter implements JsonDeserializer<RecipeWrapper> {

        @Override
        public RecipeWrapper deserialize(final @NotNull JsonElement json, final @NotNull java.lang.reflect.Type classType, final @NotNull JsonDeserializationContext ctx) throws JsonParseException {
            if (json instanceof JsonObject object) {
                // Reading the recipe type.
                final @Nullable RecipeWrapper.Type type = (object.has("type") == true && object.get("type").isJsonPrimitive() == true)
                        ? RecipeWrapper.Type.getFromString(object.get("type").getAsString())
                        : null;
                // Delegating to the appropriate deserializer.
                if (type != null)
                    return ctx.deserialize(json, type.recipeClass);
                // Throwing exception if 'type' property does not exist or is invalid.
                throw new JsonParseException("Required property \"type\" has not been specified or is invalid. Must be one of " + Arrays.toString(Type.TYPES));
            }
            // Throwing exception if JsonElement is not a JsonObject.
            throw new JsonParseException("Expected JsonObject but found " + json.getClass().getSimpleName() + ".");
        }

    }

}
