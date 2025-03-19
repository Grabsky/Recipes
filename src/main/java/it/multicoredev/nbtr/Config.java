package it.multicoredev.nbtr;

import com.google.gson.annotations.SerializedName;
import it.multicoredev.mclib.json.JsonConfig;

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
public final class Config extends JsonConfig {

    @SerializedName("namespace")
    public String namespace;

    @SerializedName("reloaded")
    public String reloaded;

    @SerializedName("recipes_list_header")
    public String recipesListHeader;

    @SerializedName("recipes_list_entry")
    public String recipesListEntry;

    @SerializedName("recipes_list_footer")
    public String recipesListFooter;

    @SerializedName("custom_items_list_header")
    public String customItemsListHeader;

    @SerializedName("custom_items_list_entry")
    public String customItemsListEntry;

    @SerializedName("custom_items_list_footer")
    public String customItemsListFooter;

    @Override
    public Config init() {
        // Setting default values...
        if (namespace == null)
            namespace = "nbtrecipes";
        if (reloaded == null)
            reloaded = "<dark_gray>› <gray>Plugin <gold>NBTRecipes<gray> has been reloaded.";
        if (recipesListHeader == null)
            recipesListHeader = "<dark_gray><st>---------------------</st>  <gray>Recipes  <dark_gray><st>---------------------</st><newline>";
        if (recipesListEntry == null)
            recipesListEntry = " <#848484>{number}. <#E0C865>{recipe}";
        if (recipesListFooter == null)
            recipesListFooter = "<newline><dark_gray><st>---------------------------------------------------</st>";
        if (customItemsListHeader == null)
            customItemsListHeader = "<dark_gray><st>-------------------</st>  <gray>Custom Items  <dark_gray><st>-------------------</st><newline>";
        if (customItemsListEntry == null)
            customItemsListEntry = " <#848484>{number}. <#E0C865>{item}";
        if (customItemsListFooter == null)
            customItemsListFooter = "<newline><dark_gray><st>---------------------------------------------------</st>";
        // Returning...
        return this;
    }
}
