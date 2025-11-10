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

import cloud.grabsky.recipes.configuration.adapters.RecipeWrapperAdapter;
import cloud.grabsky.recipes.model.DiscoverTrigger;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@JsonAdapter(RecipeWrapperAdapter.class)
public abstract class RecipeWrapper {

    @Getter(AccessLevel.PUBLIC)
    @SerializedName("key")
    protected NamespacedKey key;

    @SerializedName("type")
    protected RecipeWrapper.Type type;

    @Getter(AccessLevel.PUBLIC)
    @SerializedName("discover")
    protected DiscoverTrigger discoverTrigger;

    public RecipeWrapper(final @NotNull Type type) {
        this.type = type;
    }

    public void setFallbackKey(final @NotNull NamespacedKey key) {
        if (this.key == null)
            this.key = key;
    }

    /** Returns {@code true} if this recipe is valid. */
    public abstract boolean isValid();

    /** Converts this {@link RecipeWrapper} to Bukkit's {@link Recipe} object. */
    public abstract Recipe toBukkit();


    /** Represents a supported recipe type. */
    @RequiredArgsConstructor(access = AccessLevel.PUBLIC)
    public enum Type {
        // Crafting
        CRAFTING_SHAPED(ShapedRecipeWrapper.class),
        CRAFTING_SHAPELESS(ShapelessRecipeWrapper.class),
        // Smelting
        SMELTING(SmeltingRecipeWrapper.class),
        BLASTING(BlastingRecipeWrapper.class),
        SMOKING(SmokingRecipeWrapper.class),
        CAMPFIRE_COOKING(CampfireRecipeWrapper.class),
        // Smithing
        SMITHING(SmithingRecipeWrapper.class),
        // Stonecutting
        STONECUTTING(StonecuttingRecipeWrapper.class);

        @Getter(AccessLevel.PUBLIC)
        private final Class<? extends RecipeWrapper> recipeClass;

    }

}
