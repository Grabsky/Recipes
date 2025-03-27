# NBTRecipes
<a href="https://modrinth.com/plugin/nbtrecipes/"><img alt="modrinth" height="44" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg"></a>
<a href="https://www.spigotmc.org/resources/nbtrecipes.107230/"><img alt="spigotmc" height="44" src="badges/spigotmc_vector.svg"></a>
<a href="https://hangar.papermc.io/LoreSchaeffer/NBTRecipes"><img alt="hangar" height="44" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/hangar_vector.svg"></a>
<a href="https://legacy.curseforge.com/minecraft/bukkit-plugins/nbtrecipes"><img alt="curseforge" height="44" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg"></a>

Plugin designed to simplify creation of custom recipes, without the need writing any code or touching data-packs.
You can attach common metadata (amount, name, lore) as well as NBT tags to items processed by the recipes.
Format is very similar to the one used by data-packs.

<br />

## Requirements
Plugin requires **[Paper](https://papermc.io/software/paper)** or **[Folia](https://papermc.io/software/folia)** based server and **Java 21** or higher.

## Usage
You can place your recipes inside the `recipes` folder, or any sub-folder within this directory. Plugin automatically search them recursively and add their relative path to the recipe key.

To add a new recipe just create a text file with the `.json` extension and edit it using a text editor of your choice. Functional examples can be found in the [Examples](#examples) section.

### 1.1. Config
In the config you can change the namespace of your recipes and all the messages of the plugin.
The namespace can only contain the following characters: `a-z`, `0-9`, `_`, `-`, `/`.

Legacy color codes are not supported. Please use [MiniMessage](https://docs.advntr.dev/minimessage/format) for text formatting.

### 1.2. Commands
* **`/nbtrecipes reload`**
  * Reloads recipes, items and the configuration file.
  * Permission: `nbtr.command.reload`
* **`/nbtrecipes list_recipes`**
  * Lists all recipes added by this plugin.
  * Permission: `nbtr.command.list_recipes`
* **`/nbtrecipes list_items`**
  * Lists all registered custom items.
  * Permission: `nbtr.command.list_items`
* **`/nbtrecipes register_item (identifier)`**
  * Registers custom item that is currently held in the main hand.
  * Permission: `nbtr.command.register_item`
* **`/nbtrecipes unregister_item (identifier)`**
  * Unregisters custom item with the given identifier.
  * Permission: `nbtr.command.unregister_item`
* **`/nbtrecipes give_item (target) (identifier) [amount]`**
  * Gives specified custom item to the target.
  * Permission: `nbtr.command.give_item`

<br />

## Plugin Components
Description of recipe components and their capabilities.

### 2.1. Item
Item is an object that represents an item in the recipes. It can be used as an ingredient, input or as a result.
Majority of fields are optional; in fact, only either `material` or `registered_item` is required.

### 2.2. Tag
Tags, specifically material tags, may be described as groups of materials. They're an actual vanilla feature and are commonly referenced in various parts of the game.

They can be used in all ingredient or input slots, but cannot be mixed together with different choice types.
When the input field of a smelting recipe is set to tag `mincraft:boats` and result is set to `coal_block`, this allows all types of boats to be smelted into a block of coal.

Please note that some tags, including those added or modified by data-packs, may not work due to API limitations.

### 2.3. Choice
Choice represents set of items **or** a tag, which can be used in a specific ingredient or input slot.

Most ingredient or input slots are expected to consist of exactly one choice, which, if not tag, can be an array with multiple elements but keep in mind some recipe types may accept an array of choices.

Please refer to [Examples](#examples) section below for more details.

#### 2.3.1. Item(s) Choice
Can be used to select individual items (or registered items), or multiple items defined as an array. If no elements have metadata specified (nbt, name, lore), the recipe will compare items based on their material.
```json5
"input": { "material": "minecraft:iron_axe", "nbt": "{Damage:157}" }
```
```json5
// Item 'ruby' must be registered using the `/nbtrecipes register_item` command.
"input": { "registered_item": "ruby" }
```
```json5
"input": [
  { "material": "minecraft:stone" },
  { "material": "minecraft:cobblestone" }
]
```
```json5
// Registered items and normal items can be mixed together.
"input": [
  { "material": "minecraft:cod" },
  { "material": "minecraft:salmon" },
  { "registered_item": "raw_mackerel" },
  { "registered_item": "raw_shrimp" },
  { "registered_item": "raw_tuna" },
  { "registered_item": "raw_carp" },
  { "registered_item": "raw_koi" },
]
```
#### 2.3.2. Tag Choice
Can be used to select individual group of items. Recipe will compare items based on their material.
```json5
"input": { "tag": "minecraft:boats" }
```

### 2.4. Discover Trigger
Recipe discover trigger can make any recipe discoverable by players when they pick up an item.
This feature is optional and if left unspecified, the recipe will be automatically discovered and visible by default.

Please refer to [Examples](#examples) section below for more details.

<br />

## Examples
Collection of JSON examples for each supported recipe type.

### 3.1. Shaped Recipe
Shaped recipe applies to crafting table and inventory crafting.

<details>
  <summary><b>JSON EXAMPLE</b></summary>

```json5
{
  "type": "crafting_shaped",
  // Crafting pattern. Array must consist of either:
  // - two, two-character elements reflecting an inventory crafting grid.
  // - three, three-character elements reflecting a crafting table grid.
  "pattern": [
    "  D",
    " D ",
    "S  "
  ],
  // Key to the pattern.
  // Each character must be mapped to exactly one recipe choice, which can be an array with multiple elements.
  "key": {
    "S": [
      { "material": "stick" },
      { "material": "blaze_rod" }
    ],
    "D": { "material": "diamond" }
  },
  // Recipe result.
  "result": {
    "material": "diamond_sword",
    "amount": 1,
    "name": "Diagonally Crafted Diamond Sword",
    "lore": [
      "As the name suggests..."
    ],
    "nbt": "{CustomModelData: 2}"
  },
  // Recipe discover trigger. Optional.
  "discover": {
    // Items that discovers the recipe. List of recipe choices. Each choice can be an array with multiple elements.
    "items": [
      { "material": "diamond" }
    ]
  }
}
```

</details>

### 3.2. Shapeless Recipe
Shapeless recipe applies to crafting table and inventory crafting.

<details>
  <summary><b>JSON EXAMPLE</b></summary>

```json5
{
  "type": "crafting_shapeless",
  // Crafting ingredients. List of recipe choices. Each choice can be an array with multiple elements.
  "ingredients": [
    { "tag": "minecraft:logs" },
    { "material": "flint_and_steel" }
  ],
  // Recipe result.
  "result": { "material": "charcoal" },
  // Recipe discover trigger. Optional.
  "discover": {
    // Items that discovers the recipe. List of recipe choices. Each choice can be an array with multiple elements.
    "items": [
      { "tag": "minecraft:logs" },
      { "material": "flint_and_steel" },
    ]
  }
}
```

</details>

### 3.3. Smelting Recipes
Smelting recipes can be applied to regular furnace, blast furnace, smoker or campfire.

<details>
  <summary><b>JSON EXAMPLE</b></summary>

```json5
{
  // Recipe type. For furnace recipes you can use one of: [SMELTING, BLASTING, SMOKING, CAMPFIRE_COOKING]
  "type": "smelting",
  // Furnace input. Exactly one recipe choice, which can be an array with multiple elements.
  "input": [
    { "material": "diamond_helmet" },
    { "material": "diamond_chestplate" },
    { "material": "diamond_leggings" },
    { "material": "diamond_boots" }
  ],
  // Recipe result.
  "result": { "material": "diamond" },
  // Experience to award player after taking smelting result. Optional.
  "experience": 0.7,
  // Time it takes to cook this recipe. Measured in ticks. Optional.
  "cooking_time": 200,
  // Recipe discover trigger. Optional.
  "discover": {
    // Items that discovers the recipe. List of recipe choices. Each choice can be an array with multiple elements.
    "items": [
      { "material": "diamond_helmet" },
      { "material": "diamond_chestplate" },
      { "material": "diamond_leggings" },
      { "material": "diamond_boots" }
    ]
  }
}
```
All furnace recipe types follow the same schema.

</details>

### 3.4. Smithing Recipe
Smithing recipe applies to smithing table.

<details>
  <summary><b>JSON EXAMPLE</b></summary>

```json5
{
  "type": "smithing",
  // Base item, you can think of it as an item which upgrades (could) be applied to.
  // Exactly one recipe choice. Can be an array with multiple elements.
  "base": { "material": "iron_pickaxe" },
  // Template item, you can think of it as an upgrade which is applied to the base item. Requires 1.20 or higher.
  // Exactly one recipe choice. Can be an array with multiple elements.
  "template": { "material": "air" },
  // Addition item. For vanilla recipes, it's usually a trim material.
  // Exactly one recipe choice. Can be an array with multiple elements.
  "addition": { "material": "diamond" },
  // Recipe result. Metadata is not supported as it's copied directly from the base item.
  "result": { "material": "diamond_pickaxe" },
  // Recipe discover trigger. Optional.
  "discover": {
    // Items that discovers the recipe. List of recipe choices. Each choice can be an array with multiple elements.
    "items": [
      { "material": "iron_pickaxe" }
    ]
  }
}
```
Metadata (name, lore, nbt) is not supported for result items, as it's copied directly from the base item.

</details>

### 3.5. Stonecutting Recipe
Stonecutting recipe applies to stonecutter.

<details>
  <summary><b>JSON EXAMPLE</b></summary>

```json5
{
  "type": "stonecutting",
  // Recipe input. Exactly one recipe choice, which can be an array with multiple elements.
  "input": { "material": "oak_planks" },
  // Recipe result.
  "result": { "material": "oak_stairs" },
  // Recipe discover trigger. Optional.
  "discover": {
    // Items that discovers the recipe. List of recipe choices. Each choice can be an array with multiple elements.
    "items": [
      { "material": "oak_planks" }
    ]
  }
}
```

</details>

<br />

## Contributing

To contribute to this repository just fork this repository make your changes or add your code and make a pull request.
If you find an error or a bug you can open an issue [here](https://github.com/LoreSchaeffer/NBTRecipes/issues).

<br />

## License

NBTRecipes is released under "The 3-Clause BSD License". You can find a copy [here](https://github.com/LoreSchaeffer/NBTRecipes/blob/master/LICENSE).
