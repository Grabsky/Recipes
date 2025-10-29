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
package cloud.grabsky.recipes.configuration.adapters;

import cloud.grabsky.recipes.model.recipes.RecipeWrapper;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RecipeWrapperAdapter implements JsonDeserializer<RecipeWrapper> {

    @Override
    public @NotNull RecipeWrapper deserialize(final @NotNull JsonElement element, final @NotNull java.lang.reflect.Type classType, final @NotNull JsonDeserializationContext ctx) throws JsonParseException {
        if (element instanceof JsonObject object) {
            // Reading the recipe type.
            final @Nullable RecipeWrapper.Type type = (object.has("type") == true && object.get("type").isJsonPrimitive() == true)
                    ? ctx.deserialize(object.get("type"), RecipeWrapper.Type.class)
                    : null;
            // Delegating to the appropriate deserializer.
            if (type != null)
                return ctx.deserialize(element, type.getRecipeClass());
            // Throwing exception if 'type' property does not exist or is invalid.
            throw new JsonParseException("Required property \"type\" has not been specified or is invalid. Must be one of " + Arrays.toString(RecipeWrapper.Type.class.getEnumConstants()));
        }
        // Throwing exception if JsonElement is not a JsonObject.
        throw new JsonParseException("Expected JsonObject but found " + element.getClass().getSimpleName() + ".");
    }

}