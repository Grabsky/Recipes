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

import com.google.gson.annotations.SerializedName;
import cloud.grabsky.recipes.model.Item;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Map;
import java.util.Objects;

public final class ShapedRecipeWrapper extends RecipeWrapper {

    @SerializedName("pattern")
    private String[] pattern;

    @SerializedName("pattern_key")
    private Map<Character, RecipeChoice> patternKey;

    @SerializedName("result")
    private Item result;

    public ShapedRecipeWrapper() {
        super(RecipeWrapper.Type.CRAFTING_SHAPELESS);
    }

    @Override
    public boolean isValid() {
        // Returning false if pattern is null or empty.
        if (pattern == null || pattern.length == 0)
            return false;
        // Returning false if 3x3 pattern is not valid.
        if (pattern.length == 3)
            for (final String s : pattern) {
                if (s.length() != 3)
                    return false;
            }
        // Returning false if 2x2 pattern is not valid.
        else if (pattern.length == 2)
            for (final String s : pattern) {
                if (s.length() != 2)
                    return false;
            }
        // Returning false for other, invalid patterns.
        else return false;
        // Returning false if pattern key map is null or empty.
        if (patternKey == null || patternKey.isEmpty())
            return false;
        // Returning false if pattern key map contains null values.
        if (patternKey.values().stream().anyMatch(Objects::isNull) == true)
            return false;
        // Returning false if pattern contains non-mapped characters.
        for (final String s : pattern) {
            for (char c : s.toCharArray()) {
                if (patternKey.containsKey(c) == false && c != ' ')
                    return false;
            }
        }
        // If all above checks have passed and result is not null and valid, returning true.
        return result != null && result.isValid();
    }

    @Override
    public ShapedRecipe toBukkit() {
        // Creating new ShapedRecipe of specific shape.
        final ShapedRecipe recipe = new ShapedRecipe(super.key, result.toItemStack())
                .shape(pattern);
        // Setting specified ingredients on the recipe.
        patternKey.forEach(recipe::setIngredient);
        // Returning the recipe.
        return recipe;
    }

}
