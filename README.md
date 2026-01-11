<div align="center">

<img src="https://i.imgur.com/PBdAaFD.png" width=75%></img>

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/recipes-plugin?logo=modrinth&logoColor=white&label=downloads&labelColor=%23139549&color=%2318c25f)](https://modrinth.com/plugin/recipes-plugin)
[![Discord](https://img.shields.io/discord/1366851451208601783?cacheSeconds=3600&logo=discord&logoColor=white&label=%20&labelColor=%235865F2&color=%23707BF4)](https://discord.com/invite/PuzqF2Yd5q)
[![bStats Servers](https://img.shields.io/bstats/servers/27768?label=bStats&labelColor=%2300786D&color=%23009688)](https://bstats.org/plugin/bukkit/Recipes/27768)
[![CodeFactor Grade](https://img.shields.io/codefactor/grade/github/Grabsky/Recipes?logo=codefactor&logoColor=white&label=%20)](https://www.codefactor.io/repository/github/grabsky/recipes/issues/main)

**Recipes** is a plugin that allows to create new, or override existing recipes. Wide range of recipe types are supported. Documentation and examples are available [here](https://grabsky.github.io/docs/recipes/getting-started).

</div>

<br>

## Features
- **File Based**  
  Define recipes using JSON files.
- **Components Support**  
  Use **#tags** and 1.20.6+ **components** in item definitions.
- **Custom Items**  
  Easily add and update custom items using built-in custom item registry.
- **Override Existing Recipes**  
  Override vanilla and other plugins' recipes.

<br>

## Requirements
Plugin runs only on **Paper** (or **Folia**) **1.21.1** and above, powered by **Java 21** or higher.

<br>

## Contributing
Contributions are more than welcome. We are always looking for ways to improve the project.
```python
# Compiling and building artifacts.
$ ./gradlew clean build

# Testing changes using internal server.
$ ./gradlew runServer
$ ./gradlew runFolia
```