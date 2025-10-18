<div align="center">

# Recipes

<!-- [![GitHub Release](https://img.shields.io/github/v/release/Grabsky/Recipes?logo=github&labelColor=%2324292F&color=%23454F5A)](https://github.com/Grabsky/Recipes/releases/latest) -->
[![Modrinth Downloads](https://img.shields.io/modrinth/dt/recipes.?logo=modrinth&logoColor=white&label=downloads&labelColor=%23139549&color=%2318c25f)](https://modrinth.com/plugin/recipes.)
[![Discord](https://img.shields.io/discord/1366851451208601783?cacheSeconds=3600&logo=discord&logoColor=white&label=%20&labelColor=%235865F2&color=%23707BF4)](https://discord.com/invite/PuzqF2Yd5q)
[![CodeFactor Grade](https://img.shields.io/codefactor/grade/github/Grabsky/Recipes?logo=codefactor&logoColor=white&label=%20)](https://www.codefactor.io/repository/github/grabsky/recipes/issues/main)

**Recipes** is a plugin that allows to create new, or override existing recipes using format similar to one used by data-packs.

<sup>This is a maintained fork of **[LoreSchaeffer/NBTRecipes](https://github.com/LoreSchaeffer/NBTRecipes/)** which brings a bunch of fixes and new features.</sup>

</div>

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
* **`/recipes reload`**
  * Reloads recipes, items and the configuration file.
  * Permission: `recipes.command.reload`
* **`/recipes list_recipes`**
  * Lists all recipes added by this plugin.
  * Permission: `recipes.command.list_recipes`
* **`/recipes list_items`**
  * Lists all registered custom items.
  * Permission: `recipes.command.list_items`
* **`/recipes register_item (identifier)`**
  * Registers custom item that is currently held in the main hand.
  * Permission: `recipes.command.register_item`
* **`/recipes unregister_item (identifier)`**
  * Unregisters custom item with the given identifier.
  * Permission: `recipes.command.unregister_item`
* **`/recipes give_item (target) (identifier) [amount]`**
  * Gives specified custom item to the target.
  * Permission: `recipes.command.give_item`

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
// Item 'ruby' must be registered using the `/recipes register_item` command.
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
If you find an error or a bug you can open an issue [here](https://github.com/Grabsky/Recipes/issues).

<br />

## License

**Recipes** is released under **"The 3-Clause BSD License"**. You can find a copy in the **LICENSE** file in the root directory of this repository.
