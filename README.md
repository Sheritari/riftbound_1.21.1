# Riftbound 0.1.5b



A fantasy mod for Minecraft **1.21.1** (NeoForge).



## Features (0.1.5b)



- **Shard Dust** — adds or rerolls prefixes on blades in Shard Crafting

- **Shard Stone** — adds or rerolls suffixes on blades in Shard Crafting

- **Shard Ore** — mineable ore (creative / `/setblock` for now; worldgen planned)

- **Vaelen's Blade** — POE-style loot sword with implicit, prefixes, and suffixes

- **Rarity** — normal (white), magic (blue, 1 prefix or 1 suffix), rare (yellow, 1 prefix + 1 suffix)

- **Prefixes** — Squire's, Heated, Heavy

- **Suffixes** — of Skill, of Needling, of the Brute, of the Mongoose, of Steadiness, of Success

- **Mob loot** — hostile mobs drop dust; **Vaelen's Blade** (ilvl 1) drops only from vanilla Minecraft mobs

- **Craft tab** in the player inventory (survival and creative): horizontal input slots and a deterministic result preview

- **Transmutation** — shard dust for prefixes, shard stone for suffixes; combining both mods upgrades magic blades to rare

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

