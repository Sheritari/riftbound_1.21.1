# Riftbound 0.1.6



A fantasy mod for Minecraft **1.21.1** (NeoForge).



## Features (0.1.6)



- **Shard Dust** — adds or rerolls prefixes on blades in Shard Crafting

- **Shard Stone** — adds or rerolls suffixes on blades in Shard Crafting

- **Resonant Shard** — stacks to 20; a full stack auto-converts into an Orb of Resonant in inventory

- **Orb of Resonant** — placeholder currency (stacks to 40)

- **Localization** — English and Russian in-game strings



## Requirements



- **Java 21** (JDK)

- **NeoForge** for Minecraft 1.21.1



## Development setup



1. Clone the repository and open it as a Gradle project in your IDE.

2. Wait for Gradle sync.

3. Run the client:

   ```bat

   gradlew.bat runClient

   ```

4. In-game: open the **Riftbound** creative tab to grab items.



## Building the mod JAR



```bat

gradlew.bat build

```



Output: `build/libs/riftbound-0.1.3e.jar` — copy into your NeoForge 1.21.1 `mods` folder.



## Project layout



```

src/main/java/com/riftbound/

  RiftboundMod.java           — mod entry point

  registry/                   — blocks, items, menus, creative tab

  loot/                       — rarity, affixes, item generation

  transmutation/              — combine rules and deterministic seeds

  menu/                       — transmutation container

  client/                     — screens and inventory tabs

  event/                      — mob loot drops

  network/                    — client/server tab switching

```



## Roadmap (0.2+)



- Rare (yellow) items

- Crafting altar block

- Shard ore world generation

- Reroll currency (second crafting orb)



## License



See [LICENSE](LICENSE). Mod license: All Rights Reserved.

