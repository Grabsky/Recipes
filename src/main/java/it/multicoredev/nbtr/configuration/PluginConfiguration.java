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
package it.multicoredev.nbtr.configuration;

import revxrsal.spec.annotation.Comment;
import revxrsal.spec.annotation.ConfigSpec;
import revxrsal.spec.annotation.Key;
import revxrsal.spec.annotation.Order;
import revxrsal.spec.annotation.Reload;
import revxrsal.spec.annotation.Reset;
import revxrsal.spec.annotation.Save;

@ConfigSpec
public interface PluginConfiguration {

    @Order(0) @Key("namespace")
    @Comment("Namespace to be used for recipe registration. (Default: nbtrecipes)")
    default String namespace() {
        return "nbtrecipes";
    }

    @Order(1) @Key("minimize_exceptions_stacktrace")
    @Comment("Whether exception stacktrace should be minimized. (Default: true)")
    default boolean minimizeExceptionsStacktrace() {
        return true;
    }

    @Order(3) @Key("messages")
    @Comment("Translatable messages used across the entire plugin. MiniMessage is the only supported text format.")
    Messages messages();

    @ConfigSpec
    interface Messages {

        // Recipes > Reload

        @Order(0)
        @Key("command.recipes.reload.success")
        @Comment("Recipes > Reload")
        default String commandRecipesReloadSuccess() {
            return "<dark_gray>› <gray>Plugin <gold>NBTRecipes<gray> has been reloaded.";
        }

        // Recipes > Recipes List

        @Order(1)
        @Key("command.recipes.recipes_list.header")
        @Comment("Recipes > Recipes List")
        default String commandRecipesRecipesListHeader() {
            return "<dark_gray><st>---------------------</st>  <gray>Recipes  <dark_gray><st>---------------------</st><newline>";
        }

        @Order(2)
        @Key("command.recipes.recipes_list.entry")
        default String commandRecipesRecipesListEntry() {
            return " <#848484>{number}. <#E0C865>{recipe}";
        }

        @Order(3)
        @Key("command.recipes.recipes_list.footer")
        default String commandRecipesRecipesListFooter() {
            return "<newline><dark_gray><st>---------------------------------------------------</st>";
        }

        @Order(4)
        @Key("command.recipes.recipes_list.failure.empty")
        default String commandRecipesRecipesListFailureEmpty() {
            return "<dark_gray>› <red>No recipes were found.";
        }

        // Recipes > Items List

        @Order(5)
        @Key("command.recipes.items_list.header")
        @Comment("Recipes > Items List")
        default String commandRecipesItemsListHeader() {
            return "<dark_gray><st>-------------------</st>  <gray>Custom Items  <dark_gray><st>-------------------</st><newline>";
        }

        @Order(6)
        @Key("command.recipes.items_list.entry")
        default String commandRecipesItemsListEntry() {
            return " <#848484>{number}. <#E0C865>{identifier}";
        }

        @Order(7)
        @Key("command.recipes.items_list.footer")
        default String commandRecipesItemsListFooter() {
            return "<newline><dark_gray><st>---------------------------------------------------</st>";
        }

        @Order(8)
        @Key("command.recipes.items_list.failure.empty")
        default String commandRecipesItemsListFailureEmpty() {
            return "<dark_gray>› <red>No items were found.";
        }

        // Recipes > Register Item

        @Order(9)
        @Key("command.recipes.register_item.success")
        @Comment("Message shown when an item is successfully registered")
        default String commandRecipesRegisterItemSuccess() {
            return "<dark_gray>› <gray>Item <gold>{identifier}<gray> has been registered.";
        }

        @Order(10)
        @Key("command.recipes.register_item.success.overridden")
        default String commandRecipesRegisterItemSuccessOverridden() {
            return "<dark_gray>› <gray>Item <gold>{identifier}<gray> has been overridden.";
        }

        @Order(11)
        @Key("command.recipes.register_item.failure.invalid_item")
        default String commandRecipesRegisterItemFailureInvalidItem() {
            return "<dark_gray>› <red>Currently held item cannot be registered.";
        }

        @Order(12)
        @Key("command.recipes.register_item.failure.invalid_identifier")
        default String commandRecipesRegisterItemFailureInvalidIdentifier() {
            return "<dark_gray>› <red>Identifier <gold>{identifier}<red> contains invalid characters.";
        }

        // Recipes > Unregister Item

        @Order(13)
        @Key("command.recipes.unregister_item.success")
        @Comment("Recipes > Unregister Item")
        default String commandRecipesUnregisterItemSuccess() {
            return "<dark_gray>› <gray>Item <gold>{identifier}<gray> has been unregistered.";
        }

        @Order(14)
        @Key("command.recipes.unregister_item.failure.invalid_identifier")
        default String commandRecipesUnregisterItemFailureInvalidIdentifier() {
            return "<dark_gray>› <red>Item <gold>{identifier}<red> does not exist.";
        }

        // Recipes > Give Item

        @Order(15)
        @Key("command.recipes.give_item.success")
        @Comment("Recipes > Give Item")
        default String commandRecipesGiveItemSuccess() {
            return "<dark_gray>› <gray>Player <gold>{target}<gray> was given <gold>{amount}x {identifier}<gray>.";
        }

        @Order(16)
        @Key("command.recipes.give_item.failure")
        default String commandRecipesGiveItemFailure() {
            return "<dark_gray>› <red>Item <gold>{identifier}<red> does not exist.";
        }

    }

    /* IMPLEMENTED BY SPEC */

    /**
     * Resets this {@link org.apache.maven.model.PluginConfiguration} instance to default values.
     */
    @Reset
    void reset();

    /**
     * Saves configuration to the file.
     */
    @Save
    void save();

    /**
     * Loads configuration from the file.
     */
    @Reload
    void reload();

}
