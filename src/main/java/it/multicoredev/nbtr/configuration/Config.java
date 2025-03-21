package it.multicoredev.nbtr.configuration;

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

    @SerializedName("minimize_exceptions_stacktrace")
    public Boolean minimizeExceptionsStacktrace;

    @SerializedName("reloaded")
    public String reloaded;

    @SerializedName("recipes_list_success_header")
    public String recipesListSuccessHeader;

    @SerializedName("recipes_list_success_entry")
    public String recipesListSuccessEntry;

    @SerializedName("recipes_list_success_footer")
    public String recipesListSuccessFooter;

    @SerializedName("recipes_list_failure_empty")
    public String recipesListFailureEmpty;

    @SerializedName("custom_items_success_list_header")
    public String customItemsSuccessListHeader;

    @SerializedName("custom_items_success_list_entry")
    public String customItemsSuccessListEntry;

    @SerializedName("custom_items_success_list_footer")
    public String customItemsSuccessListFooter;

    @SerializedName("custom_items_failure_empty")
    public String customItemsFailureEmpty;

    @SerializedName("custom_items_register_success")
    public String customItemsRegisterSuccess;

    @SerializedName("custom_items_register_success_overridden")
    public String customItemsRegisterSuccessOverridden;

    @SerializedName("custom_items_register_failure_invalid_item")
    public String customItemsRegisterFailureInvalidItem;

    @SerializedName("custom_items_register_failure_invalid_identifier")
    public String customItemsRegisterFailureInvalidIdentifier;

    @SerializedName("custom_items_unregister_success")
    public String customItemsUnregisterSuccess;

    @SerializedName("custom_items_unregister_failure_invalid_identifier")
    public String customItemsUnregisterFailureInvalidIdentifier;

    @SerializedName("custom_items_give_success")
    public String customItemsGiveSuccess;

    @SerializedName("custom_items_give_failure")
    public String customItemsGiveFailure;

    @Override
    public Config init() {
        // Setting default values...
        if (namespace == null)
            namespace = "nbtrecipes";
        if (minimizeExceptionsStacktrace == null)
            minimizeExceptionsStacktrace = true;
        if (reloaded == null)
            reloaded = "<dark_gray>› <gray>Plugin <gold>NBTRecipes<gray> has been reloaded.";
        if (recipesListSuccessHeader == null)
            recipesListSuccessHeader = "<dark_gray><st>---------------------</st>  <gray>Recipes  <dark_gray><st>---------------------</st><newline>";
        if (recipesListSuccessEntry == null)
            recipesListSuccessEntry = " <#848484>{number}. <#E0C865>{recipe}";
        if (recipesListSuccessFooter == null)
            recipesListSuccessFooter = "<newline><dark_gray><st>---------------------------------------------------</st>";
        if (recipesListFailureEmpty == null)
            recipesListFailureEmpty = "<dark_gray>› <red>No recipes were found.";
        if (customItemsSuccessListHeader == null)
            customItemsSuccessListHeader = "<dark_gray><st>-------------------</st>  <gray>Custom Items  <dark_gray><st>-------------------</st><newline>";
        if (customItemsSuccessListEntry == null)
            customItemsSuccessListEntry = " <#848484>{number}. <#E0C865>{identifier}";
        if (customItemsSuccessListFooter == null)
            customItemsSuccessListFooter = "<newline><dark_gray><st>---------------------------------------------------</st>";
        if (customItemsFailureEmpty == null)
            customItemsFailureEmpty = "<dark_gray>› <red>No items were found.";
        if (customItemsRegisterSuccess == null)
            customItemsRegisterSuccess = "<dark_gray>› <gray>Item <gold>{identifier}<gray> has been registered.";
        if (customItemsRegisterSuccessOverridden == null)
            customItemsRegisterSuccessOverridden = "<dark_gray>› <gray>Item <gold>{identifier}<gray> has been overridden.";
        if (customItemsRegisterFailureInvalidItem == null)
            customItemsRegisterFailureInvalidItem = "<dark_gray>› <red>Currently held item cannot be registered.";
        if (customItemsRegisterFailureInvalidIdentifier == null)
            customItemsRegisterFailureInvalidIdentifier = "<dark_gray>› <red>Identifier <gold>{identifier}<red> contains invalid characters.";
        if (customItemsUnregisterSuccess == null)
            customItemsUnregisterSuccess = "<dark_gray>› <gray>Item <gold>{identifier}<gray> has been unregistered.";
        if (customItemsUnregisterFailureInvalidIdentifier == null)
            customItemsUnregisterFailureInvalidIdentifier = "<dark_gray>› <red>Item <gold>{identifier}<red> does not exist.";
        if (customItemsGiveSuccess == null)
            customItemsGiveSuccess = "<dark_gray>› <gray>Player <gold>{target}<gray> was given <gold>{amount}x {identifier}<gray>.";
        if (customItemsGiveFailure == null)
            customItemsGiveFailure = "<dark_gray>› <red>Item <gold>{identifier}<red> does not exist.";
        // Returning...
        return this;
    }
}
